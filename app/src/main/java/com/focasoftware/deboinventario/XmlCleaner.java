package com.focasoftware.deboinventario;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Clase para manejar y administrar algunos temas de archivos XML
 * @author GuillermoR
 *
 */
public class XmlCleaner {

	
	
	/**
	 * Funcion que lee un archivos y lo copia a un temporal reemplazando los & por Y
	 * <p>1� Busca un un temporal 
	 * <p>2� Lo crea de nuevo
	 * <p>3� Lee el archivos origen y lo copia al temporal reemplazando los & por Y
	 * <p>4� Reemplaza el archivo viejo por el temporal
	 * @param file
	 * @throws IOException
	 */
	public static void xml_cleaning_spechar(@NonNull File file) throws IOException {
		//1� Busca un un temporal 
		File fileTemp = new File(file.getParent() + "//" + "temp.xml");
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		//2� Lo crea de nuevo
		fileTemp.createNewFile();
		
		FileReader fileReader = new FileReader(file);
		FileWriter fileWriter = new FileWriter(fileTemp);
		BufferedReader input =  new BufferedReader(fileReader);
		BufferedWriter output = new BufferedWriter(fileWriter);
		
	    String line = null;
	    
	    //3� Lee el archivos origen y lo copia al temporal reemplazando los & por Y
        while (( line = input.readLine()) != null){
        	String s = line.replace("&", " Y ");
        	output.write(s);
        	//output.write(System.getProperty("line.separator"));
        }
        output.close();
        
        //fileTemp.delete();
        //4� Reemplaza el archivo viejo por el temporal
        file.delete();
        file.createNewFile();
        copyFile(fileTemp, file);
        fileTemp.delete();
        //return fileResponse;
	}
	
	
	
	/**
	 * Copia un archivo origen en uno de destino
	 * <p>1� Verifica la existnecia de los archivos de origen y destino
	 * <p>2� Se crean los input y output stream y los canales
	 * <p>3� Se procede a la transferencia
	 * <p>4� Se cierran los canales
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	private static void copyFile(@NonNull File sourceFile, @NonNull File destFile) throws IOException {
		System.out.println("::: XmlCleaner 77");
		//1� Verifica la existnecia de los archivos de origen y destino
		if (!sourceFile.exists()) {
		    return;
		}
		if (!destFile.exists()) {
		    destFile.createNewFile();
		}
		//2� Se crean los input y output stream y los canales
		FileChannel source = null;
		FileChannel destination = null;
		
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		//3� Se procede a la transferencia
		if (destination != null && source != null) {
		    destination.transferFrom(source, 0, source.size());
		}
		//4� Se cierran los canales
		if (source != null) {
		    source.close();
		}
		if (destination != null) {
		    destination.close();
		}
	}
	
	
}
