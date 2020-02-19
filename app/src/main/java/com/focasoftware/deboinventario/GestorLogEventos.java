package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * 
 * 
 * @author Renzo Staiti <renzostaiti99@gmail.com>
 *
 *En esta clase esta preparada para gestionar cualquier tipo de log en un archivo txt
 *Se puede configurar su ubicaci�n desde que se instancia el objeto la primera vez, y los tipos de log que se pueden mostrar
 *
 *tipo_0 ----------> Eventos, onclick, Onlongclick,etc.
 *
 *tipo_2 ----------> Inicio de Procesos
 *tipo_3 ----------> Mensajes del desarrollador de la aplicaci�n
 *tipo_4 ----------> Excepciones
 * 
 */

/**
 * 1- Clase de Gestora de Eventos
 */

public class GestorLogEventos {

	public String log; // tipo de log
	public String Ubicacion;// ubicacion del log
	public boolean tipo_0;// log de eventos
	public boolean tipo_1;// para algo
	public boolean tipo_2;// inicio de procesos
	public boolean tipo_3;// mensajes de la aplicaci�n
	public boolean tipo_4;// excepciones

	// getter and setter y constructor

	public GestorLogEventos() {
		this.log = "";
		this.Ubicacion = "";
		this.tipo_0 = true;
		this.tipo_1 = false;
		this.tipo_2 = true;
		this.tipo_3 = true;
		this.tipo_4 = true;

	}

	public String getUbicacion() {
		return this.Ubicacion;
	}

	public void setUbicacion(String pUbicacion) {
		this.Ubicacion = pUbicacion;
	}

	public void logInicial(String aplicacion) {
		try {

			/**
			 * Esta funci�n es para iniciar el registro de eventos cuando se
			 * quiera hacerlo Se crea un archivo en la ubicaci�n dada, m�s un
			 * archivo log.txt
			 */
			// File archivo = new File
			// (Environment.getExternalStorageDirectory()+"/deboInventario/desdeTablet/log.txt"
			// );
			File archivo = new File(this.Ubicacion + "log.txt");
			boolean sobreescribir = false;
			BufferedWriter writer = new BufferedWriter(new FileWriter(archivo,
					!sobreescribir));

			writer.newLine();
			writer.newLine();
			writer.append("LOG DE EVENTOS Y ERRORES DE " + aplicacion);
			writer.newLine();
			writer.append("fecha de inicio de actividades: " + ObtenerFecha());
			writer.newLine();
			writer.append("----------Fecha-------------Tipo----[line code]Evento-----------");
			writer.newLine();
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void log(String evento, int tipo) {

		/**
		 * @author Renzo Staiti <renzostaiti99@gmail.com> Esta es la funci�n de
		 *         log com�n que registra el log y el tipo que se le pasa por
		 *         parametros, como se puede decidir que tipo de log registrar
		 *         por medio del seteo de los tipo en true o false Seg�n el
		 *         resultado se si se llama a log_original o no
		 */

		if (this.tipo_0 == true) {

			log_original(evento, tipo);

		} else if (this.tipo_2 == true) {

			log_original(evento, tipo);

		} else if (this.tipo_3 == true) {

			log_original(evento, tipo);

		} else if (this.tipo_4 == true) {

			log_original(evento, tipo);

		} else if (this.tipo_1 == true) {

		}

	}

	private void log_original(String evento, int tipo) {

		/**
		 * 
		 * Esta clase recibe porparaetro el String del log y el tipo,
		 * simplemente los registra con la fecha y hora en el momento que se los
		 * inserta
		 * 
		 */
		try {
			// File archivo = new File
			// (Environment.getExternalStorageDirectory()+"/deboInventario/desdeTablet/log.txt"
			// );
			File archivo = new File(this.Ubicacion + "log.txt");
			boolean sobreescribir = false;
			BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, !sobreescribir));
			writer.newLine();
			writer.append("[" + ObtenerFecha() + "]---[" + tipo + "]-(" + this.Ubicacion + ")-->"
					+ evento);
			writer.newLine();
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@NonNull
    public static String ObtenerFecha() {

		/**
		 * Funci�n para obtener la fecha y personalizable sin el SimpleFormat()
		 */
		try {
			Date horaActual = new Date();

			String hora = "" + (horaActual.getYear() + 1900) + "-"
					+ (horaActual.getMonth() + 1) + "-" + horaActual.getDate()
					+ " " + horaActual.getHours() + ":"
					+ horaActual.getMinutes() + ":" + horaActual.getSeconds();
			return hora;
		} catch (Throwable error_1) {

			return "no se pudo obtener la fecha ";
		}

	}
	
	/**
	 * 
	 * @param dato dato	
	 * @param columna columna
	 * @param tipo tipo 	
	 * @throws IOException IOException
	 * 
	 * funci�n para debo Inventario
	 */
	

	public void Logdatos(String dato,String columna, int tipo) throws IOException {

		/**
		 * Esta funci�n es para iniciar el registro de eventos cuando se quiera
		 * hacerlo Se crea un archivo en la ubicaci�n dada, m�s un archivo
		 * log.txt
		 */
		
		
		File archivo = new File(this.Ubicacion + "log.txt");
		boolean sobreescribir = false;
		BufferedWriter writer = new BufferedWriter(new FileWriter(archivo,
				!sobreescribir));

		if (tipo == 1) {
			try {

				writer.append("<TABLA nombre=\"" + dato + "\">");
				writer.newLine();
				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (tipo == 2) {
			try {

				writer.append("<FILA numero=\"" + dato + "\">");
				writer.newLine();
				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (tipo == 3){
			try {

				writer.append( "<" + columna + ">" + dato + "</" + columna + ">");
				writer.newLine();
				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (tipo == 21){
			try {

				writer.append( "</FILA>");
				writer.newLine();
				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(tipo == 11){
			try {

				writer.append( "</TABLA>");
				writer.newLine();
				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}

}
