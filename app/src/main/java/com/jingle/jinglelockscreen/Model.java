package com.jingle.jinglelockscreen;

import java.util.List;

/**
 * Created by liujian on 2017/9/6.
 */

class Model {
    private String resultinfo;
    private List<ChannelModel> channellist;

    public Model(String resultinfo, List<ChannelModel> channellist) {
        this.resultinfo = resultinfo;
        this.channellist = channellist;
    }

    public String getResultinfo() {
        return resultinfo;
    }

    public void setResultinfo(String resultinfo) {
        this.resultinfo = resultinfo;
    }

    public List<ChannelModel> getChannellist() {
        return channellist;
    }

    public void setChannellist(List<ChannelModel> channellist) {
        this.channellist = channellist;
    }
}
