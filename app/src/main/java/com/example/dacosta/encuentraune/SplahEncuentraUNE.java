package com.example.dacosta.encuentraune;



import com.example.dacosta.encuentraune.PrincipalEncuentraUNE;
import com.example.dacosta.encuentraune.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;


public class SplahEncuentraUNE extends Activity {
	
	public static int retardo = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_encuentra_une);
   
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
    	
		rutinaretardo();
		mensajeB();
		
		
	}
	private void mensajeB() {
		// TODO Auto-generated method stub
		
				
	}
	private Handler manejador=new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		
//    		Intent atras=new Intent (principal.this,Presentacion.class);
//    		startActivity(atras);
    		
    		
    		
    		Intent ir = new Intent(SplahEncuentraUNE.this,PrincipalEncuentraUNE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			ir.putExtra("correo", correo);
			startActivity(ir);
    		finish();
    	}
    };

	private void rutinaretardo() {
		// TODO Auto-generated method stub
		new Thread (){
			public void run(){
				try {
					Thread.sleep(retardo);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				manejador.sendEmptyMessage(0);
			}
		}.start();
	}

 {

}
 
    
    }


  

