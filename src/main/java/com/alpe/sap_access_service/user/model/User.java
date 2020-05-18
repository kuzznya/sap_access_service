package com.alpe.sap_access_service.user.model;

import com.alpe.sap_access_service.tables.model.SAPTableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"lastTimeAccessed", "requestedTables"})
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Getter @Setter
    private Character language;

    @Column(unique = true)
    @Getter
    @JsonIgnore
    private String accessToken;

    @Getter @Setter
    @JsonIgnore
    private Date lastTimeAccessed;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SAPTableEntity> requestedTables = new ArrayList<>();

    public User(String system, String username, String password) {
        setSystem(system);
        setUsername(username);
        setPassword(password);
        this.language = null;
        this.lastTimeAccessed = new Date();
        generateAccessToken();
    }

    public User(String system, String username, String password, Character language) {
        this(system, username, password);
        setLanguage(language);
    }

    public void generateAccessToken() {
        if (accessToken == null && system != null && username != null && password != null && lastTimeAccessed != null)
            this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }
}
