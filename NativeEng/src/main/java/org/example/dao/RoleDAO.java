package org.example.dao;

import com.google.inject.Inject;
import org.example.entities.Idiom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDAO
        extends AbstractDAO {
    @Inject
    public RoleDAO(Connection connection) {
        super(connection);
    }

    public String getRoleById(int id) {
        String sql = "SELECT r.* FROM Roles r WHERE r.`Id`=?";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setInt(1, id);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                return res.getString("Role");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }
}
