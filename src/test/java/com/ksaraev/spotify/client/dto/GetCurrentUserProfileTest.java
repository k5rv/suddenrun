package com.ksaraev.spotify.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.utils.stubs.SpotifyClientStub;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetCurrentUserProfileTest {

  private final String GET_CURRENT_USER_PROFILE = "getCurrentUserProfile";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method = SpotifyClient.class.getMethod(GET_CURRENT_USER_PROFILE);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectGetCurrentUserProfileMethodConstraintViolationWhenReturnValueIsNull() {
    String message = ".<return value>: must not be null";

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_CURRENT_USER_PROFILE + message);
  }

  @Test
  void
      itShouldDetectGetCurrentUserProfileMethodConstraintViolationWhenReturnValueSpotifyUserProfileItemIsNotValid() {
    // Given
    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id(null)
            .displayName("name")
            .email("email@gmail.com")
            .uri(URI.create("spotify:user:12122604372"))
            .build();

    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, userProfileItem);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_CURRENT_USER_PROFILE + ".<return value>.id: must not be null");
  }
}
