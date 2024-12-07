package com.ksaraev.spotify.model.playlist;

import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotifyPlaylist implements SpotifyPlaylistItem {

  @NotNull private String id;

  @Valid private List<SpotifyTrackItem> tracks;

  @NotNull private String snapshotId;

  @Valid @NotNull private SpotifyUserProfileItem user;

  private String name;

  private URI uri;

  private String description;

  private Boolean isPublic;

  private Boolean isCollaborative;
}
