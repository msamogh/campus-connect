package com.msamogh.firstapp.model;

/**
 * Created by root on 6/28/15.
 */
public class ParseModelException extends Exception {

    private final String title;
    private final String message;

    public ParseModelException(String title, String message) {
        this.title = title;
        this.message = message;
    }

}
