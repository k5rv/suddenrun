package com.ksaraev.utils.helpers;

import static java.util.concurrent.ThreadLocalRandom.current;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
@AllArgsConstructor
public enum SpotifyResourceHelper {
  USER("user"),
  ARTIST("artist"),

  TRACK("track"),
  ALBUM("album"),

  PLAYLIST("playlist");

  private final String type;

  public static String getRandomSnapshotId() {
    return RandomStringUtils.randomAlphanumeric(57);
  }

  public static Integer getRandomPopularity() {
    return current().nextInt(1, 101);
  }

  public static List<String> getRandomGenres() {
    List<String> genres = new ArrayList<>();
    int genresSize = current().nextInt(1, 5);
    IntStream.range(0, genresSize)
        .forEach(index -> genres.add(index, RandomStringUtils.randomAlphabetic(1, 15)));
    return genres;
  }

  public static Map<String, Object> getExplicitContent() {
    return Map.of("filter_enabled", false, "filter_locked", false);
  }

  public static Map<String, Object> getFollowers() {
    return Map.of("href", "", "total", 100000);
  }

  public static Map<String, Object> getExternalUrls(SpotifyResourceHelper type, String id) {
    return Map.of("spotify", "https://open.spotify.com/" + type.getType() + "/" + id);
  }

  public static Map<String, Object> getExternalIds(SpotifyResourceHelper type, String id) {
    return Map.of("spotify", "https://open.spotify.com/" + type.getType() + "/" + id);
  }

  public static Map<String, Object> getVideoThumbNail(String id) {
    return Map.of("video", "https://www.video.com/" + id);
  }

  public static List<Map<String, Object>> getRestrictions() {
    return List.of(Map.of("PG18", true, "PG21", false));
  }

  public static List<Map<String, Object>> getImages() {
    try {
      return List.of(
          Map.of("height", 640, "width", 640, "url", new URL("https://i.scdn.co/image/1")),
          Map.of("height", 320, "width", 320, "url", new URL("https://i.scdn.co/image/2")),
          Map.of("height", 160, "width", 160, "url", new URL("https://i.scdn.co/image/3")));
    } catch (MalformedURLException e) {
      throw new RuntimeException("unable to create URL: " + e.getMessage(), e);
    }
  }

  public static String getCountry() {
    return "US";
  }

  public static String getPrimaryColor() {
    return "Blue";
  }

  public static String getProduct() {
    return "premium";
  }

  public static String getAlbumType() {
    return "single";
  }

  public static String getAlbumGroup() {
    return "group";
  }

  public static String getReleaseDate() {
    return "2021-04-20";
  }

  public static String getPrecisionDate() {
    return "day";
  }

  public static List<String> getAvailableMarkets() {
    return List.of("US", "GB");
  }

  public static URL getHref(SpotifyResourceHelper type, String id) {
    try {
      return new URL("https://api.spotify.com/v1/" + type + "s/" + id);

    } catch (MalformedURLException e) {
      throw new RuntimeException("unable to create URL: " + e.getMessage(), e);
    }
  }

  public static String getRandomId() {
    return RandomStringUtils.randomAlphanumeric(22);
  }

  public static String getRandomName() {
    return RandomStringUtils.randomAlphabetic(5, 15);
  }

  public static String getRandomDescription() {
    return getRandomName() + " " + getRandomName() + " " + getRandomName();
  }

  public static String getRandomEmail() {
    return RandomStringUtils.randomAlphanumeric(5, 10) + "@mail.com";
  }

  public URI getUri(String id) {
    return URI.create("spotify:" + type + ":" + id);
  }

  @Override
  public String toString() {
    return this.type;
  }
}
