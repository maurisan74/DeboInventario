package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

/**
 * Excepcion personalizada para manejar resultados de las operaciones
 * con la BDD.
 * Contiene codigos parametrizados de respustas y datos extras de las excepciones
 * @author GuillermoR
 *
 */
public class ExceptionBDD extends Throwable {

	/**
	 * Codigos de errores
	 */
	public final static int EXPORT_FRACASADO = 901;
	public final static int ERROR_TIPO_SELECT = 902;
	public final static int ERROR_TIPO_INSERT = 903;
	public final static int ERROR_TIPO_UPDATE = 904;
	public final static int ERROR_TIPO_DELETE = 905;
	
	public final static int ERROR_NO_RESULT_UNEXPECTED = 900;
	public final static int ERROR_TOO_FEW_RESULTS = 981;
	public final static int ERROR_TOO_MANY_RESULTS = 982;
	public final static int ERROR_RESULT_UNEXPECTED = 998;
	public final static int ERROR_MULTIPLE_RESULTS_UNEXPECTED = 999;
	
	private static final long serialVersionUID = 1L;
	/**
	 * Variable que dice en que tabla se origino el error
	 */
	private String tablaFuenteError = "";
	/**
	 * Variable que indica el codigo del error
	 */
	private int codigo;
	/**
	 * Variable para dar informacion extra del error en forma de comentario
	 */
	private String comentario = "";
	
	
	// El codigo da:
	// 0 <=> no existe
	// 1 <=> ya existe
	/**
	 * Constructor que solo provee codigo de error
	 */
	public ExceptionBDD(int unCodigoError) {
		codigo = unCodigoError;
	}
	
	/**
	 * Constructor que provee codigo de error y un comentario
	 * @param unCodigoError
	 * @param comm
	 */
	public ExceptionBDD(int unCodigoError, String comm) {
		codigo = unCodigoError;
		comentario = comm;
	}
	
	/**
	 * Constructor que provee la tabla fuente del error y el codigo del mismo
	 * @param unaTabla
	 * @param unCodigoError
	 */
	public ExceptionBDD(String unaTabla, int unCodigoError) {
		tablaFuenteError = unaTabla;
		codigo = unCodigoError;
	}
	
	/**
	 * Constructor que provee la tabla fuente del error, el codigo y un comentario
	 * @param unaTabla
	 * @param unCodigoError
	 * @param mensaje
	 */
	public ExceptionBDD(String unaTabla, int unCodigoError, String mensaje) {
		tablaFuenteError = unaTabla;
		codigo = unCodigoError;
		comentario = mensaje;
	}
	
	/**
	 * Devuelve la informacion del error, tabla fuente y comentario si tiene
	 */
	@NonNull
    @Override
	public String toString() {
		String retorno = "Error en la Base de Datos (BDD) [codigo=" + codigo + "]\n";
		
		if (tablaFuenteError.length() > 0) {
			retorno += "Tabla: " + tablaFuenteError + "\n";
		} 
		
		if (comentario.length() > 0) {
			retorno += "Comentario: " + comentario + "\n";
		}
		
		return retorno;
	}

	
	
	public String getTablaFuenteError() {
		return tablaFuenteError;
	}

	
	public int getCodigo() {
		return codigo;
	}

	
	public String getComentario() {
		return comentario;
	}
	

	
	
	
}
