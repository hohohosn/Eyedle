package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatParticipate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipateJpaRepository extends JpaRepository<ChatParticipate, Long> {

}
