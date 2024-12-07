package com.ksaraev.spotify.model.trackfeatures;

import com.ksaraev.spotify.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyTrackFeaturesMapper extends SpotifyMapper {
  GetRecommendationsRequest.TrackFeatures mapToRequestFeatures(
      SpotifyTrackItemFeatures trackFeatures);
}
