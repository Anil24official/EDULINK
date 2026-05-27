package com.edulink.identityservice.repository;

import com.edulink.identityservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spec §8 — immutable audit log. Delete operations are blocked at the repository layer
 * in addition to the entity-level {@code @PreRemove} guard.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUserEmailOrderByTimestampDesc(String userEmail, Pageable pageable);

    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    @Override
    default void delete(AuditLog entity) {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }

    @Override
    default void deleteById(Long id) {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }

    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }

    @Override
    default void deleteAll(Iterable<? extends AuditLog> entities) {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }

    @Override
    default void deleteAllInBatch() {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }

    @Override
    default void deleteAllById(Iterable<? extends Long> ids) {
        throw new UnsupportedOperationException("AuditLog is immutable (spec §8).");
    }
}
