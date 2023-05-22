package com.ksaraev.suddenrun.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ksaraev.suddenrun.playlist.AppPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuddenrunUser implements AppUser {

  @Id private String id;

  private String name;

  @JsonBackReference
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SuddenrunPlaylist> playlists = new ArrayList<>();

  @Override
  public void addPlaylist(AppPlaylist appPlaylist) {
    SuddenrunPlaylist playlist = (SuddenrunPlaylist) appPlaylist;
    playlists.add(playlist);
    playlist.setOwner(this);
  }

  @Override
  public void removePlaylist(AppPlaylist appPlaylist) {
    SuddenrunPlaylist playlist = (SuddenrunPlaylist) appPlaylist;
    if (!this.playlists.isEmpty()) {
      playlists.remove(playlist);
    }
    playlist.setOwner(null);
  }

  @Override
  public List<AppPlaylist> getPlaylists() {
    if (this.playlists == null) return List.of();
    return this.playlists.stream().map(AppPlaylist.class::cast).toList();
  }

  @Override
  public void setPlaylists(List<AppPlaylist> playlists) {
    if (playlists == null) {
      this.playlists = new ArrayList<>();
      return;
    }
    this.playlists = playlists.stream().map(SuddenrunPlaylist.class::cast).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    SuddenrunUser suddenrunUser = (SuddenrunUser) o;
    return getId() != null && Objects.equals(getId(), suddenrunUser.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
