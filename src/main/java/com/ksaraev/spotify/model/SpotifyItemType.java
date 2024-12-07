package com.ksaraev.spotify.model;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpotifyItemType {
    ALBUM("album"),
    ARTIST("artist"),
    PLAYLIST("playlist"),
    TRACK("track"),
    USER("user");

  private final String itemType;

  public URI createUri(String id) {
    return URI.create("spotify:" + itemType + ":" + id);
  }

  @Override
  public String toString() {
    return this.itemType;
  }
}
