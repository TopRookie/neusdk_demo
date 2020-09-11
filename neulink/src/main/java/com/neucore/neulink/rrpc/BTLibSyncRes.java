package com.neucore.neulink.rrpc;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.CmdRes;

public class BTLibSyncRes extends CmdRes {

    @SerializedName("objtype")
    private String objtype;

    @SerializedName("total")
    private long total;

    @SerializedName("pages")
    private long pages;

    @SerializedName("offset")
    private long offset;

    @SerializedName("failed")
    private String[] failed;

    public String getObjtype() {
        return objtype;
    }

    public void setObjtype(String objtype) {
        this.objtype = objtype;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public String[] getFailed() {
        return failed;
    }

    public void setFailed(String[] failed) {
        this.failed = failed;
    }
}
