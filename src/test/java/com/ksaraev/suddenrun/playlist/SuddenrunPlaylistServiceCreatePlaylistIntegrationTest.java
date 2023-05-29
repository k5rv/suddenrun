package com.ksaraev.suddenrun.playlist;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.config.GetSpotifyRecommendationRequestConfig;
import com.ksaraev.spotify.config.GetSpotifyUserTopTrackRequestConfig;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.suddenrun.exception.SuddenrunError;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import com.ksaraev.suddenrun.user.AppUserService;
import com.ksaraev.suddenrun.user.SuddenrunUser;
import com.ksaraev.suddenrun.user.SuddenrunUserRepository;
import com.ksaraev.utils.helpers.JsonHelper;
import com.ksaraev.utils.helpers.SpotifyClientHelper;
import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuddenrunPlaylistServiceCreatePlaylistIntegrationTest {

  private static final String SUDDENRUN_API_V1_PLAYLISTS = "/api/v1/playlists";

  private static final String SPOTIFY_API_V1_ME = "/v1/me";

  private static final String SPOTIFY_API_V1_ME_TOP_TRACKS = "/v1/me/top/tracks";

  private static final String SPOTIFY_API_V1_RECOMMENDATIONS = "/v1/recommendations";

  private static final String SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID = "/v1/playlists/%s";

  private static final String SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS = "/v1/users/%s/playlists";

  private static final String SPOTIFY_V1_PLAYLISTS_PLAYLIST_ID_TRACKS = "/v1/playlists/%s/tracks";

  @Autowired private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Autowired private GetSpotifyUserTopTrackRequestConfig userTopTrackRequestConfig;

  @Autowired private GetSpotifyRecommendationRequestConfig recommendationRequestConfig;

  @Autowired AppUserService appUserService;

  @Autowired SuddenrunUserRepository userRepository;

  @Autowired SuddenrunPlaylistRepository playlistRepository;

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

    int userTopTracksLimit = userTopTrackRequestConfig.getLimit();
    List<SpotifyTrackDto> spotifyUserTopTracks =
        SpotifyClientHelper.getTrackDtos(userTopTracksLimit);
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(spotifyUserTopTracks);

    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo(SPOTIFY_API_V1_ME_TOP_TRACKS))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(getUserTopTracksResponse), HttpStatus.OK.value())));

    List<SpotifyTrackDto> spotifyPlaylistTracks = new ArrayList<>();
    int recommendationsLimit = recommendationRequestConfig.getLimit();
    spotifyUserTopTracks.forEach(
        track -> {
          List<SpotifyTrackDto> recommendations =
              SpotifyClientHelper.getTrackDtos(recommendationsLimit);
          spotifyPlaylistTracks.addAll(recommendations);
          GetRecommendationsResponse getRecommendationsResponse =
              SpotifyClientHelper.createGetRecommendationsResponse(recommendations);
          WireMock.stubFor(
              WireMock.get(
                      WireMock.urlEqualTo(
                          SPOTIFY_API_V1_RECOMMENDATIONS
                              + "?"
                              + "min_tempo=120&seed_tracks="
                              + track.id()
                              + "&limit="
                              + recommendationsLimit
                              + "&min_energy=0.65&max_tempo=140"))
                  .willReturn(
                      WireMock.jsonResponse(
                          JsonHelper.objectToJson(getRecommendationsResponse),
                          HttpStatus.OK.value())));
        });

    AddPlaylistItemsResponse addPlaylistItemsResponse =
        SpotifyClientHelper.createAddPlaylistItemsResponse();
    WireMock.stubFor(
        WireMock.post(
                WireMock.urlPathEqualTo(
                    SPOTIFY_V1_PLAYLISTS_PLAYLIST_ID_TRACKS.formatted(playlistId)))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(addPlaylistItemsResponse),
                    HttpStatus.CREATED.value())));

    spotifyPlaylistDto =
        SpotifyClientHelper.getPlaylistDto(
            spotifyUserDto,
            playlistId,
            playlistName,
            playlistDescription,
            playlistIsPublic,
            spotifyPlaylistTracks);

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID.formatted(playlistId)))
            .inScenario("Get created playlist")
            .whenScenarioStateIs("Got tracks")
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyPlaylistDto), HttpStatus.OK.value())));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_PLAYLISTS)
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

    AppPlaylist appPlaylist =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), AppPlaylist.class);

    assertThat(appPlaylist).isNotNull();
    assertThat(appPlaylist.getId()).isEqualTo(playlistId);
    assertThat(appPlaylist.getSnapshotId()).isEqualTo(spotifyPlaylistDto.snapshotId());
    assertThat(appPlaylist.getTracks())
        .containsAll(
            spotifyPlaylistTracks.stream()
                .map(
                    spotifyTrack ->
                        SuddenrunTrack.builder()
                            .id(spotifyTrack.id())
                            .name(spotifyTrack.name())
                            .build())
                .toList());
  }

  @Test
  void itShouldReturnHttp409AndErrorMessageIfPlaylistAlreadyExists()
      throws Exception {
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

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(spotifyUserDto), HttpStatus.OK.value())));

    String playlistId = spotifyPlaylistDto.id();

    GetUserPlaylistsResponse getUserPlaylistsResponse =
        SpotifyClientHelper.createGetUserPlaylistResponse(List.of(spotifyPlaylistDto));

    WireMock.stubFor(
        WireMock.get(
                WireMock.urlEqualTo(
                    SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(spotifyUserId)))
            .willReturn(
                WireMock.jsonResponse(
                    JsonHelper.objectToJson(getUserPlaylistsResponse),
                    HttpStatus.OK.value())));

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
            MockMvcRequestBuilders.post(SUDDENRUN_API_V1_PLAYLISTS)
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
  void itShouldReturnAuthenticationErrorWhenSpotifyAuthorizationFailed()
          throws Exception {
    // Given
    WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_ME))
                    .willReturn(
                            WireMock.jsonResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value())));

    // When
    ResultActions createPlaylistResultActions =
            mockMvc.perform(
                    MockMvcRequestBuilders.post(SUDDENRUN_API_V1_PLAYLISTS)
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
}
