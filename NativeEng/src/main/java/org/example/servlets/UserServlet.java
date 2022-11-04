package org.example.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.example.dao.DAO;
import org.example.dao.IdiomDAO;
import org.example.dao.UserDAO;
import org.example.dao.WordsDAO;
import org.example.entities.Entity;
import org.example.entities.User;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class UserServlet extends HttpServlet {
    private final UserDAO userDAO;
    private final IdiomDAO idiomDAO;
    private final WordsDAO wordDAO;

    @Inject
    public UserServlet(@Named("userDAO") DAO userDAO,
                       @Named("idiomDAO") DAO idiomDAO,
                       @Named("wordDAO") DAO wordDAO) {
        this.userDAO = (UserDAO) userDAO;
        this.idiomDAO = (IdiomDAO) idiomDAO;
        this.wordDAO = (WordsDAO) wordDAO;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User authUser = (User) req.getAttribute("authUser");
        String userLogin = req.getParameter("userLogin");
        Entity user = (Entity) req.getAttribute("User");
        PrintWriter out = resp.getWriter();
        boolean res = false;

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            if ((authUser.getRole().equals("User") || authUser.getRole().equals("Subscriber"))
                    && !authUser.getLogin().equals((userLogin))) {
                out.write("You may delete only yours account");
            } else {
                if (user != null) {
                    res = userDAO.deleteEntity(user);
                } else {
                    if (userLogin == null) {
                        out.write("You have to set user login");
                    } else {
                        User currentUser = userDAO.getUserByLogin(userLogin);
                        res = userDAO.deleteEntity(currentUser);
                    }
                }
            }
        }

        out.write(res ? "Successfully delete user 'userLogin'" : "Unsuccessfully delete user 'userLogin'");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User authUser = (User) req.getAttribute("authUser");
        String userLogin = req.getParameter("userLogin");
        PrintWriter out = resp.getWriter();

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            if (userLogin == null) {
                if (authUser.getRole().equals("User") || authUser.getRole().equals("Subscriber")) {
                    out.write("You have to set user login");
                } else {
                    List<User> userList = userDAO.getAllUsers();

                    try {
                        getResponse(userList, resp);
                    } catch (IOException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            } else {
                User currentUser = userDAO.getUserByLogin(userLogin);

                if (currentUser == null) {
                    String message = String.format("User by login '%s' was not found", userLogin);
                    out.write(message);
                } else {
                    List<Entity> idiomList = idiomDAO.getUserIdioms(currentUser);
                    List<Entity> wordList = wordDAO.getUserWords(currentUser);

                    Map<String, List<Entity>> map = new HashMap<>();
                    map.put("Idioms", idiomList);
                    map.put("Words", wordList);

                    try {
                        getResponse(map, resp);
                    } catch (IOException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            }
        }
    }

    private void getResponse(Object content, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        String jsonRes = new Gson().toJson(content);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.write(jsonRes);
        out.flush();
    }
}
