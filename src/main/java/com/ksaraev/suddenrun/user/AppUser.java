package com.ksaraev.suddenrun.user;

import com.ksaraev.suddenrun.playlist.AppPlaylist;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AppUser {

  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  void addPlaylist(@NotNull AppPlaylist appPlaylist);

  void removePlaylist(@NotNull AppPlaylist appPlaylist);

  List<AppPlaylist> getPlaylists();

  void setPlaylists(@NotEmpty List<AppPlaylist> playlists);
}
