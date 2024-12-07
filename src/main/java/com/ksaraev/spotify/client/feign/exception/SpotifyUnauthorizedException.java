package com.ksaraev.spotify.client.feign.exception;


public class SpotifyUnauthorizedException extends SpotifyWebApiException {
    public SpotifyUnauthorizedException(String message) {
        super(message);
    }

    public SpotifyUnauthorizedException() {
        super();
    }
}
