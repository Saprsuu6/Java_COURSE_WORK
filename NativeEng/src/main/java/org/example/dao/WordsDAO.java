package org.example.dao;

import com.google.inject.Inject;
import org.example.entities.Entity;
import org.example.entities.Idiom;
import org.example.entities.User;
import org.example.entities.Word;

import javax.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class WordsDAO
        extends AbstractDAO
        implements DAO {
    private final UserDAO userDAO;

    @Inject
    public WordsDAO(Connection connection,
                    @Named("userDAO") DAO userDAO) {
        super(connection);
        this.userDAO = (UserDAO) userDAO;
    }

    /**
     * Inserts words in DB `NativeEnglish.Idioms` table
     *
     * @param entity data to insert
     * @return `id` of new record or null if fails
     */
    @Override
    public String addEntity(Entity entity) {
        Word word = (Word) entity;

        String id = UUID.randomUUID().toString();

        String sql = "INSERT INTO Words(`Id`,`Word`,`Translate`,`Example`,`User_id`)"
                + " VALUES(?,?,?,?,?)";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            prep.setString(2, word.getWord());
            prep.setString(3, word.getTranslate());
            prep.setString(4, word.getExample());
            prep.setString(5, word.getUser().getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("WordDAO::addWord " + ex.getMessage());
            System.out.println(sql);
            return null;
        }
        return id;
    }

    /**
     * Update words in DB `NativeEnglish.Words` table
     *
     * @param entity data to update
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean updateEntity(Entity entity) {
        Word word = (Word) entity;

        if (word == null || word.getId() == null) {
            return false;
        }

        Map<String, String> data = new HashMap<>();
        if (word.getWord() != null) data.put("Word", word.getWord());
        if (word.getTranslate() != null) data.put("Translate", word.getTranslate());
        if (word.getExample() != null) data.put("Example", word.getExample());

        StringBuilder sql = new StringBuilder("UPDATE Words u SET ");
        if (!super.update(data, sql)) {
            return false;
        }

        try (PreparedStatement prep = connection.prepareStatement(sql.toString())) {
            int n = 1;
            for (String fieldName : data.keySet()) {
                prep.setString(n, data.get(fieldName));
                ++n;
            }

            prep.setString(n, word.getId());
            prep.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("WordDAO::updateWord " + exception.getMessage());
            System.out.println(sql);
            return false;
        }

        return true;
    }

    /**
     * Delete words in DB `NativeEnglish.Idioms` table
     *
     * @param entity data to delete
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean deleteEntity(Entity entity) {
        Word word = (Word) entity;

        if (word == null || word.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM Words WHERE id=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, word.getUser().getId());
            return prep.execute();
        } catch (SQLException ex) {
            System.out.println("WordDAO::deleteWord " + ex.getMessage());
            System.out.println(sql);
        }

        return false;
    }

    /**
     * Checks words table for idiom given
     *
     * @param criterion value to look for
     * @return true if login is in table
     */
    @Override
    public boolean isEntityUsed(String criterion) {
        String sql = "SELECT COUNT(i.`Id`) FROM Idioms i WHERE i.`Word`=?";
        return !super.isUsed(sql, criterion);
    }

    /**
     * To get user word from table Words
     *
     * @param user to set user id in query
     * @return List<Words> or null
     */
    public List<Entity> getUserWords(User user) {
        String sql = "SELECT i.* FROM Words i WHERE i.`User_id`=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, user.getId());
            ResultSet res = prep.executeQuery();
            List<Entity> wordList = new ArrayList<>();

            while (res.next()) {
                Word word = new Word(res);
                word.setUser(user);
                wordList.add(word);
            }

            return wordList;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    /**
     * Gets word form DB by id
     *
     * @param id Credentials: id
     * @return User or null
     */
    public Word getIdiomById(String id) {
        String sql = "SELECT u.* FROM Words u WHERE u.`Id`=?";
        return getIdiom(sql, id);
    }

    /**
     * Gets word form DB by context
     *
     * @param context Credentials: login
     * @return Word or null
     */
    public Word getWordByContext(String context) {
        String sql = "SELECT u.* FROM Words u WHERE u.`Word`=?";
        return getIdiom(sql, context);
    }

    /**
     * Gets word form DB by translate
     *
     * @param translate Credentials: name
     * @return User or null
     */
    public Word getWordByTranslate(String translate) {
        String sql = "SELECT u.* FROM Words u WHERE u.`Translate`=?";
        return getIdiom(sql, translate);
    }

    private Word getIdiom(String sql, String credentials) {
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, credentials);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                Word word = new Word(res);
                String userId = res.getString("User_id");
                //word.setUser(userDAO.getUserById(userId));

                return word;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }
}
