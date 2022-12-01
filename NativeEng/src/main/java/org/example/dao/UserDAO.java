package org.example.dao;

import com.google.inject.Inject;
import org.example.entities.Entity;
import org.example.entities.User;
import org.example.services.hash.Sha1HashService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDAO
        extends AbstractDAO
        implements DAO {
    private final Sha1HashService hashService;
    private final RoleDAO roleDAO;

    @Inject
    public UserDAO(Connection connection, Sha1HashService hashService,
                   RoleDAO roleDAO) {
        super(connection);
        this.hashService = hashService;
        this.roleDAO = roleDAO;
    }

    /**
     * Inserts user in DB `NativeEnglish.Users` table
     *
     * @param entity data to insert
     * @return `id` of new record or null if fails
     */
    @Override
    public String addEntity(Entity entity) {
        User user = (User) entity;

        String id = UUID.randomUUID().toString();
        String salt = hashService.hash(UUID.randomUUID().toString());
        String passHash = this.hashPassword(user.getPassword(), salt);

        String sql = "INSERT INTO Users(`Id`,`Login`,`Password`,`Name`,`Salt`,`Role`)"
                + " VALUES(?,?,?,?,?,?)";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            prep.setString(2, user.getLogin());
            prep.setString(3, passHash);
            prep.setString(4, user.getName());
            prep.setString(5, salt);
            prep.setInt(6, Integer.parseInt(user.getRole()));
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("UserDAO::addUser " + ex.getMessage());
            System.out.println(sql);
            return null;
        }
        return id;
    }

    /**
     * Update user in DB `NativeEnglish.Users` table
     *
     * @param entity data to update
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean updateEntity(Entity entity) {
        User user = (User) entity;

        if (user == null || user.getId() == null) {
            return false;
        }

        Map<String, String> data = new HashMap<>();
        if (user.getName() != null) data.put("Name", user.getName());
        if (user.getLogin() != null) data.put("Login", user.getLogin());
        if (user.getPassword() != null) {
            String salt = hashService.hash(UUID.randomUUID().toString());
            String passHash = this.hashPassword(user.getPassword(), salt);
            user.setPassword(passHash);
            user.setSalt(salt);

            data.put("Password", user.getPassword());
            data.put("Salt", user.getSalt());
        }
        if (user.getRole() != null) data.put("Role", user.getRole());

        StringBuilder sql = new StringBuilder("UPDATE Users u SET ");
        if (!super.update(data, sql)) {
            return false;
        }

        try (PreparedStatement prep = connection.prepareStatement(sql.toString())) {
            int n = 1;
            for (String fieldName : data.keySet()) {
                if (fieldName.equals("Role")) {
                    prep.setInt(n, Integer.parseInt(data.get(fieldName)));
                } else {
                    prep.setString(n, data.get(fieldName));
                }
                ++n;
            }

            prep.setString(n, user.getId());
            prep.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("UserDAO::updateUser " + exception.getMessage());
            System.out.println(sql);
            return false;
        }

        return true;
    }

    /**
     * Delete user in DB `NativeEnglish.Users` table
     *
     * @param entity data to delete
     * @return `id` of new record or null if fails
     */
    @Override
    public boolean deleteEntity(Entity entity) {
        User user = (User) entity;

        if (user == null || user.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM Users WHERE id=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, user.getId());
            return prep.execute();
        } catch (SQLException ex) {
            System.out.println("UserDAO::deleteUser " + ex.getMessage());
            System.out.println(sql);
        }

        return false;
    }

    /**
     * Checks User table for login given
     *
     * @param criterion value to look for
     * @return true if login is in table
     */
    @Override
    public boolean isEntityUsed(String criterion) {
        String sql = "SELECT COUNT(u.`Id`) FROM Users u WHERE u.`Login`=?";
        return !super.isUsed(sql, criterion);
    }

    /**
     * Calculates hash (optionally salted) from password
     *
     * @param password Open password string
     * @return hash for DB table
     */
    public String hashPassword(String password, String salt) {
        return hashService.hash(salt + password + salt);
    }

    /**
     * Return all users from Users
     *
     * @return List<User> or null
     */
    public List<User> getAllUsers() {
        String sql = "SELECT u.* from nativeenglish.users u";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            ResultSet res = prep.executeQuery();
            List<User> userList = new ArrayList<>();

            while (res.next()) {
                User currentUser = new User(res);
                int roleId = res.getInt("Role");
                currentUser.setRole(roleDAO.getRoleById(roleId));

                userList.add(currentUser);
            }

            return userList;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    /**
     * Gets user form DB by login and password
     *
     * @param login Credentials: login
     * @param pass  Credentials: password
     * @return User or null
     */
    public User getUserByCredentials(String login, String pass) {
        String sql = "SELECT u.* FROM Users u WHERE u.`login`=?";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                User user = new User(res);

                String expectedHash = this.hashPassword(pass, user.getSalt());
                if (expectedHash.equals(user.getPassword())) {
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    /**
     * Gets user form DB by id
     *
     * @param id Credentials: id
     * @return User or null
     */
    public User getUserById(String id) {
        String sql = "SELECT u.* FROM Users u WHERE u.`Id`=?";
        return getUser(sql, id);
    }

    /**
     * Gets user form DB by Login
     *
     * @param login Credentials: login
     * @return User or null
     */
    public User getUserByLogin(String login) {
        String sql = "SELECT u.* FROM Users u WHERE u.`Login`=?";
        return getUser(sql, login);
    }

    /**
     * Gets user form DB by Name
     *
     * @param name Credentials: name
     * @return User or null
     */
    public User getUserByName(String name) {
        String sql = "SELECT u.* FROM Users u WHERE u.`Name`=?";
        return getUser(sql, name);
    }

    private User getUser(String sql, String credentials) {
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, credentials);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                User currentUser = new User(res);
                int roleId = res.getInt("Role");
                currentUser.setRole(roleDAO.getRoleById(roleId));

                return currentUser;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }
}
