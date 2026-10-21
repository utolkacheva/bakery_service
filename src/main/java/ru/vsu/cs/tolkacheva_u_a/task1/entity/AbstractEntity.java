package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractEntity {
    private final UUID id;

    protected AbstractEntity() {
        this(UUID.randomUUID());
    }

    protected AbstractEntity(UUID id) {
        this.id = Objects.requireNonNull(id, "ID не может быть null");
    }

    public UUID getId() {
        return id;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        AbstractEntity that = (AbstractEntity) other;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
