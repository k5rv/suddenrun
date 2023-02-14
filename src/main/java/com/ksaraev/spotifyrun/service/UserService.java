package com.ksaraev.spotifyrun.service;

import com.ksaraev.spotifyrun.client.SpotifyClient;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyNotFoundException;
import com.ksaraev.spotifyrun.client.items.SpotifyUserProfileItem;
import com.ksaraev.spotifyrun.exception.GetUserException;
import com.ksaraev.spotifyrun.exception.UserNotFoundException;
import com.ksaraev.spotifyrun.model.spotify.SpotifyUser;
import com.ksaraev.spotifyrun.model.user.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.ksaraev.spotifyrun.client.exception.http.SpotifyNotFoundException.SPOTIFY_USER_PROFILE_IS_NULL;
import static com.ksaraev.spotifyrun.exception.GetUserException.GET_USER_ERROR_MESSAGE;
import static com.ksaraev.spotifyrun.exception.MappingException.SPOTIFY_USER_PROFILE_MAPPING_ERROR;
import static com.ksaraev.spotifyrun.exception.UserNotFoundException.USER_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService implements SpotifyUserService {
  private final SpotifyClient spotifyClient;
  private final UserMapper userMapper;

  @Override
  public @Valid SpotifyUser getUser() {
    SpotifyUserProfileItem userProfileItem;
    try {
      userProfileItem = spotifyClient.getCurrentUserProfile();
    } catch (SpotifyNotFoundException e) {
      throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + ": " + e.getMessage(), e);
    } catch (RuntimeException e) {
      throw new GetUserException(GET_USER_ERROR_MESSAGE + ": " + e.getMessage(), e);
    }
    if (userProfileItem == null) {
      throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + ": " + SPOTIFY_USER_PROFILE_IS_NULL);
    }
    try {
      return userMapper.toModel(userProfileItem);
    } catch (RuntimeException e) {
      throw new GetUserException(
          GET_USER_ERROR_MESSAGE + ": " + SPOTIFY_USER_PROFILE_MAPPING_ERROR);
    }
  }
}
