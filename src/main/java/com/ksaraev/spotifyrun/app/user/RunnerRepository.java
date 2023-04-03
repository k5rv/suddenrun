package com.ksaraev.spotifyrun.app.user;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, String> {

  boolean existsById(@NonNull String id);


}


/*
@Query( "SELECT pg FROM Book bk join bk.pages pg WHERE bk.bookId = :bookId")
 List<Page> findPagesByBookId(@Param("bookId") String bookId);
 */