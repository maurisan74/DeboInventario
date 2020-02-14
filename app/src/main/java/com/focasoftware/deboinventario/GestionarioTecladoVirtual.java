package com.focasoftware.deboinventario;

import android.widget.EditText;

/**
 * Interface que representa metodos para administrar (Mostrar u ocultar) el teclado
 * virtual
 * @author GuillermoR
 *
 */
public interface GestionarioTecladoVirtual {
	/**
	 * Funcion a implementar para mostrar el teclado virtual sobre el editText parametro
	 * @param edit_text
	 */
	public void showKeyboard(EditText edit_text);
	/**
	 * Funcion a implementar para ocultar el teclado virtual sobre el editText parametro
	 * @param edit_text
	 */
	public void hideKeyboard(EditText edit_text);
	
}
