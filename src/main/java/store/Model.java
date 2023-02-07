package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import store.model.*;

import java.util.Properties;

public class Model implements AutoCloseable {

    private SessionFactory factory;

    public Session getSession() {
        if (factory == null) createFactory();
        return factory.getCurrentSession();
    }

    private void createFactory() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.put("hibernate.connection.url", System.getenv("url"));
        properties.put("hibernate.connection.username", System.getenv("user"));
        properties.put("hibernate.connection.password", System.getenv("password"));
        properties.put("hibernate.current_session_context_class", "thread");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        ServiceRegistry builder = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Book.class);
        configuration.addAnnotatedClass(Stock.class);
        configuration.addAnnotatedClass(StockKey.class);
        configuration.addAnnotatedClass(Store.class);
        configuration.addAnnotatedClass(Author.class);

        factory = configuration.buildSessionFactory(builder);
    }

    @Override
    public void close() throws Exception {
        if (factory != null && factory.isOpen()) factory.close();
    }
}
