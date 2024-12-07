package com.ksaraev.spotify.model.artist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotifyArtist implements SpotifyArtistItem {
  @NotNull private String id;
  @NotEmpty private String name;
  @NotNull private URI uri;
  private List<String> genres;
}
