package net.ukr.kondrashev.controllers;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.EditCategory;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/get/all/categories")
    public List<Category> doGetAllCategoris(@RequestParam String userName) {
        CustomUser customUser = customUserRepository.findByLogin(userName);
        return categoryRepository.findByUser(customUser);
    }

    @GetMapping("/get/categories")
    public List<Category> doGetCategories(@RequestParam String userName, @RequestParam(required = false, defaultValue = "0") Integer page) {
        CustomUser customUser = customUserRepository.findByLogin(userName);
        return categoryRepository.findByUser(customUser, PageRequest.of(page, 5));
    }

    @PostMapping("/add/category")
    public Category addCategory(@RequestParam String userName, @RequestBody Category category) {
        CustomUser customUser = customUserRepository.findByLogin(userName);
        if (categoryRepository.existsByName(category.getName(), customUser.getLogin()) == false) {
            return categoryRepository.save(new Category(customUser, category.getName(), customUser.getLogin(), category.getDate()));
        } else {
            return new Category();
        }
    }

    @PostMapping("/edit/category")
    public Category editCategory(@RequestBody EditCategory editCategory) {
        Category category = categoryRepository.findByName(editCategory.getName(), editCategory.getUserName());
        category.setName(editCategory.getNewName());
        return categoryRepository.save(category);
    }

    @PostMapping("/delete/categories")
    public List<String> deleteCategories(@RequestBody String categoryListId) {
        ArrayList<Long> IdListCategory = new ArrayList<>();
        for (String box : categoryListId.substring(1, categoryListId.length() - 1).split(",")) {
            IdListCategory.add(Long.parseLong(box));
        }
        ArrayList<String> nameListCategory = new ArrayList<>();
        for (long categoryId : IdListCategory) {
            Category categoryCurrent = categoryRepository.getOne(categoryId);
            nameListCategory.add(categoryCurrent.getName());
            categoryRepository.deleteById(categoryId);
        }
        return nameListCategory;
    }

    @GetMapping("/count/categories")
    public Long countCategories(@RequestParam String userName) {
        return categoryRepository.countByCategories(userName);
    }
}