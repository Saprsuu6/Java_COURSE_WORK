package org.example.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.example.dao.DAO;
import org.example.dao.IdiomDAO;
import org.example.entities.Entity;
import org.example.entities.Idiom;
import org.example.entities.User;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Singleton
public class IdiomServlet
        extends HttpServlet {
    private final IdiomDAO idiomDAO;

    @Inject
    public IdiomServlet(@Named("idiomDAO") DAO idiomDAO) {
        this.idiomDAO = (IdiomDAO) idiomDAO;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User authUser = (User) session.getAttribute("authUser");
        PrintWriter out = resp.getWriter();

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            String idiom = req.getParameter("idiom");

            Idiom forDelete = idiomDAO.getIdiomByContext(idiom);
            out.write(idiomDAO.deleteEntity(forDelete)
                    ? "Successfully delete idiom"
                    : "Unsuccessfully delete idiom");
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
            String idiom = req.getParameter("idiom");
            Idiom forUpdate = idiomDAO.getIdiomByContext(idiom);
            String reply;

            String newIdiom = req.getParameter("newIdiom");
            String translate = req.getParameter("translate");
            String example = req.getParameter("example");

            if (newIdiom != null) {
                if (idiomDAO.isEntityUsed(newIdiom)) {
                    out.write("Idiom '" + newIdiom + "' already in use");
                } else {
                    forUpdate.setIdiom(newIdiom);
                }
            }

            forUpdate.setTranslate(translate);
            forUpdate.setExample(example);

            if (idiomDAO.updateEntity(forUpdate)) {
                reply = "Successfully update";
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

        if (authUser == null) {
            out.write("You have to log in or sing up");
        } else {
            String idiom = req.getParameter("idiom");
            String translate = req.getParameter("translate");
            String example = req.getParameter("example");

            String errorMessage = null;
            try {
                // region Data validation
                if (idiom == null || idiom.isEmpty()) {
                    throw new Exception("Idiom could not be empty");
                }
                if (!idiom.equals(idiom.trim())) {
                    throw new Exception("Idiom could not contain trailing spaces");
                }
                if (idiomDAO.isEntityUsed(idiom)) {
                    throw new Exception("Idiom is already in use");
                }
                if (translate == null || translate.isEmpty()) {
                    throw new Exception("Translate could not be empty");
                }
                if (!translate.equals(translate.trim())) {
                    throw new Exception("Translate could not contain trailing spaces");
                }
                if (example == null || example.isEmpty()) {
                    throw new Exception("Example could not be empty");
                }
                if (!example.equals(example.trim())) {
                    throw new Exception("Example could not contain trailing spaces");
                }
                // endregion

                Idiom newIdiom = new Idiom();
                newIdiom.setIdiom(idiom)
                        .setTranslate(translate)
                        .setExample(example)
                        .setUser(authUser);

                String id = idiomDAO.addEntity(newIdiom);

                if (id == null) {
                    throw new Exception("Server error, try later");
                } else {
                    out.write("Successful add");
                }
            } catch (Exception exception) {
                errorMessage = exception.getMessage();
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
            String idiom = null;

            if (req.getAttribute("idiom") != null) {
                idiom = (String) req.getAttribute("idiom");
            } else if (req.getParameter("idiom") != null) {
                idiom = req.getParameter("idiom");
            }

            if (idiom == null) {
                List<Entity> idiomsList = idiomDAO.getAllIdioms();
                getResponse(idiomsList, resp);
            } else {
                Idiom currentIdiom = idiomDAO.getIdiomByContext(idiom);
                if (currentIdiom != null) {
                    getResponse(currentIdiom, resp);
                } else {
                    out.write("Such idiom did not find");
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
