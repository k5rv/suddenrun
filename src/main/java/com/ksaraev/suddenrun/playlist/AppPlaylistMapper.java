package com.ksaraev.suddenrun.playlist;

import static com.ksaraev.spotify.model.SpotifyItemType.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotify.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUserMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.List;
import org.mapstruct.*;

@Mapper(
    componentModel = SPRING,
    uses = {AppUserMapper.class, AppTrackMapper.class})
public interface AppPlaylistMapper {

  @AppPlaylistIdToSpotifyPlaylistItemUriMapper
  static URI appPlaylistIdToSpotifyPlaylistItemUri(String id) {
    return PLAYLIST.createUri(id);
  }

  @Mapping(target = "exclusions", ignore = true)
  @Mapping(target = "inclusions", ignore = true)
  @Mapping(target = "user", source = "playlistItem.user")
  AppPlaylist mapToEntity(SpotifyPlaylistItem playlistItem);

  @Mapping(
      target = "uri",
      source = "id",
      qualifiedBy = AppPlaylistIdToSpotifyPlaylistItemUriMapper.class)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "isPublic", ignore = true)
  @Mapping(target = "isCollaborative", ignore = true)
  @Mapping(target = "description", ignore = true)
  SpotifyPlaylistItem mapToDto(AppPlaylist appPlaylist);

  @ObjectFactory
  default AppPlaylist createEntity() {
    return SuddenrunPlaylist.builder()
        .tracks(List.of())
        .inclusions(List.of())
        .exclusions(List.of())
        .build();
  }

  @ObjectFactory
  default SpotifyPlaylistItem createDto() {
    return SpotifyPlaylist.builder().tracks(List.of()).build();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface AppPlaylistIdToSpotifyPlaylistItemUriMapper {}
}
