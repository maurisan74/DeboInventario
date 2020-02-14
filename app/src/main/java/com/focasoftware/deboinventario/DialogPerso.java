package com.focasoftware.deboinventario;

/**
 * Clase abstraca que contiene informaci�n para administraci�n de los 
 * dialogos personalizados
 * @author GuillermoR
 *
 */
public abstract class DialogPerso {
	
	/**
	 * Variables para ver que imagen mostrar en los dialogos personalizados
	 */
	public final static int DEFAULT = 0;
	public final static int VALIDAR = 1;
	public final static int ALERTAR = 2;
	public final static int PROHIBIR = 3;
	
	
	public final static int OPERACION_SUMAR = 11;
	public final static int OPERACION_RESTAR = 12;
	public final static int OPERACION_MODIFICAR = 13;
	
	
	public final static int IMAGEN_CODIGO_BARRAS = 201;
	public final static int IMAGEN_ARTICULO = 202;
	
	
	public final static int INPUT_NUMEROS = 401;
	public final static int INPUT_LETRAS = 402;
}
