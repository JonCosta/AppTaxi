package br.ufpr.apptaxi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigActivity extends Activity {

	private EditText editPlaca, editNome ;
	private String nome, placa ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        
        editNome = (EditText) findViewById(R.id.editConfigNome) ;
        editPlaca = (EditText) findViewById(R.id.editConfigPlaca) ;
        
        SharedPreferences sharedPref = getSharedPreferences("Config", MODE_PRIVATE) ;
        nome = sharedPref.getString("Nome", "") ;
        placa = sharedPref.getString("Placa", "") ;
        editNome.setText(nome) ;
        editPlaca.setText(placa) ;
        
        
    }//Fecha onCreate

    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btnConfigSalvar: 
    		nome = editNome.getText().toString() ;
    		placa = editPlaca.getText().toString() ;
    		
    		if((nome.equals(""))||(placa.equals(""))){
            	AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            	builder.setTitle("AVISO") ;
            	builder.setMessage("Por favor, preencha todos os campos e salve os dados") ;
            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        			}
        		});//Fecha PositiveButton
            	
            	AlertDialog alert = builder.create() ;
            	alert.show() ;
            }else{
            	SharedPreferences sharedPref = getSharedPreferences("Configurações", MODE_PRIVATE) ;
                Editor editor = sharedPref.edit() ;
                editor.clear() ;
        		editor.putString("Nome", nome) ;
        		editor.putString("Placa", placa) ;
        		editor.commit() ;
        		finish() ;
            }
    		break ;
    		
    	}//Fecha switch
    }//Fecha 
    
    public void onBackPressed(){
    	SharedPreferences sharedPref = getSharedPreferences("Config", MODE_PRIVATE) ;
        nome = sharedPref.getString("Nome", "") ;
        placa = sharedPref.getString("Placa", "") ;
        if((nome.equals(""))||(placa.equals(""))){
        	AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        	builder.setTitle("AVISO") ;
        	builder.setMessage("Por favor, preencha todos os campos e salve os dados") ;
        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			}
    		});//Fecha PositiveButton
        	
        	AlertDialog alert = builder.create() ;
        	alert.show() ;
        }
        else{
        	finish() ;
        }
    }//Fecha onReturn
    
}//Fecha Activity