package ru.job4j.todolist.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TaskRepositoryImpl implements TaskRepository {

    private final SessionFactory sf;

    public TaskRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Task add(Task task) {
        Task result = null;

        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();

            result = task;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return result;
    }

    @Override
    public boolean update(Task task) {
        var result = false;

        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.update(task);
            session.getTransaction().commit();

            result = true;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return result;
    }

    @Override
    public boolean updateByDone(int id, boolean isDone) {
        var result = false;

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            Task task = session.createQuery("from Task WHERE id = :id", Task.class)
                    .setParameter("id", id)
                    .uniqueResult();
            if (task != null) {
                task.setDone(isDone);
                result = true;
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public boolean delete(int id) {
        var result = false;

        var session = sf.openSession();
        try {
            session.beginTransaction();
            var count = session.createQuery("DELETE Task WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            session.getTransaction().commit();

            result = count > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return result;
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
            log.error("Something was wrong", e);
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
            log.error("Something was wrong", e);
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
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }
        return tasks;
    }

    @Override
    public List<Task> findByDone(boolean isDone) {
        List<Task> tasks = List.of();

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            tasks = session.createQuery("from Task where done = :isDone", Task.class)
                    .setParameter("isDone", isDone)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }
        return tasks;
    }
}
