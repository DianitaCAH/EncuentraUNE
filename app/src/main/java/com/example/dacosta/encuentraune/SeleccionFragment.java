package com.example.dacosta.encuentraune;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class SeleccionFragment extends Fragment implements LocationListener {

    View rootView;

    protected static final int DISCOVERY_REQUEST = 0;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private ProgressDialog mBluetoothConnectProgressDialog;
    ArrayAdapter<String> adapter,detectedAdapter;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices, arrayListBluetoothDevices;
    ArrayList<Devices> arrayListpaired;
    //ArrayList<String> arrayListpaired;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket mBTSocket;
    BluetoothDevice mDevice;
    BroadcastReceiver status;
    private GPS_Datos Loc;
    PrincipalEncuentraUNE activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.seleccion, container, false);



        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();

        activity = (PrincipalEncuentraUNE)getActivity();

        CheckBox visible = (CheckBox)rootView.findViewById(R.id.checkBox1_visible);
        visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibleBT();
            }
        });

        Button publicar = (Button)rootView.findViewById(R.id.btPublicar);
        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                String direccion = bluetoothAdapter.getAddress();
                String nombre = bluetoothAdapter.getName();
                TextView info = (TextView) rootView.findViewById(R.id.textInfo);
                info.setText("DISPOSITIVO: " + direccion + "\nNombre: " + nombre);
                Toast.makeText(getActivity(), "DISPOSITIVO:" + "\n" + nombre + "\n" + direccion, Toast.LENGTH_LONG).show();
            }
        });

        Button buscar = (Button)rootView.findViewById(R.id.btBuscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarDispositivos();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        itemClick();
    }

    private void buscarDispositivos() {
        arrayListpaired = activity.getArrayListpaired();
        ListView listViewDetected = (ListView)rootView.findViewById(R.id.DeviceList);
        DeviceAdapter adapter = new DeviceAdapter(getActivity(),arrayListpaired);
        listViewDetected.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(), "cargo lista FRAGMENT1", Toast.LENGTH_SHORT).show();
        Log.e("PairedDevices", String.valueOf(arrayListpaired.size()));
        /*double gps_long = 0, gps_lat = 0;
        arrayListpaired = new ArrayList<Devices>();
        ListView listViewDetected = (ListView)rootView.findViewById(R.id.DeviceList);
        DeviceAdapter adapter = new DeviceAdapter(getActivity(),arrayListpaired);
        listViewDetected.setAdapter(adapter);
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if(pairedDevice.size() > 0)  {
            for(BluetoothDevice bDevice : pairedDevice){
                //arrayListpaired.add(bDevice.getName()+"\n"+bDevice.getAddress());
                //arrayListPairedBluetoothDevices.add(bDevice);
                Devices d = new Devices();
                d.setName(bDevice.getName());
                d.setMacAddress(bDevice.getAddress());
                //Toma de coordenadas
                GPS_Datos GPS = new GPS_Datos(getActivity());
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
                arrayListpaired.add(d);
            }
            adapter.notifyDataSetChanged();

            Log.e("PairedDevices", String.valueOf(pairedDevice.size()));
        }else {
            Toast.makeText(getActivity(), "busco algo", Toast.LENGTH_SHORT).show();
            Log.e("noPairedDevices", String.valueOf(pairedDevice.size()));
        }*/
    }


    public void itemClick(){
        ListView listViewDetected = (ListView)rootView.findViewById(R.id.DeviceList);

        listViewDetected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(getString(R.string.msg_cambio_name_device));

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.datausuario, null, false);
                final EditText nombredisp = (EditText) v.findViewById(R.id.userName);
                final TextView mac = (TextView) v.findViewById(R.id.mac);
                mac.setText(arrayListpaired.get(position).getName());
                alert.setView(v);
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String nombrecitodisp = nombredisp.getText().toString();
                        Toast.makeText(getActivity(), "EL DISPOSITIVO" + "\n" + arrayListpaired.get(position).getName() + "\n"
                                + "AHORA SE LLAMA: " + "\n" + nombrecitodisp, Toast.LENGTH_LONG).show();
                        arrayListpaired.get(position).setName(nombrecitodisp);
                        DeviceAdapter adapter = new DeviceAdapter(getActivity(), arrayListpaired);
                        activity.setArrayListpaired(arrayListpaired);
                        ListView listViewDetected = (ListView)rootView.findViewById(R.id.DeviceList);
                        listViewDetected.setAdapter(adapter);
                        Log.e("Cambio nombre", activity.getArrayListpaired().get(position).getName());
                        dialog.dismiss();
                    }
                });
                AlertDialog ale = alert.create();
                ale.show();
            }
        });

    }

    public void visibleBT(){
        CheckBox visible = (CheckBox)rootView.findViewById(R.id.checkBox1_visible);
        if (visible.isChecked()) {
            Toast.makeText(getActivity(), "DISPOSITIVO VISIBLE PARA OTROS", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), DISCOVERY_REQUEST);
        } else {
            Toast.makeText(getActivity(), "DISPOSITIVO OCULTO PARA OTROS", Toast.LENGTH_LONG).show();
        }

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
