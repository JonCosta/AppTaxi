package br.ufpr.apptaxi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import br.ufpr.apptaxi.HttpClient;
import br.ufpr.apptaxi.R;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity implements LocationListener {

	private Location local ;
	private Handler handler ;
	private boolean controle ;
	private SharedPreferences sharedPref ;
	private TextView txtStatus ;
	
    Runnable run = new Runnable(){
    	public void run(){
    		if(controle){
    			mostrarCoord();
    			handler.postDelayed(this, 5000) ;
    		}
    	} //Fecha run
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = (TextView) findViewById(R.id.txtStatus) ;
        txtStatus.setText("LIVRE") ;
        
        handler = new Handler() ;
        checkConfig() ;
        controle = true ;
        handler.post(run) ;
    }//Fecha onCreate

	
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btnConfig: 
    		Intent it = new Intent(getApplicationContext(), ConfigActivity.class) ;
    		startActivity(it) ;
    		break ;
    	}//Fecha switch
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	controle = false ;
    	removerTaxi() ;
    	finish() ;
    }//Fecha onDestroy
    
    @Override
    protected void onPause() {
    	super.onPause() ;    	
    	controle = false ;
    	removerTaxi() ;
    	finish() ;
    }//Fecha onPause
    
    @Override
    protected void onResume(){
    	super.onResume() ;
    	controle = true ;
    }
    
    @Override
    protected void onRestart(){
    	super.onRestart() ;
    	controle = true ;
    }
    
    //Método que verifica se placa e nome forma informados
    public void checkConfig(){
    	//Obtém os dados gravados na SharedPreferences
        sharedPref = getSharedPreferences("Configurações", MODE_PRIVATE) ;
        if((sharedPref.getString("Nome", "").equals(""))||(sharedPref.getString("Placa", "").equals(""))){
        	//Se não houver valores gravados, leva para a tela de Configurações
        	AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        	builder.setTitle("AVISO") ;
        	builder.setMessage("Você não configurou seus dados") ;
        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				Intent it = new Intent(getApplicationContext(), ConfigActivity.class) ;
    	    		startActivity(it) ;
    			}
    		});//Fecha PositiveButton
        	
        	AlertDialog alert = builder.create() ;
        	alert.show() ;
        }//Fecha if
    }//Fecha checkConfig
    
    //Método que obtém os valores de Latitude e Longitude a partir do GPS
    public HashMap getCoordenadas(){
    	
    	
    	//Doubles para receber coordenadas do GPS
    	double lat = 0, lng = 0 ; 
    	String nome, placa ;
    	
    	//HashMap que irá receber os parâmetros que serão covertidos em JSON
    	HashMap params = new HashMap();
    	
    	//Cria o LocationManager
    	LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        String bestProvider = LM.getBestProvider(new Criteria(), true) ;
        
        //Se obtém o local mais recente marcado pelo GPS
        local = LM.getLastKnownLocation(bestProvider) ;
        
        //Obtém-se os valores de latitude e longitude
        lat = local.getLatitude() ;
        lng = local.getLongitude();
        
        Log.d("TAXI", "lat: "+lat+" lng:"+lng) ;
        
        //Obtém os dados de nome e placa do motorista
        sharedPref = getSharedPreferences("Configurações", MODE_PRIVATE) ;
        nome = sharedPref.getString("Nome", "") ;
        placa = sharedPref.getString("Placa", "") ;
        
        //Valores são inseridos no HashMap
        params.put("Latitude", lat);
    	params.put("Longitude", lng);
    	params.put("Nome", nome) ;
    	params.put("Placa", placa);

    	return params ;
    	
    }//Fecha getCoordenadas
    
    //Método que cria objeto JSON, acessa a WS e retorna dados recebidos
    public void mostrarCoord(){
    	
    	//Obtém o HashMap que tem as coordenadas do taxista, junto nome e placa
    	HashMap params = getCoordenadas() ;
    	
    	//Cria-se o objeto JSON a partir do HashMap
        JSONObject jsonParams = new JSONObject(params);
        
        //Obtém-se a 'reposta' da WebService, defindo o método a ser acessado e os parâmetros
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWebService), jsonParams);
    	
    	//Doubles para exibição das coordenadas
    	//double latExib = 0, lngExib =0;
    	String confirm = "", endereco = "", referencia = "" ;
    	boolean pedido = false ;
    	
    	//Obtém-se como resposta da WS as mesmas coordenadas enviadas
    	try {
			//latExib = resp.getDouble("coord1");
			//lngExib = resp.getDouble("coord2") ;
			confirm = resp.getString("msg") ;
			pedido = resp.getBoolean("Pedido") ;
		} catch (JSONException e) {
			Log.e("ERRO JSON", e.toString()) ;
		} catch (NullPointerException e){
			Log.e("ERRO NULL", e.toString()) ;
		}

    	//Exibe na tela a mensagem com as coordenadas recebidas da WS
    	if(pedido){
    		controle = false ;
    		
    		try{
    			endereco = resp.getString("Endereco") ;
    			referencia = resp.getString("Referencia") ;
    			
    		}catch(JSONException e){
    			e.printStackTrace() ;
    		}
    		Intent it = new Intent(this, PedidoActivity.class) ;
    		Bundle bundle = new Bundle() ;
    		bundle.putString("Endereco", endereco) ;
    		bundle.putString("Referencia", referencia) ;
    		it.putExtras(bundle) ;
    		startActivity(it) ;
    	}//Fecha if(pedido)

    	Toast.makeText(this, "Mensagem: "+confirm, Toast.LENGTH_LONG).show() ;
    	
    }//Fecha mostrarCoord
    
    //Função que remove o táxi do Array da WS
    public void removerTaxi(){
    	SharedPreferences sharedPref = getSharedPreferences("Configurações", MODE_PRIVATE) ;
    	String placa = sharedPref.getString("Placa", "") ;
    	HashMap params = new HashMap();
    	params.put("Placa", placa) ;

    	//Cria-se o objeto JSON a partir do HashMap
        JSONObject jsonParams = new JSONObject(params);
        
        //Obtém-se a 'reposta' da WebService, defindo o método a ser acessado e os parâmetros
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWebService2), jsonParams);
    	try{
    		String result = resp.getString("Result") ;
    		Log.d("REMOVER", result ) ;
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	
    	
    }//Fecha removerTaxi
    
    //---CLASSES DE LOCATION LISTENER---\\
	@Override
	public void onLocationChanged(Location location) {
		this.local = location ;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
    
}//Fecha Activity