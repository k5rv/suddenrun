package com.ksaraev.spotify.service;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import java.util.List;

public interface SpotifyUserTopTrackItemsService {

  List<SpotifyTrackItem> getUserTopTracks();
}
