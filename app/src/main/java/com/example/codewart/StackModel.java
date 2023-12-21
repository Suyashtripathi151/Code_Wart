package com.example.codewart;

public class StackModel {


    String Error;
    String Description;
    String Platform;

    String Solution;

    String Time;

    String Userid;

    StackModel(){

    }

    public StackModel(String Error, String Description, String Platform, String Solution,String Time,String Userid) {
        this.Error = Error;
        this.Description = Description;
        this.Platform = Platform;
        this.Solution=Solution;
        this.Time=Time;
        this.Userid=Userid;

    }


    public String getError() {
        return Error;
    }

    public void setError(String Error) {
        this.Error = Error;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String Platform) {
        this.Platform = Platform;
    }

    public String getSolution() {
        return Solution;
    }

    public void setSolution(String Platform) {
        this.Solution = Solution;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }













}
