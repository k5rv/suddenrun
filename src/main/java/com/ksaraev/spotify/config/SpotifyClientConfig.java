package com.ksaraev.spotify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyClientConfig {

  @Value("${spotify.client.requests.get-user-top-tracks.limit}")
  private Integer getUserTopTracksRequestLimit;

  @Value("${spotify.client.requests.get-user-top-tracks.offset}")
  private Integer getUserTopTracksRequestOffset;

  @Value("${spotify.client.requests.get-user-top-tracks.time-range}")
  private String getUserTopTracksRequestTimeRange;

  @Value("${spotify.client.requests.get-recommendations.limit}")
  private Integer getRecommendationsRequestLimit;

  @Value("${spotify.client.requests.update-playlist-items-request.position}")
  private Integer updatePlaylistItemsRequestPosition;

  @Bean
  GetSpotifyUserTopTrackRequestConfig getUserTopTracksRequestConfig() {
    return GetUserTopTracksRequestConfig.builder()
        .timeRange(this.getUserTopTracksRequestTimeRange)
        .limit(this.getUserTopTracksRequestLimit)
        .offset(this.getUserTopTracksRequestOffset)
        .build();
  }

  @Bean
  GetSpotifyRecommendationRequestConfig getRecommendationsRequestConfig() {
    return GetRecommendationsRequestConfig.builder()
        .limit(this.getRecommendationsRequestLimit)
        .build();
  }

  @Bean
  UpdateSpotifyPlaylistItemsRequestConfig getUpdatePlaylistRequestConfig() {
    return UpdatePlaylistItemsRequestConfig.builder()
        .position(this.updatePlaylistItemsRequestPosition)
        .build();
  }
}
