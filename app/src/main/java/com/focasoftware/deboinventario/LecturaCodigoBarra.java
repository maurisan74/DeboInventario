package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Activity que muestra una pantalla para leer un cdigo de barras.
 */
public class LecturaCodigoBarra extends Activity {

	
	/**
	 * EditText para leer el codigo
	 */
	private EditText textbox_codigo;
	/**
	 * Boton para validar el valor
	 */
	private Button botonValidar;
	/**
	 * Boton para cancelar la lectura
	 */
	private Button botonCancelar;
	/**
	 * Intent para guardar los datos del padre que lo llamo
	 */
	private Intent intentPadre;
	
	
	/**
	 * Metodo a ejecutarse al crearse la activity
	 * <p>1 Carga del layout
	 * <p>2 Recuperamos el intent padre
	 * <p>3 Recuperamos los elementos de UI
	 * <p>4 Cargamos los handlers
	 */
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final GestorLogEventos log = new GestorLogEventos();
        log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
        log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
        log.log("[-- 49 --]" + "Comenzo Lectura Codigo Barra" , 2);
        
        //1 Carga del layout
        setContentView(R.layout.xml_lecturacodigobarra);
        //2 Recuperamos el intent padre
        intentPadre = getIntent();
        //3 Recuperamos los elementos de UI
        textbox_codigo = (EditText) findViewById(R.id.LCB_textbox_codigo);
        botonCancelar = (Button) findViewById(R.id.LCB_botonCancelar);
        botonValidar = (Button) findViewById(R.id.LCB_botonValidar);
        
        //4 Cargamos los handlers
        try {
        botonCancelar.setOnClickListener(new View.OnClickListener() {

        	
        	public void onClick(View v) {
        		log.log("[-- 66 --]" + "Se hizo clic en el boton cancelar" , 0);
				setResult(RESULT_CANCELED, intentPadre);
				finish();
			}
		});
        } catch (Exception e){
        	
        	log.log(e.toString() , 4);
        	e.printStackTrace();
        }
        
        botonValidar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				log.log("[-- 81 --]" + "Se hizo clic ern el boton validar" , 0);
				String codigo_str = String.valueOf(textbox_codigo.getText());
				try {
					int codigo_int = Integer.parseInt(codigo_str);
					intentPadre.putExtra(ParametrosInventario.intent_codigo, codigo_int);
					setResult(RESULT_OK, intentPadre);
					finish();
				} catch (Exception e) { // Caso si el codigo entrado es non-solo numrico
					
					log.log(e.toString() , 4);
					setResult(RESULT_CANCELED, intentPadre);
					finish();
				}
				
			}
		});
        
        
        
	 }
	
	
}
