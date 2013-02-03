package br.ufpr.apptaxi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class PedidoActivity extends Activity {

	private String endereco, referencia ;
	private EditText editEndereco, editReferencia ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        
        editEndereco = (EditText) findViewById(R.id.editPedidoEnd) ;
        editReferencia = (EditText) findViewById(R.id.editPedidoRef) ;
        
        Intent it = getIntent() ;
        if(it != null){
        	Bundle params = it.getExtras();
        	endereco = params.getString("Endereco");
        	referencia = params.getString("Referencia") ;
        	
        	editEndereco.setText(endereco) ;
        	editReferencia.setText(referencia) ;
        }
        
    }//Fecha onCreate
    
    public void onClick(View view){
    	switch(view.getId()){
    	case R.id.btnPedidoAceitar: break ;
    	case R.id.btnPedidoRecusar: 
    		Intent it = new Intent(this, MainActivity.class) ;
    		startActivity(it) ;
    		//finish();
    		break ;
    	}//Fecha switch
    }//Fecha onClick

    @Override
    protected void onPause(){
    	super.onPause();
    	finish();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy() ;
    	finish() ;
    }
    
    @Override
    public void onBackPressed(){
    	
    }
    
}//Fecha activity
