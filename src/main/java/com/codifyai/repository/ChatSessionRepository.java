package com.codifyai.repository;

import com.codifyai.entity.ChatSession;
import com.codifyai.entity.ChatSessionId;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<@NonNull ChatSession,@NonNull ChatSessionId> {
}
