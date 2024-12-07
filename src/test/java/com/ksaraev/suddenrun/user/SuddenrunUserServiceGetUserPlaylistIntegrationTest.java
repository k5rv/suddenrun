package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.ksaraev.spotify.client.dto.GetUserPlaylistsResponse;
import com.ksaraev.spotify.client.dto.SpotifyPlaylistDto;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.suddenrun.exception.SuddenrunError;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylistRepository;
import com.ksaraev.suddenrun.playlist.SuddenrunUserDoesNotHaveAnyPlaylistsException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
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
class SuddenrunUserServiceGetUserPlaylistIntegrationTest {

  private static final String SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS =
      "/api/v1/users/%s/playlists";

  private static final String SPOTIFY_API_V1_PLAYLISTS_PLAYLIST_ID = "/v1/playlists/%s";

  private static final String SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS = "/v1/users/%s/playlists";

  @Autowired AppUserService appUserService;

  @Autowired SuddenrunUserRepository userRepository;

  @Autowired SuddenrunPlaylistRepository playlistRepository;

  @Autowired private SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  @Autowired private MockMvc mockMvc;

  @Test
  void itShouldGetPlaylist() throws Exception {
    // Given
    SpotifyUserProfileDto spotifyUserDto = SpotifyClientHelper.getUserProfileDto();
    String userId = spotifyUserDto.id();
    String userName = spotifyUserDto.displayName();

    SuddenrunUser appUser = SuddenrunUser.builder().id(userId).name(userName).build();

    SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
    String playlistName = spotifyPlaylistDetails.getName();
    String playlistDescription = spotifyPlaylistDetails.getDescription();
    boolean playlistIsPublic = spotifyPlaylistDetails.getIsPublic();

    List<SpotifyTrackDto> spotifyPlaylistTracks = SpotifyClientHelper.getTrackDtos(10);

    String playlistId = SpotifyResourceHelper.getRandomId();

    SpotifyPlaylistDto spotifyPlaylistDto =
        SpotifyClientHelper.getPlaylistDto(
            spotifyUserDto,
            playlistId,
            playlistName,
            playlistDescription,
            playlistIsPublic,
            spotifyPlaylistTracks);

    SuddenrunPlaylist playlist = SuddenrunHelper.getSuddenrunPlaylist(appUser);
    playlist.setSnapshotId(spotifyPlaylistDto.snapshotId());
    playlist.setId(spotifyPlaylistDto.id());
    List<AppTrack> appPlaylistTracks =
        spotifyPlaylistTracks.stream()
            .map(
                spotifyTrack ->
                    SuddenrunTrack.builder()
                        .id(spotifyTrack.id())
                        .name(spotifyTrack.name())
                        .build())
            .map(AppTrack.class::cast)
            .toList();
    playlist.setTracks(appPlaylistTracks);
    appUser.setPlaylists(List.of(playlist));
    userRepository.save(appUser);
    playlistRepository.save(playlist);

    GetUserPlaylistsResponse getUserPlaylistsResponse =
        SpotifyClientHelper.createGetUserPlaylistResponse(List.of(spotifyPlaylistDto));

    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(SPOTIFY_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId)))
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
    ResultActions getUserPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        getUserPlaylistResultActions.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    GetUserPlaylistResponse getUserPlaylistResponse =
        JsonHelper.jsonToObject(
            result.getResponse().getContentAsString(), GetUserPlaylistResponse.class);

    assertThat(getUserPlaylistResponse).isNotNull();
    assertThat(getUserPlaylistResponse.id()).isEqualTo(playlistId);
  }

  @Test
  void itShouldReturnHttp404IfPlaylistNotFound() throws Exception {
    // Given
    SuddenrunUser appUser = SuddenrunHelper.getUser();
    String userId = appUser.getId();
    userRepository.save(appUser);
    SuddenrunUserDoesNotHaveAnyPlaylistsException exception =
        new SuddenrunUserDoesNotHaveAnyPlaylistsException(userId);

    // When
    ResultActions getUserPlaylistResultActions =
        mockMvc.perform(
            MockMvcRequestBuilders.get(SUDDENRUN_API_V1_USERS_USER_ID_PLAYLISTS.formatted(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

    // Then
    MvcResult result =
        getUserPlaylistResultActions
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

    SuddenrunError error =
        JsonHelper.jsonToObject(result.getResponse().getContentAsString(), SuddenrunError.class);

    assertThat(error).isNotNull();
    assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(error.message()).isEqualTo(exception.getMessage());
  }
}
