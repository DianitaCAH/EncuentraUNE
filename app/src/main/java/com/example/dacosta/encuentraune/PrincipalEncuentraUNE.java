package com.example.dacosta.encuentraune;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class PrincipalEncuentraUNE extends FragmentActivity implements
        ActionBar.TabListener, LocationListener{

    public static final String TAB_SELECCIONAR = "1";
    public static final String TAB_LOCALIZAR = "2";
    public static final String TAB_MAPA = "3";
    private static final int REQUEST_ENABLE_BT = 2;
    public FragmentTabHost mTabHost;
    public static int tab;
    public ActionBar actionBar;
    private GPS_Datos Loc;

    BroadcastReceiver status;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<Devices> arrayListpaired;
    ArrayList<Devices> arrayNoDisponible;

    public ArrayList<Devices> getArrayNoDisponible() {
        return arrayNoDisponible;
    }

    public void setArrayNoDisponible(ArrayList<Devices> arrayNoDisponible) {
        this.arrayNoDisponible = arrayNoDisponible;
    }

    ArrayList<Devices> listaPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_encuentra_une);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);

        initTabHost();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        activarBT();

    }

    @Override
    protected void onStart() {
        super.onStart();

        cargaListaBTPaired();

    }

    public ArrayList<Devices> getArrayListpaired() {
        return listaPaired;
    }

    public void setArrayListpaired(ArrayList<Devices> listaPaired) {
        this.arrayListpaired = listaPaired;
    }

    public void cargaListaBTPaired(){
        double gps_long = 0, gps_lat = 0;
        listaPaired = new ArrayList<Devices>();
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if(pairedDevice.size() > 0)  {
            for(BluetoothDevice bDevice : pairedDevice){
                Devices d = new Devices();
                d.setName(bDevice.getName());
                d.setMacAddress(bDevice.getAddress());
                //Toma de coordenadas
                GPS_Datos GPS = new GPS_Datos(this);
                Loc = GPS.PrenderGPS();
                if (Loc.getLocationManager() != null)
                    if (Loc.isPorGPS()) {
                        Loc.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        this.onLocationChanged(Loc.getLocacion());
                    }else {
                        gps_long = Loc.getLocacion().getLongitude();
                        gps_lat = Loc.getLocacion().getLatitude();
                        //Toast.makeText(getActivity(), "Latitud GPS:" + gps_lat + ", Longitud GPS:" +
                        // gps_long, Toast.LENGTH_LONG).show();
                    }
                d.setLatitude(String.valueOf(gps_long));
                d.setLongitude(String.valueOf(gps_lat));
                listaPaired.add(d);
            }
            this.setArrayListpaired(listaPaired);
            Log.e("getLista ACTIVITY", String.valueOf(this.getArrayListpaired().size()));
            //bluetoothAdapter.cancelDiscovery();
        }
        Log.e("Cargo lista ACTIVITY", String.valueOf(listaPaired.size()));
    }

    private void activarBT() {
        status = new BroadcastReceiver(){

            @Override
            public void onReceive(Context contexto, Intent intento) {
                // TODO Auto-generated method stub
                String estadoExtra = BluetoothAdapter.EXTRA_STATE;
                int status2 = intento.getIntExtra(estadoExtra, -1);
                String action = intento.getAction();
                String toastText="";
                switch(status2){
                    case (BluetoothAdapter.STATE_TURNING_ON):{
                        toastText="BT ESTA ENCENDIENDO";
                        break;
                    }
                    case (BluetoothAdapter.STATE_ON):{
                        toastText="BT ENCENDIDO";
                        //getApplicationContext().unregisterReceiver(this);
                        break;
                    }
                    case (BluetoothAdapter.STATE_TURNING_OFF):{
                        toastText="BT APAGADO";
                        break;
                    }
                    default:
                        break;
                }
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();

            }

        };

        //preguntamos por el dispositivo BT para activarlo
        if(!bluetoothAdapter.isEnabled()){
            String accioncambiodeestado = BluetoothAdapter.ACTION_STATE_CHANGED;
            String enable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            this.registerReceiver(status, new IntentFilter(accioncambiodeestado));
            startActivityForResult(new Intent(enable), 0);
        }
    }

    //Cargo los Tabs!
    private void initTabHost() {

        mTabHost.setup(this, getSupportFragmentManager(), R.id.real_tab_content);
        mTabHost.getTabWidget().setStripEnabled(true);

        mTabHost.addTab(mTabHost.newTabSpec(TAB_SELECCIONAR).setIndicator("SELECCIONAR"),
                SeleccionFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_LOCALIZAR).setIndicator("LOCALIZAR"),
                UbicarFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MAPA).setIndicator("VER EN MAPA"),
                MapaFragment.class, null);


        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.negro));
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String s) {
                tab = mTabHost.getCurrentTab();
            }
        });

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double gps_long = location.getLongitude();
        double gps_lat = location.getLatitude();
        //Toast.makeText(getActivity(), "Latitud GPS:" + gps_lat + ", Longitud GPS:" + gps_long, Toast.LENGTH_LONG).show();
        Loc.getLocationManager().removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
