package com.example.contrans2;

public class GetSetOpenGroups {
private String name;
private String user;
private String favoring;
private String against;
private String message;
private String time_created;

    public GetSetOpenGroups() {
    }

    public GetSetOpenGroups(String name, String user, String favoring, String against, String message, String time_created) {
        this.name = name;
        this.user = user;
        this.favoring = favoring;
        this.against = against;
        this.message = message;
        this.time_created = time_created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }
}


