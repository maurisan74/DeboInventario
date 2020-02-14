package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dialogo personalizado donde se muestran los resultados de una bsqueda
 * @author GuillermoR
 *
 */
public class DialogProveComplexResultados extends Dialog {


	@NonNull
    private HashMap<Integer,Integer> codigo_proveedor_seleccionado = new HashMap<Integer, Integer>();


	/**
	 * Constructor
	 * <p>1 Carga contexto y titulo
	 * <p>2 Cargamos el layout y main layout
	 * <p>3 Boton de cancel
	 * <p>4 Carga tabla central
	 * <p>&nbsp;&nbsp;4.1 Listeners y handlers de las lineas de resultado
	 *
	 * @param context
	 * @param lista_propuestas Los resultados de la busqueda?
	 * @param listenerSeleccionar Que hace al seleccionar uno
	 * @param listenerCancel Que hace al cancelar
	 */
	public DialogProveComplexResultados(
            @NonNull Context context,
            @NonNull ArrayList<HashMap<Integer, Object>> lista_propuestas,
            View.OnClickListener listenerSeleccionar/*View.OnLongClickListener listenerSeleccionar*/,
            View.OnClickListener listenerCancel
	) {
		
		
		
		//1� Carga contexto y titulo:
			super(context);
			final Activity owner = (Activity) context;
			/**
			 * Ver como se hace para que seleccione de donde viene la llamada y no agregue
			 * lo de presione 2 veces su eleccin
			 */
			super.setTitle(lista_propuestas.size() + " resultados - Presione 2 veces su eleccion");
		
			
		//2 Cargamos el layout y main layout:
			super.setContentView(R.layout.z_dialogpersocomplexresultados);
		
		
		//3 Boton de cancel:
			Button boton_cancelar = (Button) super.findViewById(R.id.Z_DIALOG_boton_cancelar);
			boton_cancelar.setOnClickListener(listenerCancel);
		
			
		//4 Carga tabla central:
			//TableLayout tabla_resultados = (TableLayout)super.findViewById(R.id.Z_DIALOG_tabla_resultados);
			LinearLayout tabla_resultados = (LinearLayout)super.findViewById(R.id.Z_DIALOG_tabla_resultados);
			
			final GestorLogEventos log = new GestorLogEventos();
			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 75 --]" + "Inicio de Dialog Perso Complex Resultados", 2);
			
			for (HashMap<Integer, Object> hmap : lista_propuestas) {
				//Se programo en XML
//				TableRow tr = new TableRow(context);
//				TextView tv0 = new TextView(context);
//				TextView tv1 = new TextView(context);
//				TextView tv2 = new TextView(context);
//
				LayoutInflater inflater = (LayoutInflater)owner.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//				TableRow linea = (TableRow) inflater.inflate(R.layout.z_linea_busqueda_resultados, null);
//				tabla_resultados.addView(linea);
				LinearLayout linea = (LinearLayout) inflater.inflate(R.layout.z_linea_busqueda_resultados, null);
				
//				TextView tv0=(TextView)linea.getChildAt(COLUMNA_NOMBRE);
//				TextView tv1=(TextView)linea.getChildAt(COLUMNA_SECTOR);
//				TextView tv2=(TextView)linea.getChildAt(COLUMNA_CODIGO);
				
				TextView tv0=(TextView)linea.findViewById(R.id.textViewResultado);
				TextView tv1=(TextView)linea.findViewById(R.id.textViewSector);
				TextView tv2=(TextView)linea.findViewById(R.id.textViewCodigo);
				//Modificado 09/05/12 para que se muestre en la linea el codigo y sector
				String codigo,nombre;
				codigo=String.valueOf(hmap.get(ParametrosInventario.clave_prov_cod));
				nombre=String.valueOf(hmap.get(ParametrosInventario.clave_prov_desc));

				tv0.setText(codigo+"-"+ nombre);
				//Old
//				tv0.setText(String.valueOf(hmap.get(ParametrosInventario.clave_art_nombre)));
//				tv0.setTextColor(owner.getResources().getColor(R.color.white));
//				tv0.setBackgroundColor(owner.getResources().getColor(R.color.verde_oscuro));
//				tv0.setTextSize(18);
//				tv0.setPadding(2, 2, 2, 2);
				tv1.setText(String.valueOf(hmap.get(ParametrosInventario.clave_prov_cod)));
//				tv1.setVisibility(View.GONE);
				tv2.setText(String.valueOf(hmap.get(ParametrosInventario.clave_prov_desc)));
//				tv2.setVisibility(View.GONE);
				
//				tr.addView(tv0);
//				tr.addView(tv1);
//				tr.addView(tv2);
				
				
				//4.1 Listeners y handlers de las lineas de resultado
				linea.setOnTouchListener(new View.OnTouchListener() {
					
					public boolean onTouch(View v, @NonNull MotionEvent event) {
						
						log.log("[-- 123 --]" + "Selecciono un producto", 0);
						
						LinearLayout trow = (LinearLayout)v;
						TextView tv1=(TextView)trow.findViewById(R.id.textViewSector);
						TextView tv2=(TextView)trow.findViewById(R.id.textViewCodigo);
						
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							codigo_proveedor_seleccionado.clear();
							codigo_proveedor_seleccionado.put(ParametrosInventario.clave_prov_cod, Integer.parseInt(String.valueOf(tv1.getText())));
							//codigo_proveedor_seleccionado.put(ParametrosInventario.clave_prov_desc, Integer.parseInt(String.valueOf(tv2.getText())));
							
							int codigo = Integer.parseInt(String.valueOf(tv1.getText()));
							//int codigo = Integer.parseInt(String.valueOf(tv2.getText()));
							log.log("[-- 136 --]" + "codigo proveedor: " + codigo , 2);
							//((TextView)trow.getChildAt(COLUMNA_NOMBRE)).setBackgroundColor(owner.getResources().getColor(R.color.orange));
						}
						/*else if (event.getAction() == MotionEvent.ACTION_UP) {
							TableLayout padre = (TableLayout)trow.getParent();
							
							for (int i = 0 ; i < padre.getChildCount() ; i++) {
								TableRow lineaOnFoco = (TableRow) padre.getChildAt(i);
								TextView casilla = (TextView) lineaOnFoco.getChildAt(COLUMNA_NOMBRE);
								casilla.setBackgroundColor(owner.getResources().getColor(R.color.verde_oscuro));
							}
						}*/
						
						return false;
					}
				});
				
				//tr.setOnLongClickListener(listenerSeleccionar);
				linea.setOnClickListener(listenerSeleccionar);
//				linea.setBackgroundColor(owner.getResources().getColor(R.color.white));
//				linea.setPadding(1, 1, 1, 0);
				
				tabla_resultados.addView(linea);
			}
			
	}
	
	
	@NonNull
    public HashMap<Integer, Integer> get_codigo_proveedor_seleccionado() {
		return codigo_proveedor_seleccionado;
	}
	
}
