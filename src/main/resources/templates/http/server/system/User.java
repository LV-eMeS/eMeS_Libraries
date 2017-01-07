package http.server.system;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * This class represents a Web server's single user's session.
 *
 * @author eMeS
 * @version 1.0.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class User {
    String username = "";
    String password = "";
    LocalDateTime sessionCreationTime;

    public User() {
        sessionCreationTime = LocalDateTime.now();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isLoggingSessionActive() {
        if (sessionCreationTime == null)
            return false;
        LocalDateTime now = sessionCreationTime = LocalDateTime.now();
        return sessionCreationTime.plusHours(8).isAfter(now);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sessionCreationTime=" + sessionCreationTime +
                '}';
    }
}
