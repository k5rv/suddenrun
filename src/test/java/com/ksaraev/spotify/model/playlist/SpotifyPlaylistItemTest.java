package com.ksaraev.spotify.model.playlist;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.*;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyPlaylistItemTest {

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
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|null                                                    |TRUE |snapshotId: must not be null
           0moWPCTPTShumonjlsDgLe|playlist name|spotify:playlist:0moWPCTPTShumonjlsDgLe|MixjZThiNmNkZTcwNDc2MjQ0ZDYxOTQxNGM3MGNmNThhZmE1N2YyMTE1|FALSE|user: must not be null
           """)
  void itShouldDetectSpotifyPlaylistConstraintViolations(
      String id, String name, URI uri, String snapshotId, Boolean hasOwner, String message) {
    // Given
    SpotifyUserProfileItem user = null;

    if (hasOwner) user = SpotifyServiceHelper.getUserProfile();

    SpotifyPlaylistItem playlist =
        SpotifyPlaylist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .snapshotId(snapshotId)
            .user(user)
            .build();

    // When
    Set<ConstraintViolation<SpotifyPlaylistItem>> constraintViolations =
        validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations)).hasMessage(message);
  }

  @Test
  void itShouldDetectSpotifyPlaylistCascadeConstraintViolations() {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    user.setId(null);

    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    playlist.setUser(user);

    // When
    Set<ConstraintViolation<SpotifyPlaylistItem>> constraintViolations =
        validator.validate(playlist);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("user.id: must not be null");
  }
}
