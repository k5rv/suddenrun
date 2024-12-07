package com.ksaraev.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserTopTracksRequestConfig implements GetSpotifyUserTopTrackRequestConfig {
  private Integer limit;

  private Integer offset;

  private String timeRange;
}
