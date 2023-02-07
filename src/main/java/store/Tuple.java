package org.example;

public class Tuple{
    private String address;
    private Number SUM;

    public Tuple() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Number getSUM() {
        return SUM;
    }

    public void setSUM(Number SUM) {
        this.SUM = SUM;
    }

    @Override
    public String toString() {
        return String.format("Hiányzó könyvek: cím: %20s %3d db" , address , SUM.intValue());
    }

}