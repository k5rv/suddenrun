package com.ksaraev.suddenrun.playlist;

import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.user.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface AppPlaylistService {

  AppPlaylist createPlaylist(@Valid @NotNull AppUser appUser);

  Optional<AppPlaylist> getPlaylist(@Valid @NotNull AppUser appUser);

  Optional<AppPlaylist> getPlaylist(@NotNull String playlistId);

  AppPlaylist addTracks(
      @Valid @NotNull AppPlaylist appPlaylist, @NotEmpty List<AppTrack> appTracks);
}
