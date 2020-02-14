package com.focasoftware.deboinventario;

/**
 * Entidad que representa un reporte, utilizable por otras aplicaciones
 * @author GuillermoR
 *
 */
public class Reporte {
	
	/**
	 * Identificador del reporte
	 */
	private int id;
	/**
	 * Id del cliente sobre el que se hace el reporte
	 */
	private int id_cliente;
	/**
	 * Fecha del reporte
	 */
	private String fecha;
	/**
	 * Id de la sancion que se realizo
	 */
	private int id_sancion;
	/**
	 * URI de la foto relacionada con el reporte
	 */
	private String uri_foto;
	/**
	 * Informacion extra del reporte guardada en forma de observacion
	 */
	private String observacion;
	/**
	 * ID del operador que releva el reporte
	 */
	private int id_operador;
	
	
	/**
	 * Constructor completo del reporte
	 * @param unId
	 * @param unCliente
	 * @param unaFecha
	 * @param idSancion
	 * @param uriFoto
	 * @param unaObservacion
	 * @param idOperador
	 */
	public Reporte(int unId, int unCliente, String unaFecha, int idSancion, 
			String uriFoto, String unaObservacion ,int idOperador) {
		id = unId;
		id_cliente = unCliente;
		fecha = unaFecha;
		id_sancion = idSancion;
		uri_foto = uriFoto;
		observacion = unaObservacion;
		id_operador = idOperador;
	}
	
	/**
	 * Constructor de reporte con id=0
	 * @param unCliente
	 * @param unaFecha
	 * @param idSancion
	 * @param uriFoto
	 * @param unaObservacion
	 * @param idOperador
	 */
	public Reporte(int unCliente, String unaFecha, int idSancion, String uriFoto, String unaObservacion ,int idOperador) {
		this(0, unCliente, unaFecha, idSancion, uriFoto, unaObservacion, idOperador);
	}



	public int getId_cliente() {
		return id_cliente;
	}



	public String getFecha() {
		return fecha;
	}



	public int getId_sancion() {
		return id_sancion;
	}



	public String getObservacion() {
		return observacion;
	}



	public int getId_operador() {
		return id_operador;
	}



	public String getUri_foto() {
		return uri_foto;
	}



	public int getId() {
		return id;
	}
	
	
	
}
