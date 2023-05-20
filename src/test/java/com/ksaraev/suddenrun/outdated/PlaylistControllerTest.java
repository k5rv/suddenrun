package com.ksaraev.suddenrun.outdated;

import static org.mockito.ArgumentMatchers.any;

class PlaylistControllerTest {
/*
  @Mock private SpotifyUserProfileItemService userService;
  @Mock private SpotifyUserTopTrackItemsService topTracksService;
  @Mock private SpotifyRecommendationItemsService recommendationsService;
  @Mock private SpotifyPlaylistItemService playlistService;
  @Mock private AppPlaylistConfig playlistConfig;

  @Mock private AppUserService appUserService;
  @Mock private AppPlaylistService appPlaylistService;
  @Mock private AppTrackService appTrackService;

  @Mock private SpotifyUserProfileService userProfileService;

  @Captor private ArgumentCaptor<String> playlistIdArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyUserProfileItem> userArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItemDetails> playlistDetailsArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyPlaylistItem> playlistArgumentCaptor;
  @Captor private ArgumentCaptor<SpotifyTrackItemFeatures> featuresArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> userTopTracksArgumentCaptor;
  @Captor private ArgumentCaptor<List<SpotifyTrackItem>> musicRecommendationsArgumentCaptor;

  private PlaylistController underTest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    underTest = new PlaylistController(  appUserService, appTrackService, appPlaylistService, userProfileService);
  }

  @Test
  void itShouldCreatePlaylist() {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();

    SpotifyTrackItem topTrackA = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem topTrackB = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> userTopTracks = List.of(topTrackA, topTrackB);

    SpotifyTrackItem musicRecommendation = SpotifyServiceHelper.getTrack();
    List<SpotifyTrackItem> musicRecommendations = List.of(musicRecommendation);

    SpotifyTrackItemFeatures trackFeatures = SpotifyServiceHelper.getSpotifyTrackFeatures();

    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();
    playlist.setTracks(musicRecommendations);

    given(userService.getCurrentUserProfile()).willReturn(user);
    given(topTracksService.getUserTopTracks()).willReturn(userTopTracks);
    given(playlistConfig.getSize()).willReturn(2);
    given(playlistConfig.getMusicFeatures()).willReturn(trackFeatures);
    given(playlistConfig.getDetails()).willReturn(playlistDetails);
    given(recommendationsService.getRecommendations(anyList(), any()))
        .willReturn(musicRecommendations);
    given(playlistService.createPlaylist(user, playlistDetails)).willReturn(playlist);
    given(playlistService.getPlaylist(playlist.getId())).willReturn(playlist);

    // When
    underTest.createPlaylist();

    // Then
    verify(recommendationsService, times(2))
        .getRecommendations(
            userTopTracksArgumentCaptor.capture(), featuresArgumentCaptor.capture());

    Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
        .containsExactly(List.of(topTrackA), List.of(topTrackB));

    Assertions.assertThat(featuresArgumentCaptor.getAllValues())
        .containsExactly(trackFeatures, trackFeatures);

    verify(playlistService)
        .createPlaylist(userArgumentCaptor.capture(), playlistDetailsArgumentCaptor.capture());

    Assertions.assertThat(userArgumentCaptor.getValue()).isNotNull().isEqualTo(user);

    Assertions.assertThat(playlistDetailsArgumentCaptor.getValue())
        .isNotNull()
        .isEqualTo(playlistDetails);

//    verify(playlistService, times(1))
//        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

    Assertions.assertThat(musicRecommendationsArgumentCaptor.getAllValues())
        .containsExactly(musicRecommendations);

    verify(playlistService).getPlaylist(playlistIdArgumentCaptor.capture());

    Assertions.assertThat(playlistIdArgumentCaptor.getValue()).isEqualTo(playlist.getId());
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
                   3|0|0|1|FALSE|FALSE|3
                   2|1|0|2|TRUE |FALSE|3
                   2|0|1|3|FALSE|TRUE |3
                   """)
  void itShouldStopAddMusicRecommendationsWhenRecommendationsSizeIsEqualToPlaylistConfigSize(
      Integer aTracksNumber,
      Integer bTracksNumber,
      Integer cTracksNumber,
      Integer requestsNumber,
      Boolean hasAAndBTracks,
      Boolean hasAllTracks,
      Integer playlistConfigSize) {
    // Given
    SpotifyUserProfileItem user = SpotifyServiceHelper.getUserProfile();

    SpotifyTrackItem topTrackA = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem topTrackB = SpotifyServiceHelper.getTrack();
    SpotifyTrackItem topTrackC = SpotifyServiceHelper.getTrack();

    List<SpotifyTrackItem> userTopTracks = List.of(topTrackA, topTrackB, topTrackC);

    List<SpotifyTrackItem> musicRecommendationsA = SpotifyServiceHelper.getTracks(aTracksNumber);
    List<SpotifyTrackItem> musicRecommendationsB = SpotifyServiceHelper.getTracks(bTracksNumber);
    List<SpotifyTrackItem> musicRecommendationsC = SpotifyServiceHelper.getTracks(cTracksNumber);

    SpotifyTrackItemFeatures trackFeatures = SpotifyServiceHelper.getSpotifyTrackFeatures();
    SpotifyPlaylistItemDetails playlistDetails = SpotifyServiceHelper.getPlaylistDetails();
    SpotifyPlaylistItem playlist = SpotifyServiceHelper.getPlaylist();

    List<SpotifyTrackItem> playlistTracks = new ArrayList<>(musicRecommendationsA);

    if (hasAAndBTracks) {
      playlistTracks.addAll(musicRecommendationsB);
    }

    if (hasAllTracks) {
      playlistTracks.addAll(musicRecommendationsB);
      playlistTracks.addAll(musicRecommendationsC);
    }

    given(userService.getCurrentUserProfile()).willReturn(user);
    given(topTracksService.getUserTopTracks()).willReturn(userTopTracks);
    given(playlistConfig.getSize()).willReturn(playlistConfigSize);
    given(playlistConfig.getMusicFeatures()).willReturn(trackFeatures);
    given(playlistConfig.getDetails()).willReturn(playlistDetails);

    given(recommendationsService.getRecommendations(List.of(topTrackA), trackFeatures))
        .willReturn(musicRecommendationsA);

    given(recommendationsService.getRecommendations(List.of(topTrackB), trackFeatures))
        .willReturn(musicRecommendationsB);

    given(recommendationsService.getRecommendations(List.of(topTrackC), trackFeatures))
        .willReturn(musicRecommendationsC);

    given(playlistService.createPlaylist(user, playlistDetails)).willReturn(playlist);

    // When
    underTest.createPlaylist();

    // Then
    verify(recommendationsService, times(requestsNumber))
        .getRecommendations(
            userTopTracksArgumentCaptor.capture(), featuresArgumentCaptor.capture());

    if (!hasAAndBTracks && !hasAllTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA));
    }

    if (hasAAndBTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA), List.of(topTrackB));
    }

    if (hasAllTracks) {
      Assertions.assertThat(userTopTracksArgumentCaptor.getAllValues())
          .containsExactly(List.of(topTrackA), List.of(topTrackB), List.of(topTrackC));
    }

//    verify(playlistService, times(1))
//        .addTracks(playlistArgumentCaptor.capture(), musicRecommendationsArgumentCaptor.capture());

    Assertions.assertThat(musicRecommendationsArgumentCaptor.getValue())
        .isNotEmpty()
        .hasSize(playlistConfigSize)
        .hasSameElementsAs(playlistTracks);
  }

  @Test
  void itShouldThrowUserTopTracksNotFoundExceptionWhenUserTopTracksIsEmpty() {
    // Given
    given(userService.getCurrentUserProfile()).willReturn(SpotifyUserProfile.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of());
    // Then
//    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
//        .isExactlyInstanceOf(UserTopTracksNotFoundException.class)
//        .hasMessage(USER_TOP_TRACKS_NOT_FOUND);
  }

  @Test
  void itShouldThrowRecommendationsNotFoundExceptionWhenMusicRecommendationsIsEmpty() {
    // Given
    given(userService.getCurrentUserProfile()).willReturn(SpotifyUserProfile.builder().build());
    given(topTracksService.getUserTopTracks()).willReturn(List.of(SpotifyTrack.builder().build()));
    given(recommendationsService.getRecommendations(anyList(), any())).willReturn(List.of());
    // Then
//    Assertions.assertThatThrownBy(() -> underTest.createPlaylist())
//        .isExactlyInstanceOf(RecommendationsNotFoundException.class)
//        .hasMessage(RECOMMENDATIONS_NOT_FOUND);
  }*/
}