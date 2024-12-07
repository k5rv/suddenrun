package com.ksaraev.spotify.service;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyPlaylistItemService {

  List<SpotifyPlaylistItem> getUserPlaylists(
      @Valid @NotNull SpotifyUserProfileItem userProfileItem);

  SpotifyPlaylistItem getPlaylist(@NotNull String playlistId);

  SpotifyPlaylistItem createPlaylist(
      @Valid @NotNull SpotifyUserProfileItem userProfileItem,
      @Valid @NotNull SpotifyPlaylistItemDetails playlistItemDetails);

  String addTracks(
      @Valid @NotNull SpotifyPlaylistItem spotifyPlaylistItem,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems);

  String removeTracks(
      @Valid @NotNull SpotifyPlaylistItem spotifyPlaylistItem,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems);
}
