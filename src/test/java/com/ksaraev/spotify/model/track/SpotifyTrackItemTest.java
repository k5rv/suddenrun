package com.ksaraev.spotify.model.track;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyTrackItemTest {

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
           null                   |track name|spotify:track:1234567890AaBbCcDdEeFfG|51 |id: must not be null
           1234567890AaBbCcDdEeFfG|null      |spotify:track:1234567890AaBbCcDdEeFfG|51 |name: must not be empty
           1234567890AaBbCcDdEeFfG|track name|null                                 |51 |uri: must not be null
           1234567890AaBbCcDdEeFfG|track name|spotify:track:1234567890AaBbCcDdEeFfG|-1 |popularity: must be greater than or equal to 0
           1234567890AaBbCcDdEeFfG|track name|spotify:track:1234567890AaBbCcDdEeFfG|101|popularity: must be less than or equal to 100
           """)
  void itShouldDetectTrackConstraintViolations(
      String id, String name, URI uri, Integer popularity, String message) {
    // Given
    List<SpotifyArtistItem> artists =  List.of();

    SpotifyTrackItem track =
        SpotifyTrack.builder().id(id).name(name).uri(uri).popularity(popularity).artists(artists).build();

    // When
    Set<ConstraintViolation<SpotifyTrackItem>> constraintViolations = validator.validate(track);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyTrackCascadeConstraintViolations() {
    // Given
    String message = "artists[0].id: must not be null";

    SpotifyArtistItem artist = SpotifyServiceHelper.getArtist();
    artist.setId(null);

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setArtists(artists);

    // When
    Set<ConstraintViolation<SpotifyTrackItem>> constraintViolations = validator.validate(track);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
