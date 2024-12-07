package com.ksaraev.spotify.model.userprofile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ksaraev.spotify.model.SpotifyItem;

@JsonDeserialize(as = SpotifyUserProfile.class)
public interface SpotifyUserProfileItem extends SpotifyItem {

  String getEmail();

  void setEmail(String email);
}
