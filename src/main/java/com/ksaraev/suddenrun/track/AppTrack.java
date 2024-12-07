package com.ksaraev.suddenrun.track;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SuddenrunTrack.class)
public interface AppTrack {

  String getId();

  void setId(String id);

  String getName();

  void setName(String name);
}
