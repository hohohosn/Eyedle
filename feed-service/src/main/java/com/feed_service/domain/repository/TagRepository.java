package com.feed_service.domain.repository;

import com.feed_service.domain.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.count DESC")
    List<Tag> findByNameContaining(@Param("keyword") String keyword);
}
