package com.ksaraev.suddenrun.playlist;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItem;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.service.SpotifyPlaylistItemService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import com.ksaraev.suddenrun.track.AppTrack;
import com.ksaraev.suddenrun.track.AppTrackMapper;
import com.ksaraev.suddenrun.user.AppUser;
import com.ksaraev.suddenrun.user.AppUserMapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistService implements AppPlaylistService {

  private static final String PLAYLIST_WITH_ID = "Playlist with id";

  private static final String AND_SNAPSHOT_ID = "] and snapshot id [";

  private final SuddenrunPlaylistRepository repository;

  private final AppPlaylistSynchronizationService playlistSynchronizationService;

  private final SpotifyPlaylistItemService spotifyPlaylistService;

  private final SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  private final AppPlaylistMapper playlistMapper;

  private final AppTrackMapper trackMapper;

  private final AppUserMapper userMapper;

  @Override
  public AppPlaylist createPlaylist(@NotNull AppUser appUser) {
    String appUserId = appUser.getId();
    try {
      log.info("Creating playlist for user id with [" + appUserId + "]");
      SpotifyUserProfileItem spotifyUserProfile = userMapper.mapToItem(appUser);
      SpotifyPlaylistItemDetails spotifyPlaylistDetails = spotifyPlaylistConfig.getDetails();
      SpotifyPlaylistItem spotifyPlaylist =
          spotifyPlaylistService.createPlaylist(spotifyUserProfile, spotifyPlaylistDetails);
      String spotifyPlaylistId = spotifyPlaylist.getId();
      String spotifySnapshotId = spotifyPlaylist.getSnapshotId();
      log.info(
          "Created "
              + PLAYLIST_WITH_ID
              + " ["
              + spotifyPlaylistId
              + AND_SNAPSHOT_ID
              + spotifySnapshotId
              + "] in Spotify");

      spotifyPlaylist = spotifyPlaylistService.getPlaylist(spotifyPlaylistId);
      AppPlaylist appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      String appPlaylistId = appPlaylist.getId();
      String appPlaylistSnapshotId = appPlaylist.getSnapshotId();
      log.info(
          "Saved playlist with id ["
              + appPlaylistId
              + AND_SNAPSHOT_ID
              + appPlaylistSnapshotId
              + "] "
              + "for user with id ["
              + appUserId
              + "] in Suddenrun");
      return appPlaylist;
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new CreateSuddenrunPlaylistException(appUserId, e);
    }
  }

  @Override
  public Optional<AppPlaylist> getPlaylist(@NotNull AppUser appUser) {
    String userId = appUser.getId();
    try {
      log.info("Getting playlist for user with id [" + userId + "]");
      Optional<SuddenrunPlaylist> optionalSuddenrunPlaylist = repository.findByUserId(userId);
      boolean suddenrunPlaylistExists = optionalSuddenrunPlaylist.isPresent();
      if (!suddenrunPlaylistExists) {
        log.info(
            "User with id ["
                + userId
                + "] doesn't have any playlists in Suddenrun. Returning empty result.");
        return Optional.empty();
      }

      SpotifyUserProfileItem spotifyUserProfile = userMapper.mapToItem(appUser);
      List<SpotifyPlaylistItem> spotifyUserPlaylists =
          spotifyPlaylistService.getUserPlaylists(spotifyUserProfile);
      AppPlaylist targetPlaylist = optionalSuddenrunPlaylist.get();
      String playlistId = targetPlaylist.getId();
      Optional<SpotifyPlaylistItem> optionalSpotifyPlaylist =
          spotifyUserPlaylists.stream()
              .filter(spotifyPlaylist -> spotifyPlaylist.getId().equals(playlistId))
              .findFirst();
      boolean spotifyPlaylistExists = optionalSpotifyPlaylist.isPresent();
      if (!spotifyPlaylistExists) {
        log.info(
            PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + "] not found in Spotify. Deleting Suddenrun playlist and returning empty result.");
        appUser.removePlaylist(targetPlaylist);
        repository.deleteById(playlistId);
        return Optional.empty();
      }

      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(playlistId);
      String spotifyPlaylistSnapshotId = spotifyPlaylist.getSnapshotId();
      String appPlaylistSnapshotId = targetPlaylist.getSnapshotId();
      boolean snapshotsAreIdentical = spotifyPlaylistSnapshotId.equals(appPlaylistSnapshotId);
      if (snapshotsAreIdentical) {
        log.info(
            PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + "] has the exact same snapshot id ["
                + appPlaylistSnapshotId
                + "] in Suddenrun and Spotify. Returning playlist.");
        return Optional.of(targetPlaylist);
      }

      log.info(
          PLAYLIST_WITH_ID
              + " ["
              + playlistId
              + "] found in Suddenrun and Spotify with the different snapshot ids ["
              + appPlaylistSnapshotId
              + "] and ["
              + spotifyPlaylistSnapshotId
              + "] respectively. Updating Suddenrun playlist from Spotify.");

      AppPlaylist sourcePlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      AppPlaylist appPlaylist =
          playlistSynchronizationService.updatePlaylist(sourcePlaylist, targetPlaylist);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      log.info(
          "Updated playlist with id ["
              + playlistId
              + AND_SNAPSHOT_ID
              + appPlaylist.getSnapshotId()
              + "] from Spotify. Returning playlist.");
      return Optional.of(targetPlaylist);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunPlaylistException(userId, e);
    }
  }

  @Override
  public AppPlaylist addTracks(@NotNull AppPlaylist appPlaylist, List<AppTrack> appTracks) {
    String playlistId = appPlaylist.getId();
    try {
      log.info(
          "Adding ["
              + appTracks.size()
              + "] tracks to "
              + PLAYLIST_WITH_ID
              + " ["
              + playlistId
              + "]");

      boolean playlistExists = repository.existsById(playlistId);

      if (!playlistExists) {
        log.error(PLAYLIST_WITH_ID + playlistId + "] doesn't exist in Suddenrun");
        throw new SuddenrunPlaylistDoesNotExistException(playlistId);
      }

      SpotifyPlaylistItem spotifyPlaylist = spotifyPlaylistService.getPlaylist(playlistId);
      List<SpotifyTrackItem> spotifyTracks = spotifyPlaylist.getTracks().stream().toList();

      List<AppTrack> playlistExclusions = appPlaylist.getExclusions();
      if (!playlistExclusions.isEmpty())
        log.info(
            "Found ["
                + playlistExclusions.size()
                + "] tracks previously excluded outside of the Suddenrun from Spotify playlist with id ["
                + playlistId
                + "]");

      List<SpotifyTrackItem> spotifyTracksToAdd =
          playlistSynchronizationService.getTracksToAdd(
              appTracks, spotifyTracks, playlistExclusions);

      List<AppTrack> playlistInclusions = appPlaylist.getInclusions();
      if (!playlistInclusions.isEmpty()) {
        log.info(
            "Found ["
                + playlistInclusions.size()
                + "] tracks previously included outside of the Suddenrun to Spotify playlist with id ["
                + playlistId
                + "]");
      }

      List<SpotifyTrackItem> spotifyTracksToRemove =
          playlistSynchronizationService.getTracksToRemove(
              appTracks, spotifyTracks, playlistInclusions);

      if (!spotifyTracksToRemove.isEmpty()) {
        String snapshotId = spotifyPlaylistService.removeTracks(playlistId, spotifyTracksToRemove);
        log.info(
            "Removed ["
                + spotifyTracksToRemove.size()
                + "] tracks from "
                + PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + AND_SNAPSHOT_ID
                + snapshotId
                + "]");
      }

      if (!spotifyTracksToAdd.isEmpty()) {
        String snapshotId = spotifyPlaylistService.addTracks(playlistId, spotifyTracksToAdd);
        log.info(
            "Added ["
                + spotifyTracksToAdd.size()
                + "] tracks to "
                + PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + AND_SNAPSHOT_ID
                + snapshotId
                + "]");
      }

      spotifyPlaylist = spotifyPlaylistService.getPlaylist(playlistId);
      appPlaylist = playlistMapper.mapToEntity(spotifyPlaylist);
      appPlaylist.setInclusions(playlistInclusions);
      appPlaylist.setExclusions(playlistExclusions);
      appPlaylist = repository.save((SuddenrunPlaylist) appPlaylist);
      log.info(
          "Saved playlist with id ["
              + playlistId
              + AND_SNAPSHOT_ID
              + appPlaylist.getSnapshotId()
              + "] in Suddenrun");
      return appPlaylist;
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new AddSuddenrunPlaylistTracksException(playlistId, e);
    }
  }
}
