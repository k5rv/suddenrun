package com.ksaraev.suddenrun.track;

import static com.ksaraev.spotify.model.SpotifyItemType.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotify.model.track.SpotifyTrack;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = SPRING)
public interface AppTrackMapper {

  @AppTrackIdToSpotifyTrackItemUriMapper
  static URI appTrackIdToSpotifyTrackItemUriMapper(String id) {
    return TRACK.createUri(id);
  }

  AppTrack mapToEntity(SpotifyTrackItem trackItem);

  List<AppTrack> mapToEntities(List<SpotifyTrackItem> trackItems);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "uri", source = "id", qualifiedBy = AppTrackIdToSpotifyTrackItemUriMapper.class)
  @Mapping(target = "popularity", ignore = true)
  @Mapping(target = "artists", ignore = true)
  SpotifyTrackItem mapToDto(AppTrack appTrack);

  List<SpotifyTrackItem> mapToDtos(List<AppTrack> appTracks);

  @ObjectFactory
  default SpotifyTrackItem createItem() {
    return SpotifyTrack.builder().build();
  }

  @ObjectFactory
  default AppTrack createEntity() {
    return SuddenrunTrack.builder().build();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  @interface AppTrackIdToSpotifyTrackItemUriMapper {}
}
