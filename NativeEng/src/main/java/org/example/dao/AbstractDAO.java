package org.example.dao;

import org.example.entities.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAO{
    protected final Connection connection;

    public AbstractDAO(Connection connection) {
        this.connection = connection;
    }

    protected boolean update(Map<String, String> data, StringBuilder sql) {
        boolean needComma = false;
        for (String fieldName : data.keySet()) {
            sql.append(String.format(" %c u.`%s` = ? ",
                    (needComma ? ',' : ' '), fieldName));
            needComma = true;
        }
        sql.append(" WHERE u.`id` = ? ");

        return needComma;
    }

    protected boolean isUsed(String sql, String criterion) {
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, criterion);
            ResultSet res = prep.executeQuery();
            res.next();
            return res.getInt(1) <= 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
            return false;
        }
    }
}
