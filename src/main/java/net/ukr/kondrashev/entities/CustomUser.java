package net.ukr.kondrashev.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CustomUser {
    @Id
    @GeneratedValue
    private long id;

    private String login;
    private String password;
    private String date;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL)
    private List<Category> categories;

    public CustomUser(String login, String password, UserRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public CustomUser(String login, String password, String date, UserRole role) {
        this.login = login;
        this.password = password;
        this.date = date;
        this.role = role;
    }
}
