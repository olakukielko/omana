package com.aambekar.tadhack.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class Local {
    SharedPreferences local;
    Context context;

    String default_message = "You are receiving this message because one of your close friend is in the emergency situation.";

    public Local(Context context) {
        this.context = context;
        local = context.getSharedPreferences("local", 0);
    }

    public void resetFailedAttempts() {
        SharedPreferences.Editor editor = local.edit();
        editor.putInt("Failed", 0);
        editor.apply();
    }

    public int incrementFailedAttempts(){
        int attempts = getFailedAttempts();
        attempts++;
        SharedPreferences.Editor editor = local.edit();
        editor.putInt("Failed", attempts);
        editor.apply();
        return attempts;
    }

    public int getFailedAttempts() {
        return local.getInt("Failed",0);

    }

    public boolean getHooterDecision() {
        return local.getBoolean("Hooter",false);

    }

    public void setHooterDecision(Boolean decision) {
        SharedPreferences.Editor editor = local.edit();
        editor.putBoolean("Hooter", decision);
        editor.apply();
    }

    public String getAlertMessage() {
        return local.getString("AlertMessage",default_message);

    }

    public void setAlertMessage(String message) {
        SharedPreferences.Editor editor = local.edit();
        editor.putString("AlertMessage", message);
        editor.apply();
    }

    public boolean getTriggerChoice() {
        return local.getBoolean("Trigger",false);

    }

    public void setTriggerChoice(Boolean bool) {
        SharedPreferences.Editor editor = local.edit();
        editor.putBoolean("Trigger", bool);
        editor.apply();
    }


}