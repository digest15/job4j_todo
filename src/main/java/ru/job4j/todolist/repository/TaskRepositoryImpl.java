package ru.job4j.todolist.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final SessionFactory sf;

    public TaskRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Task add(Task task) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return task;
    }

    @Override
    public void update(Task task) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.update(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(Task task) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.delete(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = List.of();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            tasks = session.createQuery("from Task", Task.class)
                    .getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return tasks;
    }

    @Override
    public Optional<Task> findById(int id) {
        Optional<Task> task = Optional.empty();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            task = session.createQuery("from Task WHERE id = :id", Task.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return task;
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        List<Task> tasks = List.of();

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            tasks = session.createQuery("from Task like description = :des", Task.class)
                    .setParameter("des", key)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return tasks;
    }
}
