package com.ksaraev.suddenrun.playlist;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class SuddenrunPlaylistSynchronizationServiceTest {

  private static final String FIND_TRACKS_NONE_MATCH = "findTracksNoneMatch";

  private static final String FIND_TRACKS_MATCH = "findTracksMatch";

  private static final String UPDATE_INCLUSIONS = "updateInclusions";

  private static final String UPDATE_EXCLUSIONS = "updateExclusions";

  private AutoCloseable closeable;

  private AppPlaylistSynchronizationService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SuddenrunPlaylistSynchronizationService();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldUpdateFromSource() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist(user);
    String playlistId = targetPlaylist.getId();
    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId, user);
    List<AppTrack> tracks = sourcePlaylist.getTracks();
    String snapshotId = sourcePlaylist.getSnapshotId();

    // When
    AppPlaylist result = underTest.updateFromSource(targetPlaylist, sourcePlaylist);

    // Then
    assertThat(result.getId()).isEqualTo(playlistId);
    assertThat(result.getSnapshotId()).isEqualTo(snapshotId);
    assertThat(result.getUser()).usingRecursiveComparison().isEqualTo(user);
    assertThat(result.getTracks()).isEqualTo(tracks);
  }

  @Test
  void itShouldUpdateFromSourceDDD() {
    // Given
    SuddenrunUser user = SuddenrunHelper.getUser();
    AppPlaylist targetPlaylist = SuddenrunHelper.getSuddenrunPlaylist(user);
    targetPlaylist.setInclusions(List.of());
    targetPlaylist.setExclusions(List.of());
    List<AppTrack> tracksA = SuddenrunHelper.getTracks(10);
    List<AppTrack> tracksB = SuddenrunHelper.getTracks(10);
    List<AppTrack> tracksC = SuddenrunHelper.getTracks(10);
    List<AppTrack> tracksD = SuddenrunHelper.getTracks(10);
    List<AppTrack> targetTracks = Stream.of(tracksA, tracksB, tracksC).flatMap(Collection::stream).toList();
    targetPlaylist.setTracks(targetTracks);

    String playlistId = targetPlaylist.getId();
    AppPlaylist sourcePlaylist = SuddenrunHelper.getSuddenrunPlaylist(playlistId, user);
    sourcePlaylist.setInclusions(List.of());
    sourcePlaylist.setExclusions(List.of());
    List<AppTrack> sourceTracks = Stream.of(tracksA, tracksB, tracksD).flatMap(Collection::stream).toList();
    sourcePlaylist.setTracks(sourceTracks);
    List<AppTrack> tracks = sourcePlaylist.getTracks();
    String snapshotId = sourcePlaylist.getSnapshotId();

    // When
    AppPlaylist result = underTest.updateFromSource(targetPlaylist, sourcePlaylist);

    // Then
//    assertThat(result.getId()).isEqualTo(id);
//    assertThat(result.getSnapshotId()).isEqualTo(snapshotId);
//    assertThat(result.getUser()).usingRecursiveComparison().isEqualTo(user);
//    assertThat(result.getTracks()).isEqualTo(tracks);
  }

  @Test
  void findTracksNoneMatchPlaylistShouldReturnAllTracksIfBothPlaylistAndExclusionsAreEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setExclusions(List.of());
    List<AppTrack> appTracks = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB, trackA);
  }

  @Test
  void
      findTracksNoneMatchPlaylistShouldReturnTracksThatNotPresentInPlaylistAndExclusionsAreEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB));
    appPlaylist.setExclusions(List.of());
    List<AppTrack> appTracks = List.of(trackA, trackC);

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void findTracksNoneMatchPlaylistShouldReturnTracksThatNotPresentInPlaylistAndExclusions() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    AppTrack trackE = SuddenrunHelper.getTrack("E");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB, trackC));
    appPlaylist.setExclusions(List.of(trackD));
    List<AppTrack> appTracks = List.of(trackD, trackE);

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackE);
  }

  @Test
  void
      findTracksNoneMatchPlaylistShouldReturnTracksThatNotPresentInPlaylistAndExclusionsIfPlaylistIsEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setExclusions(List.of(trackA));
    List<AppTrack> appTracks = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }

  @Test
  void
      findTracksNoneMatchPlaylistShouldReturnEmptyResultIfTracksAreEmptyAndPlaylistHasBothTracksAndExclusions() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB));
    appPlaylist.setExclusions(List.of(trackC));
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void
      findTracksNoneMatchPlaylistShouldReturnEmptyResultIfBothTracksAndPlaylistAreEmptyButExclusionsPresent() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setExclusions(List.of(trackA));
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void
      findTracksNoneMatchPlaylistShouldReturnEmptyResultIfTracksAndPlaylistAndExclusionsAreEmpty() {
    // Given
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setExclusions(List.of());
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findTracksNoneMatchPlaylist(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findPlaylistNoneMatchTracksShouldReturnTracksThatNotPresentInPlaylist() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB, trackC));
    appPlaylist.setInclusions(List.of());
    List<AppTrack> appTracks = List.of(trackA, trackB, trackD);

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void findPlaylistNoneMatchTracksShouldReturnTracksThatNotPresentInPlaylistAndInclusions() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    AppTrack trackE = SuddenrunHelper.getTrack("E");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB, trackC, trackD));
    appPlaylist.setInclusions(List.of(trackD));
    List<AppTrack> appTracks = List.of(trackA, trackB, trackD, trackE);

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  void findPlaylistNoneMatchTracksShouldReturnEmptyResultIfPlaylistTracksIncludedInTracks() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB));
    appPlaylist.setInclusions(List.of());
    List<AppTrack> appTracks = List.of(trackA, trackB, trackC);

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findPlaylistNoneMatchTracksShouldReturnEmptyResultIfPlaylistAndInclusionsAreEmpty() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setInclusions(List.of());
    List<AppTrack> appTracks = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findPlaylistNoneMatchTracksShouldReturnEmptyResultIfPlaylistEmptyAndInclusionsArePresent() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setInclusions(List.of(trackB));
    List<AppTrack> appTracks = List.of(trackA, trackB);

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void
      findPlaylistNoneMatchTracksShouldReturnEmptyResultIfTracksAndInclusionsAreEmptyButPlaylistTracksArePresent() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB));
    appPlaylist.setInclusions(List.of());
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB, trackA);
  }

  @Test
  void
      findPlaylistNoneMatchTracksShouldReturnEmptyResultIfTracksAreEmptyButPlaylistAndInclusionsArePresent() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of(trackA, trackB));
    appPlaylist.setInclusions(List.of(trackA));
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }

  @Test
  void
      findPlaylistNoneMatchTracksShouldReturnEmptyResultIfPlaylistAndTracksAndInclusionsAreEmpty() {
    // Given
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setInclusions(List.of());
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void
      findPlaylistNoneMatchTracksShouldReturnEmptyResultIfPlaylistAndTracksEmptyButInclusionsArePresent() {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppPlaylist appPlaylist = SuddenrunHelper.getSuddenrunPlaylist();
    appPlaylist.setTracks(List.of());
    appPlaylist.setInclusions(List.of(trackA));
    List<AppTrack> appTracks = List.of();

    // When
    List<AppTrack> result = underTest.findPlaylistNoneMatchTracks(appPlaylist, appTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateInclusionsShouldAddInclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    List<AppTrack> targetTracks = List.of(trackA, trackB);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    List<AppTrack> targetPreferences = List.of();
    Method updatePreferences =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
                UPDATE_INCLUSIONS, List.class, List.class, List.class);
    updatePreferences.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updatePreferences.invoke(underTest, sourceTracks, targetTracks, targetPreferences);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateInclusionsShouldDeleteInclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    List<AppTrack> targetTracks = List.of(trackA, trackB, trackC, trackD);
    List<AppTrack> targetPreferences = List.of(trackC, trackD);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    Method updatePreferences =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
                UPDATE_INCLUSIONS, List.class, List.class, List.class);
    updatePreferences.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updatePreferences.invoke(underTest, sourceTracks, targetTracks, targetPreferences);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateExclusionsShouldAddExclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    List<AppTrack> targetTracks = List.of(trackA, trackB, trackC);
    List<AppTrack> targetExclusions = List.of();
    List<AppTrack> sourceTracks = List.of(trackA, trackB);
    Method updateExclusions =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_EXCLUSIONS, List.class, List.class, List.class);
    updateExclusions.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updateExclusions.invoke(underTest, sourceTracks, targetTracks, targetExclusions);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackC);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateExclusionsShouldDeleteExclusions() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack("A");
    AppTrack trackB = SuddenrunHelper.getTrack("B");
    AppTrack trackC = SuddenrunHelper.getTrack("C");
    AppTrack trackD = SuddenrunHelper.getTrack("D");
    List<AppTrack> targetTracks = List.of(trackA, trackB);
    List<AppTrack> targetExclusions = List.of(trackC, trackD);
    List<AppTrack> sourceTracks = List.of(trackA, trackB, trackC);
    Method updateExclusions =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            UPDATE_EXCLUSIONS, List.class, List.class, List.class);
    updateExclusions.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            updateExclusions.invoke(underTest, sourceTracks, targetTracks, targetExclusions);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackD);
  }

  @Test
  @SuppressWarnings("unchecked")
  void matchShouldReturnSourceIfItEqualsToTarget() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackB, trackA);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  @SuppressWarnings("unchecked")
  void matchShouldReturnEmptyIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of();
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackB);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void matchShouldReturnEmptyIfTargetIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of();
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void matchShouldReturnTracksThatPresentBothInTargetAndSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackC);
    Method findTracksMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_MATCH, List.class, List.class);
    findTracksMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA);
  }

  @Test
  @SuppressWarnings("unchecked")
  void noneMatchShouldReturnEmptyIfSourceEqualsToTarget() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackB, trackA);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void noneMatchShouldReturnEmptyIfSourceIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of();
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackB);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void noneMatchShouldReturnSourceIfTargetIsEmpty() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of();
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackA, trackB);
  }

  @Test
  @SuppressWarnings("unchecked")
  void noneMatchShouldReturnTracksThatPresentOnlyInSource() throws Exception {
    // Given
    AppTrack trackA = SuddenrunHelper.getTrack();
    AppTrack trackB = SuddenrunHelper.getTrack();
    AppTrack trackC = SuddenrunHelper.getTrack();
    List<AppTrack> comparisonSourceTracks = List.of(trackA, trackB);
    List<AppTrack> comparisonTargetTracks = List.of(trackA, trackC);
    Method findTracksNoneMatch =
        SuddenrunPlaylistSynchronizationService.class.getDeclaredMethod(
            FIND_TRACKS_NONE_MATCH, List.class, List.class);
    findTracksNoneMatch.setAccessible(true);

    // When
    List<AppTrack> result =
        (List<AppTrack>)
            findTracksNoneMatch.invoke(underTest, comparisonSourceTracks, comparisonTargetTracks);

    // Then
    assertThat(result).containsExactlyInAnyOrder(trackB);
  }
}
