package com.aambekar.tadhack.Model;

public class Contact {

    public String name;
    public String contact_no;


    public Contact(String Name, String Contact_no){
        name = Name;
        contact_no = Contact_no;
    }


    @Override
    public boolean equals(Object obj) {
        Contact c = (Contact) obj;
        if(this.contact_no.equals(c.contact_no)){
            return true;
        }
        return false;
    }
}
