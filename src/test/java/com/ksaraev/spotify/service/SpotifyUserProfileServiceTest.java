package com.ksaraev.spotify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.exception.GetSpotifyUserProfileException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileMapper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyUserProfileServiceTest {
  @Mock private SpotifyClient client;
  @Mock private SpotifyUserProfileMapper mapper;
  @Captor private ArgumentCaptor<SpotifyUserProfileDto> argumentCaptor;

  private AutoCloseable closeable;

  private SpotifyUserProfileItemService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyUserProfileService(client, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldGetCurrentUserProfile() {
    // Given
    SpotifyUserProfileItem userProfile = SpotifyServiceHelper.getUserProfile();
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    given(client.getCurrentUserProfile()).willReturn(userProfileDto);
    given(mapper.mapToModel(userProfileDto)).willReturn((SpotifyUserProfile) userProfile);
    // When
    underTest.getCurrentUserProfile();
    // Then
    verify(client, times(1)).getCurrentUserProfile();
    then(mapper).should().mapToModel(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isNotNull().isEqualTo(userProfileDto);
  }

  @Test
  void itShouldThrowGetSpotifyUserProfileExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.getCurrentUserProfile()).willThrow(runtimeException);
    // Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isExactlyInstanceOf(GetSpotifyUserProfileException.class)
        .hasMessage(new GetSpotifyUserProfileException(runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.getCurrentUserProfile()).willThrow(spotifyUnauthorizedException);

    // Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void
      itShouldThrowGetSpotifyUserProfileExceptionWhenSpotifyUserProfileMapperThrowsRuntimeException() {
    // Given
    RuntimeException runtimeException = new RuntimeException("message");
    given(mapper.mapToModel(any())).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getCurrentUserProfile())
        .isExactlyInstanceOf(GetSpotifyUserProfileException.class)
        .hasMessage(new GetSpotifyUserProfileException(runtimeException).getMessage());
  }
}
