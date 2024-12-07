package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.GetSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunPlaylistServiceGetPlaylistTest {

  @Mock private SuddenrunPlaylistRepository repository;

  @Mock private AppPlaylistSynchronizationService synchronizationService;

  @Mock private SpotifyPlaylistItemService spotifyPlaylistService;

  @Mock private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Mock private AppPlaylistMapper playlistMapper;

  @Mock private AppUserMapper userMapper;

  @Mock private AppTrackMapper trackMapper;

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
  void itShouldReturnEmptyOptionalIfSuddenrunUserPlaylistDoesNotExist() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String userId = suddenrunUser.getId();
    given(repository.findByUserId(userId)).willReturn(Optional.empty());

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(suddenrunUser);

    // Then
    assertThat(result).isNotPresent();
  }

  @Test
  void itShouldReturnEmptyOptionalIfSuddenrunPlaylistDoesNotExist() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String playlistId = SpotifyResourceHelper.getRandomId();
    given(repository.findById(playlistId)).willReturn(Optional.empty());

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(playlistId);

    // Then
    assertThat(result).isNotPresent();
  }

  @Test
  void itShouldReturnEmptyOptionalIfSpotifyPlaylistDoesNotExist() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);

    Optional<SuddenrunPlaylist> optionalAppPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalAppPlaylist);

    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(spotifyUser);
    List<SpotifyPlaylistItem> spotifyUserPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser)).willReturn(spotifyUserPlaylists);

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(appUser);

    // Then
    assertThat(result).isNotPresent();
  }

  @Test
  void itShouldGetUserPlaylistIfSnapshotsInSuddenrunAndSpotifyAreEqual() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    String playlistSnapshotId = appPlaylist.getSnapshotId();

    Optional<SuddenrunPlaylist> optionalAppPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalAppPlaylist);

    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    spotifyPlaylist.setSnapshotId(playlistSnapshotId);
    spotifyPlaylist.setUser(spotifyUser);
    List<SpotifyPlaylistItem> spotifyUserPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser)).willReturn(spotifyUserPlaylists);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(appUser);

    // Then
    assertThat(result)
        .isPresent()
        .hasValueSatisfying(playlist -> assertThat(playlist).isEqualTo(appPlaylist));
  }

  @Test
  void itShouldGePlaylistIfSnapshotsInSuddenrunAndSpotifyAreEqual() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    String playlistSnapshotId = appPlaylist.getSnapshotId();

    Optional<SuddenrunPlaylist> optionalAppPlaylist = Optional.of(appPlaylist);
    given(repository.findById(playlistId)).willReturn(optionalAppPlaylist);

    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);

    SpotifyPlaylistItem spotifyPlaylist = SpotifyServiceHelper.getPlaylist(playlistId);
    spotifyPlaylist.setSnapshotId(playlistSnapshotId);
    spotifyPlaylist.setUser(spotifyUser);
    List<SpotifyPlaylistItem> spotifyUserPlaylists = List.of(spotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser)).willReturn(spotifyUserPlaylists);
    given(spotifyPlaylistService.getPlaylist(playlistId)).willReturn(spotifyPlaylist);

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(playlistId);

    // Then
    assertThat(result)
        .isPresent()
        .hasValueSatisfying(playlist -> assertThat(playlist).isEqualTo(appPlaylist));
  }

  @Test
  void itShouldGetPlaylistIfSnapshotsInSuddenrunAndSpotifyAreNotEqual() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist targetAppPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String appPlaylistId = targetAppPlaylist.getId();

    Optional<SuddenrunPlaylist> optionalAppPlaylist = Optional.of(targetAppPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalAppPlaylist);

    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);

    SpotifyPlaylistItem sourceSpotifyPlaylist = SpotifyServiceHelper.getPlaylist(appPlaylistId);
    List<SpotifyTrackItem> sourceSpotifyTracks = SpotifyServiceHelper.getTracks(10);
    sourceSpotifyPlaylist.setTracks(sourceSpotifyTracks);
    sourceSpotifyPlaylist.setUser(spotifyUser);
    String spotifySourcePlaylistSnapshotId = sourceSpotifyPlaylist.getSnapshotId();
    List<SpotifyPlaylistItem> spotifyUserPlaylists = List.of(sourceSpotifyPlaylist);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser)).willReturn(spotifyUserPlaylists);
    given(spotifyPlaylistService.getPlaylist(appPlaylistId)).willReturn(sourceSpotifyPlaylist);

    SuddenrunPlaylist sourceAppPlaylist =
        SuddenrunHelper.getSuddenrunPlaylist(appPlaylistId, appUser);
    sourceAppPlaylist.setSnapshotId(spotifySourcePlaylistSnapshotId);
    List<AppTrack> sourceAppTracks =
        sourceSpotifyTracks.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.getId())
                        .name(spotifyTrack.getName())
                        .build())
            .collect(Collectors.toList());
    sourceAppPlaylist.setTracks(sourceAppTracks);
    given(playlistMapper.mapToEntity(sourceSpotifyPlaylist)).willReturn(sourceAppPlaylist);
    given(synchronizationService.updateFromSource(targetAppPlaylist, sourceAppPlaylist))
        .willReturn(sourceAppPlaylist);
    given(repository.save(sourceAppPlaylist)).willReturn(sourceAppPlaylist);

    // When
    Optional<AppPlaylist> result = underTest.getPlaylist(appUser);

    // Then
    assertThat(result)
        .isPresent()
        .hasValueSatisfying(
            playlist -> assertThat(playlist.getTracks()).containsAll(sourceAppTracks))
        .hasValueSatisfying(playlist -> assertThat(playlist).isEqualTo(sourceAppPlaylist));
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenExceptionWhileGettingByAppUser() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);
    SpotifyAccessTokenException spotifyAccessTokenException = new SpotifyAccessTokenException("message");
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser))
        .willThrow(spotifyAccessTokenException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(appUser))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessage(new SuddenrunAuthenticationException(spotifyAccessTokenException).getMessage());
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenExceptionWhileGettingByPlaylistId() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findById(playlistId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);
    SpotifyAccessTokenException spotifyAccessTokenException =
        new SpotifyAccessTokenException("message");
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser))
        .willThrow(spotifyAccessTokenException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessage(new SuddenrunAuthenticationException(spotifyAccessTokenException).getMessage());
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceExceptionWhileGettingByAppUser() {
    // Given

    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);
    RuntimeException runtimeException = new RuntimeException("message");
    GetSpotifyPlaylistException getSpotifyPlaylistException =
        new GetSpotifyPlaylistException(playlistId, runtimeException);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser))
        .willThrow(getSpotifyPlaylistException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(appUser))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasMessage(
            new SuddenrunSpotifyInteractionException(getSpotifyPlaylistException).getMessage());
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceExceptionWhileGettingByPlaylistId() {
    // Given

    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findById(playlistId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);
    RuntimeException runtimeException = new RuntimeException("message");
    GetSpotifyPlaylistException getSpotifyPlaylistException =
        new GetSpotifyPlaylistException(playlistId, runtimeException);
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser))
        .willThrow(getSpotifyPlaylistException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasMessage(
            new SuddenrunSpotifyInteractionException(getSpotifyPlaylistException).getMessage());
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfSpotifyServiceThrowsRuntimeException() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    String userName = appUser.getName();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalOfPlaylist);
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile(userId, userName);
    given(userMapper.mapToItem(appUser)).willReturn(spotifyUser);
    RuntimeException runtimeException = new RuntimeException("message");
    given(spotifyPlaylistService.getUserPlaylists(spotifyUser)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(appUser))
        .isExactlyInstanceOf(GetSuddenrunUserPlaylistException.class)
        .hasMessage(new GetSuddenrunUserPlaylistException(userId, runtimeException).getMessage());
  }

  @Test
  void itShouldThrowGetSuddenrunUserPlaylistExceptionIfMapperThrowsRuntimeException() {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    given(repository.findByUserId(userId)).willReturn(optionalOfPlaylist);
    RuntimeException runtimeException = new RuntimeException("message");
    given(userMapper.mapToItem(appUser)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(appUser))
        .isExactlyInstanceOf(GetSuddenrunUserPlaylistException.class)
        .hasMessage(new GetSuddenrunUserPlaylistException(userId, runtimeException).getMessage());
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfMapperThrowsRuntimeException() {
    // Given
    RuntimeException runtimeException = new RuntimeException("message");
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    SuddenrunPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    Optional<SuddenrunPlaylist> optionalOfPlaylist = Optional.of(appPlaylist);
    String playlistId = appPlaylist.getId();
    given(repository.findById(playlistId)).willReturn(optionalOfPlaylist);
    given(userMapper.mapToItem(appUser)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSuddenrunPlaylistException.class)
        .hasMessage(new GetSuddenrunPlaylistException(playlistId, runtimeException).getMessage());
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfFindByUserIdRepositoryThrowsRuntimeException() {
    // Given
    RuntimeException runtimeException = new RuntimeException("message");
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    given(repository.findByUserId(userId)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(appUser))
        .isExactlyInstanceOf(GetSuddenrunUserPlaylistException.class)
        .hasMessage(new GetSuddenrunUserPlaylistException(userId, runtimeException).getMessage());
  }

  @Test
  void itShouldThrowGetSuddenrunPlaylistExceptionIfFindByIdRepositoryThrowsRuntimeException() {
    // Given
    String playlistId = SpotifyResourceHelper.getRandomId();
    RuntimeException runtimeException = new RuntimeException("message");
    given(repository.findById(playlistId)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSuddenrunPlaylistException.class)
        .hasMessage(new GetSuddenrunPlaylistException(playlistId, runtimeException).getMessage());
  }
}
