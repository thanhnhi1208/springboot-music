package com.nhi.libary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhi.libary.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);
}
