package com.nhi.libary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhi.libary.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long>  {

}
