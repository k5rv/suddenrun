package com.ksaraev.spotify.client.feign.exception;

import feign.Response;

public interface FeignExceptionHandler {
  Exception handle(Response response);
}
