package com.ksaraev.spotify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotify.client.dto.GetRecommendationsResponse;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.GetSpotifyRecommendationRequestConfig;
import com.ksaraev.spotify.exception.GetSpotifyRecommendationsException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.*;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyRecommendationsServiceTest {

  private static final String GET_RECOMMENDATIONS = "getRecommendations";

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  @Mock private SpotifyClient client;

  @Mock private GetSpotifyRecommendationRequestConfig requestConfig;

  @Mock private SpotifyTrackMapper trackMapper;

  @Mock private SpotifyTrackFeaturesMapper featuresMapper;

  @Captor private ArgumentCaptor<List<SpotifyTrackDto>> dtosArgumentCapture;

  @Captor private ArgumentCaptor<SpotifyTrackFeatures> featuresArgumentCaptor;

  @Captor private ArgumentCaptor<GetRecommendationsRequest> requestArgumentCaptor;

  private ExecutableValidator executableValidator;

  private AutoCloseable closeable;

  private SpotifyRecommendationsService underTest;

  @BeforeEach
  void setUp() {
    executableValidator = factory.getValidator().forExecutables();
    closeable = MockitoAnnotations.openMocks(this);
    underTest =
        new SpotifyRecommendationsService(client, requestConfig, trackMapper, featuresMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldGetRecommendations() {
    // Given
    List<SpotifyTrackItem> seedTrackItems = SpotifyServiceHelper.getTracks(2);
    List<String> seedTrackIds = seedTrackItems.stream().map(SpotifyTrackItem::getId).toList();
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(10);
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(10);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();

    GetRecommendationsRequest.TrackFeatures requestFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsRequest request =
        GetRecommendationsRequest.builder()
            .seedTrackIds(seedTrackIds)
            .trackFeatures(requestFeatures)
            .limit(limit)
            .build();

    GetRecommendationsResponse response =
        GetRecommendationsResponse.builder().trackDtos(trackDtos).build();

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(client.getRecommendations(any(GetRecommendationsRequest.class))).willReturn(response);
    given(trackMapper.mapDtosToModels(anyList())).willReturn(trackItems);
    // When
    underTest.getRecommendations(seedTrackItems, features);
    // Then
    then(featuresMapper).should().mapToRequestFeatures(featuresArgumentCaptor.capture());
    assertThat(featuresArgumentCaptor.getValue()).isNotNull().isEqualTo(features);
    then(client).should().getRecommendations(requestArgumentCaptor.capture());
    assertThat(requestArgumentCaptor.getValue()).isNotNull().isEqualTo(request);
    then(trackMapper).should().mapDtosToModels(dtosArgumentCapture.capture());
    assertThat(dtosArgumentCapture.getAllValues()).containsExactly(trackDtos);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 6})
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTrackItemsSizeIsNotValid(
      Integer tracksNumber) throws Exception {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(tracksNumber);
    SpotifyTrackFeatures features = SpotifyTrackFeatures.builder().build();

    Method method =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {trackItems, features};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTrackItems: size must be between 1 and 5");
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTracksContainsNullElements()
      throws Exception {
    // Given
    List<SpotifyTrackItem> tracks = SpotifyServiceHelper.getTracks(1);
    tracks.add(null);

    SpotifyTrackFeatures trackFeatures = SpotifyTrackFeatures.builder().build();

    Method getRecommendations =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {tracks, trackFeatures};

    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, getRecommendations, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTrackItems[1].<list element>: must not be null");
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenSeedTrackItemsIsNull()
      throws Exception {
    // Given
    SpotifyTrackFeatures features = SpotifyTrackFeatures.builder().build();

    Method method =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {null, features};
    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".seedTrackItems: must not be null");
  }

  @Test
  void itShouldDetectGetRecommendationsCascadeConstraintViolationsWhenSeedTrackItemIsNotValid()
      throws Exception {
    // Given
    String message = ".seedTrackItems[0].id: must not be null";
    SpotifyTrackItem trackItem = SpotifyServiceHelper.getTrack();
    trackItem.setId(null);
    List<SpotifyTrackItem> trackItems = List.of(trackItem);
    SpotifyTrackFeatures features = SpotifyTrackFeatures.builder().build();

    Method method =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {trackItems, features};
    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + message);
  }

  @Test
  void itShouldDetectGetRecommendationsConstraintViolationWhenTrackItemFeaturesIsNull()
      throws Exception {
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    // Given
    Method method =
        SpotifyRecommendationsService.class.getMethod(
            GET_RECOMMENDATIONS, List.class, SpotifyTrackItemFeatures.class);

    Object[] parameterValues = {trackItems, null};
    // When
    Set<ConstraintViolation<SpotifyRecommendationsService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_RECOMMENDATIONS + ".trackItemFeatures: must not be null");
  }

  @Test
  void itShouldThrowGetSpotifyRecommendationsExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(runtimeException);
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(trackItems, features))
        .isExactlyInstanceOf(GetSpotifyRecommendationsException.class)
        .hasMessage(new GetSpotifyRecommendationsException(runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();

    GetRecommendationsRequest.TrackFeatures requestTrackFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestTrackFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.getRecommendations(any(GetRecommendationsRequest.class)))
        .willThrow(spotifyUnauthorizedException);
    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(trackItems, features))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void
      itShouldThrowGetSpotifyRecommendationsExceptionWhenMapToRequestFeaturesThrowsRuntimeException() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    RuntimeException runtimeException = new RuntimeException("message");
    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(trackItems, features))
        .isExactlyInstanceOf(GetSpotifyRecommendationsException.class)
        .hasMessage(new GetSpotifyRecommendationsException(runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowGetSpotifyRecommendationsExceptionWhenSpotifyTrackMapperThrowsRuntimeException() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> trackDtos = SpotifyClientHelper.getTrackDtos(2);
    GetRecommendationsRequest.TrackFeatures requestFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse response =
        GetRecommendationsResponse.builder().trackDtos(trackDtos).build();

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(client.getRecommendations(any(GetRecommendationsRequest.class))).willReturn(response);
    RuntimeException runtimeException = new RuntimeException("message");
    given(trackMapper.mapDtosToModels(anyList())).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getRecommendations(trackItems, features))
        .isExactlyInstanceOf(GetSpotifyRecommendationsException.class)
        .hasMessage(new GetSpotifyRecommendationsException(runtimeException).getMessage());
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackDtosIsEmpty() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> trackDtos = List.of();

    GetRecommendationsRequest.TrackFeatures requestFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse response =
        GetRecommendationsResponse.builder().trackDtos(trackDtos).build();

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(client.getRecommendations(any(GetRecommendationsRequest.class))).willReturn(response);

    // Then
    assertThat(underTest.getRecommendations(trackItems, features)).isEmpty();
    then(trackMapper).should(never()).mapDtosToModels(dtosArgumentCapture.capture());
    assertThat(dtosArgumentCapture.getAllValues()).isEmpty();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyTrackDtosAreNull() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();
    List<SpotifyTrackDto> trackDtos = new ArrayList<>();
    trackDtos.add(null);
    trackDtos.add(null);

    GetRecommendationsRequest.TrackFeatures requestFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse response =
        GetRecommendationsResponse.builder().trackDtos(trackDtos).build();

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(client.getRecommendations(any(GetRecommendationsRequest.class))).willReturn(response);
    // Then
    assertThat(underTest.getRecommendations(trackItems, features)).isEmpty();
    then(trackMapper).should(never()).mapDtosToModels(dtosArgumentCapture.capture());
    assertThat(dtosArgumentCapture.getAllValues()).isEmpty();
  }

  @Test
  void itShouldReturnNonNullElementsWhenSpotifyTrackDtosContainsNulls() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    SpotifyTrackDto trackDto = SpotifyClientHelper.getTrackDto();
    SpotifyTrackItemFeatures features = SpotifyServiceHelper.getSpotifyTrackFeatures();

    List<SpotifyTrackDto> trackDtos = new ArrayList<>();
    trackDtos.add(null);
    trackDtos.add(trackDto);
    trackDtos.add(null);

    GetRecommendationsRequest.TrackFeatures requestFeatures =
        SpotifyClientHelper.getRecommendationRequestTrackFeatures();
    Integer limit = 10;

    GetRecommendationsResponse response =
        GetRecommendationsResponse.builder().trackDtos(trackDtos).build();

    given(featuresMapper.mapToRequestFeatures(any(SpotifyTrackFeatures.class)))
        .willReturn(requestFeatures);
    given(requestConfig.getLimit()).willReturn(limit);
    given(client.getRecommendations(any(GetRecommendationsRequest.class))).willReturn(response);

    // When
    underTest.getRecommendations(trackItems, features);

    // Then
    then(trackMapper).should().mapDtosToModels(dtosArgumentCapture.capture());
    assertThat(dtosArgumentCapture.getValue()).containsExactly(trackDto);
  }
}
