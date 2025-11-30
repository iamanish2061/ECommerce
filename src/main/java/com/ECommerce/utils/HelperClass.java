package com.ECommerce.utils;

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

}
