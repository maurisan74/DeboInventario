package com.focasoftware.deboinventario;

/**
 * Entidad utilizada en otras aplicaciones para representar informacion de Mediciones 
 * @author GuillermoR
 *
 */
public class Medicion {
	//
	//
	//
	//*********************************************************************************************
	//
	//    				**************************
	//  				**************************
	//   				****    CONSTANTES    ****
	//    				**************************
	//    				**************************
	
	public final static Medicion medicion1 = new Medicion(1, "2010/12", 548.21, 645.87, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	public final static Medicion medicion2 = new Medicion(1, "2011/01", 645.87, 723.11, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	public final static Medicion medicion3 = new Medicion(1, "2011/02", 723.11, 847.73, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	public final static Medicion medicion4 = new Medicion(1, "2011/03", 847.73, 955.09, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	public final static Medicion medicion5 = new Medicion(1, "2011/04", 955.09, 1066.66, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	public final static Medicion medicion6 = new Medicion(1, "2011/05", 1066.66, 1148.45, 0.88, "2011/01/05 15:34:08", 0, "", 1);
	
	public final static Medicion[] listaMediciones = {medicion1, medicion2, medicion3, medicion4, medicion5, medicion6};
	//
	//
	//
	//*********************************************************************************************
	//
	//    				*************************
	//  				*************************
	//   				****    ATRIBUTOS    ****
	//    				*************************
	//    				*************************
	//
	/**
	 * Variable de id del medidor
	 */
	private int id_medidor;
	/**
	 * Describe el periodo de la medicion
	 */
	private String periodo;
	/**
	 * Informacion de la lectura anterior realizada
	 */
	private float lectura_anterior;
	/**
	 * Informacion de la lectura actual
	 */
	private float lectura_actual;
	/**
	 * Valor medido ?
	 */
	private float valor;
	/**
	 * Fecha de realizacion de la toma de la medicion
	 */
	private String fecha_toma;
	/**
	 * Id del error encontrado
	 */
	private int id_error;
	/**
	 * Variable para almacenar una observacion a realizarse
	 */
	private String observacion;
	/**
	 * id del operador que realiz� la medici�n
	 */
	private int id_operador;
	
	//
	//
	//
	//*********************************************************************************************
	//
	//    				*****************************
	//  				*****************************
	//   				****    CONSTRUCTORES    ****
	//    				*****************************
	//    				*****************************
	

	/**Constructor completo de la clase MEDICION
	 * 
	 * @param unMedidor
	 * @param unPeriodo
	 * @param lecturaAnterior
	 * @param lecturaActual
	 * @param unValor
	 * @param unaFecha
	 * @param idError
	 * @param unaObservacion
	 * @param idOperador
	 */
	public Medicion(int unMedidor, String unPeriodo, double lecturaAnterior, double lecturaActual, double unValor, 
										String unaFecha, int idError, String unaObservacion ,int idOperador) {
		id_medidor = unMedidor;
		periodo = unPeriodo;
		lectura_anterior = (float) lecturaAnterior;
		lectura_actual = (float) lecturaActual;
		valor = (float) unValor;
		fecha_toma = unaFecha;
		id_error = idError;
		observacion = unaObservacion;
		id_operador = idOperador;
	}
	
	/**
	 * Constructor parcial de la entidad medicion
	 * @param unMedidor
	 * @param unPeriodo
	 * @param lecturaAnterior
	 * @param lecturaActual
	 */
	public Medicion(int unMedidor, String unPeriodo, double lecturaAnterior, double lecturaActual) {
		this(unMedidor, unPeriodo, lecturaAnterior, lecturaActual, 0.0, "", 0, "", 0);
	}
	
	//
	//
	//
	//*********************************************************************************************
	//
	//    				***********************
	//  				***********************
	//   				****    METODOS    ****
	//    				***********************
	//    				***********************
	
	//** GETTERS: **//
	
	public String getPeriodo() {
		return periodo;
	}

	public float getLectura_anterior() {
		return lectura_anterior;
	}

	public float getLectura_actual() {
		return lectura_actual;
	}

	public float getValor() {
		return valor;
	}

	public String getFecha_toma() {
		return fecha_toma;
	}

	public int getId_error() {
		return id_error;
	}

	public void setID_error(int idError) {
		id_error = idError;
	}
	
	public String getObservacion() {
		return observacion;
	}
	
	public void setObservacion(String texto) {
		observacion = texto;
	}

	public int getId_operador() {
		return id_operador;
	}

	public int getId_medidor() {
		return id_medidor;
	}
	
	/**
	 * Actualiza informacion de la medici�n con los datos de los parametros
	 * @param lectura
	 * @param fecha
	 * @param codigoDeError
	 * @param observacion
	 * @param operadorId
	 */
	public void actualizarMedicionNueva(double lectura, String fecha, int codigoDeError, String observacion, int operadorId) {
		this.lectura_actual = (float) lectura;
		this.fecha_toma = fecha;
		this.id_error = codigoDeError;
		this.observacion = observacion;
		this.id_operador = operadorId;
	}

}
