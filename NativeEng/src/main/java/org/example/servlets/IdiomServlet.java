package org.example.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.example.dao.DAO;
import org.example.dao.IdiomDAO;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        super.doDelete(req, resp);
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
        super.doGet(req, resp);
    }
}
