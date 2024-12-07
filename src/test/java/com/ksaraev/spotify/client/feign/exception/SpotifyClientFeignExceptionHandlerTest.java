package com.ksaraev.spotify.client.feign.exception;

import com.ksaraev.spotify.client.exception.SpotifyClientReadingErrorResponseException;
import com.ksaraev.spotify.client.exception.SpotifyClientReadingErrorResponseIsNullException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpotifyClientFeignExceptionHandlerTest {

  private SpotifyClientFeignExceptionHandler underTest;

  @BeforeEach
  void setUp() {
    underTest = new SpotifyClientFeignExceptionHandler();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           304|SpotifyNotModifiedException
           400|SpotifyBadRequestException
           401|SpotifyUnauthorizedException
           403|SpotifyForbiddenException
           404|SpotifyNotFoundException
           429|SpotifyTooManyRequestsException
           500|SpotifyInternalServerErrorException
           502|SpotifyBadGatewayException
           503|SpotifyServiceUnavailableException
           """)
  void itShouldReturnSpotifyExceptionWhenHttpResponseErrorStatusReceived(
      Integer status, String className) throws Exception {
    // Given
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(status)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(
            Class.forName(SpotifyWebApiException.class.getPackage().getName() + "." + className));
  }

  @Test
  void itShouldReturnSpotifyExceptionWhenUnmappedHttpResponseErrorStatusReceived() {
    // Given
    Response response =
        Response.builder()
            .body("", StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(422)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(SpotifyWebApiException.class);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
           400|SpotifyBadRequestException  |{"error":{"status":400,"message":"Bad Request"}}
           401|SpotifyUnauthorizedException|{"error":"invalid_client","error_description":"Invalid client secret"}
           401|SpotifyUnauthorizedException|""
           """)
  void itShouldReturnSpotifyExceptionWithOriginalErrorMessageWhenHttpResponseErrorStatusReceived(
      Integer status, String className, String message) throws Exception {
    // Given
    Response response =
        Response.builder()
            .body(message, StandardCharsets.UTF_8)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(status)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(
            Class.forName(SpotifyWebApiException.class.getPackage().getName() + "." + className))
        .hasMessage(message);
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseBodyCantBeRead() {
    // Given
    String message = "message";
    IOException ioException = new IOException(message);
    Response response =
        Response.builder()
            .body(
                new InputStream() {
                  @Override
                  public int read() throws IOException {
                    throw ioException;
                  }
                },
                1)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(400)
            .build();
    // Then
    Assertions.assertThatThrownBy(() -> underTest.handle(response))
        .isExactlyInstanceOf(SpotifyClientReadingErrorResponseException.class)
        .hasMessage(
            new SpotifyClientReadingErrorResponseException(ioException).getMessage());
  }

  @Test
  void itShouldThrowSpotifyClientExceptionWhenResponseBodyIsNull() {
    // Given
    String message = "";
    byte[] bytes = null;
    Response response =
        Response.builder()
            .body(bytes)
            .request(
                Request.create(
                    Request.HttpMethod.GET,
                    "http://127.0.0.1",
                    Map.of(),
                    Request.Body.create("", StandardCharsets.UTF_8),
                    new RequestTemplate()))
            .status(400)
            .build();
    // Then
    Assertions.assertThat(underTest.handle(response))
        .isExactlyInstanceOf(SpotifyBadRequestException.class)
        .hasMessage(message);
  }

  @Test
  void itShouldThrowSpotifyClientErrorResponseHandlingExceptionWhenResponseIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.handle(null))
        .isExactlyInstanceOf(SpotifyClientReadingErrorResponseIsNullException.class)
        .hasMessage(new SpotifyClientReadingErrorResponseIsNullException().getMessage());
  }
}
