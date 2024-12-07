package com.ksaraev.spotify.model.playlist;

import com.ksaraev.spotify.model.SpotifyItem;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import java.util.List;

public interface SpotifyPlaylistItem extends SpotifyItem, SpotifyPlaylistItemDetails {
  String getSnapshotId();

  void setSnapshotId(String snapshotId);

  SpotifyUserProfileItem getUser();

  void setUser(SpotifyUserProfileItem user);

  List<SpotifyTrackItem> getTracks();

  void setTracks(List<SpotifyTrackItem> tracks);
}
