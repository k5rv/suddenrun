package com.ksaraev.utils.helpers;

import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SuddenrunHelper {

  public static SuddenrunUser getUser() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    List<SuddenrunPlaylist> playlists = new ArrayList<>();
    return SuddenrunUser.builder().id(id).name(name).playlists(playlists).build();
  }

  public static SuddenrunTrack getTrack() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    return SuddenrunTrack.builder().id(id).name(name).build();
  }

  public static SuddenrunTrack getTrack(String id) {
    String name = SpotifyResourceHelper.getRandomName();
    return SuddenrunTrack.builder().id(id).name(name).build();
  }

  public static List<AppTrack> getTracks(Integer size) {
    List<AppTrack> appTracks = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> appTracks.add(index, getTrack()));
    return appTracks;
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist() {
    String id = SpotifyResourceHelper.getRandomId();
    SuddenrunUser user = getUser();
    List<AppTrack> customTracks = new ArrayList<>(getTracks(3));
    List<AppTrack> rejectedTracks = new ArrayList<>(getTracks(2));
    List<AppTrack> tracks = new ArrayList<>(getTracks(10));
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
        .id(id)
        .user(user)
        .inclusions(customTracks)
        .exclusions(rejectedTracks)
        .tracks(tracks)
        .snapshotId(snapshotId)
        .build();
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist(String id) {
    SuddenrunUser user = getUser();
    List<AppTrack> customTracks = new ArrayList<>(getTracks(3));
    List<AppTrack> rejectedTracks = new ArrayList<>(getTracks(2));
    List<AppTrack> tracks = new ArrayList<>(getTracks(10));
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
        .id(id)
        .user(user)
        .inclusions(customTracks)
        .exclusions(rejectedTracks)
        .tracks(tracks)
        .snapshotId(snapshotId)
        .build();
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist(String id, SuddenrunUser user) {
    List<AppTrack> customTracks = new ArrayList<>(getTracks(3));
    List<AppTrack> rejectedTracks = new ArrayList<>(getTracks(2));
    List<AppTrack> tracks = new ArrayList<>(getTracks(10));
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
        .id(id)
        .user(user)
        .inclusions(customTracks)
        .exclusions(rejectedTracks)
        .tracks(tracks)
        .snapshotId(snapshotId)
        .build();
  }

  public static SuddenrunPlaylist getSuddenrunPlaylist(SuddenrunUser user) {
    String id = SpotifyResourceHelper.getRandomId();
    List<AppTrack> customTracks = new ArrayList<>(getTracks(3));
    List<AppTrack> rejectedTracks = new ArrayList<>(getTracks(2));
    List<AppTrack> tracks = new ArrayList<>(getTracks(10));
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SuddenrunPlaylist.builder()
        .id(id)
        .user(user)
        .inclusions(customTracks)
        .exclusions(rejectedTracks)
        .tracks(tracks)
        .snapshotId(snapshotId)
        .build();
  }
}
