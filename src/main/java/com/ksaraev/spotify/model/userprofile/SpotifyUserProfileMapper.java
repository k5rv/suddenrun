package com.ksaraev.spotify.model.userprofile;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotify.model.SpotifyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface SpotifyUserProfileMapper extends SpotifyMapper {

  @Mapping(target = "name", source = "displayName")
  SpotifyUserProfile mapToModel(SpotifyUserProfileDto userProfileDto);
}
