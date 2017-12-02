package com.dwizzel.observers;

import java.util.Observable;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public class StringObserver extends Observable {

    private String s;

    public StringObserver(String s){
        this.s = s;
    }

    public void set(String s){
        this.s = s;
        setChanged();
        notifyObservers(this.s);
    }

    public String get(){
        return s;
    }

}
