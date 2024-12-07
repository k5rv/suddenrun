package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record SpotifyPlaylistDetailsDto(
    @JsonProperty("collaborative") Boolean isCollaborative,
    @JsonProperty("public") Boolean isPublic,
    @NotEmpty String name,
    String description) {}
