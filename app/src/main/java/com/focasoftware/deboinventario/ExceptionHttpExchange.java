package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

/**
 * Excepcion personalizada que se utiliza para dar informacion sobre el resultado
 * de consultas a los web services o cualquier tipo de conexiones http
 * @author GuillermoR
 *
 */
public class ExceptionHttpExchange extends Throwable {
	

	private static final long serialVersionUID = 1L;
	/**
	 * Mensajes predefinidos de error
	 */
	public static final String mensaje1 = "La consulta HTTP ha fracasado. " +
										   "Compruebe si la conexi�n con el servido est� establecida correctamente y " +
										   "si el servidor est� trabajando";
	public static final String mensaje2 = "La conexi�n al router WiFI ha fracasado. ";
	
	public static final String mensaje3 = "La red Wifi es existente pero la conexi�n es imposible";
	/**
	 * Variable para almacenar la fuente del error
	 */
	private String fuenteError;
	/**
	 * Variable para almacenar el mensaje
	 */
	private String mensaje;
	
	
	/**
	 * Constructor que provee la fuente del error y un mensaje de error
	 * @param fuente_error
	 * @param un_mensaje
	 */
	public ExceptionHttpExchange(String fuente_error, String un_mensaje) {
		fuenteError = fuente_error;
		mensaje = un_mensaje;
	}
	
	/**
	 * Funcion para deolver en forma de cadena la fuente del error y el mensaje 
	 * concatenados
	 * @return
	 */
	@NonNull
    public String print() {
		return "Error en " + fuenteError + "\n" +
				mensaje;
	}


	/**
	 * Idem al anterior
	 */
	@NonNull
    @Override
	public String toString() {
		return "ERROR en " + fuenteError + ":\n" + mensaje;
	}
	
	
	
}