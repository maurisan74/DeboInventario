package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Dialogo que hereda de la clase Dialog y se usa para permitir
 * al usuario elegir el medio de actualizaci�n de los datos, sea este
 * por WIFI o USB.
 * @author GuillermoR
 *
 */
public class DialogPersoComplexActualizacionWifiUsb extends Dialog {

	
	@SuppressWarnings("unused")
	private Activity owner;
	
	
	/**
	 * Constructor con toda la informacion a mostrar en el dialogo, arma la UI para 
	 * dejarla lista para ser mostrada
	 * <p>1� Construcci�n del t�tulo
	 * <p>2� Cargamos el layout y main layout
	 * <p>3� Construcci�n de la imagen
	 * <p>4� Mensaje
	 * <p>5� Construir opciones
	 * <p>6� Botones
	 * 
	 * @param context
	 * @param lista
	 * @param titulo
	 * @param mensaje
	 * @param categoria_alerta
	 * @param lista_opciones
	 * @param listenerPositivoWifi
	 * @param listenerPositivoUsb
	 * @param listenerPositivoFlash
	 * @param listenerPositivoSdcard
	 * @param listenerNegativo
	 */
	public DialogPersoComplexActualizacionWifiUsb (
            @NonNull Context context,
            @NonNull final ArrayList<String> lista,
            @NonNull String titulo,
            String mensaje,
            int categoria_alerta,
            @NonNull ArrayList<String> lista_opciones,
            View.OnClickListener listenerPositivoWifi,
            View.OnClickListener listenerPositivoUsb,
            View.OnClickListener listenerPositivoFlash,
            View.OnClickListener listenerPositivoSdcard,
            View.OnClickListener listenerNegativo) {
		
		super(context);
		final Activity owner = (Activity) context;
		
		//1� Construcci�n del t�tulo:
			if (titulo.length() > 0) {
				super.setTitle(titulo);
			}
			
		//2� Cargamos el layout y main layout:
			super.setContentView(R.layout.z_dialogpersocomplexactualizacion_wifiusb);
			
		//3� Construcci�n de la imagen:
			ImageView imagen = (ImageView) super.findViewById(R.id.DIALOG_imagen);
			
			switch (categoria_alerta) {
				case DialogPerso.DEFAULT:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
					
				case DialogPerso.VALIDAR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_validar));
					break;
					
				case DialogPerso.ALERTAR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
					
				case DialogPerso.PROHIBIR:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_prohibir));
					break;
				
				default:
					imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.dialog_alertar));
					break;
			}
			
		//4� Mensaje:
			TextView texto = (TextView) super.findViewById(R.id.DIALOG_texto);
			texto.setText(mensaje);
			
		//5� Construir opciones:
			TableLayout tabla_opciones = (TableLayout) super.findViewById(R.id.DIALOG_zona_box);
			lista.clear();
			
			for (int i = 0 ; i < lista_opciones.size() ; i++) {
				final String tablaBDD = lista_opciones.get(i);
				TableRow tblRow = new TableRow(context);
				CheckBox chkBox = new CheckBox(context);
				chkBox.setId(i);
				chkBox.setText(tablaBDD);
				chkBox.setTextSize(16);
				chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked == true) {
							lista.add(tablaBDD);
						} else {
							lista.remove((String) tablaBDD);
						}
					}
				});
				
				tblRow.addView(chkBox);
				tabla_opciones.addView(tblRow);
			}
			
		//6� Botones:
			Button boton_validar_usb = (Button) super.findViewById(R.id.DIALOG_boton_validar_usb);
			Button boton_validar_wifi = (Button) super.findViewById(R.id.DIALOG_boton_validar_wifi);
			Button boton_validar_flash = (Button) super.findViewById(R.id.DIALOG_boton_flash);
			Button boton_validar_sdcard = (Button) super.findViewById(R.id.DIALOG_boton_sdcard);
			Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancelar);
			
			boton_validar_usb.setOnClickListener(listenerPositivoUsb);
			boton_validar_flash.setOnClickListener(listenerPositivoFlash);
			boton_validar_sdcard.setOnClickListener(listenerPositivoSdcard);
			boton_validar_wifi.setOnClickListener(listenerPositivoWifi);
			boton_cancelar.setOnClickListener(listenerNegativo);
	}
	
	
	
}
