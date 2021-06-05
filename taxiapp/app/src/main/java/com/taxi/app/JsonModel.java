package com.taxi.app;

public class JsonModel {
    private String carModel;
    private String price = "K4,000";
    private int numSeats;

    private String journeyLocation;
    private String journeyDestination;

    private String destination;
    private String driverID;
    private String url;
    private Boolean bookStatus = false; //Indicates whether a car is booked or not

    private String journeyURL;

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarModel() {
        return this.carModel;
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    public int getNumSeats() {
        return this.numSeats;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setBookStatus(Boolean bookStatus) {
        this.bookStatus = bookStatus;
    }

    public Boolean getBookStatus() {
        return this.bookStatus;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
    public String getDriverID() {
        return this.driverID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPrice() {
        return this.price;
    }

    public void setJourneyLocation(String journeyLocation){this.journeyLocation=journeyLocation;}
    public String getJourneyLocation(){return this.journeyLocation;}

    public void setJourneyDestination(String journeyDestination){this.journeyDestination=journeyDestination;}
    public String getJourneyDestination(){return this.journeyDestination;}

    public void setJourneyURL(String journeyURL){this.journeyURL=journeyURL;}
    public String getJourneyURL(){return this.journeyURL;}
}
