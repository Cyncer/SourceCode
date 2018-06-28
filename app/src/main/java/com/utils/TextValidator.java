package com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ketul.patel on 18/1/16.
 */
public class TextValidator {
    private static final String EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isAValidEmail(CharSequence input) {
        if (input == null)
            return false;
        Pattern pattern = Pattern.compile(EMAIL);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
