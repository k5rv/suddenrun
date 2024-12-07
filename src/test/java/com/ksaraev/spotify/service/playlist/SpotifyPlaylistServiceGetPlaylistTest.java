package com.ksaraev.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.GetSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.spotify.service.SpotifyPlaylistService;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyPlaylistServiceGetPlaylistTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  @Mock private SpotifyClient client;

  @Mock private UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  @Mock private SpotifyPlaylistMapper mapper;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDto> playlistDtoArgumentCaptor;

  private AutoCloseable closeable;

  private ExecutableValidator executableValidator;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    executableValidator = factory.getValidator().forExecutables();
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldGetPlaylist() {
    // Given
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();
    given(client.getPlaylist(any())).willReturn(playlistDto);
    given(mapper.mapToModel(any(SpotifyPlaylistDto.class))).willReturn(playlist);
    // When
    underTest.getPlaylist(playlist.getId());
    // Then
    then(client).should().getPlaylist(playlistIdArgumentCaptor.capture());
    assertThat(playlistIdArgumentCaptor.getValue()).isNotNull().isEqualTo(playlist.getId());
    then(mapper).should().mapToModel(playlistDtoArgumentCaptor.capture());
    assertThat(playlistDtoArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDto);
  }

  @Test
  void itShouldThrowGetSpotifyPlaylistExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.getPlaylist(playlistId)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessage(new GetSpotifyPlaylistException(playlistId, runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.getPlaylist(playlistId)).willThrow(spotifyUnauthorizedException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void itShouldThrowGetSpotifyPlaylistExceptionWhenPlaylistMapperThrowsRuntimeException() {
    // Given
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();
    String playlistId = playlist.getId();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto(playlistId);
    given(client.getPlaylist(any())).willReturn(playlistDto);
    RuntimeException runtimeException = new RuntimeException("message");
    given(mapper.mapToModel(any(SpotifyPlaylistDto.class))).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getPlaylist(playlistId))
        .isExactlyInstanceOf(GetSpotifyPlaylistException.class)
        .hasMessage(new GetSpotifyPlaylistException(playlistId, runtimeException).getMessage());
  }

  @Test
  void itShouldDetectGetPlaylistConstraintViolationsWhenPlaylistIdIsNull() throws Exception {
    // Given
    Method method = SpotifyPlaylistService.class.getMethod("getPlaylist", String.class);
    Object[] parameterValues = {null};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getPlaylist.playlistId: must not be null");
  }
}
