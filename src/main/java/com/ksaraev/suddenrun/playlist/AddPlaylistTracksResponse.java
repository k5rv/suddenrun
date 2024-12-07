package com.ksaraev.suddenrun.playlist;

import java.util.List;
import lombok.Builder;

@Builder
public record AddPlaylistTracksResponse(String id, List<String> trackIds) {}
