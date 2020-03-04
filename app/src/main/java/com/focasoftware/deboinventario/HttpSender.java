package com.focasoftware.deboinventario;


import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/*
 * Clase para manejar conexiones Http con un webservice o servidor web,
 * utilizada para enviar informaci�n a los webServices
 *
 * @author GuillermoR
 *
 */
public class HttpSender {

	/*
	 * Variable de codigo de soft o funcion del webservice
	 */
	private String cod_soft;
	/*
	 * URL de destino desde donde consumir el webservice
	 */
	private String url_destinacion;

	String sdcard = Environment.getExternalStorageDirectory().toString();

	/*
	 * Constructr que provee el codigo de software a ejeuctar en el URL destino
	 * <p>
	 * 1� Setea el codigo de software
	 * <p>
	 * 2� Setea la URL de destino
	 * <p>
	 * 3� Se valida la url
	 *
	 * @param codigo_software
	 * @throws ExceptionHttpExchange
	 */
	public HttpSender(String codigo_software) throws ExceptionHttpExchange {

		// 1� Setea el codigo de software
		cod_soft = codigo_software;
		// 2� Setea la URL de destino
		url_destinacion = Parametros.PREF_URL_CONEXION_SERVIDOR;
		// 3� Se valida la url
		URLValidator.esValidaEstaURL(url_destinacion);

	}

	/*
	 * Funci�n para enviar un archivo a procesarse con el numeroo de funcion correspondiente
	 * <p>
	 * 1� Creamos el archivo
	 * <p>
	 * 2� Creamos un cliente Http
	 * <p>
	 * 3� Configuramos el URL de destino para usar el metodo post
	 * <p>
	 * 4� Generamos el FileBody y la entidad multiparte de request
	 * <p>
	 * 5� Pasamos los parametros al request
	 * <p>
	 * 6� Ejecutamos la llamada a ese request
	 * <p>
	 * 7� Obtenemos el XML en formato String
	 * <p>
	 * 8� Verificamos la correcta ejecuci�n
	 *
	 * @param url_archivo_que_mandar
	 * @param numero_funcion
	 * @return
	 */
	public boolean send_xml(String url_archivo_que_mandar, String numero_funcion) {
		System.out.println("::: HTTPSEnder sector 1");
		try {
			// 1� Creamos el archivo
			File file = new File(url_archivo_que_mandar);

			// 2� Creamos un cliente Http
			HttpClient client = new DefaultHttpClient();

			// 3� Configuramos el URL de destino para usar el metodo post
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);

			// 4� Generamos el FileBody y la entidad multiparte de request
			FileBody bin = new FileBody(file, "text/xml");
			System.out.println("::: HTTPSEnder sector :"+bin);
			MultipartEntity reqEntity = new MultipartEntity();

			// 5� Pasamos los parametros al request
			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(numero_funcion));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_post, bin);
			post.setEntity(reqEntity);


//			  NB: List<NameValuePair> nameValuePairs = new
//			  ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			  BasicNameValuePair("b", "4")); try { post.setEntity(new
//			  UrlEncodedFormEntity(nameValuePairs)); } catch
//			  (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			  }


			// 6� Ejecutamos la llamada a ese request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			// 7� Obtenemos el XML en formato String
			String xmlString = EntityUtils.toString(resEntity);

			Log.v("yo", "El resultado del http sender es :" + xmlString);

			// 8� Verificamos la correcta ejecuci�n
			return xmlString.contains("EXITO");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Funcion que manda un archivo xml pasado por parametro a una url
	 * almacenada en la variable de clase
	 *
	 * @param url_archivo_que_mandar
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public boolean send_xml(String url_archivo_que_mandar) {

		try {

			File file = new File(url_archivo_que_mandar);

			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;

			HttpPost post = new HttpPost(postURL);
			System.out.println("::: HTTPSEnder sector 2");
			FileBody bin = new FileBody(file);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_DATOS));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_post, bin);
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			String xmlString = EntityUtils.toString(resEntity);

			System.out.println("::: HttpSender 184 xmlstring " + xmlString);

			return xmlString.contains("EXITO");

		} catch (Exception e) {
			System.out.println("::: HTTPSEnder catch");
			e.printStackTrace();

			return false;
		}

	}

	/*
	 * Funcion que manda un archivo xml pasado por parametro a una url
	 * almacenada en la variable de clase
	 *
	 * @param url_archivo_que_mandar
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public boolean send_compra_xml(String url_archivo_que_mandar) {

		try {

			File file = new File(url_archivo_que_mandar);

			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;

			HttpPost post = new HttpPost(postURL);
			System.out.println("::: HTTPSEnder Compras export 217");
			FileBody bin = new FileBody(file);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_COMPRAS));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_post, bin);
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			String xmlString = EntityUtils.toString(resEntity);

			System.out.println("::: HttpSender 232 Compras xmlstring " + xmlString);

			if (xmlString.contains("EXITO") == false) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			System.out.println("::: HTTPSEnder catch");
			e.printStackTrace();

			return false;
		}

	}

	/*
	 * Funcion que envia un archivo txt pasado como parametro a una url
	 * almacenada
	 *
	 * @param url_archivo_que_mandar
	 * @return
	 */
	public boolean send_txt(String url_archivo_que_mandar) {
		System.out.println("::: HTTPSEnder sector 3");
		try {

			File file = new File(url_archivo_que_mandar);

			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);
			FileBody bin = new FileBody(file, "text/plain");
			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_LOGS));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_text, bin);
			post.setEntity(reqEntity);

			/*
			 * NB: List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
			 * UrlEncodedFormEntity(nameValuePairs)); } catch
			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
			 * }
			 */

			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			String xmlString = EntityUtils.toString(resEntity);

			if (xmlString.contains("EXITO") == false) {
				return false;

			}
			return true;

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Funcion para enviar una foto a la URL almacenada en la variable de clase
	 * correspondiente
	 *
	 * @param url_foto_que_mandar
	 * @return
	 */
	public boolean send_foto(String url_foto_que_mandar) {

		try {
			File file = new File(url_foto_que_mandar);
			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);
			FileBody bin = new FileBody(file, "image/jpeg");
			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_FOTOS));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_foto, bin);

			post.setEntity(reqEntity);

			/*
			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
			 * UrlEncodedFormEntity(nameValuePairs)); } catch
			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
			 * }
			 */

			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String xmlString = EntityUtils.toString(resEntity);

				if (xmlString.contains("EXITO") == false) {
					return false;
				}
			}

			SystemClock.sleep(500);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	  /*
	 * Envia informaci�n del estado de liberacion de cierto invenatrio pasado
	 * como parametro
	 *
	 * @param numero_inventario
	 * @param estado_liberatorio
	 * @return
	 */
	public boolean send_liberacion(int numero_inventario, int estado_liberatorio) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);
			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_LIBERACION));

			String strFinal = String.valueOf(numero_inventario) + "," + String.valueOf(estado_liberatorio);
			reqEntity.addPart(Parametros.codigo_opc, new StringBody(strFinal));

			post.setEntity(reqEntity);

			/*
			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
			 * UrlEncodedFormEntity(nameValuePairs)); } catch
			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
			 * }
			 */

			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			String xmlString = "";

			if (resEntity != null) {
				xmlString = EntityUtils.toString(resEntity);
			}

			if (xmlString.contains("EXITO") == false) {
				return false;
			}

			SystemClock.sleep(500);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Envia informacion de estado de liberacion de varios inventario pasados en
	 * forma de lista de enteros
	 *
	 * @param listaIds
	 * @return
	 */
	public boolean send_liberacion(ArrayList<Integer> listaIds) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);
			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_LIBERACION));

			String strFinal = "";
			for (int param : listaIds) {
				strFinal = strFinal + String.valueOf(param) + ",";
			}
			// Sacamos el �ltimo "," de la cadena de caracteres:
			strFinal = strFinal.substring(0, strFinal.length() - 1);
			reqEntity.addPart(Parametros.codigo_opc, new StringBody(strFinal));

			post.setEntity(reqEntity);

			/*
			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
			 * UrlEncodedFormEntity(nameValuePairs)); } catch
			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
			 * }
			 */

			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			String xmlString = "";

			if (resEntity != null) {
				xmlString = EntityUtils.toString(resEntity);
			}

			if (!xmlString.contains("EXITO")) {
				return false;
			}

			SystemClock.sleep(500);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Funcion generica de envio de archivos? o manda fotos tambien?
	 *
	 * @param url_file
	 * @return
	 */
	public boolean send_file(String url_file) {

		try {
			File file = new File(url_file);
			HttpClient client = new DefaultHttpClient();
			String postURL = url_destinacion;
			HttpPost post = new HttpPost(postURL);
			FileBody bin = new FileBody(file, "text/xml");
			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_FOTOS));
			reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
			reqEntity.addPart(Parametros.codigo_xfile, bin);

			post.setEntity(reqEntity);

			/*
			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
			 * UrlEncodedFormEntity(nameValuePairs)); } catch
			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
			 * }
			 */

			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String xmlString = EntityUtils.toString(resEntity);

				if (xmlString.contains("EXITO") == false) {
					return false;
				}
			}

			SystemClock.sleep(500);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void copyFile(File sourceFile, File destFile) throws IOException {
		try {
			if (!sourceFile.exists()) {
				return;
			}
			if (!destFile.exists()) {
				destFile.createNewFile();
			}
			FileChannel source = null;
			FileChannel destination = null;
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			if (destination != null && source != null) {
				destination.transferFrom(source, 0, source.size());
			}
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		} catch (Exception e) {
		}
	}

}












//
//import android.os.Environment;
//import android.os.SystemClock;
//import android.util.Log;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.channels.FileChannel;
//import java.util.ArrayList;
//
///**
// * Clase para manejar conexiones Http con un webservice o servidor web,
// * utilizada para enviar informaci�n a los webServices
// *
// * @author GuillermoR
// *
// */
//public class HttpSender {
//
//	/**
//	 * Variable de codigo de soft o funcion del webservice
//	 */
//	private String cod_soft;
//	/**
//	 * URL de destino desde donde consumir el webservice
//	 */
//	private String url_destinacion;
//
//	String sdcard = Environment.getExternalStorageDirectory().toString();
//
//	/**
//	 * Constructr que provee el codigo de software a ejeuctar en el URL destino
//	 * <p>
//	 * 1� Setea el codigo de software
//	 * <p>
//	 * 2� Setea la URL de destino
//	 * <p>
//	 * 3� Se valida la url
//	 *
//	 * @param codigo_software
//	 * @throws ExceptionHttpExchange
//	 */
//	public HttpSender(String codigo_software) throws ExceptionHttpExchange {
//
//		// 1� Setea el codigo de software
//		cod_soft = codigo_software;
//		// 2� Setea la URL de destino
//		url_destinacion = Parametros.PREF_URL_CONEXION_SERVIDOR;
//		// 3� Se valida la url
//		URLValidator.esValidaEstaURL(url_destinacion);
//
//	}
//
//	/**
//	 * Funci�n para enviar un archivo a procesarse con el numeroo de funcion
//	 * correspondiente
//	 * <p>
//	 * 1� Creamos el archivo
//	 * <p>
//	 * 2� Creamos un cliente Http
//	 * <p>
//	 * 3� Configuramos el URL de destino para usar el metodo post
//	 * <p>
//	 * 4� Generamos el FileBody y la entidad multiparte de request
//	 * <p>
//	 * 5� Pasamos los parametros al request
//	 * <p>
//	 * 6� Ejecutamos la llamada a ese request
//	 * <p>
//	 * 7� Obtenemos el XML en formato String
//	 * <p>
//	 * 8� Verificamos la correcta ejecuci�n
//	 *
//	 * @param url_archivo_que_mandar
//	 * @param numero_funcion
//	 * @return
//	 */
//	public boolean send_xml(String url_archivo_que_mandar, String numero_funcion) {
//		System.out.println("::: HTTPSEnder sector 1");
//		try {
//			// 1� Creamos el archivo
//			File file = new File(url_archivo_que_mandar);
//			// 2� Creamos un cliente Http
//			HttpClient client = new DefaultHttpClient();
//			// 3� Configuramos el URL de destino para usar el metodo post
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			// 4� Generamos el FileBody y la entidad multiparte de request
//			FileBody bin = new FileBody(file, "text/xml");
//			System.out.println("::: HTTPSEnder sector :"+bin);
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			// 5� Pasamos los parametros al request
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(
//					numero_funcion));
//			reqEntity.addPart(Parametros.codigo_tab, new StringBody(
//					Parametros.PREF_NUMERO_DE_TERMINAL));
//			reqEntity.addPart(Parametros.codigo_post, bin);
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			// 6� Ejecutamos la llamada a ese request
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			// 7� Obtenemos el XML en formato String
//			String xmlString = EntityUtils.toString(resEntity);
//
//			Log.v("yo", "El resultado del http sender es :" + xmlString);
//
//			// 8� Verificamos la correcta ejecuci�n
//			if (xmlString.contains("EXITO") == false) {
//				return false;
//			}
//
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/*
//	 * Funcion que manda un archivo xml pasado por parametro a una url
//	 * almacenada en la variable de clase
//	 *
//	 * @param url_archivo_que_mandar
//	 * @return
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	public boolean send_xml(String url_archivo_que_mandar) {
//
//		try {
//			File file = new File(url_archivo_que_mandar);
//
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//
//			HttpPost post = new HttpPost(postURL);
//			System.out.println("::: HTTPSEnder sector 2");
//			FileBody bin = new FileBody(file);
//
//			MultipartEntity reqEntity = new MultipartEntity();
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_DATOS));
//			if (Parametros.PREF_NUMERO_DE_TERMINAL != null) {
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
//			}else
//			{
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody("1"));
//			}
//			reqEntity.addPart(Parametros.codigo_post, bin);
//			post.setEntity(reqEntity);
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String xmlString = EntityUtils.toString(resEntity);
//
//			System.out.println("::: HttpSender 184 xmlstring " + xmlString);
//
//			return xmlString.contains("EXITO");
//
//		} catch (Exception e) {
//			System.out.println("::: HTTPSEnder catch");
//			e.printStackTrace();
//
//			return false;
//		}
//
//	}
//
//	/**
//	 * Funcion que manda un archivo xml pasado por parametro a una url
//	 * almacenada en la variable de clase
//	 *
//	 * @param url_archivo_que_mandar
//	 * @return
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	public boolean send_compra_xml(String url_archivo_que_mandar) {
//
//		try {
//
//			File file = new File(url_archivo_que_mandar);
//
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//
//			HttpPost post = new HttpPost(postURL);
//			System.out.println("::: HTTPSEnder Compras export 217");
//			FileBody bin = new FileBody(file);
//
//			MultipartEntity reqEntity = new MultipartEntity();
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_COMPRAS));
//
//			if (Parametros.PREF_NUMERO_DE_TERMINAL != null) {
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
//			}else
//			{
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody("1"));
//			}
//			reqEntity.addPart(Parametros.codigo_post, bin);
//			post.setEntity(reqEntity);
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String xmlString = EntityUtils.toString(resEntity);
//
//			System.out.println("::: HttpSender 232 Compras xmlstring " + xmlString);
//
//			return xmlString.contains("EXITO");
//
//		} catch (Exception e) {
//			System.out.println("::: HTTPSEnder catch");
//			e.printStackTrace();
//
//			return false;
//		}
//
//	}
//
//	/**
//	 * Funcion que envia un archivo txt pasado como parametro a una url
//	 * almacenada
//	 *
//	 * @param url_archivo_que_mandar
//	 * @return
//	 */
//	public boolean send_txt(String url_archivo_que_mandar) {
//		System.out.println("::: HTTPSEnder sector 3");
//		try {
//
//			File file = new File(url_archivo_que_mandar);
//
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			FileBody bin = new FileBody(file, "text/plain");
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_LOGS));
//			if (Parametros.PREF_NUMERO_DE_TERMINAL != null) {
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
//			}else
//			{
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody("1"));
//			}
//			reqEntity.addPart(Parametros.codigo_text, bin);
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String xmlString = EntityUtils.toString(resEntity);
//
//			if (!xmlString.contains("EXITO")) {
//				return false;
//
//			}
//			return true;
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * Funcion para enviar una foto a la URL almacenada en la variable de clase
//	 * correspondiente
//	 *
//	 * @param url_foto_que_mandar
//	 * @return
//	 */
//	public boolean send_foto(String url_foto_que_mandar) {
//
//		try {
//			File file = new File(url_foto_que_mandar);
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			FileBody bin = new FileBody(file, "image/jpeg");
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_FOTOS));
//			if (Parametros.PREF_NUMERO_DE_TERMINAL != null) {
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
//			}else
//			{
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody("1"));
//			}
//			reqEntity.addPart(Parametros.codigo_foto, bin);
//
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
//			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//
//			if (resEntity != null) {
//				String xmlString = EntityUtils.toString(resEntity);
//
//				if (!xmlString.contains("EXITO")) {
//					return false;
//				}
//			}
//
//			SystemClock.sleep(500);
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * Envia informaci�n del estado de liberacion de cierto invenatrio pasado
//	 * como parametro
//	 *
//	 * @param numero_inventario
//	 * @param estado_liberatorio
//	 * @return
//	 */
//	public boolean send_liberacion(int numero_inventario, int estado_liberatorio) {
//		try {
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(
//					Parametros.CODIGO_FONC_EXPORT_LIBERACION));
//
//			String strFinal = String.valueOf(numero_inventario) + ","
//					+ String.valueOf(estado_liberatorio);
//			reqEntity.addPart(Parametros.codigo_opc, new StringBody(strFinal));
//
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
//			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String xmlString = "";
//
//			if (resEntity != null) {
//				xmlString = EntityUtils.toString(resEntity);
//			}
//
//			if (!xmlString.contains("EXITO")) {
//				return false;
//			}
//
//			SystemClock.sleep(500);
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * Envia informacion de estado de liberacion de varios inventario pasados en
//	 * forma de lista de enteros
//	 *
//	 * @param listaIds
//	 * @return
//	 */
//	public boolean send_liberacion(ArrayList<Integer> listaIds) {
//		try {
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(
//					Parametros.CODIGO_FONC_EXPORT_LIBERACION));
//
//			String strFinal = "";
//			for (int param : listaIds) {
//				strFinal = strFinal + String.valueOf(param) + ",";
//			}
//			// Sacamos el �ltimo "," de la cadena de caracteres:
//			strFinal = strFinal.substring(0, strFinal.length() - 1);
//			reqEntity.addPart(Parametros.codigo_opc, new StringBody(strFinal));
//
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
//			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String xmlString = "";
//
//			if (resEntity != null) {
//				xmlString = EntityUtils.toString(resEntity);
//			}
//
//			if (!xmlString.contains("EXITO")) {
//				return false;
//			}
//
//			SystemClock.sleep(500);
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * Funcion generica de envio de archivos? o manda fotos tambien?
//	 *
//	 * @param url_file
//	 * @return
//	 */
//	public boolean send_file(String url_file) {
//
//		try {
//			File file = new File(url_file);
//			HttpClient client = new DefaultHttpClient();
//			String postURL = url_destinacion;
//			HttpPost post = new HttpPost(postURL);
//			FileBody bin = new FileBody(file, "text/xml");
//			MultipartEntity reqEntity = new MultipartEntity();
//
//			reqEntity.addPart(Parametros.codigo_soft, new StringBody(cod_soft));
//			reqEntity.addPart(Parametros.codigo_fonc, new StringBody(Parametros.CODIGO_FONC_EXPORT_FOTOS));
//			if (Parametros.PREF_NUMERO_DE_TERMINAL != null) {
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody(Parametros.PREF_NUMERO_DE_TERMINAL));
//			}else
//			{
//				reqEntity.addPart(Parametros.codigo_tab, new StringBody("1"));
//			}
//			reqEntity.addPart(Parametros.codigo_xfile, bin);
//
//			post.setEntity(reqEntity);
//
//			/*
//			 * NB: NO ES POSIBLE AGREGAR EN POST DIFERENTES TIPOS, TIENEN QUE
//			 * SER DEL TIPO "ADDPART" List<NameValuePair> nameValuePairs = new
//			 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
//			 * BasicNameValuePair("a", "1")); nameValuePairs.add(new
//			 * BasicNameValuePair("b", "4")); try { post.setEntity(new
//			 * UrlEncodedFormEntity(nameValuePairs)); } catch
//			 * (UnsupportedEncodingException unEnEx) { unEnEx.printStackTrace();
//			 * }
//			 */
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//
//			if (resEntity != null) {
//				String xmlString = EntityUtils.toString(resEntity);
//
//				if (!xmlString.contains("EXITO")) {
//					return false;
//				}
//			}
//
//			SystemClock.sleep(500);
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public void copyFile(File sourceFile, File destFile) throws IOException {
//		try {
//			if (!sourceFile.exists()) {
//				return;
//			}
//			if (!destFile.exists()) {
//				destFile.createNewFile();
//			}
//			FileChannel source = null;
//			FileChannel destination = null;
//			source = new FileInputStream(sourceFile).getChannel();
//			destination = new FileOutputStream(destFile).getChannel();
//			if (destination != null && source != null) {
//				destination.transferFrom(source, 0, source.size());
//			}
//			if (source != null) {
//				source.close();
//			}
//			if (destination != null) {
//				destination.close();
//			}
//		} catch (Exception e) {
//		}
//	}
//
//}