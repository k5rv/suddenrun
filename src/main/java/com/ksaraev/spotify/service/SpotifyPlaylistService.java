package com.ksaraev.spotify.service;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.UpdateSpotifyPlaylistItemsRequestConfig;
import com.ksaraev.spotify.exception.*;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistMapper;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class SpotifyPlaylistService implements SpotifyPlaylistItemService {

  private final SpotifyClient client;

  private final UpdateSpotifyPlaylistItemsRequestConfig requestConfig;

  private final SpotifyPlaylistMapper mapper;

  @Override
  public List<SpotifyPlaylistItem> getUserPlaylists(
      @Valid @NotNull SpotifyUserProfileItem userProfileItem) {
    String userId = userProfileItem.getId();
    try {
      GetUserPlaylistsRequest request = GetUserPlaylistsRequest.builder().build();
      GetUserPlaylistsResponse response = client.getPlaylists(userId, request);

      List<SpotifyPlaylistDto> playlistDtos =
          response.playlistDtos().stream()
              .flatMap(Stream::ofNullable)
              .filter(Objects::nonNull)
              .toList();

      return playlistDtos.isEmpty() ? List.of() : mapper.mapDtosToModels(playlistDtos);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyUserPlaylistsException(userId, e);
    }
  }

  @Override
  public SpotifyPlaylistItem getPlaylist(@NotNull String playlistId) {
    try {
      SpotifyPlaylistDto playlistDto = client.getPlaylist(playlistId);
      return mapper.mapToModel(playlistDto);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new GetSpotifyPlaylistException(playlistId, e);
    }
  }

  @Override
  public SpotifyPlaylistItem createPlaylist(
      @NotNull @Valid SpotifyUserProfileItem userProfileItem,
      @NotNull @Valid SpotifyPlaylistItemDetails playlistItemDetails) {
    String userId = userProfileItem.getId();
    try {
      SpotifyPlaylistDetailsDto playlistDetailsDto =
          mapper.mapToPlaylistDetailsDto(playlistItemDetails);
      SpotifyPlaylistDto playlistDto = client.createPlaylist(userId, playlistDetailsDto);
      return mapper.mapToModel(playlistDto);
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new CreateSpotifyPlaylistException(userId, e);
    }
  }

  @Override
  public String addTracks(
      @Valid @NotNull SpotifyPlaylistItem spotifyPlaylistItem,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems) {
    String playlistId = spotifyPlaylistItem.getId();
    try {
      List<URI> trackItemUris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
      AddPlaylistItemsRequest request =
          AddPlaylistItemsRequest.builder()
              .uris(trackItemUris)
              .position(requestConfig.getPosition())
              .build();
      AddPlaylistItemsResponse response = client.addPlaylistItems(playlistId, request);
      return response.snapshotId();
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new AddSpotifyPlaylistTracksExceptions(playlistId, e);
    }
  }

  @Override
  public String removeTracks(
      @Valid @NotNull SpotifyPlaylistItem spotifyPlaylistItem,
      @Valid @Size(min = 1, max = 100) List<SpotifyTrackItem> trackItems) {
    String playlistId = spotifyPlaylistItem.getId();
    String snapshotId = spotifyPlaylistItem.getSnapshotId();
    try {
      List<URI> trackItemUris = trackItems.stream().map(SpotifyTrackItem::getUri).toList();
      RemovePlaylistItemsRequest request =
          RemovePlaylistItemsRequest.builder().snapshotId(snapshotId).uris(trackItemUris).build();
      RemovePlaylistItemsResponse response = client.removePlaylistItems(playlistId, request);
      return response.snapshotId();
    } catch (SpotifyUnauthorizedException e) {
      throw new SpotifyAccessTokenException(e);
    } catch (RuntimeException e) {
      throw new RemoveSpotifyPlaylistTracksException(playlistId, e);
    }
  }
}
