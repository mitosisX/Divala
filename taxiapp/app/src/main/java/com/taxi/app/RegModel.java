package com.taxi.app;

//Holds details during registration
public class RegModel {
    private String email;
    private String name;
    private String password;
    private String natID;
    private String base64;

    private String model;
    private String plate;
    private String seats;

    private String journey;

    private String journeyLocation;
    private String journeyDestination;

    private String uploadDOB;
    private String uploadLocation;
    private String uploadNumber;


    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email=email;}

    public String getName(){return this.name;}
    public void setName(String name){this.name=name;}

    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password=password;}

    public void setNatID(String natID){this.natID=natID;}
    public String getNatID(){return this.natID;}

    public void setBase64(String base64){this.base64=base64;}
    public String getBase64(){return this.base64;}

    public String getModel(){return this.model;}
    public void setModel(String model){this.model=model;}

    public String getPlate(){return this.plate;}
    public void setPlate(String plate){this.plate=plate;}

    public String getSeats(){return this.seats;}
    public void setSeats(String seats){this.seats=seats;}

    public String getJourney(){return this.journey;}
    public void setJourney(String journey){this.journey=journey;}
    

    public void setJourneyLocation(String journeyLocation){this.journeyLocation=journeyLocation;}
    public String getJourneyLocation(){return this.journeyLocation;}

    public void setJourneyDestination(String journeyDestination){this.journeyDestination=journeyDestination;}
    public String getJourneyDestination(){return this.journeyDestination;}

    public void setUploadDOB(String dob){ this.uploadDOB=dob;}
    public String getUploadDOB(){return this.uploadDOB;}

    public void setUploadLocation(String loc){this.uploadLocation=loc;}
    public String getUploadLocation(){return this.uploadLocation;}

    public void setUploadNumber(String num){this.uploadNumber=num;}
    public String getUploadNumber(){return this.uploadNumber;}
}
