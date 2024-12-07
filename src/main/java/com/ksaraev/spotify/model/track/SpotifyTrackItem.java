package com.ksaraev.spotify.model.track;

import com.ksaraev.spotify.model.SpotifyItem;
import com.ksaraev.spotify.model.artist.SpotifyArtistItem;
import java.util.List;

public interface SpotifyTrackItem extends SpotifyItem {

  Integer getPopularity();

  void setPopularity(Integer popularity);

  List<SpotifyArtistItem> getArtists();

  void setArtists(List<SpotifyArtistItem> artists);
}
