package com.ksaraev.spotify.model.playlist;

import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;

public interface SpotifyPlaylistItemConfig {

  SpotifyPlaylistItemDetails getDetails();

  SpotifyTrackItemFeatures getMusicFeatures();

  Integer getSize();
}
