package ru.job4j.todolist.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CrudRepository crudRepository;

    @Override
    public Category add(Category category) {
        return crudRepository.tx(session -> {
            session.save(category);
            return category;
        });
    }

    @Override
    public boolean update(Category category) {
        return crudRepository.run(session -> {
            session.update(category);
            return true;
        });
    }

    @Override
    public boolean delete(Category category) {
        return crudRepository.run(session -> {
            session.delete(category);
            return true;
        });
    }

    @Override
    public List<Category> findAll() {
        return crudRepository.query("from Category", Category.class);
    }

    @Override
    public Optional<Category> findById(int id) {
        return crudRepository.optional(
                "from Category WHERE id = :id",
                Category.class,
                Map.of("id", id)
        );
    }
}
