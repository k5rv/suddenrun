package com.ksaraev.spotify.model;

import java.net.URI;

public interface SpotifyItem {
  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  URI getUri();

  void setUri(URI uri);
}
