package com.ksaraev.spotify.client.feign.exception;



public class SpotifyInternalServerErrorException extends SpotifyWebApiException {

    public SpotifyInternalServerErrorException(String message) {
        super(message);
    }
}
