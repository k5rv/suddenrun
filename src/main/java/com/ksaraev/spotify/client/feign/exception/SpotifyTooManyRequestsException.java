package com.ksaraev.spotify.client.feign.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyTooManyRequestsException extends SpotifyWebApiException {}
