package com.ksaraev.spotify.model.artist;

import com.ksaraev.spotify.model.SpotifyItem;
import java.util.List;

public interface SpotifyArtistItem extends SpotifyItem {

  List<String> getGenres();

  void setGenres(List<String> genres);
}
