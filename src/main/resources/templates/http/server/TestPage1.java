package lv.emes.libraries.communication.http.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * For "localhost:<b>WEB_PORT</b>/testPage1" page prints out one passed parameter <b>testParam</b>.
 * <p><u>Example</u>: WEB_PORT = 8080;
 * <br>http://localhost:8080/testPage1?testParam=This is a value of parameter.
 * <br>Text "This is a value of parameter." is printed.
 */
@Controller
public class TestPage1 {
    @RequestMapping("/testPage1")
    @ResponseBody
    public String search(@RequestParam(value = "testParam", required = false) String testParam) {
        return testParam;
    }
}