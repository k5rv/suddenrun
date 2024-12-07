package com.ksaraev.spotify.model.playlist;

import com.ksaraev.spotify.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileMapper;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SpotifyUserProfileMapper.class, SpotifyTrackMapper.class})
public interface SpotifyPlaylistMapper {

  @Mapping(target = "user", source = "userProfileDto")
  @Mapping(target = "tracks", source = "playlistMusicDto.playlistTrackDtos")
  SpotifyPlaylist mapToModel(SpotifyPlaylistDto playlistDto);

  default List<SpotifyPlaylistItem> mapDtosToModels(List<SpotifyPlaylistDto> playlistDtos) {
    if (playlistDtos == null) return List.of();
    return playlistDtos.stream()
        .filter(Objects::nonNull)
        .map(this::mapToModel)
        .map(SpotifyPlaylistItem.class::cast)
        .toList();
  }

  SpotifyPlaylistDetailsDto mapToPlaylistDetailsDto(SpotifyPlaylistItemDetails playlistDetails);
}
