package com.ksaraev.spotify.model.userprofile;

import com.ksaraev.spotify.client.dto.SpotifyUserProfileDto;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
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
@ContextConfiguration(classes = {SpotifyUserProfileMapperImpl.class})
class SpotifyUserProfileMapperTest {

  @Autowired
  SpotifyUserProfileMapper underTest;

  @Test
  void itShouldMapSpotifyUserProfileItemToUser() throws Exception {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();

    SpotifyUserProfileDto userProfileItem =
        SpotifyUserProfileDto.builder()
            .id(user.getId())
            .displayName(user.getName())
            .uri(user.getUri())
            .email(user.getEmail())
            .country("CC")
            .explicitContent(Map.of("filter_enabled", false, "filter_locked", false))
            .externalUrls(Map.of("spotify", "https://open.spotify.com/user/12122604372"))
            .followers(Map.of("href", "", "total", 0))
            .href(new URL("https://api.spotify.com/v1/users/12122604372"))
            .images(
                List.of(
                    Map.of(
                        "height",
                        "",
                        "url",
                        new URL("https://scontent-cdg2-1.xx.fbcdn.net"),
                        "width",
                        "")))
            .product("premium")
            .type("user")
            .build();

    // Then
    Assertions.assertThat(underTest.mapToModel(userProfileItem))
        .isNotNull()
        .isEqualTo(user)
        .hasOnlyFields("id", "name", "email", "uri");
  }

  //
//  @Test
//  void itShouldThrowNullMappingSourceExceptionWhenUserProfileItemIsNull() {
//    // Then
//    Assertions.assertThatThrownBy(() -> underTest.mapToModel(null))
//        .isExactlyInstanceOf(MappingSourceIsNullException.class)
//        .hasMessage(MAPPING_SOURCE_IS_NULL);
//  }
}
