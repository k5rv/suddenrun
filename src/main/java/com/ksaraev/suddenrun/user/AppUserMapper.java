package com.ksaraev.suddenrun.user;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.ksaraev.spotify.model.SpotifyItemType;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import java.net.URI;
import org.mapstruct.*;

@Mapper(componentModel = SPRING)
public interface AppUserMapper {

  default AppUser mapToEntity(SpotifyUserProfileItem userProfileItem) {
    String id = userProfileItem.getId();
    String name = userProfileItem.getName();
    return SuddenrunUser.builder().id(id).name(name).build();
  }

  default SpotifyUserProfileItem mapToItem(AppUser appUser) {
    String id = appUser.getId();
    String name = appUser.getName();
    URI uri = SpotifyItemType.USER.createUri(id);
    return SpotifyUserProfile.builder().id(id).name(name).uri(uri).build();
  }

  GetUserResponse mapToDto(AppUser appUser);

  @ObjectFactory
  default SpotifyUserProfileItem createItem() {
    return SpotifyUserProfile.builder().build();
  }

  @ObjectFactory
  default AppUser createEntity() {
    return SuddenrunUser.builder().build();
  }
}
