package com.dwizzel.observers;

import java.util.Observable;

/**
 * Created by Dwizzel on 14/11/2017.
 */

public class BooleanObserver extends Observable {

    private boolean b;

    public BooleanObserver(boolean b){
        this.b = b;
    }

    public void set(boolean b){
        this.b = b;
        setChanged();
        notifyObservers(this.b);
    }

    public boolean get(){
        return b;
    }

}
