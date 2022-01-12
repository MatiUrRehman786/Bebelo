package com.buzzware.bebelo.classes;

import com.buzzware.bebelo.Model.BottleModelForEdit;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static String normals="Seagram's,Bombay Sapphire,Barceló,Santa Teresa,Cacique,Captain Morgan,Johnnie Walker Red,J&B,Absolut";

    public static String highRoller="Nordés,Bulldog,Hendrick's,Martin Miller's,Brockman's,Havana Club 7,Johnnie Walker Black,Jack Daniel's,Grey Goose,Belvedere";

    public static String wartime="Larios,Negrita,Dyc";

    public static boolean loadSettingFragment = false;

    public static LatLng selectedLocationLatLng = null;

    public static class SessionManagerConstants {

        public static final String LOCATION = "USER_LOCATION";
        public static final String USER_INFO_KEY = "USER_OBJ";
        public static final String LOGGED_IN = "IS_LOGGED_IN";
        public static final String SHOW_PROFILE = "SHOW_PROFILE";
        public static final String FILTER = "FILTER";
        public static final String LATANDLNG = "LATANDLNG";

    }

    public static List<BottleModelForEdit> normalArray=new ArrayList();
    public static List<BottleModelForEdit> highRollerArray=new ArrayList();
    public static List<BottleModelForEdit> warTimeArray=new ArrayList();






}