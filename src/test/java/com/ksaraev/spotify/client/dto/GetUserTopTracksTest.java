package com.ksaraev.spotify.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.stubs.SpotifyClientStub;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetUserTopTracksTest {

  private final String GET_USER_TOP_TRACKS = "getUserTopTracks";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method = SpotifyClient.class.getMethod(GET_USER_TOP_TRACKS, GetUserTopTracksRequest.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectGetUserTopTracksMethodConstraintViolationWhenGetUserTopTracksRequestIsNull() {
    // Given
    String message = ".request: must not be null";

    // When
    Object[] parameterValues = {null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + message);
  }

  @Test
  void
      itShouldDetectGetUserTopTracksMethodCascadeConstraintViolationWhenGetUserTopTracksRequestIsNotValid() {
    // Given
    GetUserTopTracksRequest getUserTopTracksRequest =
        GetUserTopTracksRequest.builder().limit(51).offset(0).build();

    // When
    Object[] parameterValues = {getUserTopTracksRequest};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + ".request.limit: must be less than or equal to 50");
  }

  @Test
  void itShouldDetectGetUserTopTracksMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + message);
  }

  @Test
  void
      itShouldDetectGetUserTopTracksMethodCascadeConstraintViolationWhenReturnValueGetUserTopTracksResponseIsNotValid() {
    // Given
    List<SpotifyArtistDto> artistDtos = SpotifyClientHelper.getArtistDtos(2);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistDtos(artistDtos)
            .build();

    List<SpotifyTrackDto> trackItems = List.of(trackItem);

    GetUserTopTracksResponse getUserTopTracksResponse =
        GetUserTopTracksResponse.builder().trackDtos(trackItems).build();

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, getUserTopTracksResponse);

    // Then
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_USER_TOP_TRACKS + ".<return value>.trackDtos[0].id: must not be null");
  }
}
