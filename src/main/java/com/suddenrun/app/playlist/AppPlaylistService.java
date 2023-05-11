package com.suddenrun.app.playlist;

import com.suddenrun.app.track.AppTrack;
import com.suddenrun.app.user.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface AppPlaylistService {

  AppPlaylist createPlaylist(@Valid @NotNull AppUser appUser);

  Optional<AppPlaylist> getPlaylist(@Valid @NotNull AppUser appUser);

  AppPlaylist addTracks(
      @Valid @NotNull AppPlaylist appPlaylist, @NotEmpty List<AppTrack> appTracks);
}