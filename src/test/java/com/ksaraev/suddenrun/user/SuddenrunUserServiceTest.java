package com.ksaraev.suddenrun.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.ksaraev.utils.helpers.SuddenrunHelper;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class SuddenrunUserServiceTest {

  @Mock private SuddenrunUserRepository repository;

  @Captor private ArgumentCaptor<SuddenrunUser> suddenrunUserArgumentCaptor;

  private AutoCloseable closeable;

  private AppUserService underTest;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    underTest = new SuddenrunUserService(repository);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void itShouldReturnUserIfItIsPresent() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    given(repository.findById(id)).willReturn(Optional.of(suddenrunUser));

    // When
    Optional<AppUser> appUser = underTest.getUser(id);

    // Then
    assertThat(appUser)
        .isPresent()
        .hasValueSatisfying(
            user ->
                assertThat((SuddenrunUser) user)
                    .usingRecursiveComparison()
                    .isEqualTo(suddenrunUser));
  }

  @Test
  void itShouldReturnEmptyOptionalIfUserIsNotPresent() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    given(repository.findById(id)).willReturn(Optional.empty());

    // When
    Optional<AppUser> appUser = underTest.getUser(id);

    // Then
    assertThat(appUser).isNotPresent();
  }

  @Test
  void itShouldThrowGetSuddenrunUserExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    RuntimeException runtimeException = new RuntimeException("message");
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    GetSuddenrunUserException getSuddenrunUserException =
        new GetSuddenrunUserException(id, runtimeException);
    given(repository.findById(id)).willThrow(runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.getUser(id))
        .isExactlyInstanceOf(GetSuddenrunUserException.class)
        .hasMessage(getSuddenrunUserException.getMessage());
  }

  @Test
  void itShouldRegisterUser() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    String name = suddenrunUser.getName();

    // When
    underTest.registerUser(id, name);

    // Then
    then(repository).should().save(suddenrunUserArgumentCaptor.capture());
    SuddenrunUser suddenrunUserArgumentCaptureValue = suddenrunUserArgumentCaptor.getValue();
    assertThat(suddenrunUserArgumentCaptureValue).isEqualTo(suddenrunUser);
  }

  @Test
  void itShouldThrowRegisterSuddenrunUserExceptionIfUserRepositoryThrowsRuntimeException() {
    // Given
    SuddenrunUser suddenrunUser = SuddenrunHelper.getUser();
    String id = suddenrunUser.getId();
    String name = suddenrunUser.getName();
    RuntimeException runtimeException = new RuntimeException("message");
    given(repository.save(suddenrunUser)).willThrow(runtimeException);
    RegisterSuddenrunUserException registerSuddenrunUserException =
        new RegisterSuddenrunUserException(id, runtimeException);

    // Then
    assertThatThrownBy(() -> underTest.registerUser(id, name))
        .isExactlyInstanceOf(RegisterSuddenrunUserException.class)
        .hasMessage(registerSuddenrunUserException.getMessage());
  }
}
