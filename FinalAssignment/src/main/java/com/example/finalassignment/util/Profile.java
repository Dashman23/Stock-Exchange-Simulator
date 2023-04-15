package com.example.finalassignment.util;

import java.util.HashMap;

//class to store userdata efficiently
public class Profile {
    //keeps track of all shares held
    public HashMap<String, Integer> stockProfile;
    String userId;
    double balance = 5000.0;

    public Profile(String userId) {
        this.userId = userId;
        stockProfile = new HashMap<>();
    }

    //used to update balance on each purchase
    public void setBalance(double balance) {
        this.balance = balance;
    }

    //getter
    public double getBalance() {
        return this.balance;
    }

}
