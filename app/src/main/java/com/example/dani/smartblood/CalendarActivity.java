package com.example.dani.smartblood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    private static final int VIEW_ANALISIS=1;
    private static final String NombreArchivoSalvaguarda="AnalisisSalvaguarda";

    //
    ScrollCalendar scrollcalendar;
    List<Analisis> ListAnalisis=new ArrayList<>();
    int lvlGlucosa =100;

    //-------------------METODOS ONCREATE Y ONSTOP-------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        scrollcalendar = findViewById(R.id.scrollcalendar);

        CargarDatosAnalisis();

        Intent intent = getIntent();
        if(intent.getBooleanExtra("NewAnalisis", false)){
            Analisis newAnalisisIntent = (Analisis)intent.getSerializableExtra("Analisis");
            ListAnalisis.add(newAnalisisIntent);
            Toast.makeText(this, Integer.toString(ListAnalisis.get((ListAnalisis.size()-1)).getTiempo().getYear())+"/"+
                            Integer.toString(ListAnalisis.get((ListAnalisis.size()-1)).getTiempo().getMonth())+"/"+
                            Integer.toString(ListAnalisis.get((ListAnalisis.size()-1)).getTiempo().getDay())+"///"+
                            Integer.toString(ListAnalisis.get((ListAnalisis.size()-1)).getNivelGlucosa())
                    , Toast.LENGTH_LONG).show();
        }

        //Ocultamos meses posteriores
        scrollcalendar.setMonthScrollListener(new MonthScrollListener() {
            @Override
            public boolean shouldAddNextMonth(int lastDisplayedYear, int lastDisplayedMonth) {
                // return false if you don't want to show later months
                return false;
            }
            @Override
            public boolean shouldAddPreviousMonth(int firstDisplayedYear, int firstDisplayedMonth) {
                // return false if you don't want to show previous months
                return true;
            }
        });

        //Se detecta CLIC en un dia del calendario
        scrollcalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalendarDayClicked(int year, int month, int day) {
                //NO ES PARTE DE ON CLIC. ESTA AQUI DE FORMA PROVISIONAL. ESTO SE EJECUTA EN REGISTRARANALISISACTIVITY
                //Se crea un nuevo analisis
                Analisis newAnalisis = new Analisis(new Date (year, month, day, 12, 40),lvlGlucosa,"Antes de comer", "Poco ejercicio");
                ListAnalisis.add(newAnalisis); //Se añade un nuevo objeto analisis y se rellena

                //-------ON CLIC REAL-------//
                //Se rellena una lista con los analisis del dia seleccionada
                DiaAnalisis diaAnalisis =new DiaAnalisis(new ArrayList<Analisis>());
                List<Analisis> ListAnalisisIntent=new ArrayList<>();
                for(int i=0; i<ListAnalisis.size(); i++){
                    if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                        ListAnalisisIntent.add(ListAnalisis.get(i));
                    }
                }
                //Se encapsua el List en un objeto del tipo DiaAnalisis para poder pasarlo a otra actividad como extra en el intent.
                diaAnalisis.setArrayAnalisis(ListAnalisisIntent);

                //Se crea una intent y se llama a la actividad ResumenAnalisisActivity
                Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                intent.putExtra("diaAnalisis", diaAnalisis);
                startActivityForResult(intent, VIEW_ANALISIS);

                lvlGlucosa=lvlGlucosa+5;
            }

        });

        //Modifica las apariencias de los dias en el calendario.
        scrollcalendar.setDateWatcher(new DateWatcher() {
            @State
            @Override
            public int getStateForDate(int year, int month, int day) {
                if(isSelected(year,month,day)) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        GuardarDatosAnalisis();
    }
    //----------------------------------------------------------------//

    //Mira si X dia necesita estar marcado con color rojo.
    private boolean isSelected(int year, int month, int day) {
        Log.e("SMARTBLOOD", Integer.toString(ListAnalisis.size()));
        if(ListAnalisis.size()>1){
            for(int i=0; i<ListAnalisis.size(); i++){
                if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                    return(true);
                }
            }
        }
        return(false);
    }

    //--------METODOS PARA LA SALVAGUARDA DE DATOS DE ANALISIS--------//
    private void CargarDatosAnalisis() {
        try {
            FileInputStream FIS =openFileInput(NombreArchivoSalvaguarda);
            Scanner scanner = new Scanner(FIS);
            while (scanner.hasNextLine()){
                String Analisisscanner = scanner.nextLine();
                String[] parts = Analisisscanner.split("/");
                Analisis lAnalisis = new Analisis(new Date(Integer.valueOf(parts[3]),
                        Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]),
                        Integer.valueOf(parts[7])), Integer.valueOf(parts[0]), parts[1], parts[2] );
                ListAnalisis.add(lAnalisis);
            }
            scrollcalendar.refresh();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void GuardarDatosAnalisis() {
        try {
            FileOutputStream FOS = openFileOutput(NombreArchivoSalvaguarda, MODE_PRIVATE);
            for (int i=0; i<ListAnalisis.size(); i++){
                String sAnalisis = crearStringSalvaguarda(i); //salvaguardaAnalisis
                try {
                    FOS.write(sAnalisis.getBytes());
                } catch (IOException e) {
                    Log.e("SMARTBLOOD", "No se ha podido escribir en el fichero. Excpecion: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("SMARTBLOOD", "No se ha podido abrir el archivo: " + NombreArchivoSalvaguarda + "Excepcion: " + e.getMessage());
        }
    }

    private String crearStringSalvaguarda(int i) {
        Analisis Analisis = ListAnalisis.get(i);
        Date sdAnalisis = Analisis.getTiempo();
        String ssAnalisis = Integer.valueOf(Analisis.getNivelGlucosa()) + "/" +     //Part 0
                Analisis.getNota1() + "/"                                           //Part 1
                +Analisis.getNota2() + "/"+                                         //Part 2
                String.valueOf(sdAnalisis.getYear()) + "/" +                        //Part 3
                String.valueOf(sdAnalisis.getMonth()) + "/"+                        //Part 4
                String.valueOf(sdAnalisis.getDate()) + "/"+                         //Part 5
                String.valueOf(sdAnalisis.getHours()) + "/"+                        //Part 6
                String.valueOf(sdAnalisis.getMinutes()) + "\n";                     //Part 7
        return (ssAnalisis);
    }
    //----------------------------------------------------------------//



}
