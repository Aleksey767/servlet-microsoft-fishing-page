package org.fishingPage;

import lombok.*;

import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "credentials")
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "creation_timestamp", nullable = false)
    private LocalDateTime creationTimestamp;

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void setCreationTimestampWithTimezone() {
        this.creationTimestamp = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).toLocalDateTime();
    }
}
