package com.ksaraev.spotify.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ksaraev.utils.helpers.SpotifyClientHelper;
import jakarta.validation.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetRecommendationsResponseTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
  }

  @Test
  void itShouldDetectGetRecommendationsResponseConstraintViolations() {
    // Given
    List<SpotifyArtistDto> artistItems = SpotifyClientHelper.getArtistDtos(2);

    SpotifyTrackDto trackItem =
        SpotifyTrackDto.builder()
            .id(null)
            .name("track name")
            .uri(URI.create("spotify:artist:1234567890AaBbCcDdEeFfG"))
            .popularity(51)
            .artistDtos(artistItems)
            .build();

    List<SpotifyTrackDto> trackItems = List.of(trackItem);

    GetRecommendationsResponse getRecommendationsResponse =
        GetRecommendationsResponse.builder().trackDtos(trackItems).build();

    // When
    Set<ConstraintViolation<GetRecommendationsResponse>> constraintViolations =
        validator.validate(getRecommendationsResponse);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("trackDtos[0].id: must not be null");
  }
}
