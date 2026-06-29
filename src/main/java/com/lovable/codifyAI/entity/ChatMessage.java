package com.lovable.codifyAI.entity;

import com.lovable.codifyAI.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "chat_session_id", nullable = false)
    ChatSession chatSession;

    @Column(nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role;

    @Column(length = 1000)
    String toolCalls;

    String toolCallId;
    Integer tokenUsed;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    Instant createdAt;

}
