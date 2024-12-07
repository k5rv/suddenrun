package com.ksaraev.spotify.exception;

public class GetSpotifyUserTopTracksException extends SpotifyServiceException {

  public GetSpotifyUserTopTracksException(Throwable cause) {
    super("Error while getting Spotify user top tracks: " + cause.getMessage(), cause);
  }
}
