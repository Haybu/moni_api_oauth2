package com.moni.provider.model;

import java.io.Serializable;

/**
 * Created by hmohamed on 5/13/14.
 */
public class Greeting implements Serializable {

    private final long id;
    private final String content;


    public Greeting() {
        id = 0l;
        content = null;
    }

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
