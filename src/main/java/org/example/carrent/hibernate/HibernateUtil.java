package org.example.carrent.hibernate;

import org.example.carrent.entities.RentalEntity;
import org.example.carrent.entities.UserEntity;
import org.example.carrent.entities.VehicleEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        String url = System.getenv("DATABASE_URL");
        if (url == null) throw new RuntimeException("Brak zmiennej DATABASE_URL!");

        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(UserEntity.class)
                .addAnnotatedClass(VehicleEntity.class)
                .addAnnotatedClass(RentalEntity.class)
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
