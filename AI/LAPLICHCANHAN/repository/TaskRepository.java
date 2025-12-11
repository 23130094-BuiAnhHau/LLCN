package LAPLICHCANHAN.repository;

import LAPLICHCANHAN.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findPendingByUser(UUID userId);

    void delete(UUID id);
}
