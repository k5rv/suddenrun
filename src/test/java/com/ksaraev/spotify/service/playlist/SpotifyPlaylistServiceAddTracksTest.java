package com.ksaraev.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.AddPlaylistItemsRequest;
import com.ksaraev.spotify.client.dto.AddPlaylistItemsResponse;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.AddSpotifyPlaylistTracksExceptions;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.spotify.service.SpotifyPlaylistService;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
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

class SpotifyPlaylistServiceAddTracksTest {

  private static final String ADD_TRACKS = "addTracks";

  private static final ValidatorFactory validatorFactory =
      Validation.buildDefaultValidatorFactory();

  @Mock private SpotifyClient client;
  @Mock private SpotifyPlaylistMapper mapper;

  @Mock private UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<AddPlaylistItemsRequest> requestArgumentCaptor;

  private ExecutableValidator executableValidator;

  private AutoCloseable closeable;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    executableValidator = validatorFactory.getValidator().forExecutables();
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldAddTracks() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<URI> uris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
    Integer position = 10;
    AddPlaylistItemsRequest request =
        AddPlaylistItemsRequest.builder().uris(uris).position(position).build();
    AddPlaylistItemsResponse response = SpotifyClientHelper.createAddPlaylistItemsResponse();
    given(requestConfig.getPosition()).willReturn(position);
    given(client.addPlaylistItems(any(), any())).willReturn(response);
    // When
    underTest.addTracks(playlist, trackItems);
    // Then
    then(client)
        .should()
        .addPlaylistItems(playlistIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
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
    given(client.addPlaylistItems(any(), any())).willThrow(runtimeException);
    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, trackItems))
        .isExactlyInstanceOf(AddSpotifyPlaylistTracksExceptions.class)
        .hasMessage(
            new AddSpotifyPlaylistTracksExceptions(playlistId, runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.addPlaylistItems(any(), any())).willThrow(spotifyUnauthorizedException);

    // Then
    assertThatThrownBy(() -> underTest.addTracks(playlist, trackItems))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenSpotifyPlaylistIsNull()
      throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    Method method =
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylistItem.class, List.class);
    Object[] parameterValues = {null, trackItems};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".spotifyPlaylistItem: must not be null");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListIsEmpty() throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = List.of();
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylistItem.class, List.class);

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksConstraintViolationWhenTrackListSizeMoreThan100() throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(101);
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylistItem.class, List.class);

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddTracksCascadeConstraintViolationWhenTrackListContainsNotValidElements()
      throws Exception {
    // Given
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setId(null);
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
    tracks.add(1, track);
    Object[] parameterValues = {playlist, tracks};
    Method method =
        SpotifyPlaylistService.class.getMethod(ADD_TRACKS, SpotifyPlaylistItem.class, List.class);

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(ADD_TRACKS + ".trackItems[1].id: must not be null");
  }
}
