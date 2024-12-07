package com.ksaraev.spotify.model.artist;

import com.ksaraev.spotify.client.dto.SpotifyArtistDto;
import com.ksaraev.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotifyArtistMapper extends SpotifyMapper {
  SpotifyArtist mapToArtist(SpotifyArtistDto artistItem);
}
