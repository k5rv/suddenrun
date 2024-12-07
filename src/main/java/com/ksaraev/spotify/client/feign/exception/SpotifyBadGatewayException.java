package com.ksaraev.spotify.client.feign.exception;


public class SpotifyBadGatewayException extends SpotifyWebApiException {

    public SpotifyBadGatewayException(String message) {
        super(message);
    }
}
