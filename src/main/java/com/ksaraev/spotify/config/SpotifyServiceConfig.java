package com.ksaraev.spotify.config;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylistConfig;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackFeatures;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class SpotifyServiceConfig {

  @Value("${spotify.service.playlist.details.name}")
  @NotEmpty
  private String name;

  @Value("${spotify.service.playlist.details.description}")
  @NotEmpty
  private String description;

  @Value("${spotify.service.playlist.details.public}")
  @NotEmpty
  private Boolean isPublic;

  @Value("${spotify.service.playlist.details.size}")
  @Min(1)
  @Max(50)
  private Integer size;

  @Value("${spotify.service.music-features.min-tempo}")
  @Min(1)
  @Max(220)
  private BigDecimal minTempo;

  @Value("${spotify.service.music-features.max-tempo}")
  @Min(1)
  @Max(220)
  private BigDecimal maxTempo;

  @Value("${spotify.service.music-features.min-energy}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal minEnergy;

  @Value("${spotify.service.music-features.max-energy}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal maxEnergy;

  @Value("${spotify.service.music-features.min-danceability}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal minDanceability;

  @Value("${spotify.service.music-features.max-danceability}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal maxDanceability;

  @Value("${spotify.service.music-features.min-valence}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal minValence;

  @Value("${spotify.service.music-features.max-valence}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private BigDecimal maxValence;

  @Value("${spotify.service.music-features.min-popularity}")
  @DecimalMin("0.1")
  @DecimalMax("1.0")
  private Integer minPopularity;

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
    return jsonConverter;
  }

  @Bean
  SpotifyPlaylistItemConfig getSpotifyPlaylistConfig() {
    SpotifyPlaylistItemDetails playlistDetails =
        SpotifyPlaylistDetails.builder()
            .name(this.name)
            .description(this.description)
            .isPublic(this.isPublic)
            .build();

    SpotifyTrackItemFeatures features =
        SpotifyTrackFeatures.builder()
            .minTempo(this.minTempo)
            .maxTempo(this.maxTempo)
            .minEnergy(this.minEnergy)
            .maxEnergy(this.maxEnergy)
            .minDanceability(this.minDanceability)
            .maxDanceability(this.maxDanceability)
            .minValence(this.minValence)
            .maxValence(this.maxValence)
            .minPopularity(this.minPopularity)
            .build();

    return SpotifyPlaylistConfig.builder()
        .details(playlistDetails)
        .musicFeatures(features)
        .size(size)
        .build();
  }
}
