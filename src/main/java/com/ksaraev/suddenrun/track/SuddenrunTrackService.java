package com.ksaraev.suddenrun.track;

import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.exception.SpotifyServiceException;
import com.ksaraev.spotify.model.playlist.SpotifyPlaylistItemConfig;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.trackfeatures.SpotifyTrackItemFeatures;
import com.ksaraev.spotify.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotify.service.SpotifyUserTopTrackItemsService;
import com.ksaraev.suddenrun.exception.SuddenrunAuthenticationException;
import com.ksaraev.suddenrun.exception.SuddenrunSpotifyInteractionException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunTrackService implements AppTrackService {

  private final SpotifyPlaylistItemConfig spotifyPlaylistConfig;

  private final AppTrackMapper mapper;

  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;

  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public List<AppTrack> getTracks() {
    try {
      int playlistSizeLimit = spotifyPlaylistConfig.getSize();
      SpotifyTrackItemFeatures features = spotifyPlaylistConfig.getMusicFeatures();
      List<SpotifyTrackItem> userTopTracks = getUserTopTracks();
      int topTracksSize = userTopTracks.size();
      log.info("Found [" + topTracksSize + "] top tracks in Spotify");
      List<SpotifyTrackItem> recommendations =
          getRecommendations(userTopTracks, features, playlistSizeLimit);
      int recommendationsSize = recommendations.size();
      log.info("Found [" + recommendationsSize + "] recommended tracks in Spotify");
      return mapper.mapToEntities(recommendations);
    } catch (SpotifyAccessTokenException e) {
      throw new SuddenrunAuthenticationException(e);
    } catch (SpotifyServiceException e) {
      throw new SuddenrunSpotifyInteractionException(e);
    } catch (RuntimeException e) {
      throw new GetSuddenrunTracksException(e);
    }
  }

  private List<SpotifyTrackItem> getRecommendations(
      List<SpotifyTrackItem> trackItems,
      SpotifyTrackItemFeatures features,
      Integer recommendationsSize) {
    AtomicInteger limit = new AtomicInteger();
    return trackItems.stream()
        .map(
            userTopTrack -> {
              List<SpotifyTrackItem> recommendations = new ArrayList<>();
              if (limit.get() < recommendationsSize) {
                recommendations =
                    spotifyRecommendationsService.getRecommendations(
                        List.of(userTopTrack), features);
                limit.set(limit.get() + recommendations.size());
              }
              return recommendations;
            })
        .flatMap(List::stream)
        .distinct()
        .sorted(Comparator.comparingInt(SpotifyTrackItem::getPopularity).reversed())
        .limit(recommendationsSize)
        .toList();
  }

  private List<SpotifyTrackItem> getUserTopTracks() {
    return spotifyTopTracksService.getUserTopTracks();
  }
}
