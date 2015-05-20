package com.geowarin.twitter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TwitterController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }
}
