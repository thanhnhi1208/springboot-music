package com.nhi.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nhi.libary.model.Genre;
import com.nhi.libary.service.GenreService;

@Controller
@RequestMapping("/genre")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public String listGenres(Model model) {
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("genre", new Genre());  // để form binding
        return "genre"; // file: genre.html
    }

    @PostMapping("/add")
    public String addGenre(@ModelAttribute("genre") Genre genre) {
        genreService.saveGenre(genre);
        return "redirect:/genre";
    }
    
    @PostMapping("/update")
    public String updateGenre(@ModelAttribute("genre") Genre genre) {
        Genre existingGenre = genreService.getGenreById(genre.getId());
        existingGenre.setName(genre.getName());
        genreService.saveGenre(existingGenre);
        return "redirect:/genre";
    }


}
