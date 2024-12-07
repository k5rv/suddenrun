package com.ksaraev.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.dto.SpotifyArtistDto;
import jakarta.validation.*;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyArtistDtoTest {

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
           null                  |artist name|spotify:artist:012345012345AABBccDDee|id: must not be null
           012345012345AABBccDDee|null       |spotify:artist:012345012345AABBccDDee|name: must not be empty
           012345012345AABBccDDee|artist name|null                                 |uri: must not be null
           """)
  void itShouldDetectSpotifyArtistItemConstraintViolations(
      String id, String name, URI uri, String message) {
    // Given
    SpotifyArtistDto artistItem = SpotifyArtistDto.builder().id(id).name(name).uri(uri).build();
    // When
    Set<ConstraintViolation<SpotifyArtistDto>> constraintViolations =
        validator.validate(artistItem);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
