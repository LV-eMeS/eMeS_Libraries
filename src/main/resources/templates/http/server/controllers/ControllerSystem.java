package http.server.controllers;

import http.server.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lv.emes.libraries.tools.MS_LineBuilder;

/**
 * All the methods that are used as system tools for Web application.
 * This includes methods that are responsible index for page, log in page, etc.
 */
@Controller
public class ControllerSystem {
    //All the autowirable objects
    @Autowired
    protected User user;

    @RequestMapping({"/", "/index", "/home"})
    @ResponseBody
    public String index() {
        return "Home page.";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public String login() {
        MS_LineBuilder lb = new MS_LineBuilder();
        lb
                .add("<h1>Hello, "+user.getUsername()+"!</h1>")
        ;
        return lb.toString();
    }
}