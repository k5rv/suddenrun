package com.ksaraev.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.dto.SpotifyArtistDto;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import jakarta.validation.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistTrackDtoTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           TRUE |FALSE|FALSE|trackDto: must not be null
           FALSE|TRUE |FALSE|addedBy: must not be null
           FALSE|FALSE|TRUE |addedAt: must not be null
           """)
  void itShouldDetectSpotifyPlaylistItemTrackConstraintViolations(
      Boolean isTrackItemNull, Boolean isAddedByNull, Boolean isAddedAtNull, String message) {
    // Given

    SpotifyUserProfileDto userProfileDto =
        isAddedByNull
            ? null
            : SpotifyUserProfileDto.builder()
                .id("12122604372")
                .displayName("name")
                .uri(URI.create("spotify:user:12122604372"))
                .email("email@mail.com")
                .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackDto =
        isTrackItemNull
            ? null
            : SpotifyTrackDto.builder()
                .id("1234567890AaBbCcDdEeFfG")
                .name("playlist name")
                .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
                .popularity(51)
                .artistDtos(artistItems)
                .build();

    String addedAt = isAddedAtNull ? null : "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDto)
            .addedBy(userProfileDto)
            .addedAt(addedAt)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistTrackDto>> constraintViolations =
        validator.validate(playlistItemTrack);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemTrackCascadeConstraintViolations() {
    // Given
    String message = "addedBy.id: must not be null";

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id(null)
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("artist name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackDto =
        SpotifyTrackDto.builder()
            .id("1234567890AaBbCcDdEeFfG")
            .name("playlist name")
            .uri(URI.create("spotify:track:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistDtos(artistItems)
            .build();

    SpotifyPlaylistTrackDto playlistTrackDto =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDto)
            .addedBy(userProfileItem)
            .addedAt("2020-12-04T14:14:36Z")
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistTrackDto>> constraintViolations =
        validator.validate(playlistTrackDto);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
