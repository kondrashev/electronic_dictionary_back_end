package net.ukr.kondrashev.controllers;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.entities.EditWord;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.entities.Word;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import net.ukr.kondrashev.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WordController {
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/get/words")
    public List<Word> doGetWords(@RequestParam(required = false, defaultValue = "0") Integer page,
                                 @RequestParam String categoryName, @RequestParam String userName) {
        Category category = categoryRepository.findByName(categoryName, userName);
        return wordRepository.findByCategory(category, PageRequest.of(page, 25));
    }

    @PostMapping("/add/word")
    public Word addWord(@RequestParam String categoryName, @RequestBody Word word) {
        CustomUser customUser = customUserRepository.findByLogin(word.getUserName());
        Category category = categoryRepository.findByName(categoryName, word.getUserName());
        if (wordRepository.existsByName(word.getName(), customUser.getLogin()) == false) {
            return wordRepository.save(new Word(category, word.getName(), word.getMeaning(), customUser.getLogin(), word.getDate(), categoryName));
        } else {
            return new Word();
        }
    }

    @PostMapping("/edit/word")
    public Word editNameWord(@RequestBody EditWord editWord) {
        Word word = wordRepository.findByName(editWord.getName(), editWord.getUserName());
        if (editWord.getMark().equals("name")) {
            word.setName(editWord.getNewName());
            return wordRepository.save(word);
        } else {
            word.setMeaning(editWord.getNewMeaning());
            return wordRepository.save(word);
        }
    }

    @PostMapping("/delete/words")
    public List<String> deleteWords(@RequestParam(required = false, defaultValue = "") String categoryName, @RequestBody String wordListId) {
        List<Long> IdListWords = new ArrayList<>();
        for (String box : wordListId.substring(1, wordListId.length() - 1).split(",")) {
            IdListWords.add(Long.parseLong(box));
        }
        List<String> listNamesWords = new ArrayList<>();
        Word firstWord = wordRepository.getOne(IdListWords.get(0));
        CustomUser customUser = customUserRepository.findByLogin(firstWord.getUserName());
        Category category = categoryRepository.findByName(categoryName, firstWord.getUserName());
        for (long idWord : IdListWords) {
            Word word = wordRepository.getOne(idWord);
            if (!categoryName.equals("")) {
                wordRepository.save(new Word(category, word.getName(), word.getMeaning(), customUser.getLogin(), word.getDate(), categoryName));
            }
            listNamesWords.add(word.getName());
            wordRepository.deleteById(idWord);
        }
        return listNamesWords;
    }

    @GetMapping("/search/word")
    public Word searchWord(@RequestParam String wordName, @RequestParam String userName) {
        if (wordRepository.existsByName(wordName, userName) == true) {
            return wordRepository.findByName(wordName, userName);
        } else {
            return new Word();
        }
    }

    @GetMapping("/count/words")
    public Long countWords(@RequestParam String categoryName, @RequestParam String userName) {
        return wordRepository.countByWords(categoryName, userName);
    }
}