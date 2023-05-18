package com.suddenrun.app.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.suddenrun.app.playlist.PlaylistController;
import com.suddenrun.spotify.client.dto.GetRecommendationsResponse;
import com.suddenrun.spotify.client.dto.GetUserTopTracksResponse;
import com.suddenrun.spotify.client.dto.SpotifyPlaylistDto;
import com.suddenrun.spotify.client.dto.SpotifyTrackDto;
import com.suddenrun.spotify.client.dto.SpotifyUserProfileDto;
import com.suddenrun.spotify.client.dto.UpdateItemsResponse;
import com.suddenrun.utils.helpers.JsonHelper;
import com.suddenrun.utils.helpers.SpotifyClientHelper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class PlaylistControllerIntegrationTest {

  @Autowired
  PlaylistController underTest;

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldCreatePlaylistSuccessfully() throws Exception {
    // Given
    SpotifyUserProfileDto userProfileItem = SpotifyClientHelper.getUserProfileDto();
    String userId = userProfileItem.id();

    stubFor(get(urlEqualTo("/v1/me")).willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(userProfileItem), 200)));

    List<SpotifyTrackDto> topTrackItems = SpotifyClientHelper.getTrackDtos(60);
    GetUserTopTracksResponse getUserTopTracksResponse =
        SpotifyClientHelper.createGetUserTopTracksResponse(topTrackItems);

    stubFor(
        get(urlPathEqualTo("/v1/me/top/tracks"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(getUserTopTracksResponse), 200)));

    List<SpotifyTrackDto> musicRecommendations = SpotifyClientHelper.getTrackDtos(60);
    GetRecommendationsResponse getRecommendationsResponse =
        SpotifyClientHelper.createGetRecommendationsResponse(musicRecommendations);

    stubFor(
        get(urlPathEqualTo("/v1/recommendations"))
            .inScenario("recommendations")
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(getRecommendationsResponse), 200)));

    SpotifyPlaylistDto emptyPlaylist = SpotifyClientHelper.getPlaylistDto(userId);
    String playlistId = emptyPlaylist.id();

    stubFor(
        post(urlEqualTo("/v1/users/" + userId + "/playlists"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(emptyPlaylist), 200)));

    UpdateItemsResponse updateItemsResponse = SpotifyClientHelper.createUpdateItemsResponse();

    stubFor(
        post(urlEqualTo("/v1/playlists/" + playlistId + "/tracks"))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(updateItemsResponse), 200)));

    SpotifyPlaylistDto playlist = SpotifyClientHelper.updatePlaylistDto(emptyPlaylist, musicRecommendations);

    stubFor(
        get(urlEqualTo("/v1/playlists/" + playlistId))
            .willReturn(WireMock.jsonResponse(JsonHelper.objectToJson(playlist), 200)));

    // When
    ResultActions createPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

    // Then
    createPlaylistResultActions
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(playlist.id()))
        .andExpect(jsonPath("$.snapshotId").value(playlist.snapshotId()))
        .andDo(print());
  }
}