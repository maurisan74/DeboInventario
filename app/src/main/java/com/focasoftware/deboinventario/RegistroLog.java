package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Clase accesoria para generar registros de logs de los sucesos en el sistema
 * @author GuillermoR
 *
 */
public class RegistroLog {

	
	
	
	/**
	 * Genera un archivo de log e inserta los datos pasados como parametros 
	 * como identificacion de la accion
	 * <p>1� Creamos el archivo
	 * <p>2� Calculamos el tama�o del archivo
	 * <p>3� Control de exceso de memoria
	 * <p>4� Creacion del Escritor de archivo
	 * <p>5� Escribimos la cadena
	 * <p>6� Cerramos el escritor de archivo
	 * 
	 * @param url_log
	 * @param fecha
	 * @param clase
	 * @param usuario
	 * @param accion
	 */
	public static void log(@NonNull String url_log, @NonNull Date fecha, String clase, String usuario,
                           String accion) {
		try {
			//1� Creamos el archivo
			File file = new File(url_log);
			File carpeta = new File(file.getParentFile().getPath());
			
			if (!carpeta.exists()) {
				carpeta.mkdir();
			}
			
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw;
		
			// Nos aseguramos que tenemos un archivo, no una carpeta:
				//if (file.isFile() == false) {
					//throw new Exception("Error - �Archivo de log inexistente o corrupto!");
				//}
			
			
			//2� Calculamos el tama�o del archivo:
				long i = file.length();
		
			//3� Control de exceso de memoria:
				if (i > 1000000) {
					file.delete();
					file = new File(url_log);
					file.createNewFile();
				}
				
				if (i < 1000000) { // Si archivo inferior a 1Mbytes
					
					//4� Creacion del Escritor de archivo
						fw = new FileWriter(file,true);
					
					//5� Escribimos la cadena
						fw.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(fecha) + ";" + usuario + ";" + clase + ";" + accion + "\r\n");
					
					//6� Cerramos el escritor de archivo
						fw.close();
				
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	/*
	 * Genera un log simple, guardando el reporte en la URI pasada como parametro
	 * <p>1� Creamos el archivo
	 * <p>2� Calculamos el tama�o del archivo
	 * <p>3� Control de exceso de memoria
	 * <p>4� Creacion del escritor de archivo
	 * <p>5� Escribimos el log al archivo
	 * <p>6� Cerramos el escritor
	 * @param uri
	 * @param reporte
	 */
	public static void log(@NonNull String uri, @NonNull Reporte reporte) {
		try {
			//1� Creamos el archivo
			File file = new File(uri);
			File carpeta = new File(file.getParentFile().getPath());
			
			if (carpeta.exists() == false) {
				carpeta.mkdir();
			}
			
			if (file.exists() == false){
				file.createNewFile();
			}
			FileWriter fw;
		
			// Nos aseguramos que tenemos un archivo, no una carpeta:
				//if (file.isFile() == false) {
					//throw new Exception("Error - �Archivo de log inexistente o corrupto!");
				//}
			
			//2� Calculamos el tama�o del archivo:
				long i = file.length();
		
			//3� Control de exceso de memoria:
				if (i < 1000000) { // Si archivo inferior a 1Mbytes
					
					//4� Creacion del escritor de archivo
						fw = new FileWriter(file,true);
					
					//5� Escribimos el log al archivo
						fw.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + ";" + 
								 reporte.getId() + ";" + 
								 reporte.getId_cliente() + ";" +
								 reporte.getFecha() + ";" +
								 reporte.getId_sancion() + ";" +
								 reporte.getId_operador() + ";" +
								 reporte.getObservacion() + ";" +
								 reporte.getUri_foto() + ";" +
								 "SANCION GENERADA CON EXITO" + ";" +
								 "\r\n");
					
					//6� Cerramos el escritor
						fw.close();
				
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Guarda un log especial en la URI, con los datos de la medici�n pasada como 
	 * parametro
	 * <p>1� Creamos el archivo
	 * <p>2� Calculamos el tama�o del archivo
	 * <p>3� Control de exceso de memoria
	 * <p>4� Creacion del escritor de archivo
	 * <p>5� Escribimos el log al archivo
	 * <p>6� Cerramos el escritor
	 * @param uri
	 * @param medicion
	 */
	public static void log(@NonNull String uri, @NonNull Medicion medicion) {
		try {
			//1� Creamos el archivo
			File file = new File(uri);
			File carpeta = new File(file.getParentFile().getPath());
			
			if (carpeta.exists() == false) {
				carpeta.mkdir();
			}
			
			if (file.exists() == false){
				file.createNewFile();
			}
			FileWriter fw;
		
			// Nos aseguramos que tenemos un archivo, no una carpeta:
				//if (file.isFile() == false) {
					//throw new Exception("Error - �Archivo de log inexistente o corrupto!");
				//}
			
			
			//2� Calculamos el tama�o del archivo:
				long i = file.length();
		
			//3� Control de exceso de memoria:
				if (i < 1000000) { // Si archivo inferior a 1Mbytes
					
					//4� Creacion del escritor de archivo
						fw = new FileWriter(file,true);
					
					//5� Escribimos el log al archivo
						fw.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + ";" + 
								 medicion.getId_medidor() + ";" + 
								 medicion.getPeriodo() + ";" +
								 medicion.getFecha_toma() + ";" +
								 medicion.getLectura_anterior() + ";" +
								 medicion.getLectura_actual() + ";" +
								 medicion.getValor() + ";" +
								 medicion.getObservacion() + ";" +
								 medicion.getId_operador() + ";" +
								 medicion.getId_error() + ";" +
								 "MEDICION GENERADA CON EXITO" + ";" +
								 "\r\n");
					
					//6� Cerramos el escritor
						fw.close();
				
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Genera un log con las cadenas pasadas en tab en la URI proveida
	 * <p>1� Creacion del archivo
	 * <p>2� Calculamos el tama�o del archivo
	 * <p>3� Control de exceso de memoria
	 * <p>4� Creacion del escritor de archivo
	 * <p>5� Escribimos el log al archivo
	 * <p>6� Cerramos el escritor
	 *  
	 * @param uri
	 * @param tab
	 */
	public static void log(@NonNull String uri, @NonNull String tab[]) {
		try {
			//1� Creacion del archivo
			File file = new File(uri);
			File carpeta = new File(file.getParentFile().getPath());
			
			if (carpeta.exists() == false) {
				carpeta.mkdir();
			}
			
			if (file.exists() == false){
				file.createNewFile();
			}
			FileWriter fw;
		
			// Nos aseguramos que tenemos un archivo, no una carpeta:
				//if (file.isFile() == false) {
					//throw new Exception("Error - �Archivo de log inexistente o corrupto!");
				//}
			
			
			//2� Calculamos el tama�o del archivo:
				long i = file.length();
		
			//3� Control de exceso de memoria:
				if (i < 1000000) { // Si archivo inferior a 1Mbytes
					
					//4� Creacion del escritor de archivo
						fw = new FileWriter(file,true);
					
					//5� Escribimos el log al archivo
						String porEscribir = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + ";";
						
						for (String s : tab){
							porEscribir += s + ";";
						}
						porEscribir += "\r\n";
						
						fw.write(porEscribir);
					
					//6� Cerramos el escritor
						fw.close();
				
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}

