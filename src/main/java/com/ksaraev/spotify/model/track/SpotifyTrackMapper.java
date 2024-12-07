package com.ksaraev.spotify.model.track;

import com.ksaraev.spotify.client.dto.SpotifyPlaylistTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.model.SpotifyMapper;
import com.ksaraev.spotify.model.artist.SpotifyArtistMapper;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyArtistMapper.class})
public interface SpotifyTrackMapper extends SpotifyMapper {

  @Mapping(target = "artists", source = "artistDtos")
  SpotifyTrack mapToModel(SpotifyTrackDto spotifyTrackDto);

  default List<SpotifyTrackItem> mapDtosToModels(List<SpotifyTrackDto> trackDtos) {
    if (trackDtos == null) return List.of();
    return trackDtos.stream()
        .filter(Objects::nonNull)
        .map(this::mapToModel)
        .map(SpotifyTrackItem.class::cast)
        .toList();
  }

  default List<SpotifyTrackItem> mapPlaylistTrackDtosToModels(
      List<SpotifyPlaylistTrackDto> playlistTrackDtos) {
    if (playlistTrackDtos == null) return List.of();
    return playlistTrackDtos.stream()
        .filter(Objects::nonNull)
        .map(SpotifyPlaylistTrackDto::trackDto)
        .map(this::mapToModel)
        .map(SpotifyTrackItem.class::cast)
        .toList();
  }
}
