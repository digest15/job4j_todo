package ru.job4j.todolist.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todolist.model.Priority;
import ru.job4j.todolist.repository.PriorityRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PriorityServiceImpl implements PriorityService {

    private final PriorityRepository priorityRepository;

    @Override
    public Priority add(Priority priority) {
        return priorityRepository.add(priority);
    }

    @Override
    public boolean update(Priority priority) {
        return priorityRepository.update(priority);
    }

    @Override
    public boolean delete(Priority priority) {
        return priorityRepository.delete(priority);
    }

    @Override
    public Optional<Priority> findById(int id) {
        return priorityRepository.findById(id);
    }

    @Override
    public List<Priority> findAll() {
        return priorityRepository.findAll();
    }

}
