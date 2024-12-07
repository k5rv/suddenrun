package com.ksaraev.spotify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record SpotifyUserProfileDto(
    @JsonProperty("display_name") @NotEmpty String displayName,
    @JsonProperty("explicit_content") Map<String, Object> explicitContent,
    @JsonProperty("followers") Map<String, Object> followers,
    @JsonProperty("external_urls") Map<String, Object> externalUrls,
    @NotNull String id,
    @Email String email,
    String country,
    String product,
    String type,
    @NotNull URI uri,
    URL href,
    List<Map<String, Object>> images) {}
