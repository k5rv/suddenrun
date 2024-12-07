package com.ksaraev.spotify.client.feign.exception;



public class SpotifyServiceUnavailableException extends SpotifyWebApiException {

    public SpotifyServiceUnavailableException(String message) {
        super(message);
    }
}
