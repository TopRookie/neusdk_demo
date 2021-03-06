package com.neucore.neulink.rmsg;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class FirwareUpgrRes extends CmdRes {

    @SerializedName("vinfo")
    private String vinfo;

    public FirwareUpgrRes(){
        this.cmdStr = "upgrade";
    }

    public String getVinfo() {
        return vinfo;
    }

    public void setVinfo(String vinfo) {
        this.vinfo = vinfo;
    }
}
