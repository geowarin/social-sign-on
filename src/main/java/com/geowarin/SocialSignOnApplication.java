package com.geowarin;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
@Controller
public class SocialSignOnApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SocialSignOnApplication.class)
                .showBanner(false)
                .profiles("dev")
                .run(args);
    }

    @Bean
    public SignInAdapter signInAdapter() {
        return (userId, connection, request) -> {
            authenticate(connection);
            return null;
        };
    }

    @RequestMapping(value="/signup")
    public String signup(WebRequest request) {
        ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection != null) {
            SocialSignOnApplication.authenticate(connection);
            providerSignInUtils.doPostSignUp(connection.getDisplayName(), request);
        }
        return "index";
    }

    @Bean
    @Primary
    public UsersConnectionRepository getUsersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
    }


    public static void authenticate(Connection<?> connection) {
        UserProfile userProfile = connection.fetchUserProfile();
        String username = userProfile.getUsername();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(String.format("User %s %s connected.", userProfile.getFirstName(), userProfile.getLastName()));
    }

    @Bean
    @Profile("dev")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
    }
}
