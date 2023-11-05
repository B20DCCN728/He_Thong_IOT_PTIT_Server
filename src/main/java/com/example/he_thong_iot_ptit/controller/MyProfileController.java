package com.example.he_thong_iot_ptit.controller;


import com.example.he_thong_iot_ptit.model.Profile;
import com.example.he_thong_iot_ptit.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class MyProfileController {
    @Autowired
    private ProfileRepository profileRepository;

    @GetMapping("/profile")
    public Profile getListProfile() {
        return profileRepository.findAll().get(0);
    }

}
