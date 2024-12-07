package com.ksaraev.spotify.service;

import static com.ksaraev.utils.helpers.SpotifyClientHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ksaraev.spotify.client.SpotifyClient;
import com.ksaraev.spotify.client.dto.GetUserTopTracksRequest;
import com.ksaraev.spotify.client.dto.GetUserTopTracksResponse;
import com.ksaraev.spotify.client.dto.SpotifyTrackDto;
import com.ksaraev.spotify.client.feign.exception.SpotifyUnauthorizedException;
import com.ksaraev.spotify.config.GetSpotifyUserTopTrackRequestConfig;
import com.ksaraev.spotify.exception.GetSpotifyUserTopTracksException;
import com.ksaraev.spotify.exception.SpotifyAccessTokenException;
import com.ksaraev.spotify.model.track.SpotifyTrackItem;
import com.ksaraev.spotify.model.track.SpotifyTrackMapper;
import com.ksaraev.utils.helpers.SpotifyServiceHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SpotifyUserTopTracksServiceTest {
  @Mock private SpotifyClient client;
  @Mock private GetSpotifyUserTopTrackRequestConfig config;
  @Mock private SpotifyTrackMapper mapper;
  @Captor private ArgumentCaptor<List<SpotifyTrackDto>> dtosArgumentCaptor;
  @Captor private ArgumentCaptor<GetUserTopTracksRequest> requestArgumentCaptor;
  private SpotifyUserTopTrackItemsService underTest;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SpotifyUserTopTracksService(client, config, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldGetUserTopTracks() {
    // Given
    List<SpotifyTrackItem> trackItems = SpotifyServiceHelper.getTracks(2);
    List<SpotifyTrackDto> trackDtos = getTrackDtos(2);
    GetUserTopTracksRequest request = createGetUserTopTracksRequest();
    GetUserTopTracksResponse response = createGetUserTopTracksResponse(trackDtos);
    given(config.getLimit()).willReturn(request.limit());
    given(config.getOffset()).willReturn(request.offset());
    given(config.getTimeRange()).willReturn(request.timeRange().name());
    given(client.getUserTopTracks(any(GetUserTopTracksRequest.class))).willReturn(response);
    given(mapper.mapDtosToModels(anyList())).willReturn(trackItems);
    // When
    underTest.getUserTopTracks();
    // Then
    then(client).should().getUserTopTracks(requestArgumentCaptor.capture());
    assertThat(requestArgumentCaptor.getValue()).isNotNull().isEqualTo(request);
    then(mapper).should().mapDtosToModels(dtosArgumentCaptor.capture());
    assertThat(dtosArgumentCaptor.getAllValues()).isNotEmpty().containsExactly(trackDtos);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      nullValues = "null",
      textBlock = """
           null
           UNKNOWN_TERM
           """)
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenGetUserTopTracksRequestTimeRangeIsNotValid(
      String timeRange) {
    // Given
    given(config.getTimeRange()).willReturn(timeRange);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class);
  }

  @Test
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenSpotifyClientThrowsRuntimeException() {
    // Given
    given(config.getTimeRange()).willReturn("MEDIUM_TERM");
    RuntimeException runtimeException = new RuntimeException("message");
    given(client.getUserTopTracks(any())).willThrow(runtimeException);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class)
        .hasMessageContaining(new GetSpotifyUserTopTracksException(runtimeException).getMessage());
  }

  @Test
  void itShouldThrowGetSpotifyUserTopTracksExceptionWhenSpotifyTrackMapperThrowsRuntimeException() {
    // Given
    List<SpotifyTrackDto> trackDtos = getTrackDtos(1);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    RuntimeException runtimeException = new RuntimeException("message");
    given(mapper.mapDtosToModels(trackDtos)).willThrow(runtimeException);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(GetSpotifyUserTopTracksException.class)
        .hasMessage(new GetSpotifyUserTopTracksException(runtimeException).getMessage());
  }

  @Test
  void
      itShouldThrowSpotifyAccessTokenExceptionWhenSpotifyClientThrowsSpotifyUnauthorizedException() {
    // Given
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    given(config.getTimeRange()).willReturn(timeRangeName);
    SpotifyUnauthorizedException spotifyUnauthorizedException =
        new SpotifyUnauthorizedException("message");
    given(client.getUserTopTracks(any())).willThrow(spotifyUnauthorizedException);
    // Then
    assertThatThrownBy(() -> underTest.getUserTopTracks())
        .isExactlyInstanceOf(SpotifyAccessTokenException.class)
        .hasMessage(new SpotifyAccessTokenException(spotifyUnauthorizedException).getMessage());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackDtosListIsEmpty() {
    // Given
    List<SpotifyTrackDto> trackDtos = Collections.emptyList();
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapDtosToModels(dtosArgumentCaptor.capture());
  }

  @Test
  void itShouldReturnEmptyListWhenGetUserTopTracksResponseTrackItemsListElementsAreNull() {
    // Given
    List<SpotifyTrackDto> trackItems = new ArrayList<>();
    trackItems.add(null);
    trackItems.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackItems);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // Then
    assertThat(underTest.getUserTopTracks()).isEmpty();
    then(mapper).should(never()).mapDtosToModels(dtosArgumentCaptor.capture());
  }

  @Test
  void
      itShouldReturnUserTopTracksNonNullElementsWhenGetUserTopTracksResponseTrackDtoListHasNullElements() {
    // Given
    SpotifyTrackDto trackDto = SpotifyTrackDto.builder().build();
    List<SpotifyTrackDto> trackDtos = new ArrayList<>();
    trackDtos.add(null);
    trackDtos.add(trackDto);
    trackDtos.add(null);
    String timeRangeName = GetUserTopTracksRequest.TimeRange.SHORT_TERM.name();
    GetUserTopTracksResponse getUserTopTracksResponse = createGetUserTopTracksResponse(trackDtos);
    given(config.getTimeRange()).willReturn(timeRangeName);
    given(client.getUserTopTracks(any())).willReturn(getUserTopTracksResponse);
    // When
    underTest.getUserTopTracks();
    // Then
    then(mapper).should().mapDtosToModels(dtosArgumentCaptor.capture());
    assertThat(dtosArgumentCaptor.getAllValues())
        .containsExactly(Collections.singletonList(trackDto));
  }
}
