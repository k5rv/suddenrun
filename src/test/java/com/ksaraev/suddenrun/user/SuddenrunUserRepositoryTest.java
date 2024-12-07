package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.*;

import com.ksaraev.utils.helpers.SpotifyResourceHelper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest()
class SuddenrunUserRepositoryTest {

  @Autowired private SuddenrunUserRepository underTest;

  @Test
  void itShouldReturnTrueIfUserWithIdExists() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    SuddenrunUser suddenrunUser = SuddenrunUser.builder().id(id).name(name).build();
    underTest.save(suddenrunUser);

    // When
    boolean isExists = underTest.existsById(id);

    // Then
    assertThat(isExists).isTrue();
  }

  @Test
  void itShouldReturnFalseIfUserWithIdDoesNotExists() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();

    // When
    boolean isExists = underTest.existsById(id);

    // Then
    assertThat(isExists).isFalse();
  }

  @Test
  void itShouldSaveUser() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    SuddenrunUser suddenrunUser = SuddenrunUser.builder().id(id).name(name).build();

    // When
    underTest.save(suddenrunUser);

    // Then
    Optional<SuddenrunUser> optionalUser = underTest.findById(id);
    assertThat(optionalUser)
        .isPresent()
        .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(suddenrunUser));
  }

  @Test
  void itShouldFindUserById() {
    // Given
    String id = SpotifyResourceHelper.getRandomId();
    String name = SpotifyResourceHelper.getRandomName();
    SuddenrunUser suddenrunUser = SuddenrunUser.builder().id(id).name(name).build();
    underTest.save(suddenrunUser);

    // When
    Optional<SuddenrunUser> optionalUser = underTest.findById(id);

    // Then
    assertThat(optionalUser)
        .isPresent()
        .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(suddenrunUser));
  }
}
