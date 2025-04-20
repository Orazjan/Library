package com.example.library.Prevalent;

import android.os.Build;

import java.util.Random;

public class returnRandom {
    int listSize, randomnumber;

    public returnRandom(int listSize) {
        this.listSize = listSize;
    }

    public int getRandomnumber() {
        Random rand = new Random();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            randomnumber = rand.nextInt(0, listSize);
        }
        return randomnumber;
    }

}
