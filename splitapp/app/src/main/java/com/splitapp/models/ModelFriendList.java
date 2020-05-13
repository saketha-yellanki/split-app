package com.splitapp.models;

public class ModelFriendList {
    private String name;
    private String phone;
    private String email;
    private String amount;
    private String uid;

    public ModelFriendList(String name,String email,String Phone,String uid,int amount) {
        this.name = name;
        this.email = email;
        this.amount =Integer.toString(amount);
        this.phone = phone;
        this.uid=uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
      return phone;
   }

    public void setPhone(String phone) {
      this.phone = phone;
   }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAmount(double num) {
        this.amount = Double.toString(num);
    }

    public String getAmount() {
        return this.amount;
    }

    public String getUid(){ return uid; }

    public void setUid(String uid){this.uid=uid; }


    //private int Photo;


    //    public int getPhoto(){
//        return Photo;
//    }
}
