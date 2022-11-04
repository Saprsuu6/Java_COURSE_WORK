package org.example.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class User
        implements Entity {
    private String id;
    private String login;
    private String password;
    private String name;
    private String salt;
    private String role;

    public User() {
    }

    public User(ResultSet res) throws SQLException {
        id = res.getString("Id");
        login = res.getString("Login");
        password = res.getString("Password");
        name = res.getString("Name");
        salt = res.getString("Salt");
    }

    public String getRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSalt() {
        return salt;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public User setPassword(String pass) {
        this.password = pass;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }
}
