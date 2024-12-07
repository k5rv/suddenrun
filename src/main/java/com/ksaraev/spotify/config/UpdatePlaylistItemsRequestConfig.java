package com.ksaraev.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePlaylistItemsRequestConfig implements UpdateSpotifyPlaylistItemsRequestConfig {
  private Integer position;
}
