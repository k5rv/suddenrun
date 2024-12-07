package com.ksaraev.spotify.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRecommendationsRequestConfig
    implements GetSpotifyRecommendationRequestConfig {

  private Integer limit;
}
