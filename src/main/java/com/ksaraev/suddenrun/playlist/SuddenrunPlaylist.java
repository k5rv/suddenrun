package com.ksaraev.suddenrun.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SuddenrunPlaylist implements AppPlaylist {

  @Id
  @Column(nullable = false)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  private SuddenrunUser user;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<AppTrack> inclusions = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<AppTrack> exclusions = new ArrayList<>();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<AppTrack> tracks = new ArrayList<>();

  private String snapshotId;

  public List<AppTrack> getInclusions() {
    return this.inclusions.stream().map(AppTrack.class::cast).toList();
  }

  public void setInclusions(@NotNull List<AppTrack> inclusions) {
    this.inclusions = inclusions;
  }

  public List<AppTrack> getExclusions() {
    return this.exclusions.stream().map(AppTrack.class::cast).toList();
  }

  public void setExclusions(@NotNull List<AppTrack> exclusions) {
    this.exclusions = exclusions;
  }

  @JsonIgnore
  @Override
  public AppUser getUser() {
    return this.user;
  }

  @JsonIgnore
  @Override
  public void setUser(@Valid AppUser appUser) {
    this.user = (SuddenrunUser) appUser;
  }

  @Override
  public List<AppTrack> getTracks() {
    return this.tracks;
  }

  @Override
  public void setTracks(@NotNull List<AppTrack> appTracks) {
    this.tracks = appTracks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    SuddenrunPlaylist playlist = (SuddenrunPlaylist) o;
    return getId() != null && Objects.equals(getId(), playlist.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
