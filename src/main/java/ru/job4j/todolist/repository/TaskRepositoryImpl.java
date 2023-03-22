package ru.job4j.todolist.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final CrudRepository crudRepository;

    @Override
    public Task add(Task task) {
        return crudRepository.tx(session -> {
            session.save(task);
            return task;
        });
    }

    @Override
    public boolean update(Task task) {
        return crudRepository.run(session -> {
            session.update(task);
            return true;
        });
    }

    @Override
    public boolean updateByDone(int id, boolean isDone) {
        return crudRepository.tx(session -> {
            var result = false;
            Task task = session.createQuery("from Task t where t.id = :id", Task.class)
                    .setParameter("id", id)
                    .uniqueResult();
            if (task != null) {
                task.setDone(isDone);
                result = true;
            }
            return result;
        });
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE Task WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public List<Task> findAll() {
        return crudRepository.query("from Task t join fetch t.priority", Task.class);
    }

    @Override
    public Optional<Task> findById(int id) {
        return crudRepository.optional(
                "from Task t join fetch t.priority WHERE t.id = :id",
                Task.class,
                Map.of("id", id));
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        return crudRepository.query(
                "from Task t join fetch t.priority like t.description = :des",
                Task.class,
                Map.of("des", key)
        );
    }

    @Override
    public List<Task> findByDone(boolean isDone) {
        return crudRepository.query(
                "from Task t join fetch t.priority where t.done = :isDone",
                Task.class,
                Map.of("isDone", isDone)
        );
    }
}
