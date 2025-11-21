package com.nhi.libary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhi.libary.model.Genre;
import com.nhi.libary.repository.GenreRepository;

@Service
public class GenreService {

	@Autowired
	private GenreRepository genreRepository;
	
	public List<Genre> findAllGenre(){
		return this.genreRepository.findAll();
	}
	
	 
	    public List<Genre> getAllGenres() {
	        return genreRepository.findAll();
	    }
	    
	    public void saveGenre(Genre genre) {
	        genreRepository.save(genre);
	    }
	    
	    public Genre getGenreById(Long id) {
	        return genreRepository.findById(id).orElse(null);
	    }

}
