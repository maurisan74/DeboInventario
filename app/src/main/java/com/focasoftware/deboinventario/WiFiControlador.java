package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity que permite buscar la red WIFI y conectar con la misma verificando si 
 * esta visible y disponible para conexion
 * @author GuillermoR
 *
 */
public class WiFiControlador extends Activity {
	/**
	 * Variable donde se almacena la info de contexto de la activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Variable para guardar el intent que llamo a esta aplicacion y obtener
	 * la informacion pasada como parametro
	 */
	private Intent intentPadre;
	/**
	 * Barras de progreso accesorias
	 */
	private ProgressBar pb1, pb2;
	/**
	 * TextView para mostrar un cartel de conectado
	 */
	private TextView conectado_si;
	/**
	 * TextView para mostrar un cartel de que no esta conectado
	 */
	private TextView conectado_no; 
	/**
	 * TextView para moestrar un cartel si esta visible la red
	 */
	private TextView visible_si; 
	/**
	 * TextView para mostrar un cartel de que no esta visible
	 */
	private TextView visible_no;
	/**
	 * TextView para mostrar el nombre de la red configurada
	 */
	private TextView nombre_red_config;
	/**
	 * Boton para validar la conexion a la red encontrada
	 */
	private Button boton_validar;
	/**
	 * Boton para cancelar la conextion
	 */
	private Button boton_cancelar; 
	/**
	 * Boton para intentar conectar a la red
	 */
	private Button boton_conectar; 
	/**
	 * Boton para buscar la red
	 */
	private Button boton_visible;
	/**
	 * Variable para poder acceder a los datos de WIFI del dispositivo
	 */
	@Nullable
    private WifiManager wifiManager;
	/**
	 * Administrador de conectividad para aceder a esos datos
	 */
	@Nullable
    private ConnectivityManager wifiConexion;
	/**
	 * Variable para almacenar la informacion de la red WIFI
	 */
	@Nullable
    private NetworkInfo wifiInfo;
	/**
	 * Variable para almacenar la lista de todas las redes disponibles
	 */
	private List<WifiConfiguration> listaTodasRedes = new ArrayList<WifiConfiguration>();
	
	
	/**
	 * Funcion que se llama cuando se crea la actividad: carga la UI,handlers e 
	 * informacion de la conexion
	 * <p>1� Obtenemos el intent padre
	 * <p>2� Cargar elementos gr�ficos
	 * <p>3� Configuramos la visibilidad de cada uno
	 * <p>4� Control: si ya est� conectado, no pedimos reconexion
	 * <p>5� Configuramos los HANDLES y LISTENERS:
	 * <p>6� Listener del boton visible
	 * <p>&nbsp;&nbsp;6.1 Activamos el wifi desactivando el modo avion
	 * <p>&nbsp;&nbsp;6.2 Recuperamos la lista de todos los SSID visibles
	 * <p>&nbsp;&nbsp;6.3 Configuramos la visibilidad de los botones y TextView 
	 * 		correspondientes, Si esta la WIFI predefinida en la lista de las encontradas
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xml_wificontrolador);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//1� Obtenemos el intent padre
		intentPadre = getIntent();
		
		//2� Cargar elementos gr�ficos:
			pb1 = (ProgressBar) findViewById(R.id.WIFI_progressBar1);
			pb2 = (ProgressBar) findViewById(R.id.WIFI_progressBar2);
			
			conectado_si = (TextView) findViewById(R.id.WIFI_conectada_si);
			conectado_no = (TextView) findViewById(R.id.WIFI_conectada_no);
			visible_si = (TextView) findViewById(R.id.WIFI_visible_si);
			visible_no = (TextView) findViewById(R.id.WIFI_visible_no);
			nombre_red_config = (TextView) findViewById(R.id.WIFI_red_config);
			
			boton_validar = (Button) findViewById(R.id.WIFI_boton_validar);
			boton_cancelar = (Button) findViewById(R.id.WIFI_boton_cancelar);
			boton_conectar = (Button) findViewById(R.id.WIFI_boton_conectar);
			boton_visible = (Button) findViewById(R.id.WIFI_boton_visible);
		
		//3� Configuramos la visibilidad de cada uno:
			nombre_red_config.setText("Red configurada: " + Parametros.PREF_WIFI_PRIVILEGIADO);
			
			pb1.setVisibility(View.INVISIBLE);
			pb2.setVisibility(View.INVISIBLE);
			
			boton_visible.setEnabled(true);
			boton_conectar.setEnabled(false);
			
			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			
		//4� Control: si ya est� conectado, no pedimos reconexion
			ConnectivityManager redConexion = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			
			NetworkInfo[] allNtwk = redConexion.getAllNetworkInfo();

			//Toast.makeText(ctxt, redConexion.getNetworkInfo(5).getState().toString(), Toast.LENGTH_LONG).show();
			boolean conectado = false;
			
			for (NetworkInfo ntwk : allNtwk) {
				if (ntwk.getState() == NetworkInfo.State.CONNECTED) {
					conectado = true;
					break;
				}
			}
			
			if (conectado) {
				setResult(RESULT_OK, intentPadre);
				finish();
			} else {
				wifiManager.setWifiEnabled(true);
				refreshUI();
			}

		
		//5� Configuramos los HANDLES y LISTENERS:
			boton_cancelar.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					setResult(RESULT_CANCELED, intentPadre);
					finish();
				}
			});
			
			/// TODO AAAAAAAAVVVVVVVVVVVVVIIIIIIIIIIIIRRRRRRRRRRRRREEEEEEEEEEEEEEEEEEEEEERRRRRRRRRRRRRRRRRRR!!!!!!!!!!!
			boton_cancelar.setOnLongClickListener(new View.OnLongClickListener() {
				
				
				public boolean onLongClick(View v) {
					//Para saltearnos esta parte si es por emulador
					setResult(RESULT_OK, intentPadre);
					finish();
					return true;
				}
			});
					
			boton_validar.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					setResult(RESULT_OK, intentPadre);
					finish();
				}
			});
			
			//6� Listener del boton visible
			boton_visible.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					pb1.setVisibility(View.VISIBLE);
					pb2.setVisibility(View.VISIBLE);
					
					//6.1 Activamos el wifi desactivando el modo avion:
						WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						Settings.System.putInt(ctxt.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
						Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
						intent.putExtra("state", false);
						sendBroadcast(intent);
						
						wifiManager.setWifiEnabled(true);
					
					//6.2 Recuperamos la lista de todos los SSID visibles:
						listaTodasRedes = wifiManager.getConfiguredNetworks();
						ArrayList<String> listaSSID = new ArrayList<String>();
						for (WifiConfiguration wc : listaTodasRedes ){
							listaSSID.add(wc.SSID);
						}
					//6.3 Configuramos la visibilidad de los botones y TextView correspondientes, Si esta la WIFI predefinida en la lista de las encontradas
						if (listaSSID.contains("\"" + Parametros.PREF_WIFI_PRIVILEGIADO + "\"") == true) {
							visible_si.setVisibility(View.VISIBLE);
							visible_no.setVisibility(View.INVISIBLE);
							boton_visible.setEnabled(false);
							boton_conectar.setEnabled(true);
						} else {
							Toast.makeText(ctxt, "�Red no visible!", Toast.LENGTH_SHORT).show();
							visible_no.setVisibility(View.VISIBLE);
							visible_si.setVisibility(View.INVISIBLE);
							boton_visible.setEnabled(true);
							boton_conectar.setEnabled(false);
						}
					
					pb1.setVisibility(View.INVISIBLE);
					pb2.setVisibility(View.INVISIBLE);	
				}
			});
			
			
			
			boton_conectar.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					pb1.setVisibility(View.VISIBLE);
					pb2.setVisibility(View.VISIBLE);
					
					// Tratamos de conectar la red habilitando al wifi:
						WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						Settings.System.putInt(ctxt.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
						Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
						intent.putExtra("state", false);
						sendBroadcast(intent);
						
						wifiManager.setWifiEnabled(true);
						
						
						SystemClock.sleep(2000);
						
						
					// Checkeamos el estado de la conexion:
						ConnectivityManager wifiConexion = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
						NetworkInfo wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						
						if (wifiInfo.isConnected() == true) {
							conectado_si.setVisibility(View.VISIBLE);
							conectado_no.setVisibility(View.INVISIBLE);
							boton_conectar.setEnabled(false);
							boton_validar.setEnabled(true);
						} else {
							Toast.makeText(ctxt, "�Red sin conectar!", Toast.LENGTH_SHORT).show();
							conectado_no.setVisibility(View.VISIBLE);
							conectado_si.setVisibility(View.INVISIBLE);
							boton_conectar.setEnabled(true);
							boton_validar.setEnabled(false);
						}

					pb1.setVisibility(View.INVISIBLE);
					pb2.setVisibility(View.INVISIBLE);
					
				}
			});
			
	}
	
	/**
	 * Permite pedir informacion del estado de la WIFI para saber si esta conectado
	 * @param ctxt
	 * @return
	 */
	public static boolean getEstadoWifi(@NonNull Context ctxt) {
		ConnectivityManager wifiConexion = (ConnectivityManager) ctxt.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiInfo.isConnected();
	}
	
	
	/**
	 * Refresca la UI al encontrar la Red predeterminada
	 * <p>1� Busca todas las redes
	 * <p>2� Si encuentra en la lista la prefefinida, 
	 * 		activa los botones y textView de aviso correspondientes
	 * <p>3� Verifica si esta conectado para mostrarlo por la UI
	 */
	private void refreshUI() {
		//1� Busca todas las redes
		listaTodasRedes = wifiManager.getConfiguredNetworks();
		boolean redVisible = false;
		
		//2� Si encuentra en la lista la prefefinida, activa los botones y textView de aviso correspondiente
		for (WifiConfiguration unaConfig : listaTodasRedes) {
			if ( (unaConfig.SSID).compareTo(Parametros.PREF_WIFI_PRIVILEGIADO) == 0 
							|| (unaConfig.SSID).compareTo("\"" + Parametros.PREF_WIFI_PRIVILEGIADO + "\"") == 0 ) {
				wifiManager.enableNetwork(unaConfig.networkId, true);
				visible_si.setVisibility(View.VISIBLE);
				visible_no.setVisibility(View.INVISIBLE);
				boton_visible.setEnabled(false);
				boton_conectar.setEnabled(true);
				redVisible = true;
				break;
			}
		}
		
		if (redVisible == false) {
			visible_no.setVisibility(View.VISIBLE);
			visible_si.setVisibility(View.INVISIBLE);
			conectado_no.setVisibility(View.VISIBLE);
			conectado_si.setVisibility(View.INVISIBLE);
			boton_visible.setEnabled(true);
			boton_conectar.setEnabled(false);
			boton_validar.setEnabled(false);
		} else {
			
			wifiConexion = (ConnectivityManager) ctxt.getSystemService(Activity.CONNECTIVITY_SERVICE);
			wifiInfo = wifiConexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			//3� Verifica si esta conectado para mostrarlo por la UI
			if (wifiInfo.isConnected() == true) {
				conectado_si.setVisibility(View.VISIBLE);
				conectado_no.setVisibility(View.INVISIBLE);
				boton_conectar.setEnabled(false);
				boton_validar.setEnabled(true);
			} else {
				conectado_no.setVisibility(View.VISIBLE);
				conectado_si.setVisibility(View.INVISIBLE);
				boton_conectar.setEnabled(true);
				boton_validar.setEnabled(false);
			}
		}
	} // end refreshUI
		
	
}
