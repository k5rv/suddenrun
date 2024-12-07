package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record SpotifyTrackDto(
    @JsonProperty("album") SpotifyAlbumDto albumDto,
    @JsonProperty("preview_url") URL previewUrl,
    @JsonProperty("is_local") Boolean isLocal,
    @JsonProperty("is_playable") Boolean isPlayable,
    @JsonProperty("duration_ms") Integer durationMs,
    @JsonProperty("track_number") Integer trackNumber,
    @JsonProperty("disc_number") Integer discNumber,
    @JsonProperty("linked_from") SpotifyAlbumDto sourceAlbumDto,
    @JsonProperty("artists") @Valid @NotEmpty List<SpotifyArtistDto> artistDtos,
    @JsonProperty("available_markets") List<String> availableMarkets,
    @JsonProperty("external_ids") Map<String, Object> externalIds,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    @NotNull String id,
    @NotEmpty String name,
    @Min(0) @Max(100) Integer popularity,
    String type,
    @NotNull URI uri,
    URL href,
    Boolean explicit,
    Boolean episode,
    Boolean track) {}
