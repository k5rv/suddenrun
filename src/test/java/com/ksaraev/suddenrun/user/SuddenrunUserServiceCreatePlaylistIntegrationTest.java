package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.suddenrun.exception.SuddenrunError;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylistRepository;
import com.ksaraev.utils.helpers.JsonHelper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.List;
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
class SuddenrunUserServiceCreatePlaylistIntegrationTest {

  private static final String SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS =
      "/api/v1/users/%s/playlists";

  private static final String SPOTIFY_API_V1_ME = "/v1/me";

  private static final String SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID = "/v1/playlists/%s";

  private static final String SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS = "/v1/users/%s/playlists";
  @Autowired AppUserService appUserService;
  @Autowired SuddenrunUserRepository userRepository;
  @Autowired SuddenrunPlaylistRepository playlistRepository;
  @Autowired private SpotifyPlaylistItemConfig spotifyPlaylistConfig;
  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldCreatePlaylist() throws Exception {
    // Given
    SpotifyUserProfileDto spotifyUserDto = SpotifyClientHelper.getUserProfileDto();
    String spotifyUserId = spotifyUserDto.id();
    String spotifyUserName = spotifyUserDto.displayName();

    userRepository.save(SuddenrunUser.builder().id(spotifyUserId).name(spotifyUserName).build());

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyUserDto), HttpStatus.OK.value())));

    SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
    String playlistName = spotifyPlaylistDetails.getName();
    String playlistDescription = spotifyPlaylistDetails.getDescription();
    boolean playlistIsPublic = spotifyPlaylistDetails.getIsPublic();

    SpotifyPlaylistDto spotifyPlaylistDto =
        SpotifyClientHelper.getPlaylistDto(
            spotifyUserDto, playlistName, playlistDescription, playlistIsPublic);

    String playlistId = spotifyPlaylistDto.id();

    WireMock.stubFor(
        WireMock.post(
                WireMock.urlEqualTo(
                    SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId)))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyPlaylistDto), HttpStatus.CREATED.value())));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID.formatted(playlistId)))
            .inScenario("Get created playlist")
            .willSetStateTo("Got tracks")
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyPlaylistDto), HttpStatus.OK.value())));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(
                    SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        createPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

    CreatePlaylistResponse createPlaylistResponse =
        JsonHelper.jsonToObject(
            result.getResponse().getContentAsString(), CreatePlaylistResponse.class);

    assertThat(createPlaylistResponse).isNotNull();
    assertThat(createPlaylistResponse.id()).isEqualTo(playlistId);
  }

  @Test
  void itShouldReturnHttp409AndErrorMessageIfPlaylistAlreadyExists() throws Exception {
    // Given
    SpotifyUserProfileDto spotifyUserDto = SpotifyClientHelper.getUserProfileDto();
    String spotifyUserId = spotifyUserDto.id();
    String spotifyUserName = spotifyUserDto.displayName();

    SuddenrunUser appUser = SuddenrunUser.builder().id(spotifyUserId).name(spotifyUserName).build();

    SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
    String playlistName = spotifyPlaylistDetails.getName();
    String playlistDescription = spotifyPlaylistDetails.getDescription();
    boolean playlistIsPublic = spotifyPlaylistDetails.getIsPublic();

    SpotifyPlaylistDto spotifyPlaylistDto =
        SpotifyClientHelper.getPlaylistDto(
            spotifyUserDto, playlistName, playlistDescription, playlistIsPublic);

    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    playlist.setSnapshotId(spotifyPlaylistDto.snapshotId());
    playlist.setId(spotifyPlaylistDto.id());
    appUser.setPlaylists(List.of(playlist));
    userRepository.save(appUser);
    playlistRepository.save(playlist);

    String playlistId = spotifyPlaylistDto.id();

    GetUserPlaylistsResponse getUserPlaylistsResponse =
        SpotifyClientHelper.createGetUserPlaylistResponse(List.of(spotifyPlaylistDto));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(
                    SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId)))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(getUserPlaylistsResponse), HttpStatus.OK.value())));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID.formatted(playlistId)))
            .inScenario("Get created playlist")
            .willSetStateTo("Got tracks")
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyPlaylistDto), HttpStatus.OK.value())));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(
                    SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        createPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);
    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(error.message()).contains(playlistId);
  }

  @Test
  void itShouldReturnHttp401WhenSpotifyAuthorizationFailed() throws Exception {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    userRepository.save(appUser);

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo(SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId)))
            .willReturn(
                WireMock.jsonResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        createPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);
    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void itShouldReturnHttp404WhenUserIsNotRegistered() throws Exception {
    // Given
    String userId = SpotifyResourceHelper.getRandomId();
    Exception exception = new SuddenrunUserIsNotRegisteredException(userId);

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        createPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(error.message()).isEqualTo(exception.getMessage());
  }
}
