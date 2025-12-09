package com.user_service.domain.repository;

import com.user_service.domain.model.Follow;
import com.user_service.domain.model.FollowId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

	/**
	 * 팔로우 관계 존재 여부 확인
	 */
	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

	/**
	 * 팔로우 관계 조회
	 */
	Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

	/**
	 * 팔로워 목록 조회
	 */
	@Query("SELECT f.followerId FROM Follow f WHERE f.followingId = :followingId ORDER BY f.createdAt DESC")
	Slice<Long> findFollowerIds(@Param("followingId") Long followingId, Pageable pageable);

	/**
	 * 팔로잉 목록 조회
	 */
	@Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId ORDER BY f.createdAt DESC")
	Slice<Long> findFollowingIds(@Param("followerId") Long followerId, Pageable pageable);

	/**
	 * 팔로워 수 조회
	 */
	@Query("SELECT COUNT(f) FROM Follow f WHERE f.followingId = :userId")
	long countFollowers(@Param("userId") Long userId);

	/**
	 * 팔로잉 수 조회
	 */
	@Query("SELECT COUNT(f) FROM Follow f WHERE f.followerId = :userId")
	long countFollowings(@Param("userId") Long userId);

	/**
	 * 팔로우 관계 삭제
	 */
	void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

	/**
	 * 사용자의 모든 팔로우 관계 삭제 (회원 삭제 등)
	 */
	void deleteByFollowerIdOrFollowingId(Long userId, Long userId2);

	/**
	 * 특정 사용자들이 서로 팔로우하는지 확인 (맞팔 체크)
	 */
	@Query("SELECT CASE WHEN COUNT(f) = 2 THEN true ELSE false END FROM Follow f " +
		"WHERE (f.followerId = :userId1 AND f.followingId = :userId2) " +
		"OR (f.followerId = :userId2 AND f.followingId = :userId1)")
	boolean isMutualFollow(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

	/**
	 * 여러 사용자에 대한 팔로우 여부 일괄 조회 (N+1 방지)
	 */
	@Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId AND f.followingId IN :followingIds")
	List<Long> findFollowingIdsByFollowerIdAndFollowingIdIn(
		@Param("followerId") Long followerId,
		@Param("followingIds") List<Long> followingIds
	);
}
