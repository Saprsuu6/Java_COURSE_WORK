package org.example.dao;

import org.example.entities.Entity;

public interface DAO {
    public String addEntity(Entity entity);
    public boolean updateEntity(Entity entity);
    public boolean deleteEntity(Entity entity);
    public boolean isEntityUsed(String criterion);
}
