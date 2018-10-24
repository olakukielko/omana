package com.aambekar.tadhack.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.aambekar.tadhack.Data.Local;
import com.aambekar.tadhack.R;

public class SettingsActivity extends AppCompatActivity {

    CheckBox cb_hooter;
    CheckBox cb_trigger;
    EditText et_alertText;
    Local local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cb_hooter = findViewById(R.id.cb_hooter);
        cb_trigger = findViewById(R.id.cb_trigger);
        et_alertText = findViewById(R.id.et_alertText);
        local = new Local(getApplicationContext());

        if(local.getHooterDecision()){
            cb_hooter.setChecked(true);
            cb_hooter.setText("enabled");
        }

        if(local.getTriggerChoice())
        {
            cb_trigger.setText("enabled");
            cb_trigger.setChecked(true);
        }


        et_alertText.setText(local.getAlertMessage());

        cb_hooter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    cb_hooter.setChecked(true);
                    local.setHooterDecision(true);
                    cb_hooter.setText("enabled");
                }
                else {
                    cb_hooter.setChecked(false);
                    local.setHooterDecision(false);
                    cb_hooter.setText("disabled");
                }
            }
        });

        cb_trigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    cb_trigger.setChecked(true);
                    local.setTriggerChoice(true);
                    cb_trigger.setText("enabled");
                }
                else {
                    cb_hooter.setChecked(false);
                    local.setTriggerChoice(false);
                    cb_trigger.setText("disabled");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.m_save:
                local.setAlertMessage(et_alertText.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
