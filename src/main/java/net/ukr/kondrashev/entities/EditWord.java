package net.ukr.kondrashev.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EditWord {
    private String userName;
    private String name;
    private String newName;
    private String meaning;
    private String newMeaning;
    private String mark;
}
