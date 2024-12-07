package com.ksaraev.spotify.client.dto.items;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.client.dto.SpotifyPlaylistMusicDto;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import jakarta.validation.*;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistMusicDtoTest {

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

    SpotifyUserProfileDto userProfileItem =
        isAddedByNull ? null : SpotifyClientHelper.getUserProfileDto();

    SpotifyTrackDto trackDto = isTrackItemNull ? null : SpotifyClientHelper.getTrackDto();

    String addedAt = isAddedAtNull ? null : "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDto)
            .addedBy(userProfileItem)
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
  void itShouldDetectSpotifyPlaylistItemMusicTestCascadeConstraintViolations() {
    // Given
    String message = "playlistTrackDtos[0].trackDto: must not be null";

    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();

    SpotifyPlaylistTrackDto playlistItemTrack =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(null)
            .addedBy(userProfileDto)
            .addedAt("2020-12-04T14:14:36Z")
            .build();

    List<SpotifyPlaylistTrackDto> playlistItemTracks = List.of(playlistItemTrack);

    SpotifyPlaylistMusicDto playlistMusicDto =
        SpotifyPlaylistMusicDto.builder().playlistTrackDtos(playlistItemTracks).build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistMusicDto>> constraintViolations =
        validator.validate(playlistMusicDto);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }
}
