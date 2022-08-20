package kfupm;

import java.io.Serializable;

public class Section implements Serializable{
    private String section;
    private String CRN;
    private String time;
    private String day;
    private String loc;
    private String seats;
    private String instructor;
    private String wait;

    Section(String section, String CRN, String time, String day, String loc, String seats, String instructor, String wait) {
        this.section = section;
        this.CRN = CRN;
        this.time = time;
        this.day = day;
        this.loc = loc;
        this.seats = seats;
        this.instructor = instructor;
        this.wait = wait;
    }

    public String getSection() {
        return section;
    }

    public String getCRN() {
        return CRN;
    }

    public String getTime() {
        return time;
    }

    public String getDay() {
        return day;
    }

    public String getLoc() {
        return loc;
    }
    public String getSeats() {
        return seats;
    }
    public String getInstructor() {
        return instructor;
    }
    public String getWait() {
        return wait;
    }
}
