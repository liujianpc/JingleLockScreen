package com.jingle.jinglelockscreen;

/**
 * Created by liujian on 2017/9/6.
 */

class ChannelModel {
    private String filecount;
    private String chname;
    private String url;
    private String updatetime;

    public ChannelModel(String filecount, String chname, String url, String updatetime) {
        this.filecount = filecount;
        this.chname = chname;
        this.url = url;
        this.updatetime = updatetime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getFilecount() {
        return filecount;
    }

    public void setFilecount(String filecount) {
        this.filecount = filecount;
    }

    public String getChname() {
        return chname;
    }

    public void setChname(String chname) {
        this.chname = chname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
