package com.assis.redondo.daniel.appdoikeda.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DT on 12/4/15.
 */
public class Validator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean validateEmail(String s) {
        if(s != null && !s.contentEquals("")) {
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        } else {
            return false;
        }
    }
}
