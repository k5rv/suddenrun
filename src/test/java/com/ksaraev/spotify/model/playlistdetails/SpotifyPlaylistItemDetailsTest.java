package com.ksaraev.spotify.model.playlistdetails;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.*;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpotifyPlaylistItemDetailsTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
  }

  @Test
  void itShouldDetectSpotifyPlaylistConstraintViolations() {
    // Given
    String message = "name: must not be empty";
    SpotifyPlaylistItemDetails playlistDetails = SpotifyPlaylistDetails.builder().name(null).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemDetails>> constraintViolations =
        validator.validate(playlistDetails);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
