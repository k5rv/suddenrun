package com.ksaraev.utils.helpers;

import com.ksaraev.spotify.client.dto.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class SpotifyClientHelper {

  public static SpotifyUserProfileDto getUserProfileDto() {
    String id = SpotifyResourceHelper.getRandomId();
    String displayName = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.USER.getUri(id);
    String email = SpotifyResourceHelper.getRandomEmail();
    Map<String, Object> explicitContent = SpotifyResourceHelper.getExplicitContent();
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.USER, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    String country = SpotifyResourceHelper.getCountry();
    String product = SpotifyResourceHelper.getProduct();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.USER, id);
    return SpotifyUserProfileDto.builder()
        .id(SpotifyResourceHelper.getRandomId())
        .displayName(displayName)
        .uri(uri)
        .email(email)
        .explicitContent(explicitContent)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .country(country)
        .product(product)
        .href(href)
        .build();
  }

  public static SpotifyTrackDto getTrackDto() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.TRACK.getUri(id);
    SpotifyAlbumDto albumDto = getAlbumDto();
    SpotifyAlbumDto sourceAlbumDto = getAlbumDto();
    URL previewURL = SpotifyResourceHelper.getHref(SpotifyResourceHelper.TRACK, id);
    List<SpotifyArtistDto> artistDtos = getArtistDtos(1);
    Integer popularity = SpotifyResourceHelper.getRandomPopularity();
    String type = SpotifyResourceHelper.ARTIST.getType();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.ARTIST, id);
    Boolean explicit = true;
    Boolean episode = false;
    Boolean track = true;
    Boolean isLocal = false;
    Boolean isPlayable = true;
    List<String> availableMarkets = SpotifyResourceHelper.getAvailableMarkets();
    Integer discNumber = 5;
    Integer durationMs = 18000;
    Integer trackNumber = 2;
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.TRACK, id);
    Map<String, Object> externalIds =
        SpotifyResourceHelper.getExternalIds(SpotifyResourceHelper.TRACK, id);
    return SpotifyTrackDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .albumDto(albumDto)
        .sourceAlbumDto(sourceAlbumDto)
        .previewUrl(previewURL)
        .artistDtos(artistDtos)
        .popularity(popularity)
        .type(type)
        .href(href)
        .explicit(explicit)
        .episode(episode)
        .track(track)
        .isLocal(isLocal)
        .isPlayable(isPlayable)
        .availableMarkets(availableMarkets)
        .discNumber(discNumber)
        .durationMs(durationMs)
        .trackNumber(trackNumber)
        .externalUrls(externalUrls)
        .externalIds(externalIds)
        .build();
  }

  public static SpotifyPlaylistDetailsDto getPlaylistDetailsDto() {
    String name = SpotifyResourceHelper.getRandomName();
    String description = SpotifyResourceHelper.getRandomDescription();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    return SpotifyPlaylistDetailsDto.builder()
        .name(name)
        .description(description)
        .isPublic(isPublic)
        .isCollaborative(isCollaborative)
        .build();
  }

  public static SpotifyAlbumDto getAlbumDto() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.ALBUM.getUri(id);
    String type = SpotifyResourceHelper.ALBUM.getType();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.ALBUM, id);
    String albumType = SpotifyResourceHelper.getAlbumType();
    String albumGroup = SpotifyResourceHelper.getAlbumGroup();
    Integer totalTracks = 50;
    String releaseDate = SpotifyResourceHelper.getReleaseDate();
    String precisionDate = SpotifyResourceHelper.getPrecisionDate();
    List<String> availableMarkets = SpotifyResourceHelper.getAvailableMarkets();
    List<SpotifyArtistDto> artistDtos = getArtistDtos(1);
    List<Map<String, Object>> restrictions = SpotifyResourceHelper.getRestrictions();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.ARTIST, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    return SpotifyAlbumDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .type(type)
        .href(href)
        .albumType(albumType)
        .albumGroup(albumGroup)
        .totalTracks(totalTracks)
        .releaseDate(releaseDate)
        .releaseDatePrecision(precisionDate)
        .availableMarkets(availableMarkets)
        .artistDtos(artistDtos)
        .restrictions(restrictions)
        .externalUrls(externalUrls)
        .images(images)
        .build();
  }

  public static SpotifyArtistDto getArtistDto() {
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    Integer popularity = SpotifyResourceHelper.getRandomPopularity();
    List<String> genres = SpotifyResourceHelper.getRandomGenres();
    String type = SpotifyResourceHelper.ARTIST.getType();
    URI uri = SpotifyResourceHelper.ARTIST.getUri(id);
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.ARTIST, id);
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.ARTIST, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    return SpotifyArtistDto.builder()
        .id(id)
        .name(name)
        .popularity(popularity)
        .genres(genres)
        .type(type)
        .uri(uri)
        .href(href)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .build();
  }

  public static SpotifyPlaylistDto getPlaylistDto() {
    String id = SpotifyResourceHelper.getRandomId();
    return getPlaylistDto(id);
  }

  public static SpotifyPlaylistDto getPlaylistDto(String id) {
    String name = SpotifyResourceHelper.getRandomName();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    SpotifyUserProfileDto userProfileDto = getUserProfileDto();
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    String description = SpotifyResourceHelper.getRandomDescription();
    String type = SpotifyResourceHelper.PLAYLIST.getType();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    String primaryColor = SpotifyResourceHelper.getPrimaryColor();
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.PLAYLIST, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.PLAYLIST, id);
    return SpotifyPlaylistDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileDto(userProfileDto)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isCollaborative(isCollaborative)
        .isPublic(isPublic)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .build();
  }

  public static SpotifyPlaylistDto getPlaylistDto(
      SpotifyUserProfileDto userProfileDto,
      String id,
      String name,
      String description,
      Boolean isPublic,
      List<SpotifyTrackDto> trackDtos) {
    SpotifyPlaylistMusicDto spotifyPlaylistMusicDto =
        getSpotifyPlaylistMusicDto(userProfileDto, trackDtos);
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    String type = SpotifyResourceHelper.PLAYLIST.getType();
    String primaryColor = SpotifyResourceHelper.getPrimaryColor();
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.PLAYLIST, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.PLAYLIST, id);
    return SpotifyPlaylistDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileDto(userProfileDto)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isPublic(isPublic)
        .isCollaborative(false)
        .playlistMusicDto(spotifyPlaylistMusicDto)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .build();
  }

  public static SpotifyPlaylistDto getPlaylistDto(
      SpotifyUserProfileDto userProfileDto, String name, String description, Boolean isPublic) {
    String id = SpotifyResourceHelper.getRandomId();
    URI uri = SpotifyResourceHelper.PLAYLIST.getUri(id);
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    String type = SpotifyResourceHelper.PLAYLIST.getType();
    String primaryColor = SpotifyResourceHelper.getPrimaryColor();
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.PLAYLIST, id);
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.PLAYLIST, id);
    return SpotifyPlaylistDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileDto(userProfileDto)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isPublic(isPublic)
        .isCollaborative(false)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .build();
  }

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistTrackDto() {
    SpotifyTrackDto trackItem = getTrackDto();
    SpotifyUserProfileDto addedBy = getUserProfileDto();
    return getSpotifyPlaylistTrackDto(addedBy, trackItem);
  }

  public static SpotifyPlaylistTrackDto getSpotifyPlaylistTrackDto(
      SpotifyUserProfileDto addedBy, SpotifyTrackDto trackDto) {
    String addedAt = ZonedDateTime.now().toString();
    Boolean isLocal = false;
    String primaryColor = SpotifyResourceHelper.getPrimaryColor();
    Map<String, Object> videoThumbnail = SpotifyResourceHelper.getVideoThumbNail(trackDto.id());
    return SpotifyPlaylistTrackDto.builder()
        .trackDto(trackDto)
        .addedBy(addedBy)
        .addedAt(addedAt)
        .isLocal(isLocal)
        .primaryColor(primaryColor)
        .videoThumbnail(videoThumbnail)
        .build();
  }

  public static List<SpotifyPlaylistTrackDto> getSpotifyPlaylistTrackDtos(
      SpotifyUserProfileDto addedBy, List<SpotifyTrackDto> trackDtos) {
    return trackDtos.stream()
        .filter(Objects::nonNull)
        .map(trackItem -> getSpotifyPlaylistTrackDto(addedBy, trackItem))
        .toList();
  }

  public static SpotifyPlaylistMusicDto getSpotifyPlaylistMusicDto(
      SpotifyUserProfileDto userProfileItem, List<SpotifyTrackDto> trackItems) {
    List<SpotifyPlaylistTrackDto> playlistTrackDtos =
        getSpotifyPlaylistTrackDtos(userProfileItem, trackItems);
    return SpotifyPlaylistMusicDto.builder()
        .playlistTrackDtos(playlistTrackDtos)
        .next(null)
        .previous(null)
        .href(null)
        .total(trackItems.size())
        .limit(100)
        .offset(0)
        .build();
  }

  public static SpotifyPlaylistDto updatePlaylistDto(
      SpotifyPlaylistDto playlistItem, List<SpotifyTrackDto> trackItems) {
    String id = playlistItem.id();
    String name = playlistItem.name();
    URI uri = playlistItem.uri();
    SpotifyUserProfileDto userProfileDto = playlistItem.userProfileDto();
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    String description = playlistItem.description();
    String type = SpotifyResourceHelper.PLAYLIST.getType();
    Boolean isPublic = true;
    Boolean isCollaborative = false;
    String primaryColor = SpotifyResourceHelper.getPrimaryColor();
    Map<String, Object> followers = SpotifyResourceHelper.getFollowers();
    Map<String, Object> externalUrls =
        SpotifyResourceHelper.getExternalUrls(SpotifyResourceHelper.PLAYLIST, playlistItem.id());
    List<Map<String, Object>> images = SpotifyResourceHelper.getImages();
    URL href = SpotifyResourceHelper.getHref(SpotifyResourceHelper.PLAYLIST, playlistItem.id());
    SpotifyPlaylistMusicDto playlistMusicDto =
        getSpotifyPlaylistMusicDto(playlistItem.userProfileDto(), trackItems);
    return SpotifyPlaylistDto.builder()
        .id(id)
        .name(name)
        .uri(uri)
        .userProfileDto(userProfileDto)
        .playlistMusicDto(playlistMusicDto)
        .snapshotId(snapshotId)
        .description(description)
        .type(type)
        .isCollaborative(isCollaborative)
        .isPublic(isPublic)
        .primaryColor(primaryColor)
        .followers(followers)
        .externalUrls(externalUrls)
        .images(images)
        .href(href)
        .build();
  }

  public static GetRecommendationsRequest.TrackFeatures getRecommendationRequestTrackFeatures() {
    BigDecimal minTempo = new BigDecimal(120);
    BigDecimal maxTempo = new BigDecimal(140);
    BigDecimal minEnergy = new BigDecimal("0.65");
    return GetRecommendationsRequest.TrackFeatures.builder()
        .minTempo(minTempo)
        .maxTempo(maxTempo)
        .minEnergy(minEnergy)
        .build();
  }

  public static GetUserTopTracksRequest createGetUserTopTracksRequest() {
    Integer offset = 0;
    Integer limit = 50;
    GetUserTopTracksRequest.TimeRange timeRange = GetUserTopTracksRequest.TimeRange.SHORT_TERM;
    return GetUserTopTracksRequest.builder()
        .offset(offset)
        .limit(limit)
        .timeRange(timeRange)
        .build();
  }

  public static GetUserPlaylistsResponse createGetUserPlaylistResponse(
      List<SpotifyPlaylistDto> playlistDtos) {
    URL href = null;
    try {
      href =
          new URL(
              "https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Integer offset = 0;
    Integer total = 50;
    Integer limit = 50;
    return GetUserPlaylistsResponse.builder()
        .href(href)
        .playlistDtos(playlistDtos)
        .limit(limit)
        .offset(offset)
        .total(1)
        .next(null)
        .previous(null)
        .total(total)
        .build();
  }

  public static GetUserTopTracksResponse createGetUserTopTracksResponse(
      List<SpotifyTrackDto> trackDtos) {
    URL href;
    try {
      href =
          new URL(
              "https://api.spotify.com/v1/me/top/tracks?limit=1&offset=0&time_range=short_term");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    Integer offset = 0;
    Integer total = 50;
    Integer limit = 50;
    return GetUserTopTracksResponse.builder()
        .href(href)
        .trackDtos(trackDtos)
        .limit(limit)
        .offset(offset)
        .total(1)
        .next(null)
        .previous(null)
        .total(total)
        .build();
  }

  public static GetRecommendationsResponse createGetRecommendationsResponse(
      List<SpotifyTrackDto> trackDtos) {
    List<Map<String, Object>> seeds =
        List.of(
            Map.of(
                "initialPoolSize",
                428,
                "afterFilteringSize",
                238,
                "afterRelinkingSize",
                238,
                "id",
                "0000567890AaBbCcDdEeFfG",
                "type",
                "ARTIST",
                "href",
                "https://api.spotify.com/v1/artists/0000567890AaBbCcDdEeFfG"),
            Map.of(
                "initialPoolSize",
                425,
                "afterFilteringSize",
                222,
                "afterRelinkingSize",
                222,
                "id",
                "112233445AaBbCcDdEeFfG",
                "type",
                "TRACK",
                "href",
                "https://api.spotify.com/v1/tracks/1122AA4450011CcDdEeFfG"),
            Map.of(
                "initialPoolSize",
                160,
                "afterFilteringSize",
                58,
                "afterRelinkingSize",
                58,
                "id",
                "genre name",
                "type",
                "GENRE",
                "href",
                ""));
    return GetRecommendationsResponse.builder().trackDtos(trackDtos).seeds(seeds).build();
  }

  public static AddPlaylistItemsResponse createAddPlaylistItemsResponse() {
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return AddPlaylistItemsResponse.builder().snapshotId(snapshotId).build();
  }

  public static RemovePlaylistItemsResponse createRemovePlaylistItemsResponse() {
    String snapshotId = SpotifyResourceHelper.getRandomSnapshotId();
    return RemovePlaylistItemsResponse.builder().snapshotId(snapshotId).build();
  }

  public static List<SpotifyPlaylistDto> getPlaylistDtos(Integer size) {
    return getSpotifyClientDtos(size, SpotifyPlaylistDto.class);
  }

  public static List<SpotifyArtistDto> getArtistDtos(Integer size) {
    return getSpotifyClientDtos(size, SpotifyArtistDto.class);
  }

  public static List<SpotifyTrackDto> getTrackDtos(Integer size) {
    return getSpotifyClientDtos(size, SpotifyTrackDto.class);
  }

  private static <T> List<T> getSpotifyClientDtos(Integer size, Class<T> type) {
    List<T> dtos = new ArrayList<>();
    IntStream.range(0, size).forEach(index -> dtos.add(index, getSpotifyClientDto(type)));
    return dtos;
  }

  private static <T> T getSpotifyClientDto(Class<T> type) {
    if (type.isAssignableFrom(SpotifyUserProfileDto.class)) return type.cast(getUserProfileDto());
    if (type.isAssignableFrom(SpotifyAlbumDto.class)) return type.cast(getAlbumDto());
    if (type.isAssignableFrom(SpotifyArtistDto.class)) return type.cast(getArtistDto());
    if (type.isAssignableFrom(SpotifyTrackDto.class)) return type.cast(getTrackDto());
    if (type.isAssignableFrom(SpotifyPlaylistDetailsDto.class))
      return type.cast(getPlaylistDetailsDto());
    if (type.isAssignableFrom(SpotifyPlaylistDto.class)) return type.cast(getPlaylistDto());
    if (type.isAssignableFrom(SpotifyPlaylistTrackDto.class))
      return type.cast(getSpotifyPlaylistTrackDto());
    throw new UnsupportedOperationException("not supported type:" + type);
  }
}
