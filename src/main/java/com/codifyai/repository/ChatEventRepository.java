package com.codifyai.repository;

import com.codifyai.entity.ChatEvent;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<@NonNull ChatEvent,@NonNull Long> {
}
