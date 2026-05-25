package org.example.carrent.repositories.impl;

import org.example.carrent.entities.UserEntity;
import org.example.carrent.hibernate.HibernateUtil;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements IUserRepository {

    public UserHibernateRepository() {
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserEntity entity = session.get(UserEntity.class, login);
            return Optional.ofNullable(entity).map(this::toModel);
        }
    }

    @Override
    public List<User> getUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserEntity", UserEntity.class)
                    .list()
                    .stream()
                    .map(this::toModel)
                    .toList();
        }
    }

    @Override
    public void add(User user) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            UserEntity existing = session.get(UserEntity.class, user.getLogin());

            if (existing != null) {
                existing.setPasswordHash(user.getPasswordHash());
                existing.setRole(user.getRole().name());
                session.merge(existing);
            } else {
                UserEntity entity = new UserEntity(
                        user.getLogin(),
                        user.getPasswordHash(),
                        user.getRole().name()
                );

                session.persist(entity);
            }

            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean remove(String login) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            UserEntity entity = session.get(UserEntity.class, login);

            if (entity == null) {
                tx.rollback();
                return false;
            }

            session.remove(entity);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean update(User user) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            UserEntity entity = session.get(UserEntity.class, user.getLogin());

            if (entity == null) {
                tx.rollback();
                return false;
            }

            entity.setPasswordHash(user.getPasswordHash());
            entity.setRole(user.getRole().name());

            session.merge(entity);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    private User toModel(UserEntity entity) {
        return new User(
                entity.getLogin(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole())
        );
    }
}