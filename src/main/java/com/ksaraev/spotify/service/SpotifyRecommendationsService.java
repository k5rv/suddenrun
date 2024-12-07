package com.ksaraev.spotify.service;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotify.client.dto.GetRecommendationsResponse;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.GetSpotifyRecommendationRequestConfig;
import com.ksaraev.spotify.exception.GetSpotifyRecommendationsException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackFeaturesMapper;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SpotifyRecommendationsService implements SpotifyRecommendationItemsService {

  private final SpotifyClient client;

  private final GetSpotifyRecommendationRequestConfig requestConfig;

  private final SpotifyTrackMapper trackMapper;

  private final SpotifyTrackFeaturesMapper featuresMapper;

  @Override
  public List<SpotifyTrackItem> getRecommendations(
      @NotNull List<SpotifyTrackItem> seedTrackItems, @NotNull SpotifyTrackItemFeatures trackItemFeatures) {
    try {
      List<String> seedTrackIds = seedTrackItems.stream().map(SpotifyTrackItem::getId).toList();

      GetRecommendationsRequest request =
          GetRecommendationsRequest.builder()
              .seedTrackIds(seedTrackIds)
              .trackFeatures(featuresMapper.mapToRequestFeatures(trackItemFeatures))
              .limit(requestConfig.getLimit())
              .build();

      GetRecommendationsResponse response = client.getRecommendations(request);

      List<SpotifyTrackDto> trackDtos =
          response.trackDtos().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return trackDtos.isEmpty() ? List.of() : trackMapper.mapDtosToModels(trackDtos);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyRecommendationsException(e);
    }
  }
}
