package com.spike.bot.adapter.irblaster;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator{

        private static Pattern pattern;
        private static Matcher matcher;

        private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";

        public PasswordValidator(){
            pattern = Pattern.compile(PASSWORD_PATTERN);
        }

        /**
         * Validate password with regular expression
         * @param password password for validation
         * @return true valid password, false invalid password
         */
        public static boolean validate(final String password){
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);
            return matcher.matches();

        }
    }