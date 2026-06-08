package org.example.carrent.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carrent.entities.UserEntity;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
@Transactional
public class UserHibernateRepository implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        UserEntity entity = entityManager.find(UserEntity.class, login);
        return Optional.ofNullable(entity).map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return entityManager
                .createQuery("FROM UserEntity", UserEntity.class)
                .getResultList()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public void add(User user) {
        entityManager.merge(toEntity(user));
    }

    @Override
    public boolean remove(String login) {
        UserEntity entity = entityManager.find(UserEntity.class, login);

        if (entity == null) {
            return false;
        }

        entityManager.remove(entity);
        return true;
    }

    @Override
    public boolean update(User user) {
        UserEntity existing = entityManager.find(UserEntity.class, user.getLogin());

        if (existing == null) {
            return false;
        }

        existing.setPasswordHash(user.getPasswordHash());
        existing.setRole(user.getRole().name());

        entityManager.merge(existing);
        return true;
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getLogin(),
                user.getPasswordHash(),
                user.getRole().name()
        );
    }

    private User toModel(UserEntity entity) {
        return new User(
                entity.getLogin(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole())
        );
    }
}