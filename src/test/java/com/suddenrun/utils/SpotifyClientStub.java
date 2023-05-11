package com.suddenrun.utils;

import com.suddenrun.client.SpotifyClient;
import com.suddenrun.client.dto.*;

public class SpotifyClientStub implements SpotifyClient {

  @Override
  public SpotifyUserProfileDto getCurrentUserProfile() {
    return null;
  }

  @Override
  public GetUserTopTracksResponse getUserTopTracks(GetUserTopTracksRequest request) {
    return null;
  }

  @Override
  public GetRecommendationsResponse getRecommendations(GetRecommendationsRequest request) {
    return null;
  }

  @Override
  public GetUserPlaylistsResponse getPlaylists(String userId, GetUserPlaylistsRequest request) {
    return null;
  }

  @Override
  public SpotifyPlaylistDto createPlaylist(
      String userId, SpotifyPlaylistDetailsDto playlistItemDetails) {
    return null;
  }

  @Override
  public SpotifyPlaylistDto getPlaylist(String playlistId) {
    return null;
  }

  @Override
  public UpdateUpdateItemsResponse addPlaylistItems(String playlistId, UpdatePlaylistItemsRequest request) {
    return null;
  }

  @Override
  public RemovePlaylistItemsResponse removePlaylistItems(String playlistId, RemovePlaylistItemsRequest request) {
    return null;
  }
}