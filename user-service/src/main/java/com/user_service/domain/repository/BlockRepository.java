package com.user_service.domain.repository;

import com.user_service.domain.model.Block;
import com.user_service.domain.model.BlockId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockId> {

	/**
	 * 차단 관계 존재 여부 확인
	 */
	boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	/**
	 * 차단 관계 조회
	 */
	Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	/**
	 * 내가 차단한 사용자 목록 조회
	 */
	@Query("SELECT b.blockedId FROM Block b WHERE b.blockerId = :blockerId ORDER BY b.createdAt DESC")
	Slice<Long> findBlockedIds(@Param("blockerId") Long blockerId, Pageable pageable);

	/**
	 * 나를 차단한 사용자 목록 조회 (관리자용)
	 */
	@Query("SELECT b.blockerId FROM Block b WHERE b.blockedId = :blockedId ORDER BY b.createdAt DESC")
	Slice<Long> findBlockerIds(@Param("blockedId") Long blockedId, Pageable pageable);

	/**
	 * 차단한 사용자 수 조회
	 */
	@Query("SELECT COUNT(b) FROM Block b WHERE b.blockerId = :userId")
	long countBlockedUsers(@Param("userId") Long userId);

	/**
	 * 차단 관계 삭제
	 */
	void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	/**
	 * 사용자의 모든 차단 관계 삭제 (회원 삭제 시)
	 */
	void deleteByBlockerIdOrBlockedId(Long userId, Long userId2);

	/**
	 * 양방향 차단 여부 확인
	 */
	@Query("SELECT CASE WHEN COUNT(b) = 2 THEN true ELSE false END FROM Block b " +
		"WHERE (b.blockerId = :userId1 AND b.blockedId = :userId2) " +
		"OR (b.blockerId = :userId2 AND b.blockedId = :userId1)")
	boolean isMutualBlock(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

	/**
	 * 여러 사용자에 대한 차단 여부 일괄 조회 (N+1 방지)
	 */
	@Query("SELECT b.blockedId FROM Block b WHERE b.blockerId = :blockerId AND b.blockedId IN :blockedIds")
	List<Long> findBlockedIdsByBlockerIdAndBlockedIdIn(
		@Param("blockerId") Long blockerId,
		@Param("blockedIds") List<Long> blockedIds
	);

	/**
	 * 양방향 차단 확인 (A가 B를 차단했거나 B가 A를 차단한 경우)
	 */
	@Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Block b " +
		"WHERE (b.blockerId = :userId1 AND b.blockedId = :userId2) " +
		"OR (b.blockerId = :userId2 AND b.blockedId = :userId1)")
	boolean isEitherBlocked(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}