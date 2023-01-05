package org.example.configurations;

import com.google.inject.servlet.ServletModule;
import org.example.servlets.*;

public class ConfigServlet extends ServletModule {
    @Override
    protected void configureServlets() {
        //serve("*").with(ErrorServlet.class);
        serve("/").with(HomeServlet.class);
        //serve("/user/").with(UserServlet.class);
        serve("/word/").with(WordServlet.class);
        //serve("/idiom/").with(IdiomServlet.class);
    }
}
