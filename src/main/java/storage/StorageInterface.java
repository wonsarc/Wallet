package storage;

import java.util.List;
import java.util.UUID;

public interface StorageInterface<T> {
    void save(T item);

    List<T> loadAll();

    T load(UUID id);

    void update(T item);

    void delete(UUID id);
}

