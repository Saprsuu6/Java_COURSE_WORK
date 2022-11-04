package org.example.configurations;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.example.dao.*;
import org.example.services.hash.HashService;
import org.example.services.hash.Sha1HashService;

import java.rmi.Naming;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigModule
        extends AbstractModule
        implements AutoCloseable {

    @Override
    public void close() throws Exception {
        if (connection != null)
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        if (mySqlDriver != null)
            try {
                DriverManager.deregisterDriver(mySqlDriver);
            } catch (Exception ignored) {
            }
    }

    @Override
    protected void configure() {
        bind(HashService.class).to(Sha1HashService.class);
        bind(DAO.class).annotatedWith(Names.named("userDAO")).to(UserDAO.class);
        bind(DAO.class).annotatedWith(Names.named("idiomDAO")).to(IdiomDAO.class);
        bind(DAO.class).annotatedWith(Names.named("wordDAO")).to(WordsDAO.class);
    }

    private Connection connection;
    private Driver mySqlDriver;

    @Provides
    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            mySqlDriver = new com.mysql.cj.jdbc.Driver();
            String connectionString = "jdbc:mysql://localhost:3306/NativeEnglish" +
                    "?useUnicode=true&characterEncoding=UTF-8";
            connection = DriverManager.getConnection(connectionString, "AndrySaprigin", "cormax25524");
        }
        return connection;
    }
}
