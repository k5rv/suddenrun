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
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetPlaylistTest {

  private final String GET_PLAYLIST = "getPlaylist";
  private SpotifyClient object;
  private Method method;
  private ExecutableValidator executableValidator;

  @BeforeEach
  void setUp() throws Exception {
    object = new SpotifyClientStub();
    method = SpotifyClient.class.getMethod(GET_PLAYLIST, String.class);
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
  }

  @Test
  void itShouldDetectGetPlaylistMethodConstraintViolationWhenPlaylistIdIsNull() {
    // Given
    String message = ".playlistId: must not be null";

    // When
    Object[] parameterValues = {null};
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_PLAYLIST + message);
  }

  @Test
  void itShouldDetectGetPlaylistMethodConstraintViolationWhenReturnValueIsNull() {
    // Given
    String message = ".<return value>: must not be null";
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, null);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_PLAYLIST + message);
  }

  @Test
  void
      itShouldDetectGetPlaylistMethodCascadeConstraintViolationWhenReturnValueSpotifyPlaylistItemIsNotValid() {
    // Given
    String message = ".<return value>.id: must not be null";

    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();

    SpotifyPlaylistDto playlistItem =
        SpotifyPlaylistDto.builder()
            .id(null)
            .name("playlist name")
            .uri(URI.create("spotify:playlist:0moWPCTPTShumonjlsDgLe"))
            .userProfileDto(userProfileDto)
            .snapshotId("MywyM2Y2Zjg5YTdlNGQ3MmI2OGFiN2NiZmQ4NTNlZDdlMjE2OTFjODM4")
            .build();
    // When
    Set<ConstraintViolation<SpotifyClient>> constraintViolations =
        executableValidator.validateReturnValue(object, method, playlistItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(GET_PLAYLIST + message);
  }
}
