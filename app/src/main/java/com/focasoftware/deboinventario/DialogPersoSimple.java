package com.focasoftware.deboinventario;

import android.app.AlertDialog;

import androidx.annotation.Nullable;

/**
 * Interface que obliga a quienes la implementan a implementar los metodos para 
 * mostrar un dialogo de Ok o un dialogo de Si o No
 * @author GuillermoR
 *
 */
public interface DialogPersoSimple {

	/**Dialogo en el cual se mostra un texto con titulo, de informacion, 
	 * confirmandose con un OK sin implicar ninguna accion posterior
	 * 
	 * @param titulo
	 * @param mensaje
	 * @return
	 */
	public AlertDialog showSimpleDialogOK(String titulo, String mensaje);
	
	/**
	 * Dialog en el cual el usuario tiene que contestar con S� o No, 
	 * el "SI dando lugar a un vinculo hacia una ACTIVITY" pasada como parametro
	 * clase
	 * 
	 * @param titulo
	 * @param mensaje
	 * @param clase
	 * @return
	 */
	@Nullable
    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje, Class<?> clase);
}
