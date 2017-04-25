package com.hmt.oauth.passport.web.message;

public interface Messages {

     String getMessage(String code);

     String getMessage(String code, Object... args);

}
