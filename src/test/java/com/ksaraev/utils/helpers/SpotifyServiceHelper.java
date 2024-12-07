package com.ksaraev.utils.helpers;

import com.ksaraev.spotify.model.SpotifyItem;
import com.ksaraev.spotify.model.artist.SpotifyArtist;
import com.ksaraev.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrack;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SpotifyServiceHelper {

  public static SpotifyUserProfileItem getUserProfile() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.USER.getUri(id);
    String email = SpotifyResourceHelper.getRandomEmail();
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).email(email).build();
  }

  public static SpotifyUserProfileItem getUserProfile(String id, String name) {
    URI uri = SpotifyResourceHelper.USER.getUri(id);
    String email = SpotifyResourceHelper.getRandomEmail();
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).email(email).build();
  }

  public static SpotifyArtistItem getArtist() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.ARTIST.getUri(id);
    List<String> genres = SpotifyResourceHelper.getRandomGenres();
    return SpotifyArtist.builder().id(id).name(name).uri(uri).genres(genres).build();
  }

  public static SpotifyTrackItem getTrack() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.TRACK.getUri(id);
    Integer popularity = SpotifyResourceHelper.getRandomPopularity();
    List<SpotifyArtistItem> artists = getArtists(1);
    return SpotifyTrack.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .popularity(popularity)
        .artists(artists)
        .build();
  }

  public static SpotifyTrackItem getTrackWithPopularity(Integer popularity) {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.TRACK.getUri(id);
    List<SpotifyArtistItem> artists = getArtists(1);
    return SpotifyTrack.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .popularity(popularity)
        .artists(artists)
        .build();
  }

  public static SpotifyPlaylistItemDetails getPlaylistDetails() {
    String name = SpotifyResourceHelper.getRandomName();
    String description = SpotifyResourceHelper.getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylistDetails.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyPlaylistItem getPlaylist(String id) {
    String name = SpotifyResourceHelper.getRandomName();
    String description = SpotifyResourceHelper.getRandomDescription();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    SpotifyUserProfileItem user = getUserProfile();
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylist.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .user(user)
        .snapshotId(snapshotId)
        .build();
  }

  public static SpotifyPlaylistItem getPlaylist() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    String description = SpotifyResourceHelper.getRandomDescription();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    SpotifyUserProfileItem user = getUserProfile();
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .description(description)
            .isPublic(isPublic)
            .isCollaborative(isCollaborative)
            .user(user)
            .snapshotId(snapshotId)
            .build();
  }

  public static SpotifyPlaylistItem getPlaylist(SpotifyUserProfileItem user, SpotifyPlaylistItemDetails playlistDetails) {
    String id = SpotifyResourceHelper.getRandomId();
    String name = playlistDetails.getName();
    String description = playlistDetails.getDescription();
    Boolean isPublic = playlistDetails.getIsPublic();
    Boolean isCollaborative = playlistDetails.getIsCollaborative();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SpotifyPlaylist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .description(description)
            .isPublic(isPublic)
            .isCollaborative(isCollaborative)
            .user(user)
            .snapshotId(snapshotId)
            .build();
  }

  public static SpotifyPlaylistItem getPlaylist(SpotifyUserProfileItem user) {
    String id = SpotifyResourceHelper.getRandomId();
    SpotifyPlaylistItemDetails playlistDetails = getPlaylistDetails();
    String name = playlistDetails.getName();
    String description = playlistDetails.getDescription();
    Boolean isPublic = playlistDetails.getIsPublic();
    Boolean isCollaborative = playlistDetails.getIsCollaborative();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return SpotifyPlaylist.builder()
            .id(id)
            .name(name)
            .uri(uri)
            .description(description)
            .isPublic(isPublic)
            .isCollaborative(isCollaborative)
            .user(user)
            .snapshotId(snapshotId)
            .build();
  }

  public static SpotifyTrackItemFeatures getSpotifyTrackFeatures() {
    BigDecimal minTempo = new BigDecimal(120);
    BigDecimal maxTempo = new BigDecimal(140);
    BigDecimal minEnergy = new BigDecimal("0.65");
    return SpotifyTrackFeatures.builder()
        .minTempo(minTempo)
        .maxTempo(maxTempo)
        .minEnergy(minEnergy)
        .build();
  }

  public static List<SpotifyPlaylistItem> getPlaylists(Integer size) {
    return getSpotifyItems(size, SpotifyPlaylistItem.class);
  }

  public static List<SpotifyArtistItem> getArtists(Integer size) {
    return getSpotifyItems(size, SpotifyArtistItem.class);
  }

  public static List<SpotifyTrackItem> getTracks(Integer size) {
    return getSpotifyItems(size, SpotifyTrackItem.class);
  }

  private static <T extends SpotifyItem> T getSpotifyItem(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileItem.class)) return type.cast(getUserProfile());
    if (type.isAssignableFrom(SpotifyTrackItem.class)) return type.cast(getTrack());
    if (type.isAssignableFrom(SpotifyArtistItem.class)) return type.cast(getArtist());
    if (type.isAssignableFrom(SpotifyPlaylistItemDetails.class))
      return type.cast(getPlaylistDetails());
    if (type.isAssignableFrom(SpotifyPlaylistItem.class)) return type.cast(getPlaylist());
    throw new UnsupportedOperationException("not supported type:" + type);
  }

  private static <T extends SpotifyItem> List<T> getSpotifyItems(Integer size, Class<T> type) {
    List<T> items = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> items.add(index, getSpotifyItem(type)));
    return items;
  }
}
