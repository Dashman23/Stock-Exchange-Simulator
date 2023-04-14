package com.example.finalassignment.util;

import java.util.HashMap;

public class Profile {
    public HashMap<String, Integer> stockProfile;
    String userId;
    double balance = 5000.0;

    public Profile(String userId) {
        this.userId = userId;
        stockProfile = new HashMap<>();
    }

    //may be used to add money to an account in the future
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public double getBalance() {
        return this.balance;
    }

}
