package com.lovable.codifyAI.repository;

import com.lovable.codifyAI.entity.Plan;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<@NonNull Plan,@NonNull Long> {
}
