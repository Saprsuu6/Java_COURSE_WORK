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

    public void setId(String id) {
        this.id = id;
    }

    public void setWord(String idiom) {
        this.word = idiom;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
