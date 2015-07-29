package com.example.dacosta.encuentraune;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        FrameLayout map = (FrameLayout)rootView.findViewById(R.id.mapa);

        activity = (PrincipalEncuentraUNE)getActivity();


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        arrayNoDisponible = activity.getArrayListpaired();

        mapa();

    }


    public void mapa(){
        mapa =((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        LatLng caracas = new LatLng(10.4377896,-66.8417591);


        //vista personalizada del mapa
        CameraPosition posicion = new CameraPosition.Builder()
                .target(caracas)	 //centramos el punto en caracas
                .zoom(15)			//tamano
                .bearing(45)   // orientacion con el noreste
                .tilt(70) 			//se baja el punto de vista de la camara
                .build();

        CameraUpdate refresca = CameraUpdateFactory.newCameraPosition(posicion);

        mapa.animateCamera(refresca);
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (arrayNoDisponible.size() > 0) {
            for (int i = 0; arrayNoDisponible.size() > i; i++) {
                mapa.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(arrayNoDisponible.get(i).getLatitude()),
                        Double.valueOf(arrayNoDisponible.get(i).getLongitude()))).title(arrayNoDisponible.get(i).getName()+" "+ arrayNoDisponible.get(i).getTime()));
                i++;
            }
        }else{
            Toast.makeText(getActivity(), "NO SE TIENEN ULTIMA UBICACION DEL GPS", Toast.LENGTH_LONG).show();
        }


        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng punto) {
                // hago zoomOut para ver marcadores
                    CameraUpdate marcador = CameraUpdateFactory.zoomOut();
                    mapa.animateCamera(marcador);

            }
        });

    }
}
