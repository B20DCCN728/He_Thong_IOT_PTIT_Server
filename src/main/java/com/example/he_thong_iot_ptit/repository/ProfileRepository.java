package com.example.he_thong_iot_ptit.repository;

import com.example.he_thong_iot_ptit.model.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
}
