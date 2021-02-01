package net.ukr.kondrashev.controllers;

import lombok.Getter;
import lombok.Setter;
import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.entities.Word;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import net.ukr.kondrashev.entities.UserRole;
import net.ukr.kondrashev.repositories.WordRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Setter
@Getter
public class CustomUserController {
    @Autowired
    private ShaPasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WordRepository wordRepository;

    private String login;

    @PostMapping("/load/users")
    public List<CustomUser> uploadUsers(@RequestParam String pattern, @RequestBody String listUsers) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(listUsers);
            List<JSONObject> users = jsonArray;
            for (JSONObject user : users) {
                CustomUser customUser = new CustomUser((String) user.get("login"), (String) user.get("password"), (String) user.get("date"), UserRole.USER);
                if (customUserRepository.existsByLogin(customUser.getLogin()) == false) {
                    customUserRepository.save(customUser);
                }
                List<JSONObject> categories = (JSONArray) user.get("categories");
                for (JSONObject currentCategory : categories) {
                    Category category = new Category(customUser, (String) currentCategory.get("name"), (String) currentCategory.get("userName"), (String) currentCategory.get("date"));
                    if (categoryRepository.existsByName(category.getName(), customUser.getLogin()) == false) {
                        categoryRepository.save(category);
                    }
                    List<JSONObject> words = (JSONArray) currentCategory.get("words");
                    for (JSONObject currentWord : words) {
                        Word word = new Word(category, (String) currentWord.get("name"), (String) currentWord.get("meaning"), customUser.getLogin(), (String) currentWord.get("date"), (String) currentWord.get("categoryName"));
                        if (wordRepository.existsByName(word.getName(), customUser.getLogin()) == false) {
                            wordRepository.save(word);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return customUserRepository.findByUser(pattern);
    }

    @GetMapping("/get/users")
    public List<CustomUser> doGetUsers(@RequestParam String pattern) {
        return customUserRepository.findByUser(pattern);
    }

    @GetMapping("/get/current/user")
    public CustomUser getCurrentUser() {
        return customUserRepository.findByLogin(getLogin());
    }

    @GetMapping("/")
    public void startApplication(final HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        setLogin(user.getUsername());
        if (user.getUsername().equals("admin")) {
            response.sendRedirect("https://dictionary-bb66c.firebaseapp.com/?admin");
        } else {
            response.sendRedirect("https://dictionary-bb66c.firebaseapp.com/?user");
        }
    }

    @GetMapping("/exit")
    public void exit(final HttpServletResponse response) throws IOException {
        setLogin(null);
        response.sendRedirect("https://dictionary-bb66c.firebaseapp.com");
    }

    @PostMapping("/add/user")
    public CustomUser addUser(@RequestBody CustomUser customUser) {
        String passHash = passwordEncoder.encodePassword(customUser.getPassword(), null);
        if (customUserRepository.existsByLogin(customUser.getLogin()) == false) {
            return customUserRepository.save(new CustomUser(customUser.getLogin(), passHash, customUser.getDate(), UserRole.USER));
        } else {
            return new CustomUser();
        }
    }

    @PostMapping("/delete/users")
    public List<String> deleteUsers(@RequestBody String userListId) throws Exception {
        ArrayList<Long> IdListUser = new ArrayList<>();
        for (String box : userListId.substring(1, userListId.length() - 1).split(",")) {
            IdListUser.add(Long.parseLong(box));
        }
        ArrayList<String> loginListUser = new ArrayList<>();
        for (long userId : IdListUser) {
            CustomUser customUser = customUserRepository.getOne(userId);
            loginListUser.add(customUser.getLogin());
            customUserRepository.deleteById(userId);
        }
        return loginListUser;
    }
}
