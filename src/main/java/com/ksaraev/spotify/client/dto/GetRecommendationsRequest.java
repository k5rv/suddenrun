package com.ksaraev.spotify.client.dto;

import feign.Param;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record GetRecommendationsRequest(
    @Param("seed_artists") List<String> seedArtistIds,
    @Param("seed_tracks") List<String> seedTrackIds,
    @Param("seed_genres") List<String> seedGenres,
    TrackFeatures trackFeatures,
    Integer limit) {

  @Builder
  public record TrackFeatures(
      Integer maxPopularity,
      Integer minPopularity,
      Integer popularity,
      Integer maxKey,
      Integer minKey,
      Integer key,
      Integer maxMode,
      Integer minMode,
      Integer mode,
      Integer maxDurationMs,
      Integer minDurationMs,
      Integer durationMs,
      Integer maxTimeSignature,
      Integer minTimeSignature,
      Integer timeSignature,
      BigDecimal minTempo,
      BigDecimal maxTempo,
      BigDecimal tempo,
      BigDecimal minEnergy,
      BigDecimal maxEnergy,
      BigDecimal energy,
      BigDecimal minAcousticness,
      BigDecimal maxAcousticness,
      BigDecimal acousticness,
      BigDecimal minDanceability,
      BigDecimal maxDanceability,
      BigDecimal danceability,
      BigDecimal maxInstrumentalness,
      BigDecimal minInstrumentalness,
      BigDecimal instrumentalness,
      BigDecimal maxLiveness,
      BigDecimal minLiveness,
      BigDecimal liveness,
      BigDecimal minLoudness,
      BigDecimal maxLoudness,
      BigDecimal loudness,
      BigDecimal maxSpeechiness,
      BigDecimal minSpeechiness,
      BigDecimal speechiness,
      BigDecimal maxValence,
      BigDecimal minValence,
      BigDecimal valence) {}
}
