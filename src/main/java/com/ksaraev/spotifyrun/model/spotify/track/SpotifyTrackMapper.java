package com.ksaraev.spotifyrun.model.spotify.track;

import com.ksaraev.spotifyrun.client.api.items.SpotifyPlaylistTrackDto;
import com.ksaraev.spotifyrun.client.api.items.SpotifyTrackDto;
import com.ksaraev.spotifyrun.model.spotify.artist.SpotifyArtistMapper;
import com.ksaraev.spotifyrun.model.SpotifyMapper;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyArtistMapper.class})
public interface SpotifyTrackMapper extends SpotifyMapper {

  @Mapping(target = "artists", source = "artistItems")
  SpotifyTrack mapToTrack(SpotifyTrackDto spotifyTrackDto);

  default List<SpotifyTrackItem> mapItemsToTracks(List<SpotifyTrackDto> trackItems) {
    if (trackItems == null) return List.of();
    return trackItems.stream()
        .filter(Objects::nonNull)
        .map(this::mapToTrack)
        .map(SpotifyTrackItem.class::cast)
        .toList();
  }

  default List<SpotifyTrackItem> mapPlaylistItemsToTracks(
      List<SpotifyPlaylistTrackDto> playlistItemTracks) {
    if (playlistItemTracks == null) return List.of();
    return playlistItemTracks.stream()
        .filter(Objects::nonNull)
        .map(SpotifyPlaylistTrackDto::trackItem)
        .map(this::mapToTrack)
        .map(SpotifyTrackItem.class::cast)
        .toList();
  }
}