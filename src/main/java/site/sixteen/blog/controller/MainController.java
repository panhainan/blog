package site.sixteen.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Controller
public class MainController {


    @GetMapping({"/blog", "/"})
    public String index() {
        return "index";
    }
}
