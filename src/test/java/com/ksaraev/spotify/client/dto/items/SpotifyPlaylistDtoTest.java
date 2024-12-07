package com.ksaraev.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import jakarta.validation.*;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistDtoTest {

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
            null                  |playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |id: must not be null
            0moWPCTPTShumonjlsDgLe|null         |spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |name: must not be empty
            0moWPCTPTShumonjlsDgLe|playlist name|null                                   |MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|TRUE |uri: must not be null
            0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|null                                                    |TRUE |snapshotId: must not be null
            0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|FALSE|userProfileDto: must not be null
            """)
  void itShouldDetectSpotifyPlaylistItemConstraintViolations(
      String id, String name, URI uri, String snapshotId, Boolean hasOwner, String message) {
    // Given
    SpotifyUserProfileDto userProfileDto = null;

    if (hasOwner) {
      userProfileDto =
          SpotifyUserProfileDto.builder()
              .id("12122604372")
              .displayName("name")
              .uri(URI.create("spotify:user:12122604372"))
              .email("email@mail.com")
              .build();
    }

    SpotifyPlaylistDto playlistItem =
        SpotifyPlaylistDto.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .snapshotId(snapshotId)
            .userProfileDto(userProfileDto)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistDto>> constraintViolations =
        validator.validate(playlistItem);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistItemCascadeConstraintViolations() {
    // Given
    SpotifyUserProfileDto userProfileDto =
        SpotifyUserProfileDto.builder()
            .id(null)
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyPlaylistDto playlistItem =
        SpotifyPlaylistDto.builder()
            .id("0moWPCTPTShumonjlsDgLe")
            .name("playlist name")
            .uri(URI.create("spotify:playlist:0moWPCTPTShumonjlsDgLe"))
            .snapshotId("MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1")
            .userProfileDto(userProfileDto)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistDto>> constraintViolations =
        validator.validate(playlistItem);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("userProfileDto.id: must not be null");
  }
}
