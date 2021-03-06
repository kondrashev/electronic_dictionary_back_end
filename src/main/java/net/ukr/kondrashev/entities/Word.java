package net.ukr.kondrashev.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Word")
@NoArgsConstructor
@Getter
@Setter
public class Word {
    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private String meaning;
    private String userName;
    private String date;
    private String categoryName;

    public Word(Category category, String name, String meaning, String userName, String date, String categoryName) {
        this.category = category;
        this.name = name;
        this.meaning = meaning;
        this.userName = userName;
        this.date = date;
        this.categoryName = categoryName;
    }
}