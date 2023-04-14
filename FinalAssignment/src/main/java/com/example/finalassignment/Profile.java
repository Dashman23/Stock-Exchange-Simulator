package com.example.finalassignment;

import java.util.ArrayList;
import java.util.HashMap;

public class Profile {
    HashMap<String, Integer> stockProfile;
    HashMap<String, Integer> nextTickActions;
    String userId;
    double balance = 5000;

    Profile(String userId) {
        this.userId = userId;
        stockProfile = new HashMap<>();
        nextTickActions = new HashMap<>();
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
