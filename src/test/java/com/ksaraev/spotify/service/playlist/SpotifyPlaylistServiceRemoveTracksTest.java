package com.ksaraev.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.RemovePlaylistItemsRequest;
import com.ksaraev.spotify.client.dto.RemovePlaylistItemsResponse;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.RemoveSpotifyPlaylistTracksException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.spotify.service.SpotifyPlaylistService;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyPlaylistServiceRemoveTracksTest {

  private static final String REMOVE_TRACKS = "removeTracks";

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @Mock private SpotifyClient client;

  @Mock private SpotifyPlaylistMapper mapper;

  @Mock private UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<RemovePlaylistItemsRequest> requestArgumentCaptor;

  private AutoCloseable closeable;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldRemoveTracks() {
    // Given
    SpotifyPlaylistItem spotifyPlaylistItem = SpotifyServiceHelper.getPlaylist();
    String playlistId = spotifyPlaylistItem.getId();
    String snapshotId = spotifyPlaylistItem.getSnapshotId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<URI> uris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
    RemovePlaylistItemsRequest request =
        RemovePlaylistItemsRequest.builder().uris(uris).snapshotId(snapshotId).build();
    RemovePlaylistItemsResponse response = SpotifyClientHelper.createRemovePlaylistItemsResponse();
    given(client.removePlaylistItems(any(), any())).willReturn(response);

    // When
    underTest.removeTracks(spotifyPlaylistItem, trackItems);

    // Then
    then(client)
        .should()
        .removePlaylistItems(playlistIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlistId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
  }

  @Test
  void itShouldThrowAddTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.removePlaylistItems(any(), any())).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.removeTracks(playlist, trackItems))
        .isExactlyInstanceOf(RemoveSpotifyPlaylistTracksException.class)
        .hasMessage(
            new RemoveSpotifyPlaylistTracksException(playlistId, runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    given(client.removePlaylistItems(any(), any())).willThrow(new SpotifyUnauthorizedException());
    // Then
    assertThatThrownBy(() -> underTest.removeTracks(playlist, trackItems))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class);
  }

  @Test
  void itShouldDetectRemoveTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNull()
      throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    Method method =
        SpotifyPlaylistService.class.getMethod(
            REMOVE_TRACKS, SpotifyPlaylistItem.class, List.class);
    Object[] parameterValues = {null, trackItems};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        validator.forExecutables().validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".spotifyPlaylistItem: must not be null");
  }

  @Test
  void itShouldDetectRemoveTracksCascadeConstraintViolationWhenSpotifySnapshotIdIsNull()
      throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    playlist.setSnapshotId(null);
    Method method =
        SpotifyPlaylistService.class.getMethod(
            REMOVE_TRACKS, SpotifyPlaylistItem.class, List.class);
    Object[] parameterValues = {playlist, trackItems};
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        validator.forExecutables().validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".spotifyPlaylistItem.snapshotId: must not be null");
  }

  @Test
  void itShouldDetectRemoveTracksConstraintViolationWhenTrackListIsEmpty() throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = List.of();
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(
            REMOVE_TRACKS, SpotifyPlaylistItem.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        validator.forExecutables().validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectRemoveTracksConstraintViolationWhenTrackListSizeMoreThan100()
      throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(101);
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(
            REMOVE_TRACKS, SpotifyPlaylistItem.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        validator.forExecutables().validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectRemoveTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements()
      throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
    tracks.add(1, track);
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(
            REMOVE_TRACKS, SpotifyPlaylistItem.class, List.class);
    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        validator.forExecutables().validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(REMOVE_TRACKS + ".trackItems[1].id: must not be null");
  }
}
