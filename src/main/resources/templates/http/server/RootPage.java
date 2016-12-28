package lv.emes.libraries.communication.http.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * When "localhost:<b>WEB_PORT</b>" is accessed, returns root HTML page text.
 * @see lv.emes.libraries.communication.http.server.SpringRunner#WEB_PORT
 */
@Controller
public class RootPage {
    @RequestMapping("/")
    @ResponseBody
    public String form() {
        String ret = "Hello, World! This is root page.";
        return ret;
    }
}