package com.dcurreli.spese.utils;

import android.util.Log;

import com.dcurreli.spese.objects.Spesa;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class SpesaUtils {

    private static final String TAG = "SpesaUtils";

    @NotNull
    private static String getDataWithUnderscores(Spesa spesa){
        return spesa.getData().replace("/","_");
    }
    private static String formatDataForPath(Spesa spesa){
        String[] dataSpesa = spesa.getData().split("/");
        return dataSpesa[1] + "_" + dataSpesa[2]+"_"+getMonthAsText(dataSpesa[1])+"/prodotto/";
    }
    private static String getMonthAsText(String mese){
        Log.i(TAG,"Mese: "+mese);
        switch (mese){
            case "1":
            case "01": return "Gennaio";
            case "2":
            case "02": return "Febbraio";
            case "3":
            case "03": return "Marzo";
            case "4":
            case "04": return "Aprile";
            case "5":
            case "05": return "Maggio";
            case "6":
            case "06": return "Giugno";
            case "7":
            case "07": return "Luglio";
            case "8":
            case "08": return "Agosto";
            case "9":
            case "09": return "Settembre";
            case "10": return "Ottobre";
            case "11": return "Novembre";
            case "12": return "Dicembre";
            default: return "null";
        }
    }

    @NotNull
    public static String getSpesaPath(Spesa spesa){
        return formatDataForPath(spesa)+getDataWithUnderscores(spesa)+"_"+spesa.getLuogo()+"_"+spesa.getPagatore();
    }


}
