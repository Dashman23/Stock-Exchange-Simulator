package com.example.finalassignment;

import java.util.HashMap;

public class Profile {
    HashMap<String, Integer> stockProfile;
    String userId;
    double balance = 5000;

    Profile(String userId) {
        this.userId = userId;
        stockProfile = new HashMap<>();
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return this.balance;
    }

}
