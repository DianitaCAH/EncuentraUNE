package com.example.dacosta.encuentraune;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by dacosta on 7/24/2015.
 */
public class MapaFragment extends Fragment{

    View rootView;
    GoogleMap mapa;
    PrincipalEncuentraUNE activity;
    ArrayList<Devices> arrayNoDisponible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.mapa, container, false);

        activity = (PrincipalEncuentraUNE)getActivity();

        //Cargo la vista inicial del mapa
        cargarMapa();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        arrayNoDisponible = activity.getArrayListpaired();
        //Coloco los marcadores en el mapa
        //setUpMarker(arrayNoDisponible);
    }

    public void cargarMapa(){
        mapa =((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        //coordenadas de la U.N.E.
        LatLng caracas = new LatLng(10.4377896,-66.8417591);


        //vista personalizada del mapa
        CameraPosition posicion = new CameraPosition.Builder()
                .target(caracas)	 //centramos el punto en caracas
                .zoom(15)			//tamano
                .bearing(45)        // orientacion con el noreste
                .tilt(70) 			//se baja el punto de vista de la camara
                .build();

        CameraUpdate refresca = CameraUpdateFactory.newCameraPosition(posicion);

        mapa.animateCamera(refresca);
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setMyLocationEnabled(true);

        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng punto) {
                // hago zoomOut en el mapa para ver marcadores
                CameraUpdate marcador = CameraUpdateFactory.zoomOut();
                mapa.animateCamera(marcador);
                //coloco los marcadores
                setUpMarker(arrayNoDisponible);
            }
        });
    }

    public void setUpMarker(ArrayList<Devices> arrayList){
        if (!arrayList.isEmpty()) {
            for (int i = 0; arrayList.size() > i; i++) {
                if (!arrayList.get(i).isDisponible()) {
                    mapa.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(arrayList.get(i).getLatitude()),
                            Double.valueOf(arrayList.get(i).getLongitude()))).title(arrayList.get(i).getName() + " " + arrayList.get(i).getTime()));
                    Log.e("setUpMarker", "coloco marker " + i);
                }else{
                    Log.e("setUpMarker", "NO coloco marker " + i);
                }
            }
        }

    }



}
