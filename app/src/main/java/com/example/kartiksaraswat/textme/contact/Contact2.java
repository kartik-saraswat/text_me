package com.example.kartiksaraswat.textme.contact;

import java.io.Serializable;

/**
 * Created by Kartik Saraswat on 19-07-2016.
 */
public class Contact2 implements Serializable{

    public String name;
    public String number;

    public Contact2(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public Contact2(String number) {
        this.name = "";
        this.number = number;
    }

    public String getName() {
        return name.isEmpty() ? number : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString(){
        if(!getName().isEmpty()){
            return getName();
        } else{
            return getNumber();
        }
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Contact2){
            return ((Contact2)o).getName().equals(name) && ((Contact2)o).getNumber().equals(number);
        }
        return false;
    }


}
