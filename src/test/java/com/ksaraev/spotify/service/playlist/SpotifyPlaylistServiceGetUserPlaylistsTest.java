package com.ksaraev.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.GetUserPlaylistsRequest;
import com.ksaraev.spotify.client.dto.GetUserPlaylistsResponse;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.GetSpotifyUserPlaylistsException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.spotify.service.SpotifyPlaylistService;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import jakarta.validation.*;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyPlaylistServiceGetUserPlaylistsTest {
  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  @Mock private SpotifyClient client;

  @Mock private UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  @Mock private SpotifyPlaylistMapper mapper;

  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;

  @Captor private ArgumentCaptor<GetUserPlaylistsRequest> requestArgumentCaptor;

  @Captor private ArgumentCaptor<List<SpotifyPlaylistDto>> playlistDtosArgumentCaptor;

  private ExecutableValidator executableValidator;

  private AutoCloseable closeable;

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
  void itShouldGetUserPlaylists() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = SpotifyClientHelper.getPlaylistDtos(2);
    List<SpotifyPlaylistItem> playlists = SpotifyServiceHelper.getPlaylists(2);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createGetUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    given(mapper.mapDtosToModels(any())).willReturn(playlists);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).should().mapDtosToModels(playlistDtosArgumentCaptor.capture());
    assertThat(playlistDtosArgumentCaptor.getValue()).isNotNull().isEqualTo(playlistDtos);
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyPlaylistDtosIsEmpty() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = List.of();
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createGetUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).shouldHaveNoInteractions();
  }

  @Test
  void itShouldReturnEmptyListWhenSpotifyPlaylistDtosAreNull() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = new ArrayList<>();
    playlistDtos.add(null);
    playlistDtos.add(null);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createGetUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).shouldHaveNoInteractions();
  }

  @Test
  void itShouldReturnNonNullElementsWhenSpotifyPlaylistDtosContainsNulls() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = new ArrayList<>();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    playlistDtos.add(null);
    playlistDtos.add(playlistDto);
    playlistDtos.add(null);
    GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createGetUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    // When
    underTest.getUserPlaylists(userProfile);
    // Then
    then(client)
        .should()
        .getPlaylists(userIdArgumentCaptor.capture(), requestArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(requestArgumentCaptor.getValue()).isEqualTo(request);
    then(mapper).should().mapDtosToModels(playlistDtosArgumentCaptor.capture());
    assertThat(playlistDtosArgumentCaptor.getValue()).isNotNull().containsExactly(playlistDto);
  }

  @Test
  void itShouldThrowGetSpotifyUserPlaylistsExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.getPlaylists(any(), any())).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(GetSpotifyUserPlaylistsException.class)
        .hasMessageContaining(userId)
        .hasMessage(new GetSpotifyUserPlaylistsException(userId, runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.getPlaylists(any(), any())).willThrow(spotifyUnauthorizedException);

    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void itShouldThrowGetSpotifyUserPlaylistsExceptionWhenPlaylistMapperThrowsRuntimeException() {
    // Given
    String message = "message";
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    String userId = userProfile.getId();
    List<SpotifyPlaylistDto> playlistDtos = SpotifyClientHelper.getPlaylistDtos(2);
    GetUserPlaylistsResponse response =
        SpotifyClientHelper.createGetUserPlaylistResponse(playlistDtos);
    given(client.getPlaylists(any(), any())).willReturn(response);
    RuntimeException runtimeException = new RuntimeException(message);
    given(mapper.mapDtosToModels(any())).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getUserPlaylists(userProfile))
        .isExactlyInstanceOf(GetSpotifyUserPlaylistsException.class)
        .hasMessage(new GetSpotifyUserPlaylistsException(userId, runtimeException).getMessage());
  }

  @Test
  void itShouldDetectGetUserPlaylistsConstraintViolationsWhenSpotifyUserItemIsNull()
      throws Exception {
    // Given
    Method method =
        SpotifyPlaylistService.class.getMethod("getUserPlaylists", SpotifyUserProfileItem.class);
    Object[] parameterValues = {null};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getUserPlaylists.userProfileItem: must not be null");
  }

  @Test
  void itShouldDetectGetUserPlaylistsConstraintViolationsWhenSpotifyUserItemIsNotValid()
      throws Exception {
    // Given
    SpotifyUserProfileItem userProfileItem = SpotifyServiceHelper.getUserProfile();
    userProfileItem.setId(null);
    Method method =
        SpotifyPlaylistService.class.getMethod("getUserPlaylists", SpotifyUserProfileItem.class);
    Object[] parameterValues = {userProfileItem};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("getUserPlaylists.userProfileItem.id: must not be null");
  }
}
