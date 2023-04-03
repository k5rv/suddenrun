package com.ksaraev.spotifyrun.app.track;

import com.ksaraev.spotifyrun.app.playlist.AppPlaylistConfig;
import com.ksaraev.spotifyrun.model.spotify.track.SpotifyTrackItem;
import com.ksaraev.spotifyrun.service.SpotifyRecommendationItemsService;
import com.ksaraev.spotifyrun.service.SpotifyUserTopTrackItemsService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackService implements AppTrackService {

  private final AppPlaylistConfig playlistConfig;

  private final AppTrackMapper appTrackMapper;

  private final SpotifyUserTopTrackItemsService spotifyTopTracksService;

  private final SpotifyRecommendationItemsService spotifyRecommendationsService;

  @Override
  public List<AppTrack> getTracks() {
    List<SpotifyTrackItem> userTopTracks = spotifyTopTracksService.getUserTopTracks();
    List<SpotifyTrackItem> recommendations =
        userTopTracks.stream()
            .map(
                userTopTrackItem ->
                    spotifyRecommendationsService.getRecommendations(
                        List.of(userTopTrackItem), playlistConfig.getMusicFeatures()))
            .flatMap(List::stream)
            .sorted(Comparator.comparingInt(SpotifyTrackItem::getPopularity).reversed())
            .distinct()
            .limit(playlistConfig.getSize())
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Collections.shuffle(list);
                      return list;
                    }));
    return recommendations.stream()
        .filter(Objects::nonNull)
        .map(appTrackMapper::mapToEntity)
        .toList();
  }
}