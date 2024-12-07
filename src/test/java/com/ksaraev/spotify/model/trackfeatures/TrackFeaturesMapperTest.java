package com.ksaraev.spotify.model.trackfeatures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ksaraev.spotify.client.dto.GetRecommendationsRequest;
import com.ksaraev.spotify.model.MappingSourceIsNullException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpotifyTrackFeaturesMapperImpl.class})
class TrackFeaturesMapperTest {

  @Autowired SpotifyTrackFeaturesMapper underTest;

  @Test
  void itShouldMapSpotifyTrackFeaturesToGetRecommendationsRequestTrackFeatures() {
    // Given
    Integer maxPopularity = 100;
    Integer minPopularity = 99;
    Integer popularity = 98;
    Integer maxKey = 97;
    Integer minKey = 96;
    Integer key = 95;
    Integer maxMode = 94;
    Integer minMode = 93;
    Integer mode = 92;
    Integer maxDurationMs = 91;
    Integer minDurationMs = 90;
    Integer durationMs = 89;
    Integer maxTimeSignature = 88;
    Integer minTimeSignature = 87;
    Integer timeSignature = 86;
    BigDecimal minTempo = new BigDecimal(85);
    BigDecimal maxTempo = new BigDecimal(84);
    BigDecimal tempo = new BigDecimal(83);
    BigDecimal minEnergy = new BigDecimal(82);
    BigDecimal maxEnergy = new BigDecimal(81);
    BigDecimal energy = new BigDecimal(80);
    BigDecimal minAcousticness = new BigDecimal(79);
    BigDecimal maxAcousticness = new BigDecimal(78);
    BigDecimal acousticness = new BigDecimal(77);
    BigDecimal minDanceability = new BigDecimal(76);
    BigDecimal maxDanceability = new BigDecimal(75);
    BigDecimal danceability = new BigDecimal(74);
    BigDecimal maxInstrumentalness = new BigDecimal(73);
    BigDecimal minInstrumentalness = new BigDecimal(72);
    BigDecimal instrumentalness = new BigDecimal(71);
    BigDecimal maxLiveness = new BigDecimal(70);
    BigDecimal minLiveness = new BigDecimal(69);
    BigDecimal liveness = new BigDecimal(68);
    BigDecimal minLoudness = new BigDecimal(67);
    BigDecimal maxLoudness = new BigDecimal(66);
    BigDecimal loudness = new BigDecimal(65);
    BigDecimal maxSpeechiness = new BigDecimal(64);
    BigDecimal minSpeechiness = new BigDecimal(63);
    BigDecimal speechiness = new BigDecimal(62);
    BigDecimal maxValence = new BigDecimal(61);
    BigDecimal minValence = new BigDecimal(60);
    BigDecimal valence = new BigDecimal(59);

    GetRecommendationsRequest.TrackFeatures trackFeatures =
        new GetRecommendationsRequest.TrackFeatures(
            maxPopularity,
            minPopularity,
            popularity,
            maxKey,
            minKey,
            key,
            maxMode,
            minMode,
            mode,
            maxDurationMs,
            minDurationMs,
            durationMs,
            maxTimeSignature,
            minTimeSignature,
            timeSignature,
            minTempo,
            maxTempo,
            tempo,
            minEnergy,
            maxEnergy,
            energy,
            minAcousticness,
            maxAcousticness,
            acousticness,
            minDanceability,
            maxDanceability,
            danceability,
            maxInstrumentalness,
            minInstrumentalness,
            instrumentalness,
            maxLiveness,
            minLiveness,
            liveness,
            minLoudness,
            maxLoudness,
            loudness,
            maxSpeechiness,
            minSpeechiness,
            speechiness,
            maxValence,
            minValence,
            valence);

    SpotifyTrackItemFeatures spotifyTrackFeatures =
        SpotifyTrackFeatures.builder()
            .maxPopularity(maxPopularity)
            .minPopularity(minPopularity)
            .popularity(popularity)
            .maxKey(maxKey)
            .minKey(minKey)
            .key(key)
            .maxMode(maxMode)
            .minMode(minMode)
            .mode(mode)
            .maxDurationMs(maxDurationMs)
            .minDurationMs(minDurationMs)
            .durationMs(durationMs)
            .maxTimeSignature(maxTimeSignature)
            .minTimeSignature(minTimeSignature)
            .timeSignature(timeSignature)
            .minTempo(minTempo)
            .maxTempo(maxTempo)
            .tempo(tempo)
            .minEnergy(minEnergy)
            .maxEnergy(maxEnergy)
            .energy(energy)
            .minAcousticness(minAcousticness)
            .maxAcousticness(maxAcousticness)
            .acousticness(acousticness)
            .minDanceability(minDanceability)
            .maxDanceability(maxDanceability)
            .danceability(danceability)
            .maxInstrumentalness(maxInstrumentalness)
            .minInstrumentalness(minInstrumentalness)
            .instrumentalness(instrumentalness)
            .maxLiveness(maxLiveness)
            .minLiveness(minLiveness)
            .liveness(liveness)
            .minLoudness(minLoudness)
            .maxLoudness(maxLoudness)
            .loudness(loudness)
            .maxSpeechiness(maxSpeechiness)
            .minSpeechiness(minSpeechiness)
            .speechiness(speechiness)
            .maxValence(maxValence)
            .minValence(minValence)
            .valence(valence)
            .build();

    // Then
    assertThat(underTest.mapToRequestFeatures(spotifyTrackFeatures)).isNotNull();
    assertThat(underTest.mapToRequestFeatures(spotifyTrackFeatures)).isEqualTo(trackFeatures);
  }

  @Test
  void itShouldThrowNullMappingSourceExceptionWhenSpotifyTrackFeaturesIsNull() {
    // Then
    assertThatThrownBy(() -> underTest.mapToRequestFeatures(null))
        .isExactlyInstanceOf(MappingSourceIsNullException.class)
        .hasMessage(new MappingSourceIsNullException().getMessage());
  }
}
