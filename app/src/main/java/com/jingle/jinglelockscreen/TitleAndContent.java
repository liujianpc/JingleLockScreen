package com.jingle.jinglelockscreen;

import java.io.Serializable;

/**
 * Created by liujian on 2017/9/8.
 */

class TitleAndContent implements Serializable {
    String title;
    String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
