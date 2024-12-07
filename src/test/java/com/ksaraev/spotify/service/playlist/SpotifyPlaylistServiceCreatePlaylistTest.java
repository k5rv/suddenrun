package com.ksaraev.spotify.service.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDetailsDto;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.CreateSpotifyPlaylistException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylist;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
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

class SpotifyPlaylistServiceCreatePlaylistTest {

  private static final String CREATE_PLAYLIST = "createPlaylist";

  private static final ValidatorFactory validatorFactory =
      Validation.buildDefaultValidatorFactory();

  @Mock private SpotifyClient client;

  @Mock private UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  @Mock private SpotifyPlaylistMapper mapper;

  @Captor private ArgumentCaptor<String> userIdArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDetails> playlistDetailsArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDto> playlistDtoArgumentCaptor;

  @Captor private ArgumentCaptor<SpotifyPlaylistDetailsDto> playlistDetailsDtoArgumentCaptor;

  private ExecutableValidator executableValidator;

  private AutoCloseable closeable;

  private SpotifyPlaylistItemService underTest;

  @BeforeEach
  void setUp() {
    executableValidator = validatorFactory.getValidator().forExecutables();
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyPlaylistService(client, requestConfig, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyPlaylistDetailsDto playlistDetailsDto = SpotifyClientHelper.getPlaylistDetailsDto();
    SpotifyPlaylistDto playlistDto = SpotifyClientHelper.getPlaylistDto();
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistDetails playlistDetails =
        (SpotifyPlaylistDetails) SpotifyServiceHelper.getPlaylistDetails();
    SpotifyPlaylist playlist = (SpotifyPlaylist) SpotifyServiceHelper.getPlaylist();
    given(mapper.mapToPlaylistDetailsDto(any(SpotifyPlaylistItemDetails.class)))
        .willReturn(playlistDetailsDto);
    given(client.createPlaylist(any(), any())).willReturn(playlistDto);
    given(mapper.mapToModel(any(SpotifyPlaylistDto.class))).willReturn(playlist);
    // When
    underTest.createPlaylist(userProfile, playlistDetails);
    // Then
    then(mapper).should().mapToPlaylistDetailsDto(playlistDetailsArgumentCaptor.capture());
    assertThat(playlistDetailsArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDetails);
    then(client)
        .should()
        .createPlaylist(userIdArgumentCaptor.capture(), playlistDetailsDtoArgumentCaptor.capture());
    assertThat(userIdArgumentCaptor.getValue()).isNotNull().isEqualTo(userProfile.getId());
    assertThat(playlistDetailsDtoArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDetailsDto);
    then(mapper).should().mapToModel(playlistDtoArgumentCaptor.capture());
    assertThat(playlistDtoArgumentCaptor.getValue())
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(playlistDto);
  }

  @Test
  void itShouldThrowCreatePlaylistExceptionWhenPlaylistMapperThrowsRuntimeException() {
    // Given
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile();
    String userId = spotifyUser.getId();
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    RuntimeException runtimeException = new RuntimeException("message");
    given(mapper.mapToPlaylistDetailsDto(any(SpotifyPlaylistItemDetails.class)))
        .willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(CreateSpotifyPlaylistException.class)
        .hasMessage(new CreateSpotifyPlaylistException(userId, runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyUserProfileItem spotifyUser = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistItemDetails spotifyPlaylistDetails = SpotifyServiceHelper.getPlaylistDetails();
    SpotifyUnauthorizedException spotifyUnauthorizedException = new SpotifyUnauthorizedException();
    given(client.createPlaylist(any(), any())).willThrow(spotifyUnauthorizedException);

    // Then
    assertThatThrownBy(() -> underTest.createPlaylist(spotifyUser, spotifyPlaylistDetails))
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyUserProfileIsNull()
      throws Exception {
    // Given
    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
    Method method =
        SpotifyPlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUserProfileItem.class, SpotifyPlaylistItemDetails.class);
    Object[] parameterValues = {null, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".userProfileItem: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistConstraintViolationsWhenSpotifyPlaylistItemDetailsIsNull()
      throws Exception {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    Method method =
        SpotifyPlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUserProfileItem.class, SpotifyPlaylistItemDetails.class);
    Object[] parameterValues = {userProfile, null};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".playlistItemDetails: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistCascadeConstraintViolationsWhenSpotifyUserIsNotValid()
      throws Exception {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    user.setId(null);
    Method method =
        SpotifyPlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUserProfileItem.class, SpotifyPlaylistItemDetails.class);
    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".userProfileItem.id: must not be null");
  }

  @Test
  void itShouldDetectCreatePlaylistCascadeConstraintViolationsWhenSpotifyPlaylistDetailsIsNotValid()
      throws Exception {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();
    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
    playlistDetails.setName(null);
    Method method =
        SpotifyPlaylistService.class.getMethod(
            CREATE_PLAYLIST, SpotifyUserProfileItem.class, SpotifyPlaylistItemDetails.class);
    Object[] parameterValues = {user, playlistDetails};

    // When
    Set<ConstraintViolation<SpotifyPlaylistItemService>> constraintViolations =
        executableValidator.validateParameters(underTest, method, parameterValues);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage(CREATE_PLAYLIST + ".playlistItemDetails.name: must not be empty");
  }
}
