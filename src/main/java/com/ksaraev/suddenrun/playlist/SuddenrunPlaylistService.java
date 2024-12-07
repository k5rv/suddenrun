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
import jakarta.validation.constraints.NotNull;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunPlaylistService implements AppPlaylistService {

  private static final String PLAYLIST_WITH_ID = "Playlist with id";

  private static final String AND_SNAPSHOT_ID = "] and snapshot id [";

  private final SuddenrunPlaylistRepository repository;

  private final AppPlaylistSynchronizationService synchronizationService;

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
      log.info("Getting Suddenrun playlist for user with id [" + userId + "]");
      Optional<SuddenrunPlaylist> optionalAppPlaylist = repository.findByUserId(userId);
      if (optionalAppPlaylist.isEmpty()) {
        log.info(
            "Suddenrun user with id ["
                + userId
                + "] doesn't have any playlists. Returning empty result.");
        return Optional.empty();
      }
      AppPlaylist appPlaylist = optionalAppPlaylist.get();
      return getPlaylist(appPlaylist);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunUserPlaylistException(userId, e);
    }
  }

  @Override
  public Optional<AppPlaylist> getPlaylist(@NotNull String playlistId) {
    try {
      log.info("Getting Suddenrun playlist with id [" + playlistId + "]");
      Optional<SuddenrunPlaylist> optionalAppPlaylist = repository.findById(playlistId);
      if (optionalAppPlaylist.isEmpty()) {
        log.info(
            "Suddenrun playlist with id ["
                + playlistId
                + "] doesn't exist. Returning empty result.");
        return Optional.empty();
      }

      AppPlaylist appPlaylist = optionalAppPlaylist.get();
      return getPlaylist(appPlaylist);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunPlaylistException(playlistId, e);
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

      Optional<SuddenrunPlaylist> optionalAppPlaylist = repository.findById(playlistId);
      if (optionalAppPlaylist.isEmpty()) {
        log.error(PLAYLIST_WITH_ID + playlistId + "] doesn't exist in Suddenrun");
        throw new SuddenrunPlaylistDoesNotExistException(playlistId);
      }
      AppPlaylist targetAppPlaylist = optionalAppPlaylist.get();
      SpotifyPlaylistItem spotifyPlaylist = playlistMapper.mapToDto(targetAppPlaylist);
      List<AppTrack> targetAppTracks = new ArrayList<>(targetAppPlaylist.getTracks());

      List<AppTrack> appTrackRemovals =
          synchronizationService.findPlaylistNoneMatchTracks(targetAppPlaylist, appTracks);
      if (!appTrackRemovals.isEmpty()) {
        targetAppTracks.removeAll(appTrackRemovals);
        List<SpotifyTrackItem> spotifyTrackRemovals = trackMapper.mapToDtos(appTrackRemovals);
        String removalSnapshotId =
            spotifyPlaylistService.removeTracks(spotifyPlaylist, spotifyTrackRemovals);
        log.info(
            "Removed ["
                + appTrackRemovals.size()
                + "] tracks from "
                + PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + AND_SNAPSHOT_ID
                + removalSnapshotId
                + "]");
      }

      List<AppTrack> appTrackAdditions =
          synchronizationService.findTracksNoneMatchPlaylist(targetAppPlaylist, appTracks);
      if (!appTrackAdditions.isEmpty()) {
        targetAppTracks.addAll(appTrackAdditions);
        List<SpotifyTrackItem> spotifyTrackAdditions = trackMapper.mapToDtos(appTrackAdditions);
        String snapshotId =
            spotifyPlaylistService.addTracks(spotifyPlaylist, spotifyTrackAdditions);
        log.info(
            "Added ["
                + appTrackAdditions.size()
                + "] tracks to "
                + PLAYLIST_WITH_ID
                + " ["
                + playlistId
                + AND_SNAPSHOT_ID
                + snapshotId
                + "]");
      }

      targetAppPlaylist.setTracks(targetAppTracks);
      SpotifyPlaylistItem sourceSpotifyPlaylist = spotifyPlaylistService.getPlaylist(playlistId);
      AppPlaylist sourceAppPlaylist = playlistMapper.mapToEntity(sourceSpotifyPlaylist);
      targetAppPlaylist =
          synchronizationService.updateFromSource(targetAppPlaylist, sourceAppPlaylist);
      targetAppPlaylist = repository.save((SuddenrunPlaylist) targetAppPlaylist);
      log.info(
          "Saved playlist with id ["
              + playlistId
              + AND_SNAPSHOT_ID
              + targetAppPlaylist.getSnapshotId()
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

  private Optional<AppPlaylist> getPlaylist(AppPlaylist targetAppPlaylist) {

    String playlistId = targetAppPlaylist.getId();
    AppUser appUser = targetAppPlaylist.getUser();
    SpotifyUserProfileItem spotifyUserProfile = userMapper.mapToItem(appUser);
    List<SpotifyPlaylistItem> spotifyUserPlaylists =
        spotifyPlaylistService.getUserPlaylists(spotifyUserProfile);

    Optional<SpotifyPlaylistItem> optionalSpotifyPlaylist =
        spotifyUserPlaylists.stream()
            .filter(spotifyPlaylist -> spotifyPlaylist.getId().equals(playlistId))
            .findFirst();

    if (optionalSpotifyPlaylist.isEmpty()) {
      log.info(
          PLAYLIST_WITH_ID
              + " ["
              + playlistId
              + "] not found in Spotify. Deleting Suddenrun playlist and returning empty result.");
      appUser.removePlaylist(targetAppPlaylist);
      repository.deleteById(playlistId);
      return Optional.empty();
    }

    SpotifyPlaylistItem sourceSpotifyPlaylist = spotifyPlaylistService.getPlaylist(playlistId);
    String sourceSpotifyPlaylistSnapshotId = sourceSpotifyPlaylist.getSnapshotId();
    String targetAppPlaylistSnapshotId = targetAppPlaylist.getSnapshotId();
    boolean snapshotsAreIdentical =
        sourceSpotifyPlaylistSnapshotId.equals(targetAppPlaylistSnapshotId);

    if (snapshotsAreIdentical) {
      log.info(
          PLAYLIST_WITH_ID
              + " ["
              + playlistId
              + "] has the exact same snapshot id ["
              + targetAppPlaylistSnapshotId
              + "] in Suddenrun and Spotify. Returning playlist.");
      return Optional.of(targetAppPlaylist);
    }

    log.info(
        PLAYLIST_WITH_ID
            + " ["
            + playlistId
            + "] found in Suddenrun and Spotify with the different snapshot ids ["
            + targetAppPlaylistSnapshotId
            + "] and ["
            + sourceSpotifyPlaylistSnapshotId
            + "] respectively. Updating Suddenrun playlist from Spotify.");

    AppPlaylist sourceAppPlaylist = playlistMapper.mapToEntity(sourceSpotifyPlaylist);
    targetAppPlaylist =
        synchronizationService.updateFromSource(targetAppPlaylist, sourceAppPlaylist);
    targetAppPlaylist = repository.save((SuddenrunPlaylist) targetAppPlaylist);
    log.info(
        "Updated playlist with id ["
            + playlistId
            + AND_SNAPSHOT_ID
            + targetAppPlaylist.getSnapshotId()
            + "] from Spotify. Returning playlist.");
    return Optional.of(targetAppPlaylist);
  }
}
