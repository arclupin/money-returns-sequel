package com.ncl.team5.lloydsmockup;

import  android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;




public class Login extends Activity {
private EditText password;
    private ViewFlipper slider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        password = (EditText) findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);

        slider = (ViewFlipper) findViewById(R.id.sliding_advert);
        runSlider();

    }

private void runSlider()
{
    slider.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    slider.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
    slider.setAutoStart(true);
    slider.setFlipInterval(2800);
    slider.startFlipping();
}


    public void lauchMain(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
