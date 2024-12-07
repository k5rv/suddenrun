package com.ksaraev.suddenrun.user;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyUserProfileItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.playlist.*;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class SuddenrunUserController {

  private final AppUserService suddenrunUserService;

  private final AppPlaylistService suddenrunPlaylistService;

  private final SpotifyUserProfileItemService spotifyUserProfileService;

  private final AppUserMapper mapper;

  @GetMapping("/current")
  public GetCurrentUserResponse getCurrentUser() {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String userId = userProfileItem.getId();
      String userName = userProfileItem.getName();
      Optional<AppUser> optionalUser = suddenrunUserService.getUser(userId);
      boolean userPresent = optionalUser.isPresent();
      return GetCurrentUserResponse.builder()
          .id(userId)
          .name(userName)
          .isRegistered(userPresent)
          .build();
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PostMapping(path = "/{user_id}")
  public GetUserResponse registerUser(@NotNull @PathVariable(value = "user_id") String userId) {
    try {
      SpotifyUserProfileItem userProfileItem = spotifyUserProfileService.getCurrentUserProfile();
      String spotifyUserId = userProfileItem.getId();
      String spotifyUserName = userProfileItem.getName();
      boolean userIdsAreIdentical = spotifyUserId.equals(userId);
      if (!userIdsAreIdentical) {
        throw new SuddenrunUserDoesNotMatchCurrentSpotifyUserException(userId);
      }
      AppUser appUser = suddenrunUserService.registerUser(spotifyUserId, spotifyUserName);
      return mapper.mapToDto(appUser);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    }
  }

  @PostMapping(path = "/{user_id}/playlists")
  public CreatePlaylistResponse createPlaylist(
      @NotNull @PathVariable(value = "user_id") String userId) {
    AppUser appUser = getUser(userId);
    suddenrunPlaylistService
        .getPlaylist(appUser)
        .ifPresent(
            playlist -> {
              throw new SuddenrunPlaylistAlreadyExistsException(playlist.getId());
            });
    AppPlaylist appPlaylist = suddenrunPlaylistService.createPlaylist(appUser);
    String playlistId = appPlaylist.getId();
    return CreatePlaylistResponse.builder().id(playlistId).build();
  }

  @GetMapping(path = "/{user_id}/playlists")
  public GetUserPlaylistResponse getUserPlaylist(
      @NotNull @PathVariable(value = "user_id") String userId) {
    AppUser appUser = getUser(userId);
    AppPlaylist appPlaylist =
        suddenrunPlaylistService
            .getPlaylist(appUser)
            .orElseThrow(() -> new SuddenrunUserDoesNotHaveAnyPlaylistsException(userId));
    String playlistId = appPlaylist.getId();
    return GetUserPlaylistResponse.builder().id(playlistId).build();
  }

  private AppUser getUser(String userId) {
    return suddenrunUserService
        .getUser(userId)
        .orElseThrow(() -> new SuddenrunUserIsNotRegisteredException(userId));
  }
}
