package com.ksaraev.spotify.config;

public interface GetSpotifyUserTopTrackRequestConfig {

  Integer getLimit();

  Integer getOffset();

  String getTimeRange();
}
