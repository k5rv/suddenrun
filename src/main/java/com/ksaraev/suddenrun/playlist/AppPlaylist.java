package com.ksaraev.suddenrun.playlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.user.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonDeserialize(as = SuddenrunPlaylist.class)
public interface AppPlaylist {

  String getId();

  void setId(String id);

  AppUser getUser();

  void setUser(@Valid AppUser appUser);

  List<AppTrack> getTracks();

  void setTracks(@NotNull List<AppTrack> tracks);

  List<AppTrack> getInclusions();

  void setInclusions(@NotNull List<AppTrack> inclusions);

  List<AppTrack> getExclusions();

  void setExclusions(@NotNull List<AppTrack> exclusions);

  String getSnapshotId();

  void setSnapshotId(String snapshotId);
}
