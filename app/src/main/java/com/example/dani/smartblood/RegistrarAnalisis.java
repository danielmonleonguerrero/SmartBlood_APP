package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Date;

public class RegistrarAnalisis extends AppCompatActivity {
    private static final int REGISTER_ANALISIS=2;

    private TextView nivelGlucosaView;
    private ImageView bloodropView;
    private RadioGroup nota1_rdgroup, nota2_rdgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_analisis);
        nivelGlucosaView=findViewById(R.id.nivelGlucosaView);
        nota1_rdgroup=findViewById(R.id.nota1_rdgroup);
        nota2_rdgroup=findViewById(R.id.nota2_rdgroup);
        bloodropView=findViewById(R.id.bloodropView);
        Glide.with(RegistrarAnalisis.this).load(PathImageGenerator(Integer.valueOf(nivelGlucosaView.getText().toString()))).into(bloodropView);
    }

    public void onRegistrar(View view) {
        Date currentTime = Calendar.getInstance().getTime();
        int id_nota1=nota1_rdgroup.getCheckedRadioButtonId();
        int id_nota2=nota2_rdgroup.getCheckedRadioButtonId();
        RadioButton nota1_esc_rdbutton = new RadioButton(RegistrarAnalisis.this);
        RadioButton nota2_esc_rdbutton = new RadioButton(RegistrarAnalisis.this);
        if(id_nota1!=-1) nota1_esc_rdbutton=findViewById(id_nota1);
        else nota1_esc_rdbutton.setText("");
        if(id_nota2!=-1) nota2_esc_rdbutton=findViewById(id_nota2);
        else nota2_esc_rdbutton.setText("");

        Analisis analisis = new Analisis(new Date((currentTime.getYear()+1900), currentTime.getMonth(), currentTime.getDate(),
                currentTime.getHours(), currentTime.getMinutes()),
                70, nota1_esc_rdbutton.getText().toString(), nota2_esc_rdbutton.getText().toString());

        Intent intent = new Intent(RegistrarAnalisis.this, CalendarActivity.class);
        intent.putExtra("NewAnalisis", true);
        intent.putExtra("Analisis", analisis);

        startActivityForResult(intent, REGISTER_ANALISIS);
    }

    private String PathImageGenerator(int nivelGlucosa) {
        String path ="file:///android_asset/bloodrop" + String.valueOf(nivelGlucosa) + ".png";
        return path;
    }
}
