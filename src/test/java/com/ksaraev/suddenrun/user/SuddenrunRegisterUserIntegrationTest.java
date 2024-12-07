package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.suddenrun.exception.SuddenrunError;
import com.ksaraev.utils.helpers.JsonHelper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuddenrunRegisterUserIntegrationTest {

  private static final String SPOTIFY_API_V1_ME = "/v1/me";

  private static final String SUDDENRUN_API_V1_USERS = "/api/v1/users";

  @Autowired private AppUserService appUserService;

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldRegisterUser() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    String userId = userProfileDto.id();
    String userName = userProfileDto.displayName();

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(userProfileDto), HttpStatus.OK.value())));

    // When
    ResultActions userRegistrationResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
            userRegistrationResultActions
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

    GetUserResponse getUserResponse =
            JsonHelper.jsonToObject(result.getResponse().getContentAsString(), GetUserResponse.class);

    assertThat(getUserResponse).isNotNull();
    assertThat(getUserResponse.id()).isEqualTo(userId);
    assertThat(getUserResponse.name()).isEqualTo(userName);

  }

  @Test
  void itShouldThrowSuddenrunUserDoesNotMatchCurrentSpotifyUserException() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    String fraudUserId = SpotifyResourceHelper.getRandomId();

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(userProfileDto), HttpStatus.OK.value())));

    // When
    ResultActions userRegistrationRepeatedAttemptResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS + "/" + fraudUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
            userRegistrationRepeatedAttemptResultActions
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andReturn();

    SuddenrunError error =
            JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(error.message())
        .isEqualTo(new SuddenrunUserDoesNotMatchCurrentSpotifyUserException(fraudUserId).getMessage());
  }

  @Test
  void itShouldThrowSuddenrunUserIsAlreadyRegisteredExceptionIfUserIsAlreadyRegistered()
      throws Exception {
    // Given
    SpotifyUserProfileDto userProfileDto = SpotifyClientHelper.getUserProfileDto();
    String userId = userProfileDto.id();
    String userName = userProfileDto.displayName();

    appUserService.registerUser(userId, userName);

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(userProfileDto), HttpStatus.OK.value())));

    // When
    ResultActions userRegistrationRepeatedAttemptResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        userRegistrationRepeatedAttemptResultActions
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(error.message())
        .isEqualTo(new SuddenrunUserIsAlreadyRegisteredException(userId).getMessage());
  }

  @Test
  void itShouldReturnAuthenticationErrorWhenSpotifyAuthorizationFailed() throws Exception {
    // Given
    String userId = SpotifyResourceHelper.getRandomId();

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())));

    // When
    ResultActions userRegistrationResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        userRegistrationResultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    assertThat(error.message()).isEqualTo("Suddenrun authentication error");
  }

  @Test
  void itShouldReturnInternalServerErrorWhenSpotifyServiceCallFailed() throws Exception {
    // Given
    String userId = SpotifyResourceHelper.getRandomId();

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(WireMock.jsonResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value())));

    // When
    ResultActions userRegistrationResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        userRegistrationResultActions
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(error.message()).isEqualTo("Suddenrun internal error: please contact support");
  }
}
