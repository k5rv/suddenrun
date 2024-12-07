package com.ksaraev.spotify.model.playlist;


import com.ksaraev.spotify.client.dto.*;
import com.ksaraev.spotify.model.MappingSourceIsNullException;
import com.ksaraev.spotify.model.artist.SpotifyArtist;
import com.ksaraev.spotify.model.artist.SpotifyArtistItem;
import com.ksaraev.spotify.model.artist.SpotifyArtistMapperImpl;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistDetails;
import com.ksaraev.spotify.model.playlistdetails.SpotifyPlaylistItemDetails;
import com.ksaraev.spotify.model.track.SpotifyTrack;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.track.SpotifyTrackMapperImpl;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfile;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileItem;
import com.ksaraev.spotify.model.userprofile.SpotifyUserProfileMapperImpl;
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
@ContextConfiguration(
    classes = {
      SpotifyPlaylistMapperImpl.class,
      SpotifyArtistMapperImpl.class,
      SpotifyTrackMapperImpl.class,
      SpotifyUserProfileMapperImpl.class
    })
class SpotifyPlaylistItemMapperTest {

  @Autowired
  SpotifyPlaylistMapper underTest;

  @Test
  void itShouldMapToPlaylist() throws Exception {
    // Given
    String userId = "12122604372";
    String userName = "Konstantin";
    URI userUri = URI.create("spotify:user:12122604372");
    String email = "email@mail.com";

    String playlistId = "0S4WIUelgktE36rVcG7ZRy";
    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;
    URI playlistUri = URI.create("spotify:playlist:0S4WIUelgktE36rVcG7ZRy");
    String playlistSnapshotId = "MSw0NjNmNjc3ZTQwOWQzYzQ1N2ZjMzlkOGM5MjA4OGMzYjc1Mjk1NGFh";

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

    SpotifyArtistItem artist =
        SpotifyArtist.builder().id(artistID).name(artistName).uri(artistUri).build();

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

    SpotifyUserProfileItem spotifyUser =
        SpotifyUserProfile.builder().id(userId).name(userName).uri(userUri).email(email).build();

    SpotifyPlaylist playlist =
        SpotifyPlaylist.builder()
            .id(playlistId)
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .user(spotifyUser)
            .description(playlistDescription)
            .tracks(List.of(trackOne, trackTwo))
            .build();

    SpotifyUserProfileDto userProfileDto =
        SpotifyUserProfileDto.builder()
            .id(userId)
            .displayName(userName)
            .uri(userUri)
            .email(email)
            .build();

    SpotifyArtistDto artistDto =
        SpotifyArtistDto.builder().id(artistID).name(artistName).uri(artistUri).build();

    List<SpotifyArtistDto> artistDtos = List.of(artistDto);

    SpotifyTrackDto trackDtoOne =
        SpotifyTrackDto.builder()
            .id(trackOneId)
            .name(trackOneName)
            .uri(trackOneUri)
            .artistDtos(artistDtos)
            .popularity(trackOnePopularity)
            .build();

    SpotifyTrackDto trackDtoTwo =
        SpotifyTrackDto.builder()
            .id(trackTwoId)
            .name(trackTwoName)
            .uri(trackTwoUri)
            .popularity(trackTwoPopularity)
            .artistDtos(artistDtos)
            .build();

    String addedAt = "2020-12-04T14:14:36Z";

    SpotifyPlaylistTrackDto playlistItemTrackOne =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDtoOne)
            .addedBy(userProfileDto)
            .addedAt(addedAt)
            .build();

    SpotifyPlaylistTrackDto playlistItemTrackTwo =
        SpotifyPlaylistTrackDto.builder()
            .trackDto(trackDtoTwo)
            .addedBy(userProfileDto)
            .addedAt(addedAt)
            .build();

    List<SpotifyPlaylistTrackDto> playlistTrackDtos =
        List.of(playlistItemTrackOne, playlistItemTrackTwo);

    SpotifyPlaylistMusicDto playlistItemMusic =
        SpotifyPlaylistMusicDto.builder().playlistTrackDtos(playlistTrackDtos).build();

    SpotifyPlaylistDto playlistItem =
        SpotifyPlaylistDto.builder()
            .id(playlistId)
            .name(playlistName)
            .uri(playlistUri)
            .snapshotId(playlistSnapshotId)
            .userProfileDto(userProfileDto)
            .description(playlistDescription)
            .isPublic(isPublic)
            .isCollaborative(isCollaborative)
            .playlistMusicDto(playlistItemMusic)
            .primaryColor("any")
            .followers(Map.of("href", "", "total", 100000))
            .externalUrls(
                Map.of("spotify", "https://open.spotify.com/artist/012345012345AABBccDDee"))
            .href(new URL("https://api.spotify.com/v1/artists/012345012345AABBccDDee"))
            .type("playlist")
            .images(
                List.of(
                    Map.of(
                        "height", 640, "width", 640, "url", new URL("https://i.scdn.co/image/1")),
                    Map.of(
                        "height", 320, "width", 320, "url", new URL("https://i.scdn.co/image/2")),
                    Map.of(
                        "height", 160, "width", 160, "url", new URL("https://i.scdn.co/image/3"))))
            .build();
    // Then
    Assertions.assertThat(underTest.mapToModel(playlistItem))
        .isEqualTo(playlist)
        .hasOnlyFields(
            "id",
            "name",
            "description",
            "uri",
            "tracks",
            "isCollaborative",
            "isPublic",
            "user",
            "snapshotId");
  }

  @Test
  void itShouldMapToPlaylistItemDetails() {
    // Given
    String playlistName = "name";
    String playlistDescription = "description";
    Boolean isCollaborative = false;
    Boolean isPublic = false;

    SpotifyPlaylistItemDetails spotifyPlaylistDetails =
        SpotifyPlaylistDetails.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    SpotifyPlaylistDetailsDto playlistItemDetails =
        SpotifyPlaylistDetailsDto.builder()
            .name(playlistName)
            .description(playlistDescription)
            .isCollaborative(isCollaborative)
            .isPublic(isPublic)
            .build();

    // Then
    Assertions.assertThat(underTest.mapToPlaylistDetailsDto(spotifyPlaylistDetails))
        .isNotNull()
        .hasOnlyFields("name", "description", "isCollaborative", "isPublic")
        .usingRecursiveComparison()
        .isEqualTo(playlistItemDetails);
  }

  @Test
  void mapToPlaylistShouldThrowWhenSpotifyPlaylistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToModel(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(new MappingSourceIsNullException().getMessage());
  }

  @Test
  void mapToPlaylistItemDetailsShouldThrowWhenSpotifyPlaylistItemIsNull() {
    // Then
    Assertions.assertThatThrownBy(() -> underTest.mapToPlaylistDetailsDto(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(new MappingSourceIsNullException().getMessage());
  }
}
