package com.ECommerce.utils;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HelperClass {

    public static String maskEmail(String email){
        boolean mask = false;
        StringBuilder maskedString = new StringBuilder("");
        for (int i=0; i<email.length(); i++){
            if(i==2) mask=true;
            if(email.charAt(i) == '@')
                mask=false;
            if(mask){
                maskedString.append("*");
            }else{
                maskedString.append(email.charAt(i));
            }
        }
        return maskedString.toString();
    }

    public static String generateSlug(String name){
        if (name == null || name.isBlank()) return "";

        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }


}







