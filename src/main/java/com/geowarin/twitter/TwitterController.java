package com.geowarin.twitter;

import com.geowarin.SocialSignOnApplication;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
public class TwitterController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping(value="/signup", method= RequestMethod.GET)
    public String signup(WebRequest request) {
        ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection != null) {
            SocialSignOnApplication.authenticate(connection);
        }
        return "index";
    }
}
