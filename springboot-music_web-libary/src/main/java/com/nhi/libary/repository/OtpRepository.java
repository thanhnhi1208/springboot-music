package com.nhi.libary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhi.libary.model.OTP;

public interface OtpRepository extends JpaRepository<OTP, Long> {

	OTP findByEmailAndChucNang(String email, String chucNang);
}
