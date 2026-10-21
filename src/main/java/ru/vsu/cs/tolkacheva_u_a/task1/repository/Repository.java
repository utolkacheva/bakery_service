package ru.vsu.cs.tolkacheva_u_a.task1.repository;

import ru.vsu.cs.tolkacheva_u_a.task1.entity.AbstractEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<E extends AbstractEntity> {
    void save(E entity);
    void deleteById(UUID id);
    Optional<E> findById(UUID id);
    List<E> findAll();
    boolean existsById(UUID id);
}
