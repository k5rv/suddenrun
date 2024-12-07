package com.ksaraev.suddenrun.track;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.spotify.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotify.service.SpotifyUserTopTrackItemsService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.Collection;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunTrackServiceTest {

  @Mock private SpotifyPlaylistItemConfig config;

  @Mock private AppTrackMapper mapper;

  @Mock private SpotifyUserTopTrackItemsService spotifyTopTracksService;

  @Mock private SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> mapperArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> recommendationsArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyTrackItemFeatures> featuresArgumentCaptor;

  private AutoCloseable closeable;

  private AppTrackService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SuddenrunTrackService(
            config, mapper, spotifyTopTracksService, spotifyRecommendationsService);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldReturnTracks() {
    // Given
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);

    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);

    SpotifyTrackItem trackItem = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(trackItem);
    given(spotifyTopTracksService.getUserTopTracks()).willReturn(userTopTracks);

    int recommendationsNumber = 10;
    List<SpotifyTrackItem> recommendations = SpotifyServiceHelper.getTracks(recommendationsNumber);
    given(spotifyRecommendationsService.getRecommendations(userTopTracks, features))
        .willReturn(recommendations);

    List<AppTrack> appTracks = SuddenrunHelper.getTracks(playlistSize);
    given(mapper.mapToEntities(any())).willReturn(appTracks);

    // When
    List<AppTrack> tracks = underTest.getTracks();

    // Then
    then(mapper).should().mapToEntities(mapperArgumentCaptor.capture());
    List<SpotifyTrackItem> trackItemsArgumentCaptorValue = mapperArgumentCaptor.getValue();
    Assertions.assertThat(trackItemsArgumentCaptorValue)
        .isNotNull()
        .hasSameElementsAs(recommendations);
    Assertions.assertThat(tracks).hasSameElementsAs(appTracks);
  }

  @Test
  void itShouldGeNumberOfTracksThatEqualsToConfigPlaylistSize() {
    // Given
    int playlistSize = 2;
    given(config.getSize()).willReturn(playlistSize);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);

    SpotifyTrackItem topTrackItemA = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem topTrackItemB = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem topTrackItemC = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(topTrackItemA, topTrackItemB, topTrackItemC);
    given(spotifyTopTracksService.getUserTopTracks()).willReturn(userTopTracks);

    SpotifyTrackItem recommendationTrackItemA = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem recommendationTrackItemB = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem recommendationTrackItemC = SpotifyServiceHelper.getTrack();
    given(spotifyRecommendationsService.getRecommendations(List.of(topTrackItemA), features))
        .willReturn(List.of(recommendationTrackItemA));
    given(spotifyRecommendationsService.getRecommendations(List.of(topTrackItemB), features))
        .willReturn(List.of(recommendationTrackItemB));
    given(spotifyRecommendationsService.getRecommendations(List.of(topTrackItemC), features))
        .willReturn(List.of(recommendationTrackItemC));

    // When
    underTest.getTracks();

    // Then
    verify(spotifyRecommendationsService, times(2))
        .getRecommendations(
            recommendationsArgumentCaptor.capture(), featuresArgumentCaptor.capture());
    verify(mapper).mapToEntities(mapperArgumentCaptor.capture());
    List<SpotifyTrackItem> mapperArgumentCaptorValues =
        mapperArgumentCaptor.getAllValues().stream().flatMap(Collection::stream).toList();
    assertThat(mapperArgumentCaptorValues)
        .hasSize(playlistSize)
        .contains(recommendationTrackItemA, recommendationTrackItemB);
  }

  @Test
  void itShouldThrowGetSuddenrunTracksExceptionIfSpotifyClientThrowsRuntimeException() {
    // Given
    String message = "message";
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);
    given(spotifyTopTracksService.getUserTopTracks()).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getTracks())
        .isExactlyInstanceOf(GetSuddenrunTracksException.class)
        .hasMessageContaining(message);
  }

  @Test
  void itShouldThrowGetSuddenrunTracksExceptionIfTrackMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);

    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);

    SpotifyTrackItem trackItem = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(trackItem);
    given(spotifyTopTracksService.getUserTopTracks()).willReturn(userTopTracks);

    int recommendationsNumber = 10;
    List<SpotifyTrackItem> recommendations = SpotifyServiceHelper.getTracks(recommendationsNumber);
    given(spotifyRecommendationsService.getRecommendations(userTopTracks, features))
        .willReturn(recommendations);

    given(mapper.mapToEntities(any())).willThrow(new RuntimeException(message));

    // Then
    assertThatThrownBy(() -> underTest.getTracks())
        .isExactlyInstanceOf(GetSuddenrunTracksException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSuddenrunSpotifyInteractionExceptionIfSpotifyServiceThrowsSpotifyServiceException() {
    // Given
    String message = "message";
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);
    given(spotifyTopTracksService.getUserTopTracks())
        .willThrow(new SpotifyServiceException(message));

    // Then
    assertThatThrownBy(() -> underTest.getTracks())
        .isExactlyInstanceOf(SuddenrunSpotifyInteractionException.class)
        .hasMessageContaining(message);
  }

  @Test
  void
      itShouldThrowSuddenrunAuthenticationExceptionIfSpotifyServiceThrowsSpotifyAccessTokenException() {
    // Given
    String message = "message";
    int playlistSize = 10;
    given(config.getSize()).willReturn(playlistSize);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    given(config.getMusicFeatures()).willReturn(features);
    given(spotifyTopTracksService.getUserTopTracks())
        .willThrow(new SpotifyAccessTokenException(message));

    // Then
    assertThatThrownBy(() -> underTest.getTracks())
        .isExactlyInstanceOf(SuddenrunAuthenticationException.class)
        .hasMessageContaining(message);
  }
}
