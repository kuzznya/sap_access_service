package com.alpe.sap_access_service.user.model;

import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
import javax.persistence.*;
import java.util.Date;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"language", "lastTimeAccessed"})
public class User {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private long id;

    @NonNull
    @Getter @Setter
    private String system;
    @NonNull
    @Getter @Setter
    private String username;
    @NonNull
    @Getter @Setter
    private String password;
    @Getter @Setter
    private Character language;

    @Column(unique = true)
    @Getter
    private String accessToken;

    @Getter
    private Date lastTimeAccessed;

    public User(String system, String username, String password) {
        setSystem(system);
        setUsername(username);
        setPassword(password);
        this.language = null;
        this.lastTimeAccessed = new Date();
        this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public User(String system, String username, String password, Character language) {
        this(system, username, password);
        setLanguage(language);
    }

    public void setAccessToken() {
        if (accessToken == null && system != null && username != null && password != null && lastTimeAccessed != null)
            this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public void setLastTimeAccessed(Date lastTimeAccessed) {
        this.lastTimeAccessed = lastTimeAccessed;
        setAccessToken();
    }
}
