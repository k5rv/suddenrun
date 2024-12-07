package com.ksaraev.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.dto.SpotifyAlbumDto;
import jakarta.validation.*;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyAlbumDtoTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock =
          """
           null       |Konstantin|spotify:album:0t58MAAaEcFOfp5aeljocU|id: must not be null
           12122604372|null      |spotify:album:0t58MAAaEcFOfp5aeljocU|name: must not be empty
           12122604372|Konstantin|null                                |uri: must not be null
           """)
  void itShouldDetectSpotifyUserProfileItemConstraintViolations(
      String id, String name, URI uri, String message) {
    // Given
    SpotifyAlbumDto albumItem = SpotifyAlbumDto.builder().id(id).name(name).uri(uri).build();
    // When
    Set<ConstraintViolation<SpotifyAlbumDto>> constraintViolations = validator.validate(albumItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
