package com.ksaraev.spotify.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record GetUserTopTracksRequest(
    @Min(1) @Max(50) Integer limit, @Min(0) Integer offset, TimeRange timeRange) {

  @Getter
  @AllArgsConstructor
  public enum TimeRange {
    LONG_TERM("long_term"),
    MEDIUM_TERM("medium_term"),
    SHORT_TERM("short_term");

    private final String term;

    @Override
    public String toString() {
      return this.term;
    }
  }
}
