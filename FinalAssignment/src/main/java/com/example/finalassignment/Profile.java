package com.example.finalassignment;

import java.util.ArrayList;

public class Profile {
    ArrayList<Integer> stockProfile;
    ArrayList<Integer> nextTickActions;
    String userId;
    double balance;

    Profile(String userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }


}
