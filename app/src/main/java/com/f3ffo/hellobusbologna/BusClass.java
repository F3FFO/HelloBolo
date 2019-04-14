package com.f3ffo.hellobusbologna;

public class BusClass {

    private String linecode;
    private String stopcode;
    private String stopname;
    private String zonecode;

    public BusClass(String linecode, String stopcode, String stopname, String zonecode) {
        this.linecode = linecode;
        this.stopcode = stopcode;
        this.stopname = stopname;
        this.zonecode = zonecode;
    }

    public String getLinecode() {
        return linecode;
    }

    public void setLinecode(String linecode) {
        this.linecode = linecode;
    }

    public String getStopcode() {
        return stopcode;
    }

    public void setStopcode(String stopcode) {
        this.stopcode = stopcode;
    }

    public String getStopname() {
        return stopname;
    }

    public void setStopname(String stopname) {
        this.stopname = stopname;
    }

    public String getZonecode() {
        return zonecode;
    }

    public void setZonecode(String zonecode) {
        this.zonecode = zonecode;
    }

}
