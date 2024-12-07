package com.ksaraev.spotify.model.trackfeatures;

import java.math.BigDecimal;

public interface SpotifyTrackItemFeatures {

  Integer getKey();

  Integer getMaxPopularity();

  Integer getMinPopularity();

  Integer getMode();

  Integer getDurationMs();

  Integer getTimeSignature();

  BigDecimal getTempo();

  BigDecimal getEnergy();

  BigDecimal getValence();

  BigDecimal getLiveness();

  BigDecimal getLoudness();

  BigDecimal getSpeechiness();

  BigDecimal getAcousticness();

  BigDecimal getDanceability();

  BigDecimal getInstrumentalness();

  Integer getPopularity();

  BigDecimal getMaxAcousticness();

  BigDecimal getMaxEnergy();

  BigDecimal getMaxInstrumentalness();

  Integer getMaxKey();

  BigDecimal getMaxLiveness();

  BigDecimal getMaxLoudness();

  Integer getMaxMode();

  BigDecimal getMaxSpeechiness();

  BigDecimal getMaxTempo();

  BigDecimal getMaxValence();

  Integer getMaxTimeSignature();

  BigDecimal getMaxDanceability();

  Integer getMaxDurationMs();

  BigDecimal getMinAcousticness();

  BigDecimal getMinEnergy();

  BigDecimal getMinInstrumentalness();

  Integer getMinKey();

  BigDecimal getMinLiveness();

  BigDecimal getMinLoudness();

  Integer getMinMode();

  BigDecimal getMinSpeechiness();

  BigDecimal getMinTempo();

  BigDecimal getMinValence();

  Integer getMinTimeSignature();

  BigDecimal getMinDanceability();

  Integer getMinDurationMs();
}
