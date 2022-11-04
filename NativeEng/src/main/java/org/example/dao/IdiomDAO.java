package org.example.dao;

import com.google.inject.Inject;
import org.example.entities.Entity;
import org.example.entities.Idiom;
import org.example.entities.User;

import javax.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class IdiomDAO
        extends AbstractDAO
        implements DAO {
    private final UserDAO userDAO;

    @Inject
    public IdiomDAO(Connection connection,
                    @Named("userDAO") DAO userDAO) {
        super(connection);
        this.userDAO = (UserDAO) userDAO;
    }

    /**
     * Inserts idiom in DB `NativeEnglish.Idioms` table
     *
     * @param entity data to insert
     * @return `id` of new record or null if fails
     */
    @Override
    public String addEntity(Entity entity) {
        Idiom idiom = (Idiom) entity;

        String id = UUID.randomUUID().toString();

        String sql = "INSERT INTO Idioms(`Id`,`Idiom`,`Translate`,`Example`,`User_id`)"
                + " VALUES(?,?,?,?,?)";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            prep.setString(2, idiom.getIdiom());
            prep.setString(3, idiom.getTranslate());
            prep.setString(4, idiom.getExample());
            prep.setString(5, idiom.getUser().getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("IdiomDAO::addIdiom " + ex.getMessage());
            System.out.println(sql);
            return null;
        }
        return id;
    }

    /**
     * Update idiom in DB `NativeEnglish.Idioms` table
     *
     * @param entity data to update
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean updateEntity(Entity entity) {
        Idiom idiom = (Idiom) entity;

        if (idiom == null || idiom.getId() == null) {
            return false;
        }

        Map<String, String> data = new HashMap<>();
        if (idiom.getIdiom() != null) data.put("Idiom", idiom.getIdiom());
        if (idiom.getTranslate() != null) data.put("Translate", idiom.getTranslate());
        if (idiom.getExample() != null) data.put("Example", idiom.getExample());

        StringBuilder sql = new StringBuilder("UPDATE Idioms u SET ");
        if (!super.update(data, sql)) {
            return false;
        }

        try (PreparedStatement prep = connection.prepareStatement(sql.toString())) {
            int n = 1;
            for (String fieldName : data.keySet()) {
                prep.setString(n, data.get(fieldName));
                ++n;
            }

            prep.setString(n, idiom.getId());
            prep.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("IdiomDAO::updateIdiom " + exception.getMessage());
            System.out.println(sql);
            return false;
        }

        return true;
    }

    /**
     * Delete idiom in DB `NativeEnglish.Idioms` table
     *
     * @param entity data to delete
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean deleteEntity(Entity entity) {
        Idiom idiom = (Idiom) entity;

        if (idiom == null || idiom.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM Idioms WHERE id=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, idiom.getUser().getId());
            return prep.execute();
        } catch (SQLException ex) {
            System.out.println("IdiomDAO::deleteIdiom " + ex.getMessage());
            System.out.println(sql);
        }

        return false;
    }

    /**
     * Checks Idioms table for idiom given
     *
     * @param criterion value to look for
     * @return true if login is in table
     */
    @Override
    public boolean isEntityUsed(String criterion) {
        String sql = "SELECT COUNT(i.`Id`) FROM Idioms i WHERE i.`Idiom`=?";
        return !super.isUsed(sql, criterion);
    }

    /**
     * To get user idioms from table Idioms
     *
     * @param user to set user id in query
     * @return List<Idiom> or null
     */
    public List<Entity> getUserIdioms(User user) {
        String sql = "SELECT i.* FROM Idioms i WHERE i.`User_id`=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, user.getId());
            ResultSet res = prep.executeQuery();
            List<Entity> idiomList = new ArrayList<>();

            while (res.next()) {
                Idiom idiom = new Idiom(res);
                idiom.setUser(user);
                idiomList.add(idiom);
            }

            return idiomList;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    /**
     * Gets idiom form DB by id
     *
     * @param id Credentials: id
     * @return User or null
     */
    public Idiom getIdiomById(String id) {
        String sql = "SELECT i.* FROM Idioms i WHERE i.`Id`=?";
        return getIdiom(sql, id);
    }

    /**
     * Gets idiom form DB by context
     *
     * @param context Credentials: login
     * @return User or null
     */
    public Idiom getIdiomByContext(String context) {
        String sql = "SELECT i.* FROM Idioms i WHERE i.`Idiom`=?";
        return getIdiom(sql, context);
    }

    /**
     * Gets idiom form DB by translate
     *
     * @param translate Credentials: name
     * @return User or null
     */
    public Idiom getIdiomByTranslate(String translate) {
        String sql = "SELECT i.* FROM Users i WHERE i.`Translate`=?";
        return getIdiom(sql, translate);
    }

    private Idiom getIdiom(String sql, String credentials) {
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, credentials);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                Idiom idiom = new Idiom(res);
                String userId = res.getString("User_id");
                idiom.setUser(userDAO.getUserById(userId));

                return idiom;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }
}
