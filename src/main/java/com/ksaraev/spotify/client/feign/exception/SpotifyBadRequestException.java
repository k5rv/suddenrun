package com.ksaraev.spotify.client.feign.exception;



public class SpotifyBadRequestException extends SpotifyWebApiException {

    public SpotifyBadRequestException(String message) {
        super(message);
    }
}
