package com.ksaraev.spotify.model.playlistdetails;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotifyPlaylistDetails implements SpotifyPlaylistItemDetails {

  @NotEmpty private String name;

  private Boolean isPublic;

  private String description;

  private Boolean isCollaborative;
}
