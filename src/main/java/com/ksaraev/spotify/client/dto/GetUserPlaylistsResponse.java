package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder
public record GetUserPlaylistsResponse(
    URL href,
    @JsonProperty("items") @Valid List<SpotifyPlaylistDto> playlistDtos,
    Integer limit,
    Integer offset,
    Integer total,
    String next,
    String previous) {}
