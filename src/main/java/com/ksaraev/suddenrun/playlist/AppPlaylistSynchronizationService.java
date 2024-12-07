package com.ksaraev.suddenrun.playlist;

import com.ksaraev.suddenrun.track.AppTrack;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AppPlaylistSynchronizationService {

  AppPlaylist updateFromSource(
      @NotNull AppPlaylist targetPlaylist, @NotNull AppPlaylist sourcePlaylist);

  List<AppTrack> findTracksNoneMatchPlaylist(
      @NotNull AppPlaylist appPlaylist, @NotNull List<AppTrack> appTracks);

  List<AppTrack> findPlaylistNoneMatchTracks(
      @NotNull AppPlaylist appPlaylist, @NotNull List<AppTrack> appTracks);
}
