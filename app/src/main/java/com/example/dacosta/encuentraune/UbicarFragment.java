package com.example.dacosta.encuentraune;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by dacosta on 7/24/2015.
 */
public class UbicarFragment extends Fragment implements LocationListener{


    View rootView;
    private int MAX_INTENTO_BT = 3;
    private GPS_Datos Loc;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ArrayList<Devices> arrayListpaired;
    ArrayList<Devices> arrayNoDisponible;
    BluetoothSocket mBTSocket;
    BluetoothAdapter bluetoothAdapter;

    private int intento_bt = 0;
    PrincipalEncuentraUNE activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ubicar, container, false);

        activity = (PrincipalEncuentraUNE)getActivity();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        arrayListpaired = activity.getArrayListpaired();
        arrayNoDisponible = new ArrayList<Devices>();

        Button showISP = (Button)rootView.findViewById(R.id.btShowISP);
        showISP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listViewDetected = (ListView) rootView.findViewById(R.id.DeviceListDisponible);
                listViewDetected.setVisibility(View.VISIBLE);
                mostrarISPdisponibles();
            }
        });

        Button localizar = (Button)rootView.findViewById(R.id.btShowLocation);
        localizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listViewDetected = (ListView) rootView.findViewById(R.id.DeviceListDisponible);
                listViewDetected.setVisibility(View.GONE);
                buscarDevices();
                alertarNOdisponibles(arrayListpaired);
            }
        });

    }

    private void buscarDevices() {
        Log.e("buscarDevices", "buscandoBT itmes...");

        if (bluetoothAdapter == null) {
            Toast.makeText(getActivity(), "BT sin conexion", Toast.LENGTH_LONG).show();
        } else {
            if(!bluetoothAdapter.isEnabled()){
                Toast.makeText(getActivity(), "Debe activar el BT", Toast.LENGTH_LONG).show();
            } else {
                Log.e("buscarDevices", "conectando a la lista...");
                actualizarCoordenadas(arrayListpaired);
            }
        }
    }

    //trato de conectarme con los dispositivos y actualizar sus coordenadas
    private void actualizarCoordenadas(ArrayList<Devices> devicesArrayList) {
        double gps_long = 0, gps_lat = 0;
        for (int i = 0; devicesArrayList.size() > i; i++) {
            BluetoothDevice mDevice = bluetoothAdapter.getRemoteDevice(devicesArrayList.get(i).getMacAddress());
            //intento conectar con los dispositivos
            if (mBTSocket!=null)
                closeSocket(mBTSocket);
            if (connecBTDevice(mDevice)) {
                Log.e("conectarBT", "conectando y actualizando a "+devicesArrayList.get(i).getName());
                //Toma de coordenadas
                Toast.makeText(getActivity(), "ACTUALIZANDO UBICACION", Toast.LENGTH_LONG).show();
                GPS_Datos GPS = new GPS_Datos(activity);
                Loc = GPS.PrenderGPS();
                if (Loc.getLocationManager() != null)
                    if (Loc.isPorGPS()) {
                        Loc.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        this.onLocationChanged(Loc.getLocacion());
                        arrayListpaired.get(i).setLatitude(String.valueOf(Loc.getLocacion().getLongitude()));
                        arrayListpaired.get(i).setLongitude(String.valueOf(Loc.getLocacion().getLatitude()));
                    } else {
                        gps_long = Loc.getLocacion().getLongitude();
                        gps_lat = Loc.getLocacion().getLatitude();
                        arrayListpaired.get(i).setLatitude(String.valueOf(gps_lat));
                        arrayListpaired.get(i).setLongitude(String.valueOf(gps_long));
                    }
                arrayListpaired.get(i).setDisponible(true);
                arrayListpaired.get(i).setTime(horaStrging());

            }else{
                arrayListpaired.get(i).setDisponible(false);
                Log.e("conectarBT", "se puso false!!");
            }


        }
        activity.setArrayListpaired(arrayListpaired);

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "DISPOSITIVOS LOCALIZADOS EXITOSAMENTE", Toast.LENGTH_LONG).show();

        }
    };

    public boolean connecBTDevice(final BluetoothDevice mDevice) {
        if (intento_bt <= MAX_INTENTO_BT) {
            try {
                bluetoothAdapter.cancelDiscovery();
                mBTSocket = mDevice.createRfcommSocketToServiceRecord(applicationUUID);
                mBTSocket.connect();
                if (mBTSocket.isConnected())
                    mHandler.sendEmptyMessage(0);

                Log.e("run", "conecto");
                return true;
            } catch (final IOException eConnectException) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        intento_bt++;
                        Toast.makeText(getActivity(), "Fallo conexion con algun ISP", Toast.LENGTH_LONG).show();
                        if (intento_bt <= MAX_INTENTO_BT) {
                            Thread.currentThread().interrupt();
                            //intento conectarme de nuevo
                            connecBTDevice(mDevice);
                        } else {
                            CerrarConexion(mBTSocket);
                            Log.e("run", "NO conecto");
                        }
                    }
                });
                return false;
            }

        } else {
            CerrarConexion(mBTSocket);
            return false;
        }
    }

    private void CerrarConexion(BluetoothSocket mBTSocket) {
        intento_bt = 0;
        closeSocket(mBTSocket);
        Thread.currentThread().interrupt();
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d("TAG", "SocketClosed");
        } catch (IOException ex) {
            Log.d("TAG", "CouldNotCloseSocket");
        }
    }

    //Tomo la hora y fecha
    public String horaStrging() {
        SimpleDateFormat dfv = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        Date date = new Date();
        return dfv.format(date.getTime());
    }


    @Override
    public void onLocationChanged(Location location) {
        double gps_long = location.getLongitude();
        double gps_lat = location.getLatitude();
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

    //muestro lista de dispositivos
    public void mostrarISPdisponibles() {
        ArrayList<Devices> deviceDispo = new ArrayList<>();
        for (int i = 0; arrayListpaired.size() > i; i++) {
            if (arrayListpaired.get(i).isDisponible()) {
                deviceDispo.add(arrayListpaired.get(i));
            }
        }
        if (deviceDispo.isEmpty()) {
            Toast.makeText(getActivity(), "LISTA DE DISP NO DISPONIBLES", Toast.LENGTH_LONG).show();
            ListView listViewDetected = (ListView) rootView.findViewById(R.id.DeviceListDisponible);
            DeviceAdapter adapter = new DeviceAdapter(getActivity(), arrayListpaired);
            listViewDetected.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getActivity(), "LISTA DE DISP DISPONIBLES", Toast.LENGTH_LONG).show();
            ListView listViewDetected = (ListView) rootView.findViewById(R.id.DeviceListDisponible);
            DeviceAdapter adapter = new DeviceAdapter(getActivity(), deviceDispo);
            listViewDetected.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Log.d("deviceDispo", String.valueOf(deviceDispo.size()));
        }
    }

    public void alertarNOdisponibles(ArrayList<Devices> devicesArrayList){
        int alertar = 0;
        for (int i = 0; devicesArrayList.size() > i; i++) {
            if (devicesArrayList.get(i).isDisponible())
                alertar++;
            else
                alertar--;
        }
        if (alertar>0)
            dialogISPloss();
    }

    //dialogo para alertar que algun dispositivo esta perdido
    private void dialogISPloss() throws Resources.NotFoundException{
        int icono = R.drawable.warning;

        String mensajeDialogo ="Para ver coordenadas vaya al MAPA";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("ALGUN DISPOSITIVO NO FUE LOCALIZADO")
                .setMessage(mensajeDialogo)
                .setIcon(icono)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }


}
