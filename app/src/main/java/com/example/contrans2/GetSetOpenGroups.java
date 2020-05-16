package com.example.contrans2;

public class GetSetOpenGroups {
private String name;
private String date_created;
private String favoring;
private String against;
// zie https://www.youtube.com/watch?v=vyMH0NsY4q4 recyclerview tutorial
    // ik snap die constructors niet goed, wanneer wel een lege, wanneer niet.....
    public GetSetOpenGroups() {
    }

    public GetSetOpenGroups(String name, String date_created, String favoring, String against) {
        this.name = name;
        this.date_created = date_created;
        this.favoring = favoring;
        this.against = against;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getFavoring() {
        return favoring;
    }

    public void setFavoring(String favoring) {
        this.favoring = favoring;
    }

    public String getAgainst() {
        return against;
    }

    public void setAgainst(String against) {
        this.against = against;
    }
}
