package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record GetRecommendationsResponse(
    @JsonProperty("tracks") @Valid List<SpotifyTrackDto> trackDtos,
    List<Map<String, Object>> seeds) {}
