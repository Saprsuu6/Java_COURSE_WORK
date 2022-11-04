package org.example;

import com.google.inject.Inject;
import org.example.dao.DAO;
import org.example.dao.IdiomDAO;
import org.example.dao.UserDAO;
import org.example.entities.Entity;
import org.example.entities.Idiom;
import org.example.entities.User;

import javax.inject.Named;

public class App {
    private final DAO userDAO;
    private final DAO idiomDAO;

    @Inject
    public App(@Named("userDAO") DAO userDAO,
               @Named("idiomDAO") DAO idiomDAO) {
        this.userDAO = userDAO;
        this.idiomDAO = idiomDAO;
    }

    public void run() {
//        User user = ((UserDAO)userDAO).getUserByLogin("Saprsuu6");
//
//        Idiom idiom = new Idiom();
//        idiom.setIdiom("It's rains cat and dogs")
//                .setTranslate("Льёт как из ведра")
//                .setExample("Outside rains cat and dogs")
//                .setUser(user);
//
//        idiomDAO.addEntity(idiom);

//        Idiom idiom = ((IdiomDAO) idiomDAO).getIdiomByContext("It's rains cat and dogs");
//
 //       idiom.setTranslate("It's rain");
 //       idiomDAO.updateEntity(idiom);
    }
}