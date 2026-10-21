package ru.vsu.cs.tolkacheva_u_a.task1.repository.impl;

import ru.vsu.cs.tolkacheva_u_a.task1.entity.AbstractEntity;
import ru.vsu.cs.tolkacheva_u_a.task1.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractInMemoryRepository<E extends AbstractEntity> implements Repository<E> {
    private final Map<UUID, E> storage = new HashMap<>();

    @Override
    public void save(E entity) {
        if (entity == null) throw new IllegalArgumentException("Нельзя сохранить null.");
        storage.put(entity.getId(), entity);
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public Optional<E> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<E> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }
}