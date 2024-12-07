package com.ksaraev.spotify.client.feign.exception;

public class SpotifyNotModifiedException extends SpotifyWebApiException {

    public SpotifyNotModifiedException(String message) {
        super(message);
    }

}
