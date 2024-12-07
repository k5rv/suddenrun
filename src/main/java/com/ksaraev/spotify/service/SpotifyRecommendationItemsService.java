package com.ksaraev.spotify.service;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface SpotifyRecommendationItemsService {

  List<SpotifyTrackItem> getRecommendations(
      @NotNull @Size(min = 1, max = 5) @Valid List<@NotNull SpotifyTrackItem> seedTrackItems,
      @NotNull SpotifyTrackItemFeatures trackItemFeatures);
}
