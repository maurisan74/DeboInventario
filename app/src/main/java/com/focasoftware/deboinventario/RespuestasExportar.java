package com.focasoftware.deboinventario;

/**
 * Clase accesoria para reconocer las respuestas al exportar. 
 * Creada en especial para manejar de forma refinada las excepciones y errores al 
 * exportar en el manejo de Bases de datos y archivos.
 * @author GuillermoR
 *
 */
public class RespuestasExportar {
	
	public static final int CODIGO_OK=0;
	public static final int CODIGO_WARNING=1;
	public static final int CODIGO_ERROR=2;
	
	
	private int codigoError;
	
	private String mensaje;
	
	public RespuestasExportar(int codigo,String msj) {
		this.codigoError=codigo;
		this.mensaje=msj;
		
	}

	public int getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(int codigoError) {
		this.codigoError = codigoError;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	

}
