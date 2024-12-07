package com.ksaraev.spotify.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddPlaylistItemsRequestTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = factory.getValidator();
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListIsEmpty() {
    // Given
    List<URI> uris = List.of();
    AddPlaylistItemsRequest updateItemsRequest =
        AddPlaylistItemsRequest.builder().uris(uris).build();

    // When
    Set<ConstraintViolation<AddPlaylistItemsRequest>> constraintViolations =
        validator.validate(updateItemsRequest);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("uris: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListHasMoreThan100Elements() {
    // Given
    URI uri = URI.create("spotify:resource:a");
    List<URI> uris = new ArrayList<>();
    IntStream.rangeClosed(0,100).forEach(index -> uris.add(index, uri));
    AddPlaylistItemsRequest updateItemsRequest =
        AddPlaylistItemsRequest.builder().uris(uris).build();

    // When
    Set<ConstraintViolation<AddPlaylistItemsRequest>> constraintViolations =
        validator.validate(updateItemsRequest);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
            .hasMessage("uris: size must be between 1 and 100");
  }

  @Test
  void itShouldDetectAddItemsRequestConstraintViolationsWhenUriListHasNullElements() {
    // Given
    URI uriA = URI.create("spotify:resource:a");
    URI uriC = URI.create("spotify:resource:c");
    List<URI> uris = new ArrayList<>();
    uris.add(uriA);
    uris.add(null);
    uris.add(uriC);
    AddPlaylistItemsRequest updateItemsRequest =
        AddPlaylistItemsRequest.builder().uris(uris).build();

    // When
    Set<ConstraintViolation<AddPlaylistItemsRequest>> constraintViolations =
        validator.validate(updateItemsRequest);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(new ConstraintViolationException(constraintViolations))
        .hasMessage("uris[1].<list element>: must not be null");
  }




}
