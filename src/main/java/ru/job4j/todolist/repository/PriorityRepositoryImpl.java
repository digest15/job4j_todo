package ru.job4j.todolist.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.Priority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PriorityRepositoryImpl implements PriorityRepository {

    private final CrudRepository crudRepository;

    @Override
    public Priority add(Priority priority) {
        return crudRepository.tx(session -> {
            session.save(priority);
            return priority;
        });
    }

    @Override
    public boolean update(Priority priority) {
        return crudRepository.run(session -> {
            session.update(priority);
            return true;
        });
    }

    @Override
    public boolean delete(Priority priority) {
        return crudRepository.run(session -> {
            session.delete(priority);
            return true;
        });
    }

    @Override
    public Optional<Priority> findById(int id) {
        return crudRepository.optional(
                "from Priority WHERE id = :id",
                Priority.class,
                Map.of("id", id));
    }

    @Override
    public List<Priority> findAll() {
        return crudRepository.query("from Priority", Priority.class);
    }
}
