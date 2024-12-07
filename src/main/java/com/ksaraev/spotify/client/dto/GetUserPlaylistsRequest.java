package com.ksaraev.spotify.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record GetUserPlaylistsRequest(@Min(1) @Max(50) Integer limit, @Min(0) Integer offset) {}
