package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.GetSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUserMapper;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistServiceAddTracksTest {

  @Mock private SuddenrunPlaylistRepository repository;

  @Mock private AppPlaylistSynchronizationService synchronizationService;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

  @Captor private ArgumentCaptor<SpotifyPlaylistItem> spotifyPlaylistArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> appTrackRemovalsArgumentCapture;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> appTrackAdditionsArgumentCapture;

  @Captor private ArgumentCaptor<AppPlaylist> targetAppPlaylistArgumentCaptor;

  private AutoCloseable closeable;

  private AppPlaylistService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunPlaylistService(
            repository,
            synchronizationService,
            spotifyPlaylistService,
            spotifyPlaylistConfig,
            playlistMapper,
            trackMapper,
            userMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldThrowSuddenrunAddPlaylistTracksExceptionIfSuddenrunPlaylistDoesNotExist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    List<AppTrack> tracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    user.addPlaylist(playlist);
    String playlistId = playlist.getId();
    given(repository.existsById(playlistId)).willReturn(false);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, tracks))
        .isExactlyInstanceOf(AddSuddenrunPlaylistTracksException.class)
        .hasCauseExactlyInstanceOf(SuddenrunPlaylistDoesNotExistException.class)
        .hasMessageContaining(playlistId);
  }

  @Test
  void itShouldAddPlaylistTracks() {
    // Given
    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist();
    String playlistId = spotifyPlaylist.getId();
    String snapshotId = spotifyPlaylist.getSnapshotId();

    List<AppTrack> appTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist targetAppPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    targetAppPlaylist.setId(playlistId);
    targetAppPlaylist.setSnapshotId(snapshotId);
    targetAppPlaylist.setInclusions(List.of());
    targetAppPlaylist.setExclusions(List.of());
    List<AppTrack> targetAppTracks = new ArrayList<>(targetAppPlaylist.getTracks());
    given(repository.findById(playlistId)).willReturn(Optional.of(targetAppPlaylist));

    List<SpotifyTrackItem> spotifyTrackRemovals = SpotifyServiceHelper.getTracks(5);

    List<AppTrack> appTrackRemovals =
        spotifyTrackRemovals.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.getId())
                        .name(spotifyTrack.getName())
                        .build())
            .collect(Collectors.toList());

    given(playlistMapper.mapToDto(targetAppPlaylist)).willReturn(spotifyPlaylist);

    targetAppTracks.addAll(appTrackRemovals);
    given(synchronizationService.findPlaylistNoneMatchTracks(targetAppPlaylist, appTracks))
        .willReturn(appTrackRemovals);

    given(trackMapper.mapToDtos(appTrackRemovals)).willReturn(spotifyTrackRemovals);

    String removalSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    given(spotifyPlaylistService.removeTracks(any(), any())).willReturn(removalSnapshotId);

    List<SpotifyTrackItem> spotifyTrackAdditions = SpotifyServiceHelper.getTracks(5);

    List<AppTrack> appTrackAdditions =
        spotifyTrackAdditions.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.getId())
                        .name(spotifyTrack.getName())
                        .build())
            .collect(Collectors.toList());

    given(synchronizationService.findTracksNoneMatchPlaylist(targetAppPlaylist, appTracks))
        .willReturn(appTrackAdditions);

    given(trackMapper.mapToDtos(appTrackAdditions)).willReturn(spotifyTrackAdditions);

    String additionSnapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    targetAppTracks.addAll(appTrackAdditions);
    given(spotifyPlaylistService.addTracks(any(), any())).willReturn(additionSnapshotId);

    SpotifyPlaylistItem sourceSpotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    sourceSpotifyPlaylist.setTracks(spotifyTrackAdditions);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(sourceSpotifyPlaylist);

    SuddenrunPlaylist sourceAppPlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId);
    targetAppPlaylist.setTracks(targetAppTracks);
    targetAppPlaylist.setSnapshotId(additionSnapshotId);
    given(playlistMapper.mapToEntity(sourceSpotifyPlaylist)).willReturn(targetAppPlaylist);

    given(synchronizationService.updateFromSource(targetAppPlaylist, sourceAppPlaylist))
        .willReturn(sourceAppPlaylist);

    given(repository.save(sourceAppPlaylist)).willReturn(sourceAppPlaylist);

    // When
    AppPlaylist actualAppPlaylist = underTest.addTracks(targetAppPlaylist, appTracks);

    // Then
    then(spotifyPlaylistService)
        .should()
        .removeTracks(
            spotifyPlaylistArgumentCaptor.capture(), appTrackRemovalsArgumentCapture.capture());
    assertThat(spotifyPlaylistArgumentCaptor.getValue()).isEqualTo(spotifyPlaylist);
    assertThat(appTrackRemovalsArgumentCapture.getValue())
        .containsExactlyElementsOf(spotifyTrackRemovals);

    then(spotifyPlaylistService)
        .should()
        .addTracks(
            spotifyPlaylistArgumentCaptor.capture(), appTrackAdditionsArgumentCapture.capture());
    assertThat(appTrackAdditionsArgumentCapture.getValue())
        .containsExactlyElementsOf(spotifyTrackAdditions);

    then(synchronizationService)
        .should()
        .updateFromSource(targetAppPlaylistArgumentCaptor.capture(), any());
    List<AppTrack> actualAppTracks = targetAppPlaylistArgumentCaptor.getValue().getTracks();
    assertThat(actualAppTracks)
        .containsAll(appTrackAdditions)
        .doesNotContainAnyElementsOf(appTrackRemovals);

    assertThat(actualAppPlaylist.getTracks())
        .containsAll(appTrackAdditions)
        .doesNotContainAnyElementsOf(appTrackRemovals);
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenException() {
    // Given
    String message = "message";
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    given(repository.findById(playlistId)).willReturn(Optional.of(suddenrunPlaylist));
    SpotifyAccessTokenException spotifyAccessTokenException =
        new SpotifyAccessTokenException(message);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willThrow(spotifyAccessTokenException);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessage(new SuddenrunAuthenticationException(spotifyAccessTokenException).getMessage());
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceException() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    given(repository.findById(playlistId)).willReturn(Optional.of(suddenrunPlaylist));
    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTracks = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTracks);
    RuntimeException runtimeException = new RuntimeException("message");
    GetSpotifyPlaylistException getSpotifyPlaylistException =
        new GetSpotifyPlaylistException(playlistId, runtimeException);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willThrow(getSpotifyPlaylistException);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasCauseExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessage(
            new SuddenrunSpotifyInteractionException(getSpotifyPlaylistException).getMessage());
  }

  @Test
  void itShouldThrowAddSuddenrunPlaylistTracksExceptionIfSpotifyServiceThrowsRuntimeException() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    List<AppTrack> suddenrunTracks = SuddenrunHelper.getTracks(10);
    SuddenrunPlaylist suddenrunPlaylist = SuddenrunHelper.getSuddenrunPlaylist(suddenrunUser);
    suddenrunUser.addPlaylist(suddenrunPlaylist);
    String playlistId = suddenrunPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalPlaylist = Optional.of(suddenrunPlaylist);
    given(repository.findById(playlistId)).willReturn(optionalPlaylist);
    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    List<SpotifyTrackItem> spotifyTracks = SpotifyServiceHelper.getTracks(10);
    spotifyPlaylist.setTracks(spotifyTracks);
    RuntimeException runtimeException = new RuntimeException("message");
    given(spotifyPlaylistService.getPlaylist(playlistId)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(suddenrunPlaylist, suddenrunTracks))
        .isExactlyInstanceOf(AddSuddenrunPlaylistTracksException.class)
        .hasMessage(
            new AddSuddenrunPlaylistTracksException(playlistId, runtimeException).getMessage());
  }
}
