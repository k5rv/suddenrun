package com.ksaraev.spotify.model;


public class MappingSourceIsNullException extends RuntimeException {

  public MappingSourceIsNullException() {
    super("Mapping source is null");
  }
}
