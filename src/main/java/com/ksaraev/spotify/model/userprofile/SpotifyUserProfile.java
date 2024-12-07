package com.ksaraev.spotify.model.userprofile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.*;

@Data
@Builder
public class SpotifyUserProfile implements SpotifyUserProfileItem {
  @NotNull private String id;
  @NotEmpty private String name;
  @Email private String email;
  @NotNull private URI uri;
}
