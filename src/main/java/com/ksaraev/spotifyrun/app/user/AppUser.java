package com.ksaraev.spotifyrun.app.user;


import com.ksaraev.spotifyrun.app.playlist.AppPlaylist;import com.ksaraev.spotifyrun.app.playlist.Playlist;
import java.util.List;

public interface AppUser {

  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  void addPlaylist(AppPlaylist appPlaylist);
  void removePlaylist(AppPlaylist appPlaylist);
  List<AppPlaylist> getPlaylists();

  void setPlaylists(List<AppPlaylist> playlists);
}