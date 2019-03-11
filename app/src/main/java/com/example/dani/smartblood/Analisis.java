package com.example.dani.smartblood;

import java.io.Serializable;
import java.util.Date;

public class Analisis implements Serializable {

    public int getNivelGlucosa() {
        return nivelGlucosa;
    }

    public String getNota1() {
        return nota1;
    }

    public String getNota2() {
        return nota2;
    }

    public Date getTiempo() {

        return tiempo;
    }

    public void setTiempo(Date tiempo) {
        this.tiempo = tiempo;
    }

    public void setNivelGlucosa(int nivelGlucosa) {
        this.nivelGlucosa = nivelGlucosa;
    }

    public void setNota1(String nota1) {
        this.nota1 = nota1;
    }

    public void setNota2(String nota2) {
        this.nota2 = nota2;
    }


    public Analisis(Date tiempo, int nivelGlucosa, String nota1, String nota2) {
        this.tiempo = tiempo;
        this.nivelGlucosa = nivelGlucosa;
        this.nota1 = nota1;
        this.nota2 = nota2;
    }

    Date tiempo;
    int nivelGlucosa;
    String nota1, nota2;
}
