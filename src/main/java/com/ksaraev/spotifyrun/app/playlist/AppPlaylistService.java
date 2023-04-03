package com.ksaraev.spotifyrun.app.playlist;

import com.ksaraev.spotifyrun.app.track.AppTrack;import com.ksaraev.spotifyrun.app.user.AppUser;
import java.util.List;import java.util.Optional;

public interface AppPlaylistService {

  AppPlaylist createPlaylist(AppUser appUser);

  Optional<AppPlaylist> getPlaylist(AppUser appUser);

  AppPlaylist addTracks(AppPlaylist appPlaylist, List<AppTrack> appTracks);
}