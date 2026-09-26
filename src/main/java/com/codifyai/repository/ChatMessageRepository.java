package com.codifyai.repository;

import com.codifyai.entity.ChatMessage;
import com.codifyai.entity.ChatSession;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<@NonNull ChatMessage, @NonNull Long> {
    @Query("""
            SELECT DISTINCT m FROM ChatMessage m
            LEFT JOIN FETCH m.events e
            WHERE m.chatSession = :chatSession
            ORDER BY m.createdAt ASC
            
            """)
    List<ChatMessage> findByChatSession(ChatSession chatSession);
}
