package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import lombok.Builder;

@Builder
public record RemovePlaylistItemsRequest(
    @JsonProperty("uris") @Size(min = 1, max = 100) @NotEmpty List<@NotNull URI> uris,
    @NotNull String snapshotId) {}
