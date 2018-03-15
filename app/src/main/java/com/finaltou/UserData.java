package com.finaltou;

/**
 * Created by user1 on 2018-02-23.
 */


public class UserData {
    private String name;
    private String birth;
    private long startdate;

    public UserData(String name, String birth, long startdate) {
        this.name = name;
        this.birth = birth;
        this.startdate = startdate;
    }

    public String getName() {return name;}

    public String getBirth() {return birth;}

    public long getStartdate() { return startdate;}
}
