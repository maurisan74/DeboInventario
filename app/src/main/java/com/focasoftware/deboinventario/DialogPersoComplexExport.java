package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Dialgo personalizado que permite al usuario elegir el medio de exportacion y si
 * borrar los datos o no posteriormente
 * @author GuillermoR
 *
 */
public class DialogPersoComplexExport extends Dialog{
	/*
	 * Quien lo llamo
	 */
	@SuppressWarnings("unused")
	private Activity owner;
	/*
	 * Hay que borrar despues los datos exportados?
	 */
	private boolean borrar_luego = false;

	/*
	 * Constructor completo que incializa la UI y carga los handlers correspondientes
	 * <p>1 Construccin del ttulo
	 * <p>2 Cargamos el layout y main layout
	 * <p>3 Construccin de la imagen
	 * <p>4 Mensaje
	 * <p>5 Check Box de supresion
	 * <p>6 Botones
	 *
	 * @param context
	 * @param titulo
	 * @param mensaje
	 * @param activar_borrar
	 * @param listenerRedWifi
	 * @param listenerUsb
	 * @param listenerNegativo
	 */
	public DialogPersoComplexExport (Context context,
									 String titulo, String mensaje, boolean activar_borrar,
									 View.OnClickListener listenerRedWifi,
									 View.OnClickListener listenerUsb,
									 View.OnClickListener listenerNegativo) {

		super(context);
		final Activity owner = (Activity) context;

		//1 Construccin del ttulo:
		if (titulo.length() > 0) {
			super.setTitle(titulo);
		}


		//2 Cargamos el layout y main layout:
		super.setContentView(R.layout.z_dialogpersocomplex_export);


		//3 Construccin de la imagen:
		ImageView imagen = (ImageView) super.findViewById(R.id.DIALOG_imagen);
		imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_export));


		//4 Mensaje:
		TextView texto = (TextView) super.findViewById(R.id.DIALOG_texto);
		texto.setText(mensaje);


		//5 Check Box de supresion:
		CheckBox chkbox = (CheckBox) super.findViewById(R.id.DIaLOG_chkbox);
		if (activar_borrar == true) {
			chkbox.setVisibility(View.VISIBLE);
		}
		else {
			chkbox.setVisibility(View.INVISIBLE);
		}

		chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == true) {
					borrar_luego = true;
				}
				else {
					borrar_luego = false;
				}
			}
		});


		//6 Botones:
		Button boton_wifi = (Button) super.findViewById(R.id.DIALOG_boton_wifi);
		Button boton_usb = (Button) super.findViewById(R.id.DIALOG_boton_usb);
		Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancelar);

		boton_wifi.setOnClickListener(listenerRedWifi);
		boton_usb.setOnClickListener(listenerUsb);
		boton_cancelar.setOnClickListener(listenerNegativo);
	}


	public boolean isBorrar_luego() {
		return borrar_luego;
	}
}







//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
///**
// * Dialgo personalizado que permite al usuario elegir el medio de exportacion y si
// * borrar los datos o no posteriormente
// * @author GuillermoR
// *
// */
//public class DialogPersoComplexExport extends Dialog{
//	/**
//	 * Quien lo llamo
//	 */
//	@SuppressWarnings("unused")
//	private Activity owner;
//	/**
//	 * Hay que borrar despues los datos exportados?
//	 */
//	private boolean borrar_luego = false;
//
//	/*
//	 * Constructor completo que incializa la UI y carga los handlers correspondientes
//	 * <p>1� Construcci�n del t�tulo
//	 * <p>2� Cargamos el layout y main layout
//	 * <p>3� Construcci�n de la imagen
//	 * <p>4� Mensaje
//	 * <p>5� Check Box de supresion
//	 * <p>6� Botones
//	 *
//	 * @param context
//	 * @param titulo
//	 * @param mensaje
//	 * @param activar_borrar
//	 * @param listenerRedWifi
//	 * @param listenerUsb
//	 * @param listenerNegativo
//	 */
//	public DialogPersoComplexExport (@NonNull Context context,
//                                     @NonNull String titulo, String mensaje, boolean activar_borrar,
//                                     View.OnClickListener listenerRedWifi,
//                                     View.OnClickListener listenerUsb,
//                                     View.OnClickListener listenerNegativo) {
//
//		super(context);
//		final Activity owner = (Activity) context;
//
//		//1� Construcci�n del t�tulo:
//			if (titulo.length() > 0) {
//				super.setTitle(titulo);
//			}
//
//
//		//2� Cargamos el layout y main layout:
//			super.setContentView(R.layout.z_dialogpersocomplex_export);
//
//
//		//3� Construcci�n de la imagen:
//			ImageView imagen = (ImageView) super.findViewById(R.id.DIALOG_imagen);
//			imagen.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_export));
//
//
//		//4� Mensaje:
//			TextView texto = (TextView) super.findViewById(R.id.DIALOG_texto);
//			texto.setText(mensaje);
//
//
//		//5� Check Box de supresion:
//			CheckBox chkbox = (CheckBox) super.findViewById(R.id.DIaLOG_chkbox);
//			if (activar_borrar == true) {
//				chkbox.setVisibility(View.VISIBLE);
//			}
//			else {
//				chkbox.setVisibility(View.INVISIBLE);
//			}
//
//			chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					if (isChecked == true) {
//						borrar_luego = true;
//					}
//					else {
//						borrar_luego = false;
//					}
//				}
//			});
//
//
//		//6� Botones:
//			Button boton_wifi = (Button) super.findViewById(R.id.DIALOG_boton_wifi);
//			Button boton_usb = (Button) super.findViewById(R.id.DIALOG_boton_usb);
//			Button boton_cancelar = (Button) super.findViewById(R.id.DIALOG_boton_cancelar);
//
//			boton_wifi.setOnClickListener(listenerRedWifi);
//			boton_usb.setOnClickListener(listenerUsb);
//			boton_cancelar.setOnClickListener(listenerNegativo);
//	}
//
//
//	public boolean isBorrar_luego() {
//		return borrar_luego;
//	}
//}
