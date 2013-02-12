package br.ufpr.apptaxi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PedidoActivity extends Activity {

	private String endereco, referencia ;
	private EditText editEndereco, editReferencia ;
	private int posicao ;
	boolean loadSound = false ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        
        playSound(getApplicationContext(), R.raw.alert) ;
        playSound(getApplicationContext(), R.raw.alert) ;
        playSound(getApplicationContext(), R.raw.alert) ;
        
        editEndereco = (EditText) findViewById(R.id.editPedidoEnd) ;
        editReferencia = (EditText) findViewById(R.id.editPedidoRef) ;
        
        Intent it = getIntent() ;
        if(it != null){
        	//Recebe da Intent os valores do pedido, e sua posi��o, que ser� usada posteriormente
        	Bundle params = it.getExtras();
        	endereco = params.getString("Endereco");
        	referencia = params.getString("Referencia") ;
        	posicao = params.getInt("PosicaoPedido") ;
        	
        	//Valores s�o colocados nos EditText
        	editEndereco.setText(endereco) ;
        	editReferencia.setText(referencia) ;
        }//Fecha if
        
    }//Fecha onCreate
    
    //M�todo que toca o som gravado na pasta raw
    public void playSound(Context context, int soundID){
    	MediaPlayer mp = MediaPlayer.create(context, soundID) ;
    	mp.start() ;
    }//Fecha playSound
    
    public void onClick(View view){
    	switch(view.getId()){
    	case R.id.btnPedidoAceitar: 
    		enviarConfirmPedido() ;
    		break ;
    	case R.id.btnPedidoRecusar: 
    		//Retorna para a MainActivity
    		Intent it = new Intent(this, MainActivity.class) ;
    		startActivity(it) ;
    		finish(); //Encerra esta atividade para que n�o haja retorno
    		break ;
    	}//Fecha switch
    }//Fecha onClick

    //M�todo que envia a confirma��o de pedido para a WS
    public void enviarConfirmPedido(){
    	//Obt�m-se as informa��es do taxista, gravadas na SharedPreferences
    	boolean ok = false ; //Boolean a ser usado em sequencia
    	SharedPreferences sharedPref = getSharedPreferences("Configura��es", MODE_PRIVATE) ;
    	String nomeTaxista = sharedPref.getString("Nome", "") ;
    	String placaTaxi = sharedPref.getString("Placa", "") ;
    	
    	//Colocam-se as informa��es do taxista como par�metros para m�todo da WS
    	HashMap params = new HashMap() ;
    	params.put("NomeTaxista", nomeTaxista) ;
    	params.put("PlacaTaxi", placaTaxi) ;
    	params.put("Indice", posicao) ; //Posi��o do Pedido recebido no ArrayList
    	JSONObject jsonParams = new JSONObject(params) ;
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSenviarConfirmPedido), jsonParams) ;
    	
    	//Recebe como resposta um boolean que verifica se a confirma��o foi enviada com sucesso
    	try{
    		ok = resp.getBoolean("OK") ;
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	
    	//Se a confirma��o foi enviada corretamente, abre nova MainActivity com par�metro 
    	if(ok){
    		Intent it = new Intent(this, MainActivity.class) ;
    		Bundle param = new Bundle() ;
    		param.putBoolean("Corrida", true) ; //Indicar� que o aplicativo n�o deve enviar coordenadas ao abrir a tela
    		it.putExtras(param) ;
    		startActivity(it) ;
    		finish() ;
    	}
    }//Fecha enviaConfirmPedido
    

    //----M�TODOS PADR�O ANDROID---\\
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
