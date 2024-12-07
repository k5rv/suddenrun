package com.ksaraev.spotify.model.playlistdetails;

public interface SpotifyPlaylistItemDetails {
  String getName();

  void setName(String name);

  String getDescription();

  void setDescription(String description);

  Boolean getIsPublic();

  void setIsPublic(Boolean isPublic);

  Boolean getIsCollaborative();

  void setIsCollaborative(Boolean isCollaborative);
}
