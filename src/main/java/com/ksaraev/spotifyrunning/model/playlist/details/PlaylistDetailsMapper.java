package com.ksaraev.spotifyrunning.model.playlist.details;

import com.ksaraev.spotifyrunning.client.items.SpotifyPlaylistItemDetails;
import com.ksaraev.spotifyrunning.model.spotify.SpotifyPlaylistDetails;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlaylistDetailsMapper {
  SpotifyPlaylistItemDetails toDto(SpotifyPlaylistDetails playlistDetails);
}
