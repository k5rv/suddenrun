package com.ksaraev.suddenrun.user;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuddenrunUserRepository extends JpaRepository<SuddenrunUser, String> {

  boolean existsById(@NonNull String id);
}
