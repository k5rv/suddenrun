package com.ksaraev.spotify.client.feign.exception;



public class SpotifyNotFoundException extends SpotifyWebApiException {

    public SpotifyNotFoundException(String message) {
        super(message);
    }
}
