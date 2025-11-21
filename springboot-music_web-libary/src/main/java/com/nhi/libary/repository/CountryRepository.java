package com.nhi.libary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhi.libary.model.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

}
