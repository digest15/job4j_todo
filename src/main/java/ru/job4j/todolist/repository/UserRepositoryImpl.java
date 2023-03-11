package ru.job4j.todolist.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todolist.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sf;

    public UserRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public User add(User user) {
        User result = null;

        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();

            result = user;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return result;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        Optional<User> user = Optional.empty();

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            user = session.createQuery("from User where login = :login and password = :password", User.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> user = Optional.empty();

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            user = session.createQuery("from User where id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return user;
    }

    @Override
    public Collection<User> findAll() {
        List<User> users = List.of();

        Session session = sf.openSession();
        try {
            session.beginTransaction();
            users = session.createQuery("from User", User.class)
                    .getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error("Something was wrong", e);
        } finally {
            session.close();
        }

        return users;
    }

    @Override
    public boolean delete(int id) {
        var result = false;

        var session = sf.openSession();
        try {
            session.beginTransaction();
            var count = session.createQuery("DELETE User WHERE id = :id")
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
}
