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
    public List<User> getUsers() {
        return entityManager
                .createQuery("FROM UserEntity", UserEntity.class)
                .getResultList()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        UserEntity entity = entityManager.find(UserEntity.class, login);

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(toModel(entity));
    }

    @Override
    public void add(User user) {
        entityManager.createNativeQuery("""
                INSERT INTO users (login, password_hash, role, address)
                VALUES (:login, :passwordHash, :role, :address)
                ON CONFLICT (login) DO UPDATE SET
                    password_hash = EXCLUDED.password_hash,
                    role = EXCLUDED.role,
                    address = EXCLUDED.address
                """)
                .setParameter("login", user.getLogin())
                .setParameter("passwordHash", user.getPasswordHash())
                .setParameter("role", user.getRole().name())
                .setParameter("address", user.getAddress())
                .executeUpdate();
    }

    @Override
    public boolean update(User user) {
        int updated = entityManager.createNativeQuery("""
                UPDATE users
                SET password_hash = :passwordHash,
                    role = :role,
                    address = :address
                WHERE login = :login
                """)
                .setParameter("passwordHash", user.getPasswordHash())
                .setParameter("role", user.getRole().name())
                .setParameter("address", user.getAddress())
                .setParameter("login", user.getLogin())
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean remove(String login) {
        int deleted = entityManager.createNativeQuery("""
                DELETE FROM users
                WHERE login = :login
                """)
                .setParameter("login", login)
                .executeUpdate();

        return deleted > 0;
    }

    private User toModel(UserEntity entity) {
        return new User(
                entity.getLogin(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole()),
                entity.getAddress()
        );
    }
}