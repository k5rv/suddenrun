package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;

@Builder
public record SpotifyPlaylistTrackDto(
    @JsonProperty("track") @Valid @NotNull SpotifyTrackDto trackDto,
    @JsonProperty("added_at") @NotNull String addedAt,
    @JsonProperty("added_by") @Valid @NotNull SpotifyUserProfileDto addedBy,
    @JsonProperty("is_local") Boolean isLocal,
    @JsonProperty("primary_color") String primaryColor,
    @JsonProperty("video_thumbnail") Map<String, Object> videoThumbnail) {}
