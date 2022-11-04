package org.example.entities;

import org.example.dao.UserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Idiom
        implements Entity {
    private String id;
    private String idiom;
    private String translate;
    private String example;
    private User user;

    public Idiom() {
    }

    public Idiom(ResultSet res) throws SQLException {
        id = res.getString("Id");
        idiom = res.getString("Idiom");
        translate = res.getString("Translate");
        example = res.getString("Example");
    }

    public String getId() {
        return id;
    }

    public String getIdiom() {
        return idiom;
    }

    public String getTranslate() {
        return translate;
    }

    public String getExample() {
        return example;
    }

    public User getUser() {
        return user;
    }

    public Idiom setId(String id) {
        this.id = id;
        return this;
    }

    public Idiom setIdiom(String idiom) {
        this.idiom = idiom;
        return this;
    }

    public Idiom setTranslate(String translate) {
        this.translate = translate;
        return this;
    }

    public Idiom setExample(String example) {
        this.example = example;
        return this;
    }

    public Idiom setUser(User user) {
        this.user = user;
        return this;
    }
}
