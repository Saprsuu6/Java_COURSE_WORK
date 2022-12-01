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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
        HttpSession session = req.getSession();
        User authUser = (User) session.getAttribute("authUser");
        PrintWriter out = resp.getWriter();

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            boolean res = false;
            String userLogin = null;

            if (req.getParameter("userLogin") != null) {
                userLogin = req.getParameter("userLogin");
            } else if (req.getAttribute("userLogin") != null) {
                userLogin = (String) req.getAttribute("userLogin");
            }

            if ((authUser.getRole().equals("User") || authUser.getRole().equals("Subscriber"))
                    && !authUser.getLogin().equals((userLogin))) {
                out.write("You may delete only yours account");
            } else {
                if (!(authUser.getRole().equals("User") || authUser.getRole().equals("Subscriber"))) {
                    if (userLogin == null) {
                        out.write("You have to set user login");
                    } else {
                        User currentUser = userDAO.getUserByLogin(userLogin);
                        res = userDAO.deleteEntity(currentUser);
                    }
                } else {
                    res = userDAO.deleteEntity(authUser);
                    session.removeAttribute("authUser");
                }
            }

            out.write(res ? "Successfully delete user 'userLogin'" : "Unsuccessfully delete user 'userLogin'");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User authUser = (User) session.getAttribute("authUser");
        PrintWriter out = resp.getWriter();

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            User newUser = new User();
            String reply;

            String userLogin = req.getParameter("userLogin");
            String userName = req.getParameter("userName");
            String userPassword = req.getParameter("userPassword");
            String userRole = req.getParameter("userRole");

            if (userLogin != null) {
                if (userDAO.isEntityUsed(userLogin)) {
                    out.write("Login '" + userLogin + "' already in use");
                } else {
                    newUser.setLogin(userLogin);
                }
            }

            newUser.setId(authUser.getId());
            newUser.setName(userName);
            newUser.setPassword(userPassword);
            newUser.setRole(userRole);

            if (userDAO.updateEntity(newUser)) {
                reply = "Successfully update";

                if (userPassword != null) {
                    reply += ": Password was changed successfully";
                }

                User changedUser = userDAO.getUserById(authUser.getId());
                session.setAttribute("authUser", changedUser);
            } else {
                reply = "Update error";
            }

            out.write(reply);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User authUser = (User) session.getAttribute("authUser");
        PrintWriter out = resp.getWriter();

//        StringBuffer jb = new StringBuffer();
//        String line = null;
//        try {
//            BufferedReader reader = req.getReader();
//            while ((line = reader.readLine()) != null)
//                jb.append(line);
//        } catch (Exception e) { /*report an error*/ }
//
//        JSONObject jsonObject;
//        try {
//            jsonObject =  HTTP.toJSONObject(jb.toString());
//        } catch (JSONException e) {
//            // crash and burn
//            throw new IOException("Error parsing JSON request string");
//        }

        if (authUser != null) {
            out.write("You already have registered");
        } else {
            String userLogin = req.getParameter("userLogin");
            String userPassword = req.getParameter("userPassword");
            String confirmPassword = req.getParameter("confirmPassword");
            String userName = req.getParameter("userName");
            String roleKey = req.getParameter("roleKey");

            String errorMessage = null;
            try {
                // region Data validation
                if (userLogin == null || userLogin.isEmpty()) {
                    throw new Exception("Login could not be empty");
                }
                if (!userLogin.equals(userLogin.trim())) {
                    throw new Exception("Login could not contain trailing spaces");
                }
                if (userDAO.isEntityUsed(userLogin)) {
                    throw new Exception("Login is already in use");
                }
                if (userPassword == null || userPassword.isEmpty()) {
                    throw new Exception("Password could not be empty");
                }
                if (!userPassword.equals(confirmPassword)) {
                    throw new Exception("Passwords mismatch");
                }
                if (userName == null || userName.isEmpty()) {
                    throw new Exception("Name could not be empty");
                }
                if (!userName.equals(userName.trim())) {
                    throw new Exception("Name could not contain trailing spaces");
                }
                // endregion

                User user = new User();
                user.setName(userName);
                user.setLogin(userLogin);
                user.setPassword(userPassword);

                // region Set role
                switch (roleKey) {
                    case "adminKey":
                        user.setRole("1");
                        break;
                    case "editorKey":
                        user.setRole("2");
                        break;
                    case "authorKey":
                        user.setRole("3");
                        break;
                    case "subscriberKey":
                        user.setRole("5");
                        break;
                    default:
                        user.setRole("4");
                }
                // endregion

                String id = userDAO.addEntity(user);

                if (id == null) {
                    throw new Exception("Server error, try later");
                } else {
                    User newUser = userDAO.getUserByLogin(userLogin);
                    session.setAttribute("authUser", newUser);
                    out.write("Successful add");
                }
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
                out.write(errorMessage);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User authUser = (User) session.getAttribute("authUser");
        PrintWriter out = resp.getWriter();

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            String userLogin = null;

            if (req.getParameter("userLogin") != null) {
                userLogin = req.getParameter("userLogin");
            } else if (req.getAttribute("userLogin") != null) {
                userLogin = (String) req.getAttribute("userLogin");
            }

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
