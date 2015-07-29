package com.example.dacosta.encuentraune;

/**
 * Created by dacosta on 7/27/2015.
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;


public class GPS_Datos {

    protected Context context;

    public boolean isPorGPS() {
        return porGPS;
    }

    public void setPorGPS(boolean porGPS) {
        this.porGPS = porGPS;
    }

    public Location getLocacion() {
        return Locacion;
    }

    public void setLocacion(Location locacion) {
        Locacion = locacion;
    }

    public LocationManager getLocationManager() {
        return LocationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        LocationManager = locationManager;
    }

    private boolean porGPS;
    private Location Locacion;
    private LocationManager LocationManager;

    public GPS_Datos(){
        super();
    }

    public GPS_Datos(Context cont){
        //super(cont);
        this.context = cont;
    }


    public GPS_Datos PrenderGPS() {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        GPS_Datos gps = new GPS_Datos();
        Location net_loc = null, gps_loc = null;

        if (gps_enabled)
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gps_loc != null) {
            gps.setPorGPS(true);
            gps.setLocacion(gps_loc);
            gps.setLocationManager(lm);
            return gps;
        }
        if (net_loc != null) {
            gps.setPorGPS(false);
            gps.setLocacion(net_loc);
            gps.setLocationManager(lm);
            return gps;
        }
        return gps;
    }


}