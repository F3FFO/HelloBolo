package com.f3ffo.hellobusbologna.hellobus;

public class CheckFileDate {

    private boolean versioncheck;
    private String version;

    public CheckFileDate(boolean versioncheck, String version) {
        this.versioncheck = versioncheck;
        this.version = version;
    }
    public CheckFileDate(boolean versioncheck) {
        this.versioncheck = versioncheck;
    }

    public boolean isVersioncheck() {
        return versioncheck;
    }

    public void setVersioncheck(boolean versioncheck) {
        this.versioncheck = versioncheck;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }



}
