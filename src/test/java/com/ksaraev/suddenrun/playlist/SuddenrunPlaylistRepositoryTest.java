package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest()
class SuddenrunPlaylistRepositoryTest {

  @Autowired EntityManager entityManager;
  @Autowired private SuddenrunPlaylistRepository underTest;

  @Test
  void itShouldSavePlaylist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    user.setPlaylists(List.of(playlist));
    entityManager.persist(user);

    // When
    SuddenrunPlaylist result = underTest.save(playlist);

    // Then
    assertThat(result).usingRecursiveComparison().isEqualTo(playlist);
  }

  @Test
  void itShouldFindPlaylistById() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = playlist.getId();
    user.setPlaylists(List.of(playlist));
    entityManager.persist(user);
    underTest.save(playlist);

    // When
    Optional<SuddenrunPlaylist> optionalPlaylist = underTest.findById(playlistId);

    // Then
    assertThat(optionalPlaylist)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(playlist));
  }

  @Test
  void itShouldDetermineEqualityOfInstances() {
    // Given
    String playlistId = "playlistIdA";

    SuddenrunUser userA0 = SuddenrunUser.builder().id("userIdA").name("userA").build();
    AppTrack trackA0 = SuddenrunTrack.builder().id("trackIdA").name("trackA").build();
    AppTrack trackB0 = SuddenrunTrack.builder().id("trackIdB").name("trackB").build();
    AppTrack trackC0 = SuddenrunTrack.builder().id("trackIdC").name("trackC").build();
    SuddenrunPlaylist playlistA0 =
        SuddenrunPlaylist.builder().id(playlistId).snapshotId("playlistIdASnapshot").build();
    playlistA0.setUser(userA0);
    playlistA0.setInclusions(List.of(trackC0));
    playlistA0.setExclusions(List.of());
    playlistA0.setTracks(List.of(trackA0, trackB0));
    userA0.setPlaylists(List.of(playlistA0));

    SuddenrunUser userA1 = SuddenrunUser.builder().id("userIdA").name("userA").build();
    AppTrack trackA1 = SuddenrunTrack.builder().id("trackIdA").name("trackA").build();
    AppTrack trackB1 = SuddenrunTrack.builder().id("trackIdB").name("trackB").build();
    AppTrack trackC1 = SuddenrunTrack.builder().id("trackIdC").name("trackC").build();
    SuddenrunPlaylist playlistA1 =
        SuddenrunPlaylist.builder().id(playlistId).snapshotId("playlistIdASnapshot").build();
    playlistA1.setUser(userA1);
    playlistA1.setInclusions(List.of(trackC1));
    playlistA1.setExclusions(List.of());
    playlistA0.setTracks(List.of(trackA1, trackB1));
    userA1.setPlaylists(List.of(playlistA1));

    entityManager.persist(userA0);
    underTest.save(playlistA0);

    // When
    Optional<SuddenrunPlaylist> optionalPlaylist = underTest.findById(playlistId);

    // Then
    assertThat(optionalPlaylist)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualTo(playlistA1));
  }

  @Test
  void itShouldReturnTrueIfPlaylistExists() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = playlist.getId();
    user.setPlaylists(List.of(playlist));
    entityManager.persist(user);
    underTest.save(playlist);

    // When
    boolean isExists = underTest.existsById(playlistId);

    // Then
    assertThat(isExists).isTrue();
  }

  @Test
  void itShouldReturnFalseIfPlaylistDoesNotExist() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = playlist.getId();

    // When
    boolean isExists = underTest.existsById(playlistId);

    // Then
    assertThat(isExists).isFalse();
  }

  @Test
  void itShouldFindPlaylistByOwnerId() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    String userId = user.getId();
    entityManager.persist(user);
    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(user);
    underTest.save(playlist);

    // When
    Optional<SuddenrunPlaylist> optionalPlaylist = underTest.findByUserId(userId);

    // Then
    assertThat(optionalPlaylist)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(playlist));
  }
}
