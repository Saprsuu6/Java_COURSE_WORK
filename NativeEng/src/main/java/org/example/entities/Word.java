package org.example.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Word
        implements Entity {
    private String id;
    private String word;
    private String translate;
    private String example;
    private User user;

    public Word() {
    }

    public Word(ResultSet res) throws SQLException {
        id = res.getString("Id");
        word = res.getString("Word");
        translate = res.getString("Translate");
        example = res.getString("Example");
    }

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
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

    public Word setId(String id) {
        this.id = id;
        return this;
    }

    public Word setWord(String idiom) {
        this.word = idiom;
        return this;
    }

    public Word setTranslate(String translate) {
        this.translate = translate;
        return this;
    }

    public Word setExample(String example) {
        this.example = example;
        return this;
    }

    public Word setUser(User user) {
        this.user = user;
        return this;
    }
}
