package com.ksaraev.spotify.model.track;

import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.model.artist.SpotifyArtist;
import com.ksaraev.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotify.model.artist.SpotifyArtistMapperImpl;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpotifyTrackMapperImpl.class, SpotifyArtistMapperImpl.class})
class SpotifyTrackMapperTest {

  @Autowired
  SpotifyTrackMapper underTest;

  @Test
  void itShouldMapSpotifyTrackItemToTrack() throws Exception {
    // Given
    SpotifyArtistItem artist = SpotifyServiceHelper.getArtist();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem track = SpotifyServiceHelper.getTrack();
    track.setArtists(artists);

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder()
            .id(artist.getId())
            .name(artist.getName())
            .uri(artist.getUri())
            .genres(artist.getGenres())
            .build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(track.getId())
            .name(track.getName())
            .uri(track.getUri())
            .artistDtos(artistItems)
            .albumDto(SpotifyAlbumDto.builder().build())
            .popularity(track.getPopularity())
            .href(new URL("https://api.spotify.com/v1/track/2222030405AaBbCcDdEeFf"))
            .track(true)
            .episode(false)
            .previewUrl(new URL("https://p.scdn.co/mp3-preview/2?cid=1"))
            .isLocal(false)
            .isPlayable(true)
            .durationMs(20000)
            .trackNumber(1)
            .discNumber(1)
            .sourceAlbumDto(SpotifyAlbumDto.builder().build())
            .availableMarkets(List.of("US"))
            .externalUrls(
                Map.of("spotify", "https://open.spotify.com/track/2222030405AaBbCcDdEeFf"))
            .externalIds(Map.of("spotify", "https://open.spotify.com/track/2222030405AaBbCcDdEeFf"))
            .type("track")
            .explicit(false)
            .build();

    // Then
    Assertions.assertThat(underTest.mapToModel(trackItem))
        .isEqualTo(track)
        .hasOnlyFields("id", "name", "uri", "popularity", "artists");
  }

  @Test
  void itShouldMapPlaylistItemsToTracks() {
    // Given
    String artistID = "0102030405AaBbCcDdEeFf";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0102030405AaBbCcDdEeFf");

    String trackOneId = "1222030405AaBbCcDdEeFf";
    String trackOneName = "track 1 name";
    URI trackOneUri = URI.create("spotify:track:1222030405AaBbCcDdEeFf");
    Integer trackOnePopularity = 1;

    String trackTwoId = "2222030405AaBbCcDdEeFf";
    String trackTwoName = "track 2 name";
    URI trackTwoUri = URI.create("spotify:track:2222030405AaBbCcDdEeFf");
    Integer trackTwoPopularity = 2;

    SpotifyArtistItem artist = SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem trackOne =
        SpotifyTrack.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrackItem trackTwo =
        SpotifyTrack.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id("12122604372")
            .displayName("name")
            .uri(URI.create("spotify:user:12122604372"))
            .email("email@mail.com")
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackDtoOne =
        SpotifyTrackDto.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistDtos(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackDto trackDtoTwo =
        SpotifyTrackDto.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistDtos(artistItems)
            .build();

    String addedAt = "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrackOne =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDtoOne)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    SpotifyPlaylistTrackDto playlistItemTrackTwo =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDtoTwo)
            .addedBy(userProfileItem)
            .addedAt(addedAt)
            .build();

    List<SpotifyPlaylistTrackDto> playlistItemTracks =
        List.of(playlistItemTrackOne, playlistItemTrackTwo);

    // Then
    Assertions.assertThat(underTest.mapPlaylistTrackDtosToModels(playlistItemTracks))
        .containsExactly(trackOne, trackTwo);
  }

  @Test
  void itShouldMapSpotifyTrackItemsToTracks() {
    // Given
    String artistID = "0102030405AaBbCcDdEeFf";
    String artistName = "artist name";
    URI artistUri = URI.create("spotify:artist:0102030405AaBbCcDdEeFf");

    String trackOneId = "1222030405AaBbCcDdEeFf";
    String trackOneName = "track 1 name";
    URI trackOneUri = URI.create("spotify:track:1222030405AaBbCcDdEeFf");
    Integer trackOnePopularity = 1;

    String trackTwoId = "2222030405AaBbCcDdEeFf";
    String trackTwoName = "track 2 name";
    URI trackTwoUri = URI.create("spotify:track:2222030405AaBbCcDdEeFf");
    Integer trackTwoPopularity = 2;

    SpotifyArtistItem artist = SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistItem> artists = List.of(artist);

    SpotifyTrackItem trackOne =
        SpotifyTrack.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .popularity(trackOnePopularity)
            .artists(artists)
            .build();

    SpotifyTrackItem trackTwo =
        SpotifyTrack.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artists(artists)
            .build();

    SpotifyArtistDto artistItem =
        SpotifyArtistDto.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistDto> artistItems = List.of(artistItem);

    SpotifyTrackDto trackItemOne =
        SpotifyTrackDto.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistDtos(artistItems)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackDto trackItemTwo =
        SpotifyTrackDto.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistDtos(artistItems)
            .build();

    // Then
    Assertions.assertThat(underTest.mapDtosToModels(List.of(trackItemOne, trackItemTwo)))
        .containsExactly(trackOne, trackTwo);
  }

  @Test
  void itShouldReturnEmptyListWhenSourceIsNull() {
    Assertions.assertThat(underTest.mapDtosToModels(null)).isEmpty();
  }
}
