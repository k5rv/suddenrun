package com.ksaraev.suddenrun.playlist;

import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SuddenrunPlaylistRepository extends JpaRepository<SuddenrunPlaylist, String> {

  @NotNull
  Optional<SuddenrunPlaylist> findById(@NotNull String playlistId);

  boolean existsById(@NotNull String playlistId);

  Optional<SuddenrunPlaylist> findByUserId(String userId);

  @Transactional
  @Modifying
  @Query("DELETE FROM SuddenrunPlaylist p WHERE p.id = :playlistId")
  void deleteById(@NonNull String playlistId);
}
