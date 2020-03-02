package com.focasoftware.deboinventario;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 * Clase que maneja las conexiones con los Web Services. Tambin maneja lectura
 * y escritura de archivos de exportacin.
 *
 * @author GuillermoR
 *
 */
public class HttpReader {

	/**
	 * Variable de clase para el cliente http donde nos conectemos
	 */
	private HttpClient httpclient;
	/**
	 * Variable para almacenar la peticion post
	 */
	private HttpPost httppost;
	/**
	 * Variable para obtener la respuesta del request
	 */
	private HttpResponse response;
	/**
	 * Variable para obtener los datos de la respuesta
	 */
	private HttpEntity entity;
	/**
	 * Variable de clase donde se almacenara el xml recibido
	 */
	private String xmlString;

	/**
	 * Constructor usado para la creacin de un lector de flujo XML para cargar
	 * Inventarios y referencias desde el web service
	 * <p>
	 * 1 Objetos de conexin al web service
	 * <p>
	 * 2 Nuestra URL
	 * <p>
	 * 3 Instanciacin del objeto httppost
	 * <p>
	 * 4 Implementacin en el URL
	 *
	 * Crea la conexion del Cliente Http con el webService y setea los
	 * parametros
	 *
	 * @param url
	 *            URL del webService
	 * @param codigoFoncionLlamada
	 *            codigo de funcion del webService
	 * @throws ExceptionHttpExchange
	 */
	static GestorLogEventos log = new GestorLogEventos();

	public HttpReader(String url, int codigoFoncionLlamada)
			throws ExceptionHttpExchange {

		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
		log.log("[-- 100 --]" + "Inicia el HttpReader", 2);
		// 1 Objetos de conexin al web service:
		httpclient = new DefaultHttpClient();

		// 2 Nuestra URL:
		httppost = new HttpPost(url);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				2);

		// 3 Instanciacin del objeto httppost:
		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_INVENTARIOS) {
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_soft,
					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_fonc,
					ParametrosInventario.CODIGO_FONC_INVENTARIOS));
		} else if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_REFERENCIAS) {
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_soft,
					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_fonc,
					ParametrosInventario.CODIGO_FONC_REFERENCIAS));
		} else {
			throw new ExceptionHttpExchange("HTTP READER",
					"La funcion solicitada tiene que instanciarse con otro constructor");
		}

		// 4 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {

			log.log("[-- 128 --]" + unEnEx.toString() + unEnEx.getMessage(), 4);
			unEnEx.printStackTrace();
		}
	}

	/**
	 * Constructor usado para la creacon de un lector de flujo XML para la
	 * lectura del webservice de los articulos
	 * <p>
	 * 1 Objetos de conexin al web service
	 * <p>
	 * 2 Nuestra URL
	 * <p>
	 * 3 Instanciacin del objeto httppost
	 * <p>
	 * 4 Implementacin en el URL
	 *
	 * Crea la conexion del Cliente Http con el webService y setea los
	 * parametros
	 *
	 * @param url
	 * @param codigoFoncionLlamada
	 * @param parametros
	 * @throws ExceptionHttpExchange
	 */
	public HttpReader(String url, int codigoFoncionLlamada,
					  ArrayList<Integer> parametros) throws ExceptionHttpExchange {
		System.out.println("::: HTTPREADER conecta al webservice");
		// 1 Objetos de conexin al web service:
		httpclient = new DefaultHttpClient();

		// 2 Nuestra URL:
		URLValidator.esValidaEstaURL(url);

		httppost = new HttpPost(url);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				3);

		// 3 Instanciacin del objeto httppost:
		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_ARTICULOS) {
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_soft,
					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_fonc,
					ParametrosInventario.CODIGO_FONC_ARTICULOS));

			String strFinal = "";
			for (int num : parametros) {
				strFinal = strFinal + String.valueOf(num) + ",";
			}
			// Sacqmos el ltimo "," de la cadena de caracteres:
			strFinal = strFinal.substring(0, strFinal.length() - 1);
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_opc, strFinal));
		} else {
			throw new ExceptionHttpExchange("HTTP READER",
					"La foncion solicitada tiene que instanciarse con otro constructor");
		}

		// 4 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 182 --]" + unEnEx.toString() + unEnEx.getMessage(), 4);
			unEnEx.printStackTrace();
		}

	}

	/**
	 * Constructor del lector de webservice para la carga de la foto
	 * <p>
	 * 1 Objetos de conexin al web service
	 * <p>
	 * 2 Nuestra URL
	 * <p>
	 * 3 Instanciacin del objeto httppost
	 * <p>
	 * 4 Implementacin en el URL
	 *
	 * Crea la conexion y setea los parametros
	 *
	 * @param url
	 * @param codigoFoncionLlamada
	 * @param nombre_foto
	 * @throws ExceptionHttpExchange
	 */

	public HttpReader(String url, int codigoFoncionLlamada, String nombre_foto)
			throws ExceptionHttpExchange {
		// 1 Objetos de conexin al web service:
		httpclient = new DefaultHttpClient();

		// 2 Nuestra URL:
		URLValidator.esValidaEstaURL(url);

		httppost = new HttpPost(url);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				3);

		// 3 Instanciacin del objeto httppost:
		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_FOTO) {
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_soft,
					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_fonc,
					ParametrosInventario.CODIGO_FONC_FOTO));
			listaParametrosPost.add(new BasicNameValuePair(
					Parametros.codigo_opc, nombre_foto));
		} else {
			throw new ExceptionHttpExchange("HTTP READER",
					"La foncion solicitada tiene que instanciarse con otro constructor");
		}

		// 4 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 229 --]" + unEnEx.toString() + "__"
					+ unEnEx.getMessage(), 4);
			unEnEx.printStackTrace();
		}
	}

	/**
	 * Se ejecuta para leer una Foto desde el webservice, previamente se debe
	 * haber creado con el constructor corespondiente
	 * <p>
	 * 1 Ejecucion de la consulta del http post
	 * <p>
	 * 2 Obtenemos los bytes de la respuesta
	 * <p>
	 * 3 Devolvemos el arreglo de bytes de la foto
	 *
	 * @return un arreglo de bytes que contiene informacin de la foto
	 * @throws ExceptionHttpExchange
	 */
	public byte[] readFoto() throws ExceptionHttpExchange {
		byte[] ba = null;
		try {
			// 1 Ejecucion de la consulta del http post
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			// 2 Obtenemos los bytes de la respuesta
			ba = EntityUtils.toByteArray(entity);

		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 257 --]" + e.toString() + "__" + e.getMessage(), 4);

			e.printStackTrace();
			return null;
		}
		// 3 Devolvemos el arreglo de bytes de la foto
		return ba;

	}

	/**
	 * Funcion que lee desde el WebService los articulos
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Lectura web
	 * <p>
	 * 3 Obtensin de la respuesta
	 * <p>
	 * 4 Parseo de la respuesta XML en la tabla de retorno
	 * <p>
	 * 5 Liberacin de los recursos
	 *
	 * @return un HashMap con los articulos indexados
	 * @throws ExceptionHttpExchange
	 */
	public HashMap<String, HashMap<String, String>> readArticulos()
			throws ExceptionHttpExchange {
		System.out.println("::: Httpreader 344 Lectura webservice");
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta;
		tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2 Lectura web
		try {
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			// 3 Obtensin de la respuesta
			xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 358 xmlString "+ xmlString);
			if (entity == null) {
				throw new ExceptionHttpExchange(
						"Recuperacion de los ARTICULOS",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}

			// 4 Parseo de la respuesta XML en la tabla de retorno
			tablaRespuesta = parserArticulos(xmlString);

			// 5 Liberacin de los recursos:
			if (entity != null) {
				entity.consumeContent();
			} else {
				throw new ExceptionHttpExchange("Leer ARTICULOS",
						"Imposible recuperar operadores via HTTP (1)");
			}

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 308 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer ARTICULOS",
					"Imposible recuperar operadores via HTTP (2): "
							+ e.toString());
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 314 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer ARTICULOS",
					"Imposible recuperar operadores via HTTP (3): "
							+ e.toString());
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 320 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer ARTICULOS",
					"Imposible recuperar operadores via HTTP (4): "
							+ e.toString());
		}

		return tablaRespuesta;
	}

	/**
	 * Lee los INVENTARIOS disponibles en la base de datos desde el WebService y
	 * les muestra si existen.
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Lectura web
	 * <p>
	 * 3 Obtensin de la respuesta
	 * <p>
	 * 4 Parseo de la respuesta XML en la tabla de retorno
	 * <p>
	 * 5 Liberacion de los recursos
	 *
	 * @return un HashMap con los inventarios, Sino return null
	 * @throws ExceptionHttpExchange
	 */
	public LinkedHashMap<String, String> readInventarios(int condR)
			throws ExceptionHttpExchange {
		System.out.println("::: Httpreader 433 readInventarios");
		System.out.println("::: Httpreader 433 readInventarios condR " + condR);
		// 1 Variable de retorno:
//		LinkedHashMap<String, HashMap<String, String>> tablaRespuesta = new LinkedHashMap<String, HashMap<String, String>>();
		LinkedHashMap<String,String> tablaRespuesta = new LinkedHashMap<String, String>();
		// 2 Lectura web
		try {


//			System.out.println("::: Httpreader 439 readInventarios");
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			if (entity == null) {
				throw new ExceptionHttpExchange("Recuperacion de INVENTARIOS",
						"La consulta HTTP al servidor no ha devuelto resultados.");
			}
			// 3 Obtensin de la respuesta
			xmlString = EntityUtils.toString(entity);
			System.out.println( "::: HTTPREADER 448 XML " +xmlString);
			// 4 Parseo de la respuesta XML en la tabla de retorno
			tablaRespuesta = parserInventarios(xmlString);
			System.out.println( "::: HTTPREADER 451 tabla " +tablaRespuesta);
			// 5 Liberacin de los recursos:
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 368 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 374 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 381 --]" + e.toString() + "__" + e.getMessage(), 4);
			return null;
		}
		return tablaRespuesta;
	}

	/**
	 * Lee desde el webService los detalles de los inventarios con los
	 * inventarios?
	 * <p>
	 * 1 Variable de retorno:
	 * <p>
	 * 2 Lectura web
	 * <p>
	 * 3 Obtension de la respuesta en formato de cadena XML
	 * <p>
	 * 4 Parseo de la cadena a una tabla temporal
	 * <p>
	 * 5 Hay que fusionar estos articulos
	 * <p>
	 * &nbsp;&nbsp;5.1 Si el numeroInventario no existe, lo metemos en la tabla
	 * de respuestsa
	 * <p>
	 * nbsp;&nbsp;5.2 Para cada articulo de ese inventario
	 * <p>
	 * nbsp;&nbsp;nbsp;&nbsp;5.2.1 Si ya tenemos este articulo en memoria,
	 * concatenamos el codigo de barra nuevo con los existentes
	 * <p>
	 * nbsp;&nbsp;nbsp;&nbsp;5.2.2 Sino,lo metemos
	 * <p>
	 * 6 Liberacin de los recursos
	 *
	 * @return
	 */
	public LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> readDetallesInventarios() {
		// 1 Variable de retorno:
		System.out.println("::: HttpReader 524 readDetallesInventario");
		LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> tablaRespuesta, tablaTemp;
		tablaTemp = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();
		tablaRespuesta = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();

		// 2 Lectura web
		try {
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			// 3 Obtension de la respuesta en formato de cadena XML
			xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 534 xmlString " + xmlString);
			// 4 Parseo de la cadena a una tabla temporal
			tablaTemp = parserDetallesTodosInventarios(xmlString);

			// /////////////////////////
			// WARNING!!!! //
			// /////////////////////////
			// La tabla obtenida puede contener articulos en doble, pero con
			// codigos de barra diferentes
			// 5 Hay que fusionar estos articulos
			for (int numeroInventario : tablaTemp.keySet()) {

				// 5.1 Si el numeroInventario no existe, lo metemos en la tabla
				// de respuestsa
				if (tablaRespuesta.containsKey(numeroInventario) == false) {
					tablaRespuesta
							.put(numeroInventario,
									new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>());
				}

				// 5.2 Para cada articulo de ese inventario
				for (HashMap<String, Integer> tablaUnArticulo : (tablaTemp
						.get(numeroInventario)).keySet()) {
					System.out.println("::: HttpReader 557");
					// 5.2.1 Si ya tenemos este articulo en memoria,
					// concatenamos el codigo de barra nuevo con los existentes
					if (tablaRespuesta.get(numeroInventario).containsKey(
							tablaUnArticulo) == true) {
						String codbarViejo = tablaRespuesta
								.get(numeroInventario)
								.get(tablaUnArticulo)
								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
						String codbarNuevo = tablaTemp
								.get(numeroInventario)
								.get(tablaUnArticulo)
								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
						tablaRespuesta
								.get(numeroInventario)
								.get(tablaUnArticulo)
								.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
										codbarViejo + "," + codbarNuevo);
						System.out.println("::: HttpReader 575");}
					// 5.2.2 Sino,lo metemos
					else {
						tablaRespuesta.get(numeroInventario).put(
								tablaUnArticulo,
								tablaTemp.get(numeroInventario).get(
										tablaUnArticulo));
					}
				}
			}
			// 6 Liberacin de los recursos:
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 453--]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 459 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 465 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		}
		System.out.println("::: HttpReader 621 tabla respuesta " + tablaRespuesta);
		return tablaRespuesta;
	}

	/**
	 * Lee las referencias desde el webService
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Lectura web
	 * <p>
	 * 3 Obtension de los datos en formato de cadena XML
	 * <p>
	 * 4 Parseamos los datos a una tabla
	 * <p>
	 * 5 Liberacin de los recursos
	 *
	 * @return Un HashMap con las referencias leidas desde el webService
	 * @throws ExceptionHttpExchange
	 */
	public HashMap<String, HashMap<String, String>> readReferencias()
			throws ExceptionHttpExchange {
		System.out.println("::: Httpreader referencias");
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta;
		tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2 Lectura web
		try {
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			// 3 Obtension de los datos en formato de cadena XML
			xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 653 xmlString " + xmlString);

			if (entity == null) {
				throw new ExceptionHttpExchange(
						"Recuperacion de los REFERENCIAS",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}

			// 4 Parseamos los datos a una tabla
			tablaRespuesta = parserReferencias(xmlString);

			// 5 Liberacin de los recursos:
			if (entity != null) {
				entity.consumeContent();
			} else {
				throw new ExceptionHttpExchange("Leer REFERENCIAS",
						"Imposible recuperar REFERENCIAS via HTTP (1)");
			}

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 512 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar REFERENCIAS via HTTP (2): "
							+ e.toString());
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 518 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar REFERENCIAS via HTTP (3): "
							+ e.toString());
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 524 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar REFERENCIAS via HTTP (4): "
							+ e.toString());
		}

		return tablaRespuesta;
	}

	/**
	 * Lee las referencias por parte, para no generar una conexion muy pesada,
	 * iniciando en indice_inicial y finalizando en indice_final
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Objetos de conexin al web service
	 * <p>
	 * 3 Nuestra URL
	 * <p>
	 * 4 Instanciacin del objeto httppost
	 * <p>
	 * 5 Implementacin en el URL
	 * <p>
	 * 6 Lectura web
	 * <p>
	 * 7 Lectura de los datos a la cadena XML
	 * <p>
	 * 8 Liberacin de los recursos
	 * <p>
	 * 9 Parseo de los datos a una tabla de respuesta
	 *
	 * @param indice_inicial
	 * @param indice_final
	 * @return un HashMap con la parte correspondiente de las referencias
	 * @throws ExceptionHttpExchange
	 */
	public static HashMap<String, HashMap<String, String>> readParteReferencias(
			int indice_inicial, int indice_final) throws ExceptionHttpExchange {
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2 Objetos de conexin al web service:
		HttpClient httpclient = new DefaultHttpClient();

		// 3 Nuestra URL:
		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				3);

		// 4 Instanciacin del objeto httppost:
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
				ParametrosInventario.CODIGO_FONC_REFERENCIAS_POR_PARTES));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_opc,
				String.valueOf(indice_inicial) + ","
						+ String.valueOf(indice_final)));

		// 5 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {
			unEnEx.printStackTrace();
		}

		// 6 Lectura web
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity == null) {
				throw new ExceptionHttpExchange("Recuperacion de REFERENCIAS",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}
			// 7 Lectura de los datos a la cadena XML
			String xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 780 xmlString " + xmlString);
			// 8 Liberacin de los recursos:
			entity.consumeContent();
			// 9 Parseo de los datos a una tabla de respuesta
			tablaRespuesta = parserReferencias(xmlString);

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar operadores via HTTP (2)");
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar operadores via HTTP (3)");
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar operadores via HTTP (4)");
		}

		return tablaRespuesta;
	}


	/**
	 * Lee los proveedores por parte, para no generar una conexion muy pesada,
	 * iniciando en indice_inicial y finalizando en indice_final
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Objetos de conexin al web service
	 * <p>
	 * 3 Nuestra URL
	 * <p>
	 * 4 Instanciacin del objeto httppost
	 * <p>
	 * 5 Implementacin en el URL
	 * <p>
	 * 6 Lectura web
	 * <p>
	 * 7 Lectura de los datos a la cadena XML
	 * <p>
	 * 8 Liberacin de los recursos
	 * <p>
	 * 9 Parseo de los datos a una tabla de respuesta
	 *
	 * @param indice_inicial
	 * @param indice_final
	 * @return un HashMap con la parte correspondiente de los proveedores
	 * @throws ExceptionHttpExchange
	 */
	public static HashMap<String, HashMap<String, String>> readParteProveedores(
			int indice_inicial, int indice_final) throws ExceptionHttpExchange {
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2 Objetos de conexin al web service:
		HttpClient httpclient = new DefaultHttpClient();

		// 3 Nuestra URL:
		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				3);

		// 4 Instanciacin del objeto httppost:
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
				ParametrosInventario.CODIGO_FONC_PROVEEDORES_CANTIDAD));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_opc,
				String.valueOf(indice_inicial) + ","
						+ String.valueOf(indice_final)));

		// 5 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {
			unEnEx.printStackTrace();
		}

		// 6 Lectura web
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity == null) {
				throw new ExceptionHttpExchange("Recuperacion de PROVEEDORES",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}
			// 7 Lectura de los datos a la cadena XML
			String xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 890 xmlString " + xmlString);
			// 8 Liberacin de los recursos:
			entity.consumeContent();
			// 9 Parseo de los datos a una tabla de respuesta
			tablaRespuesta = parserProveedores(xmlString);

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer PROVEEDORES",
					"Imposible recuperar operadores via HTTP (2)");
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS",
					"Imposible recuperar operadores via HTTP (3)");
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer PROVEEDORES",
					"Imposible recuperar operadores via HTTP (4)");
		}

		return tablaRespuesta;
	}


	/**
	 * Funcion que lee las referencias totales DESDE EL USB (esta mal ubicada,
	 * pero se prefirio dejarla por comodidad de cambio en la forma de leer)
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2Obtenemos la referencia al fichero XML de entrada
	 * <p>
	 * 3 Copiado desde el pen drive hasta la tablet
	 * <p>
	 * 4 Lectura de los datos desde el archivo a una cadena XML
	 * <p>
	 * 5 Parseo de la cadena XML a una tabla
	 *
	 * @param ctxt
	 * @return un HashMap con las referencias
	 */
	public static HashMap<String, HashMap<String, String>> readReferenciasUSB(
			Context ctxt) {
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2Obtenemos la referencia al fichero XML de entrada
		try {

			String xmlFile = ParametrosInventario.CARPETA_MAETABLET
					+ ParametrosInventario.PREF_USB_IMPORT_MAESTRO_NOMBRE;
			String xmlDest = Parametros.PREF_USB_IMPORT + "maestro.xml";

			File archivo_fuente = new File(xmlFile);
			File archivo_destino = new File(xmlDest);
			// 3 Copiado desde el pen drive hasta la tablet
			copyFile(archivo_fuente, archivo_destino);

			String xmlString;

			// 4 Lectura de los datos desde el archivo a una cadena XML
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			InputStream inputStream = new FileInputStream(new File(xmlDest));
			org.w3c.dom.Document doc = documentBuilderFactory
					.newDocumentBuilder().parse(inputStream);
			StringWriter stw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
			xmlString = stw.toString();
			System.out.println("::: HttpReader 871 xmlString " + xmlString);
			// 5 Parseo de la cadena XML a una tabla
			tablaRespuesta = parserReferencias(xmlString);

		} catch (UnsupportedEncodingException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 655 --]" + e.toString() + "__" + e.getMessage(), 4);
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (FileNotFoundException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 662 --]" + e.toString() + "__" + e.getMessage(), 4);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 668 --]" + e.toString() + "__" + e.getMessage(), 4);
			// TODO Auto-generated catch block

			String msj = e.getMessage();
			e.printStackTrace();
		}

		return tablaRespuesta;
	}

	/**
	 * Funcion que lee las referencias de un archivo XML con cierto formato y
	 * las parsea en un HashMap. El archivo es solo una parte del maestro, el
	 * nombre se pasa como parametro Esta funcion se usa para importar las
	 * referencias por USB
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Obtenemos la referencia al fichero XML de entrada
	 * <p>
	 * 3 Leemos los datos del archivo en una cadena con formato XML
	 * <p>
	 * 4 Parseamos los datos del XML a una tabla
	 *
	 * @param ctxt
	 * @param archivo
	 * @return el HashMap con las referencias leidas y paresadas
	 */
	public static HashMap<String, HashMap<String, String>> readReferenciasVariosUSB(
			Context ctxt, File archivo) {
		// 1 Variable de retorno:
		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();

		// 2 Obtenemos la referencia al fichero XML de entrada
		try {

			String xmlString = "";

			// 3 Leemos los datos del archivo en una cadena con formato XML
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			InputStream inputStream = new FileInputStream(archivo);
			org.w3c.dom.Document doc = documentBuilderFactory
					.newDocumentBuilder().parse(inputStream);
			StringWriter stw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
			xmlString = stw.toString();
			System.out.println("::: HttpReader 952 xmlString " + xmlString);
			Log.v("yo", "Leida la cadena xml");

			// 4 Parseamos los datos del XML a una tabla
			Log.e("pasa por aca 2 ", tablaRespuesta.toString());

			tablaRespuesta = parserReferencias(xmlString);

			Log.v("yo", "Parseadas las referencias");
		} catch (UnsupportedEncodingException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 720 --]" + e.toString() + "__" + e.getMessage(), 4);
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (FileNotFoundException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 727 --]" + e.toString() + "__" + e.getMessage(), 4);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 734 --]" + e.toString() + "__" + e.getMessage(), 4);

			String msj = e.getMessage();
			e.printStackTrace();
		}

		return tablaRespuesta;
	}

	/**
	 * Funcion accesoria que copia un archivo en un destino dado
	 * <p>
	 * 1 Verificamos la existencia de los archivos de destino y fuente
	 * <p>
	 * 2 Creamos los canales de archivo
	 * <p>
	 * 3 Creamos los Input y output stream
	 * <p>
	 * 4 Realizamos la transferencia de los datos
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	private static void copyFile(File sourceFile, File destFile)
			throws IOException {
		// 1 Verificamos la existencia de los archivos de destino y fuente
		if (!sourceFile.exists()) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		// 2 Creamos los canales de archivo
		FileChannel source = null;
		FileChannel destination = null;
		// 3 Creamos los Input y output stream
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		// 4 Realizamos la transferencia de los datos
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}
	}

	/**
	 * Devuelve cuantas referencias hay para saber como leerlas parcialmente
	 * <p>
	 * 1 Objetos de conexin al web service
	 * <p>
	 * 2 Nuestra URL
	 * <p>
	 * 3 Instanciacin del objeto httppost
	 * <p>
	 * 4 Implementacin en el URL
	 * <p>
	 * 5 Lectura web
	 * <p>
	 * 6 Lectura de los datos en formato XML string
	 * <p>
	 * 7 Liberacin de los recursos
	 * <p>
	 * 8 Parseo y retorno de la respuesta
	 *
	 * @return
	 * @throws ExceptionHttpExchange
	 */
	public static int readCantidadReferencias() throws ExceptionHttpExchange {
		// 1 Objetos de conexin al web service:
		HttpClient httpclient = new DefaultHttpClient();

		// 2 Nuestra URL:
		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				2);

		// 3 Instanciacin del objeto httppost:
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
				ParametrosInventario.CODIGO_FONC_REFERENCIAS_CANTIDAD));

		// 4 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 816 --]" + unEnEx.toString() + "__"
					+ unEnEx.getMessage(), 4);
			unEnEx.printStackTrace();
		}

		// 5 Lectura web
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity == null) {
				throw new ExceptionHttpExchange(
						"Recuperacion de la CANTIDAD DE ARTICULOS",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}
			// 6 Lectura de los datos en formato XML string
			String xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 1105 xmlString " + xmlString);
			// 7 Liberacin de los recursos:
			entity.consumeContent();
			// 8 Parseo y retorno de la respuesta
			return parserCantidadReferencias(xmlString);

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 838 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD ARTICULOS REFERENCIA",
					"Imposible recuperar cantidad via HTTP (2)");
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[--844  --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD ARTICULOS REFERENCIA",
					"Imposible recuperar cantidad via HTTP (3)");
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 850 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD ARTICULOS REFERENCIA",
					"Imposible recuperar cantidad via HTTP (4)");
		}
	}


	public static HashMap<String, String> readConfiguraciones() throws ExceptionHttpExchange {
		HashMap<String, String> tablaRespuesta = new HashMap<String, String>();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(2);
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft, Parametros.CODIGO_SOFT_DEBOINVENTARIO));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc, Parametros.CODIGO_FONC_CONFIGURACIONES));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {
			unEnEx.printStackTrace();
		}
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity == null) {
				throw new ExceptionHttpExchange("Recuperacion de REFERENCIAS", "La consulta HTTP al servidor no ha devuelto resultados (0)");
			}
			// 7 Lectura de los datos a la cadena XML
			String xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 780 xmlString " + xmlString);
			// 8 Liberacin de los recursos:
			entity.consumeContent();
			// 9 Parseo de los datos a una tabla de respuesta
			tablaRespuesta = parserConfiguraciones(xmlString);

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS", "Imposible recuperar operadores via HTTP (2)");
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS", "Imposible recuperar operadores via HTTP (3)");
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange("Leer REFERENCIAS", "Imposible recuperar operadores via HTTP (4)");
		}
		return tablaRespuesta;
	}


	/*Lee la cantidad de proveedores */
	public static int readCantidadProveedores() throws ExceptionHttpExchange {
		// 1 Objetos de conexin al web service:
		HttpClient httpclient = new DefaultHttpClient();
		// 2 Nuestra URL:
		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
				2);
		// 3 Instanciacin del objeto httppost:
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
				ParametrosInventario.CODIGO_FONC_REFERENCIAS_CANTIDAD));

		// 4 Implementacin en el URL:
		try {
			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
		} catch (UnsupportedEncodingException unEnEx) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 816 --]" + unEnEx.toString() + "__"
					+ unEnEx.getMessage(), 4);
			unEnEx.printStackTrace();
		}

		// 5 Lectura web
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity == null) {
				throw new ExceptionHttpExchange(
						"Recuperacion de la CANTIDAD DE PROVEEDORES",
						"La consulta HTTP al servidor no ha devuelto resultados (0)");
			}
			// 6 Lectura de los datos en formato XML string
			String xmlString = EntityUtils.toString(entity);
			System.out.println("::: HttpReader 1105 xmlString " + xmlString);
			// 7 Liberacin de los recursos:
			entity.consumeContent();
			// 8 Parseo y retorno de la respuesta
			return parserCantidadProveedores(xmlString);

		} catch (ClientProtocolException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 838 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD PROVEEDORES ",
					"Imposible recuperar cantidad via HTTP (2)");
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[--844  --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD PROVEEDORES",
					"Imposible recuperar cantidad via HTTP (3)");
		} catch (Exception e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 850 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			throw new ExceptionHttpExchange(
					"Leer CANTIDAD PROVEEDORES",
					"Imposible recuperar cantidad via HTTP (4)");
		}
	}



	/**
	 * Toma una cadena con informacion en formato XML y la parsea en un HashMap
	 * <p>
	 * 1 Construccin de la estructura de respuesta
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Recorrido del arbol de los datos XML
	 * <p>
	 * 4 Guardado de los datos en el hashmap
	 * <p>
	 * 5 Metemos el hashmap en la matriz grande
	 *
	 * @param xmlString
	 * @return un HashMap con los articulos parseados
	 * @throws Exception
	 */
	private HashMap<String, HashMap<String, String>> parserArticulos(
			String xmlString) throws Exception {
		System.out.println("::: HttpReader 1163");
		// 1 Construccin de la estructura de respuesta:
		HashMap<String, HashMap<String, String>> matrizTodosArticulos;
		matrizTodosArticulos = new HashMap<String, HashMap<String, String>>();

		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 888 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 894 --]" + parConfExc.toString() + "__"
					+ parConfExc.getMessage(), 4);
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 900 --]" + se.toString() + "__" + se.getMessage(), 4);
			se.printStackTrace();
			return null;
		}

		// 3 Recorrido del arbol de los datos XML:
		NodeList listaArticulos = doc
				.getElementsByTagName(Parametros.bal_xml_articulo_root);
		for (int i = 0; i < listaArticulos.getLength(); i++) {
			NodeList listaHijos = listaArticulos.item(i).getChildNodes();
			HashMap<String, String> hashtmapUnArticulo = new HashMap<String, String>();
			for (int j = 0; j < listaHijos.getLength(); j++) {
				String nombre = listaHijos.item(j).getNodeName();
				String valor = listaHijos.item(j).getTextContent();
				// 4 Guardado de los datos en el hashmap
				hashtmapUnArticulo.put(ParametrosInventario.CONVERSOR_BALIZAS
								.xml2bdd(nombre, ParametrosInventario.tabla_articulos),
						valor);
			}
			// 5 Metemos el hashmap en la matriz grande
			matrizTodosArticulos.put(hashtmapUnArticulo
							.get(ParametrosInventario.bal_bdd_articulo_codigo_barra),
					hashtmapUnArticulo);
		}
		return matrizTodosArticulos;
	}

	/**
	 * Mtodo que desarma el archivo XML al formato String pasado en argumento y
	 * devuelve una matriz LinkedHashMap<String,HashMap<String,String>> de todas
	 * las entradas de la tabla INVENTARIO de la base de datos
	 * <p>
	 * 1 Variable de retorno
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Recorrido de los datos del docunento
	 * <p>
	 * &nbsp;&nbsp;3.1 Creamos el hashmap para un inventario
	 * <p>
	 * &nbsp;&nbsp;3.2 Agregamos ese inventario al hash map general de todos los
	 * inventarios
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private LinkedHashMap<String, String> parserInventarios(
			String xmlString) throws Exception {
		System.out.println("::: HTTPREADER 1252 parserinventarios");
		// 1 Variable de retorno:
//		LinkedHashMap<String, String>> matrizTodosInventarios = new LinkedHashMap<String, HashMap<String, String>>();
		LinkedHashMap<String, String> matrizTodosInventarios = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> matrizTodosInventariosDos = new LinkedHashMap<String,String>();
		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			System.out.println("::: HTTPREADER 1266 xml "+xmlString);
			inStream.setCharacterStream(new StringReader(xmlString));
//			System.out.println("::: HTTPREADER 1266 instream "+inStream);
//			SAXParserFactory spf = SAXParserFactory.newInstance();
//	            SAXParser sp = spf.newSAXParser();
//	            XMLReader xr = sp.getXMLReader();
//			 inStream.setCharacterStream(new StringReader(xmlString.toString()));
//	            Log.w("AndroidParseXMLActivity", "Parse2");
//	            xr.parse(inStream);
//	            Log.w("AndroidParseXMLActivity", "Parse3");
			doc = db.parse(inStream);
//			System.out.println("::: HTTPREADER 1266 doc "+doc);
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 957 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 963 --]" + parConfExc.toString() + "__"
					+ parConfExc.getMessage(), 4);
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 969 --]" + se.toString() + "__" + se.getMessage(), 4);
			se.printStackTrace();
			return null;
		}
		// 3 Recorrido de los datos del docunento
		NodeList listaInventarios = doc
				.getElementsByTagName(Parametros.bal_xml_inventario_root);

		String tagNum = "";
		String valTag = "";
		String fechatag = "";
		String descTag="";
		String nombre = "";
		String valor = "";
		String todo = "";
		String comparar = "";
		int co = 0 ;
//		for (int i = 0; i < listaInventarios.getLength(); i++) {
//			Node inventario = listaInventarios.item(i);
//			NodeList datosInventario = inventario.getChildNodes();
//			for (int j = 0; j < datosInventario.getLength(); j++) {
//				Node dato = datosInventario.item(j);
//				NodeList datosInventarioTag = dato.getChildNodes();
//				for (int z = 0; z < datosInventarioTag.getLength(); z++) {
//					Node tag = datosInventarioTag.item(z);
//					if(tag.getNodeType()==Node.ELEMENT_NODE){
//					System.out.println("::: HttpReader NODEEEE "  + tag.getNodeName());
//					Node datoContenido = tag.getFirstChild();
//					valTag =tag.getNodeName();
//					if(datoContenido !=null && datoContenido.getNodeType()==Node.TEXT_NODE){
//						System.out.println("::: HttpReader Contenido nodo " + datoContenido.getNodeValue());
//						System.out.println("::: HttpReader Contenido valTAg " + valTag);
//						if(valTag.equals("N")){
//							tagNum=datoContenido.getNodeValue();
//							System.out.println("::::: HttpReader VALOR QUE NECESITOOOOO " + tagNum);
//						}
//					}
//				}
//				}
//			}
		System.out.println("::: HttpReaderr 1360 " +listaInventarios);

		/*Lo siguiente es para saber con que inventario se esta trabajando*/
		boolean condicionRadio = ParametrosInventario.InventariosVentas;
		int condR = 0;
		if(condicionRadio == true){
			condR=-1;

		}else{
			condR=-2;
		}

		for (int i = 0; i < listaInventarios.getLength(); i++) {
			System.out.println("::: HttpReaderr 1363");

			NodeList listaHijos = listaInventarios.item(i).getChildNodes();

			HashMap<String, String> hashtmapUnInventario = new HashMap<String, String>();

			for (int j = 0; j < listaHijos.getLength(); j++) {

				Node dato = listaHijos.item(j);
				NodeList datosInventarioTag = dato.getChildNodes();

//				co++;
//				if(co==0 || (comparar!=tagNum)){

				for (int z = 0; z < datosInventarioTag.getLength(); z++) {
					nombre = datosInventarioTag.item(z).getNodeName();
					valor = datosInventarioTag.item(z).getTextContent();

					if(nombre.equals("N")){
						tagNum=valor;
					}
					if(nombre.equals("D")){
						descTag=valor;
					}
					if(nombre.equals("F")){
						fechatag=valor;
					}
					if(nombre.equals("DEP")){
						comparar=valor;
					}
//						Log.e("nombre", nombre);
//						Log.e("valor", valor);


				}
				System.out.println("::: HttpReader 1398 nombre " + nombre + " valor " + valor);

				if ( (!(tagNum.trim().length()==0)) && (!(descTag.trim().length()==0)) && (!(fechatag.trim().length()==0)) ) {

					todo = descTag + " -" + fechatag;
//									todo = descTag + " -" + fechatag + " - D " + comparar;
					int nn = Integer.parseInt(comparar);
					System.out.println("::: HttpReader 1398 x entrar condR " + condR + " comparar " + nn);

					if(condR==-1 && nn==0){
						System.out.println("::: HttpReader 1398 ventas ");
						hashtmapUnInventario.put(
								ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
										ParametrosInventario.tabla_inventarios), todo);

						matrizTodosInventarios.put(tagNum,
								todo);

					}else if(condR==-2 && nn==1){
						System.out.println("::: HttpReader 1398 deposito ");
						hashtmapUnInventario.put(
								ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
										ParametrosInventario.tabla_inventarios), todo);

						matrizTodosInventarios.put(tagNum,
								todo);

					}
//									hashtmapUnInventario.put(
//												ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
//														ParametrosInventario.tabla_inventarios), todo);
//
//										matrizTodosInventarios.put(tagNum,
//												todo);
				}
			}

			// 3.2 Agregamos ese inventario al hash map general de todos los
			// inventarios

//			matrizTodosInventarios.put(hashtmapUnInventario
//					.get(ParametrosInventario.bal_bdd_inventario_numero),
//					hashtmapUnInventario);
//			System.out.println("MATRIZZZZZZZZ 3 :  " + matrizTodosInventarios);
		}

		System.out.println("::: HttpReader 1423 matriz " + matrizTodosInventarios);
		return matrizTodosInventarios;
	}

	/**
	 * Paresa la cadena XML pasada como parametros en la estructura de retorno
	 * de detalles de inventarios
	 * <p>
	 * 1 Construccin de la estructura de respuesta
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Recorrido del arbol de los datos XML
	 * <p>
	 * &nbsp;&nbsp;3.1 Buscamos y guardamos los datos cada articlo
	 * <p>
	 * &nbsp;&nbsp;3.2 Si el id de inventario cambio, guardamos el LinkedHashMap
	 * anterior con el numero de inventario y creamos uno nuevo
	 * <p>
	 * &nbsp;&nbsp;3.3 Obtenemos los codigos del articulo
	 * <p>
	 * &nbsp;&nbsp;3.4 Si no esta en la matriz lo metemos
	 * <p>
	 * &nbsp;&nbsp;3.5 Si esta le agregamos el codigo de barra nuevo
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> parserDetallesTodosInventarios(
			String xmlString) throws Exception {
		System.out.println("::: HttpReader 1376");
		// 1 Construccin de la estructura de respuesta:
		LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> matrizDetallesTodosInventarios;
		matrizDetallesTodosInventarios = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();

		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1028 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1033 --]" + parConfExc.toString() + "__"
					+ parConfExc.getMessage(), 4);
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1040 --]" + se.toString() + "__" + se.getMessage(), 4);
			se.printStackTrace();
			return null;
		}
		System.out.println("::: HttpReader 1504");
		// 3 Recorrido del arbol de los datos XML:
		NodeList listaArticulos = doc
				.getElementsByTagName(Parametros.bal_xml_articulo_root);
		int numeroInventarioActual = -1;
		LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>> matrizTodosArticulosEsteInventario;
		matrizTodosArticulosEsteInventario = new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>();

		// int contador = 0;
		System.out.println("::: HttpReader 1513");
		for (int i = 0; i < listaArticulos.getLength(); i++) {
			System.out.println("::: HttpReader 1515");
			// 3.1 Buscamos y guardamos los datos cada articlo
			NodeList listaHijos = listaArticulos.item(i).getChildNodes();
			HashMap<String, String> hashmapArticulo = new HashMap<String, String>();

			for (int j = 0; j < listaHijos.getLength(); j++) {
				System.out.println("::: HttpReader 1521");
				String nombre = listaHijos.item(j).getNodeName();
				String valor = listaHijos.item(j).getTextContent();
				hashmapArticulo.put(ParametrosInventario.CONVERSOR_BALIZAS
								.xml2bdd(nombre, ParametrosInventario.tabla_articulos),
						valor);
			}
			System.out.println("::: HttpReader 1528");
			// A ver si lo agregamos en el mismo LinkedHashMap o si lo ponemos
			// en uno nuevo:
			if (Integer.parseInt(hashmapArticulo
					.get(ParametrosInventario.bal_bdd_articulo_inventario)) != numeroInventarioActual) {
				System.out.println("::: HttpReader 1533");
				// 3.2 Si el id de inventario cambio, guardamos el LinkedHashMap
				// anterior con el numero de inventario y creamos uno nuevo:
				if (numeroInventarioActual >= 0) {
					System.out.println("::: HttpReader 1537");
					matrizDetallesTodosInventarios.put(numeroInventarioActual,
							matrizTodosArticulosEsteInventario);
				}
				numeroInventarioActual = Integer.parseInt(hashmapArticulo
						.get(ParametrosInventario.bal_bdd_articulo_inventario));
				matrizTodosArticulosEsteInventario = new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>();
			}
			System.out.println("::: HttpReader 1545");
			// 3.3 Obtenemos los codigos del articulo
			HashMap<String, Integer> codigosArticulo = new HashMap<String, Integer>();
			codigosArticulo
					.put(ParametrosInventario.bal_bdd_articulo_sector,
							Integer.parseInt(hashmapArticulo.get(ParametrosInventario.bal_bdd_articulo_sector)));
			System.out.println("::: HttpReader 1552");
			codigosArticulo
					.put(ParametrosInventario.bal_bdd_articulo_codigo,
							Integer.parseInt(hashmapArticulo.get(ParametrosInventario.bal_bdd_articulo_codigo)));

			// 3.4 Si no esta en la matriz lo metemos
			if (!matrizTodosArticulosEsteInventario.containsKey(codigosArticulo)) {
				matrizTodosArticulosEsteInventario.put(codigosArticulo,
						hashmapArticulo);
			} else {
				// 3.5 Si esta le agregamos el codigo de barra nuevo
				String codbarNuevo = hashmapArticulo
						.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
				String codbarViejo = matrizTodosArticulosEsteInventario.get(
						codigosArticulo).get(
						ParametrosInventario.bal_bdd_articulo_codigo_barra);
				matrizTodosArticulosEsteInventario.get(codigosArticulo).put(
						ParametrosInventario.bal_bdd_articulo_codigo_barra,
						codbarViejo + "," + codbarNuevo);
			}
		}
		System.out.println("::: HttpReader 1574");
		matrizDetallesTodosInventarios.put(numeroInventarioActual,
				matrizTodosArticulosEsteInventario);
		System.out.println("::: HttpReader 1577");
		return matrizDetallesTodosInventarios;
	}

	/*
	 * Paresa las referencias pasadas en formato XML String a un HashMap
	 * <p>
	 * 1 Construccin de la estructura de respuesta
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Recorrido del arbol de los datos XML
	 * <p>
	 * 4 Guardamos los datos de una referencia
	 * <p>
	 * 5 Agregamos esos datos a la matriz mayor
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private static HashMap<String, HashMap<String, String>> parserReferencias(String xmlString) throws Exception {
		System.out.println("::: HttpReader 1516");
		Log.e("paso parsereferencias ", "paso por aca");
		// 1 Construccin de la estructura de respuesta:
		HashMap<String, HashMap<String, String>> matrizTodasReferencias;
		matrizTodasReferencias = new HashMap<String, HashMap<String, String>>();

		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			System.out.println("::: HttpReader 1516 xmlString " + xmlString);
			inStream.setCharacterStream(new StringReader(xmlString));
//			System.out.println("::: HttpReader 1516 inStream " + inStream.getEncoding() );
//			System.out.println("::: HttpReader 1516 doc " + db.parse(inStream) );
			doc = db.parse(inStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		}
		// 3 Recorrido del arbol de los datos XML:
		NodeList listaReferencias = doc
				.getElementsByTagName(Parametros.bal_xml_referencia_root);
		System.out.println("::: HttpReader 1516 nodelist " + listaReferencias);
		for (int i = 0; i < listaReferencias.getLength(); i++) {
			NodeList listaHijos = listaReferencias.item(i).getChildNodes();
			HashMap<String, String> hashtmapUnaReferencia = new HashMap<String, String>();
			// 4 Guardamos los datos de una referencia
			for (int j = 0; j < listaHijos.getLength(); j++) {

				String nombre = listaHijos.item(j).getNodeName();
				System.out.println("::: HttpReader 1516 nombre " + nombre);
				String valor = listaHijos.item(j).getTextContent();
				System.out.println("::: HttpReader 1516 valor " + valor);

				hashtmapUnaReferencia.put(ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,ParametrosInventario.tabla_referencias), valor);
			}
			// 5 Agregamos esos datos a la matriz mayor
			matrizTodasReferencias.put(hashtmapUnaReferencia.get(ParametrosInventario.bal_bdd_referencia_codigo_barra) + i,hashtmapUnaReferencia);
		}
		System.out.println("::: HttpReader 1576 matriz " + matrizTodasReferencias);
		return matrizTodasReferencias;
	}


	private static HashMap<String, String> parserConfiguraciones(String xmlString) throws Exception {
		System.out.println("::: HttpReader 1516");
		Log.e("paso parserConfig", "paso por aca");
		// 1 Construccin de la estructura de respuesta:
		HashMap<String, String> matrizTodasReferencias;
		matrizTodasReferencias = new HashMap<String, String>();

		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			System.out.println("::: HttpReader 1516 xmlString " + xmlString);
			inStream.setCharacterStream(new StringReader(xmlString));
//			System.out.println("::: HttpReader 1516 inStream " + inStream.getEncoding() );
//			System.out.println("::: HttpReader 1516 doc " + db.parse(inStream) );
			doc = db.parse(inStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		}
		// 3 Recorrido del arbol de los datos XML:
		NodeList listaConfiguraciones = doc.getElementsByTagName(Parametros.bal_xml_configuracion_root );
		System.out.println("::: HttpReader 1516 nodelist " + listaConfiguraciones);
		for (int i = 0; i < listaConfiguraciones.getLength(); i++) {
			NodeList listaHijos = listaConfiguraciones.item(i).getChildNodes();
			HashMap<String, String> hashtmapUnaReferencia = new HashMap<String, String>();
			// 4 Guardamos los datos de una referencia
			String variable = "";
			String valor = "";
			for (int j = 0; j < listaHijos.getLength(); j++) {
				String nodeName = listaHijos.item(j).getNodeName();
				String nodeValue = listaHijos.item(j).getTextContent();
				if(nodeName.equals("VARIABLE")) {
					variable = nodeValue;
				}else {
					valor = nodeValue;
				}
			}
			// 5 Agregamos esos datos a la matriz mayor
			matrizTodasReferencias.put(variable, valor);
		}
		System.out.println("::: HttpReader 1576 matriz " + matrizTodasReferencias);
		return matrizTodasReferencias;
	}


	/*
	 * Paresa los proveedores pasados en formato XML String a un HashMap
	 * <p>
	 * 1 Construccin de la estructura de respuesta
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Recorrido del arbol de los datos XML
	 * <p>
	 * 4 Guardamos los datos de una referencia
	 * <p>
	 * 5 Agregamos esos datos a la matriz mayor
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private static HashMap<String, HashMap<String, String>> parserProveedores(String xmlString) throws Exception {
		System.out.println("::: HttpReader Proveedores 1900");
		Log.e("paso proveedores ", "paso por aca");
		// 1 Construccin de la estructura de respuesta:
		HashMap<String, HashMap<String, String>> matrizTodosProveedores;
		matrizTodosProveedores = new HashMap<String, HashMap<String, String>>();
		// 2 Creacin de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			System.out.println("::: HttpReader 1914 xmlString " + xmlString);
			inStream.setCharacterStream(new StringReader(xmlString));

			doc = db.parse(inStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException parConfExc) {
			parConfExc.printStackTrace();
			return null;
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		}
		// 3 Recorrido del arbol de los datos XML:
		NodeList listaProveedores = doc
				.getElementsByTagName(Parametros.bal_xml_proveedores_root);
		System.out.println("::: HttpReader 1931 nodelist " + listaProveedores);
		System.out.println("::: HttpReader 1931 nodelist " + listaProveedores.getLength());
		for (int i = 0; i < listaProveedores.getLength(); i++) {
			NodeList listaHijos = listaProveedores.item(i).getChildNodes();
			HashMap<String, String> hashtmapUnProveedor = new HashMap<String, String>();
			// 4 Guardamos los datos de una referencia
			for (int j = 0; j < listaHijos.getLength(); j++) {

				String nombre = listaHijos.item(j).getNodeName();
				System.out.println("::: HttpReader 1939 nombre " + nombre);
				String valor = listaHijos.item(j).getTextContent();
				System.out.println("::: HttpReader 1941 valor " + valor);

				hashtmapUnProveedor.put(ParametrosInventario.CONVERSOR_BALIZAS.xml2bddProv(nombre,ParametrosInventario.tabla_proveedores), valor);
			}
			// 5 Agregamos esos datos a la matriz mayor
			matrizTodosProveedores.put(hashtmapUnProveedor.get(ParametrosInventario.bal_bdd_proveedores_codigo) + i,hashtmapUnProveedor);
		}
		System.out.println("::: HttpReader 1948 matriz " + matrizTodosProveedores);
		return matrizTodosProveedores;
	}

	/**
	 * Paresa la cadena XML con la informacion de cantida de referencias a un
	 * entero
	 * <p>
	 * 1 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Parsear
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private static int parserCantidadReferencias(String xmlString)
			throws Exception {
		System.out.println("::: HttpReader 1584");
		// 1� Creaci�n de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;

		// 2� Creaci�n de los elementos de trabajo con el archivo XML:
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			System.out.println("::: HTTPREADER 1598 xml "+xmlString);
			System.out.println("::: HTTPREADER 1599 instream "+inStream);
//
			doc = db.parse(inStream);
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1180 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return 0;
		} catch (ParserConfigurationException parConfExc) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1186 --]" + parConfExc.toString() + "__"
					+ parConfExc.getMessage(), 4);
			parConfExc.printStackTrace();
			return 0;
		} catch (SAXException se) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1192 --]" + se.toString() + "__" + se.getMessage(), 4);
			se.printStackTrace();
			return 0;
		}

		// 3� Parsear:
		NodeList nudoCantidad = doc
				.getElementsByTagName(Parametros.bal_xml_referencias_cantidad_total);
		try {
			Node noeud = nudoCantidad.item(0);
			String resultado = noeud.getTextContent();
			return Integer.parseInt(resultado);
		} catch (Exception e) {
			return 0;
		}
	}


	/**
	 * Paresa la cadena XML con la informacion de cantida de proveedores a un
	 * entero
	 * <p>
	 * 1 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 2 Creacin de los elementos de trabajo con el archivo XML
	 * <p>
	 * 3 Parsear
	 *
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private static int parserCantidadProveedores(String xmlString)
			throws Exception {
		System.out.println("::: HttpReader 1584 Proveedores");
		// 1� Creaci�n de los elementos de trabajo con el archivo XML:
		DocumentBuilderFactory factory = null;
		DocumentBuilder db = null;
		InputSource inStream = null;
		Document doc = null;

		// 2� Creaci�n de los elementos de trabajo con el archivo XML:
		try {
			factory = DocumentBuilderFactory.newInstance();
			db = factory.newDocumentBuilder();
			inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			System.out.println("::: HTTPREADER 1598 xml "+xmlString);
			System.out.println("::: HTTPREADER 1599 instream "+inStream);
//
			doc = db.parse(inStream);
		} catch (IOException e) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1180 --]" + e.toString() + "__" + e.getMessage(), 4);
			e.printStackTrace();
			return 0;
		} catch (ParserConfigurationException parConfExc) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1186 --]" + parConfExc.toString() + "__"
					+ parConfExc.getMessage(), 4);
			parConfExc.printStackTrace();
			return 0;
		} catch (SAXException se) {

			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
			log.log("[-- 1192 --]" + se.toString() + "__" + se.getMessage(), 4);
			se.printStackTrace();
			return 0;
		}

		// 3� Parsear:
		NodeList nudoCantidad = doc
				.getElementsByTagName(Parametros.bal_xml_referencias_cantidad_total);
		try {
			Node noeud = nudoCantidad.item(0);
			String resultado = noeud.getTextContent();
			return Integer.parseInt(resultado);
		} catch (Exception e) {
			return 0;
		}
	}

}






















//import android.content.Context;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.io.UnsupportedEncodingException;
//import java.nio.channels.FileChannel;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
///**
// * Clase que maneja las conexiones con los Web Services. Tambin maneja lectura
// * y escritura de archivos de exportacin.
// *
// * @author GuillermoR
// *
// */
//public class HttpReader {
//
//	/**
//	 * Variable de clase para el cliente http donde nos conectemos
//	 */
//	private HttpClient httpclient;
//	/**
//	 * Variable para almacenar la peticion post
//	 */
//	private HttpPost httppost;
//	/**
//	 * Variable para obtener la respuesta del request
//	 */
//	private HttpResponse response;
//	/**
//	 * Variable para obtener los datos de la respuesta
//	 */
//	private HttpEntity entity;
//	/**
//	 * Variable de clase donde se almacenara el xml recibido
//	 */
//	private String xmlString;
//
//	/**
//	 * Constructor usado para la creacin de un lector de flujo XML para cargar
//	 * Inventarios y referencias desde el web service
//	 * <p>
//	 * 1 Objetos de conexin al web service
//	 * <p>
//	 * 2 Nuestra URL
//	 * <p>
//	 * 3 Instanciacin del objeto httppost
//	 * <p>
//	 * 4 Implementacin en el URL
//	 *
//	 * Crea la conexion del Cliente Http con el webService y setea los
//	 * parametros
//	 *
//	 * @param url
//	 *            URL del webService
//	 * @param codigoFoncionLlamada
//	 *            codigo de funcion del webService
//	 * @throws ExceptionHttpExchange
//	 */
//	@NonNull
//    static GestorLogEventos log = new GestorLogEventos();
//
//	public HttpReader(String url, int codigoFoncionLlamada)
//			throws ExceptionHttpExchange {
//
//		log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//		log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//		log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//		log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//		log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//		log.log("[-- 100 --]" + "Inicia el HttpReader", 2);
//		// 1 Objetos de conexin al web service:
//		httpclient = new DefaultHttpClient();
//
//		// 2 Nuestra URL:
//		httppost = new HttpPost(url);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				2);
//
//		// 3 Instanciacin del objeto httppost:
//		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_INVENTARIOS) {
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_soft,
//					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_fonc,
//					ParametrosInventario.CODIGO_FONC_INVENTARIOS));
//		} else if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_REFERENCIAS) {
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_soft,
//					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_fonc,
//					ParametrosInventario.CODIGO_FONC_REFERENCIAS));
//		} else {
//			throw new ExceptionHttpExchange("HTTP READER",
//					"La funcion solicitada tiene que instanciarse con otro constructor");
//		}
//
//		// 4 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//
//			log.log("[-- 128 --]" + unEnEx.toString() + unEnEx.getMessage(), 4);
//			unEnEx.printStackTrace();
//		}
//	}
//
//	/**
//	 * Constructor usado para la creacon de un lector de flujo XML para la
//	 * lectura del webservice de los articulos
//	 * <p>
//	 * 1 Objetos de conexin al web service
//	 * <p>
//	 * 2 Nuestra URL
//	 * <p>
//	 * 3 Instanciacin del objeto httppost
//	 * <p>
//	 * 4 Implementacin en el URL
//	 *
//	 * Crea la conexion del Cliente Http con el webService y setea los
//	 * parametros
//	 *
//	 * @param url
//	 * @param codigoFoncionLlamada
//	 * @param parametros
//	 * @throws ExceptionHttpExchange
//	 */
//	public HttpReader(@NonNull String url, int codigoFoncionLlamada,
//                      @NonNull ArrayList<Integer> parametros) throws ExceptionHttpExchange {
//		System.out.println("::: HTTPREADER conecta al webservice");
//		// 1 Objetos de conexin al web service:
//		httpclient = new DefaultHttpClient();
//
//		// 2 Nuestra URL:
//		URLValidator.esValidaEstaURL(url);
//
//		httppost = new HttpPost(url);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				3);
//
//		// 3 Instanciacin del objeto httppost:
//		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_ARTICULOS) {
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_soft,
//					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_fonc,
//					ParametrosInventario.CODIGO_FONC_ARTICULOS));
//
//			String strFinal = "";
//			for (int num : parametros) {
//				strFinal = strFinal + String.valueOf(num) + ",";
//			}
//			// Sacqmos el ltimo "," de la cadena de caracteres:
//			strFinal = strFinal.substring(0, strFinal.length() - 1);
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_opc, strFinal));
//		} else {
//			throw new ExceptionHttpExchange("HTTP READER",
//					"La foncion solicitada tiene que instanciarse con otro constructor");
//		}
//
//		// 4 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 182 --]" + unEnEx.toString() + unEnEx.getMessage(), 4);
//			unEnEx.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Constructor del lector de webservice para la carga de la foto
//	 * <p>
//	 * 1 Objetos de conexin al web service
//	 * <p>
//	 * 2 Nuestra URL
//	 * <p>
//	 * 3 Instanciacin del objeto httppost
//	 * <p>
//	 * 4 Implementacin en el URL
//	 *
//	 * Crea la conexion y setea los parametros
//	 *
//	 * @param url
//	 * @param codigoFoncionLlamada
//	 * @param nombre_foto
//	 * @throws ExceptionHttpExchange
//	 */
//
//	public HttpReader(@NonNull String url, int codigoFoncionLlamada, String nombre_foto)
//			throws ExceptionHttpExchange {
//		// 1 Objetos de conexin al web service:
//		httpclient = new DefaultHttpClient();
//
//		// 2 Nuestra URL:
//		URLValidator.esValidaEstaURL(url);
//
//		httppost = new HttpPost(url);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				3);
//
//		// 3 Instanciacin del objeto httppost:
//		if (codigoFoncionLlamada == ParametrosInventario.FONCION_CARGAR_FOTO) {
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_soft,
//					Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_fonc,
//					ParametrosInventario.CODIGO_FONC_FOTO));
//			listaParametrosPost.add(new BasicNameValuePair(
//					Parametros.codigo_opc, nombre_foto));
//		} else {
//			throw new ExceptionHttpExchange("HTTP READER",
//					"La foncion solicitada tiene que instanciarse con otro constructor");
//		}
//
//		// 4 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 229 --]" + unEnEx.toString() + "__"
//					+ unEnEx.getMessage(), 4);
//			unEnEx.printStackTrace();
//		}
//	}
//
//	/**
//	 * Se ejecuta para leer una Foto desde el webservice, previamente se debe
//	 * haber creado con el constructor corespondiente
//	 * <p>
//	 * 1 Ejecucion de la consulta del http post
//	 * <p>
//	 * 2 Obtenemos los bytes de la respuesta
//	 * <p>
//	 * 3 Devolvemos el arreglo de bytes de la foto
//	 *
//	 * @return un arreglo de bytes que contiene informacin de la foto
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public byte[] readFoto() throws ExceptionHttpExchange {
//		byte[] ba = null;
//		try {
//			// 1 Ejecucion de la consulta del http post
//			response = httpclient.execute(httppost);
//			entity = response.getEntity();
//			// 2 Obtenemos los bytes de la respuesta
//			ba = EntityUtils.toByteArray(entity);
//
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 257 --]" + e.toString() + "__" + e.getMessage(), 4);
//
//			e.printStackTrace();
//			return null;
//		}
//		// 3 Devolvemos el arreglo de bytes de la foto
//		return ba;
//
//	}
//
//	/**
//	 * Funcion que lee desde el WebService los articulos
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Lectura web
//	 * <p>
//	 * 3 Obtensin de la respuesta
//	 * <p>
//	 * 4 Parseo de la respuesta XML en la tabla de retorno
//	 * <p>
//	 * 5 Liberacin de los recursos
//	 *
//	 * @return un HashMap con los articulos indexados
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public HashMap<String, HashMap<String, String>> readArticulos()
//			throws ExceptionHttpExchange {
//		System.out.println("::: Httpreader 344 Lectura webservice");
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta;
//		tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Lectura web
//		try {
//			response = httpclient.execute(httppost);
//			entity = response.getEntity();
//			// 3 Obtensin de la respuesta
//			xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 358 xmlString "+ xmlString);
//			if (entity == null) {
//				throw new ExceptionHttpExchange(
//						"Recuperacion de los ARTICULOS",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//
//			// 4 Parseo de la respuesta XML en la tabla de retorno
//			tablaRespuesta = parserArticulos(xmlString);
//
//			// 5 Liberacin de los recursos:
//			if (entity != null) {
//				entity.consumeContent();
//			} else {
//				throw new ExceptionHttpExchange("Leer ARTICULOS",
//						"Imposible recuperar operadores via HTTP (1)");
//			}
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 308 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer ARTICULOS",
//					"Imposible recuperar operadores via HTTP (2): "
//							+ e.toString());
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 314 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer ARTICULOS",
//					"Imposible recuperar operadores via HTTP (3): "
//							+ e.toString());
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 320 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer ARTICULOS",
//					"Imposible recuperar operadores via HTTP (4): "
//							+ e.toString());
//		}
//
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Lee los INVENTARIOS disponibles en la base de datos desde el WebService y
//	 * les muestra si existen.
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Lectura web
//	 * <p>
//	 * 3 Obtensin de la respuesta
//	 * <p>
//	 * 4 Parseo de la respuesta XML en la tabla de retorno
//	 * <p>
//	 * 5 Liberacion de los recursos
//	 *
//	 * @return un HashMap con los inventarios, Sino return null
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public LinkedHashMap<String, String> readInventarios(int condR)
//			throws ExceptionHttpExchange {
//		System.out.println("::: Httpreader 433 readInventarios");
//		System.out.println("::: Httpreader 433 readInventarios condR " + condR);
//		// 1 Variable de retorno:
////		LinkedHashMap<String, HashMap<String, String>> tablaRespuesta = new LinkedHashMap<String, HashMap<String, String>>();
//		LinkedHashMap<String,String> tablaRespuesta = new LinkedHashMap<String, String>();
//		// 2 Lectura web
//		try {
//
//
////			System.out.println("::: Httpreader 439 readInventarios");
//			response = httpclient.execute(httppost);
//			entity = response.getEntity();
//			if (entity == null) {
//				throw new ExceptionHttpExchange("Recuperacion de INVENTARIOS",
//						"La consulta HTTP al servidor no ha devuelto resultados.");
//			}
//			// 3 Obtensin de la respuesta
//			xmlString = EntityUtils.toString(entity);
//			System.out.println( "::: HTTPREADER 448 XML " +xmlString);
//			// 4 Parseo de la respuesta XML en la tabla de retorno
//			tablaRespuesta = parserInventarios(xmlString);
//			System.out.println( "::: HTTPREADER 451 tabla " +tablaRespuesta);
//			// 5 Liberacin de los recursos:
//			if (entity != null) {
//				entity.consumeContent();
//			}
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 368 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 374 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 381 --]" + e.toString() + "__" + e.getMessage(), 4);
//			return null;
//		}
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Lee desde el webService los detalles de los inventarios con los
//	 * inventarios?
//	 * <p>
//	 * 1 Variable de retorno:
//	 * <p>
//	 * 2 Lectura web
//	 * <p>
//	 * 3 Obtension de la respuesta en formato de cadena XML
//	 * <p>
//	 * 4 Parseo de la cadena a una tabla temporal
//	 * <p>
//	 * 5 Hay que fusionar estos articulos
//	 * <p>
//	 * &nbsp;&nbsp;5.1 Si el numeroInventario no existe, lo metemos en la tabla
//	 * de respuestsa
//	 * <p>
//	 * nbsp;&nbsp;5.2 Para cada articulo de ese inventario
//	 * <p>
//	 * nbsp;&nbsp;nbsp;&nbsp;5.2.1 Si ya tenemos este articulo en memoria,
//	 * concatenamos el codigo de barra nuevo con los existentes
//	 * <p>
//	 * nbsp;&nbsp;nbsp;&nbsp;5.2.2 Sino,lo metemos
//	 * <p>
//	 * 6 Liberacin de los recursos
//	 *
//	 * @return
//	 */
//	@Nullable
//    public LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> readDetallesInventarios() {
//		// 1 Variable de retorno:
//		System.out.println("::: HttpReader 524 readDetallesInventario");
//		LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> tablaRespuesta, tablaTemp;
//		tablaTemp = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();
//		tablaRespuesta = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();
//
//		// 2 Lectura web
//		try {
//			response = httpclient.execute(httppost);
//			entity = response.getEntity();
//			// 3 Obtension de la respuesta en formato de cadena XML
//			xmlString = EntityUtils.toString(entity);
//System.out.println("::: HttpReader 534 xmlString " + xmlString);
//			// 4 Parseo de la cadena a una tabla temporal
//			tablaTemp = parserDetallesTodosInventarios(xmlString);
//
//			// /////////////////////////
//			// WARNING!!!! //
//			// /////////////////////////
//			// La tabla obtenida puede contener articulos en doble, pero con
//			// codigos de barra diferentes
//			// 5 Hay que fusionar estos articulos
//			for (int numeroInventario : tablaTemp.keySet()) {
//
//				// 5.1 Si el numeroInventario no existe, lo metemos en la tabla
//				// de respuestsa
//				if (tablaRespuesta.containsKey(numeroInventario) == false) {
//					tablaRespuesta
//							.put(numeroInventario,
//									new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>());
//				}
//
//				// 5.2 Para cada articulo de ese inventario
//				for (HashMap<String, Integer> tablaUnArticulo : (tablaTemp
//						.get(numeroInventario)).keySet()) {
//					System.out.println("::: HttpReader 557");
//					// 5.2.1 Si ya tenemos este articulo en memoria,
//					// concatenamos el codigo de barra nuevo con los existentes
//					if (tablaRespuesta.get(numeroInventario).containsKey(
//							tablaUnArticulo) == true) {
//						String codbarViejo = tablaRespuesta
//								.get(numeroInventario)
//								.get(tablaUnArticulo)
//								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//						String codbarNuevo = tablaTemp
//								.get(numeroInventario)
//								.get(tablaUnArticulo)
//								.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//						tablaRespuesta
//								.get(numeroInventario)
//								.get(tablaUnArticulo)
//								.put(ParametrosInventario.bal_bdd_articulo_codigo_barra,
//										codbarViejo + "," + codbarNuevo);
//						System.out.println("::: HttpReader 575");}
//					// 5.2.2 Sino,lo metemos
//					else {
//						tablaRespuesta.get(numeroInventario).put(
//								tablaUnArticulo,
//								tablaTemp.get(numeroInventario).get(
//										tablaUnArticulo));
//					}
//				}
//			}
//			// 6 Liberacin de los recursos:
//			if (entity != null) {
//				entity.consumeContent();
//			}
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 453--]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 459 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 465 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		}
//		System.out.println("::: HttpReader 621 tabla respuesta " + tablaRespuesta);
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Lee las referencias desde el webService
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Lectura web
//	 * <p>
//	 * 3 Obtension de los datos en formato de cadena XML
//	 * <p>
//	 * 4 Parseamos los datos a una tabla
//	 * <p>
//	 * 5 Liberacin de los recursos
//	 *
//	 * @return Un HashMap con las referencias leidas desde el webService
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public HashMap<String, HashMap<String, String>> readReferencias()
//			throws ExceptionHttpExchange {
//		System.out.println("::: Httpreader referencias");
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta;
//		tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Lectura web
//		try {
//			response = httpclient.execute(httppost);
//			entity = response.getEntity();
//			// 3 Obtension de los datos en formato de cadena XML
//			xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 653 xmlString " + xmlString);
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange(
//						"Recuperacion de los REFERENCIAS",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//
//			// 4 Parseamos los datos a una tabla
//			tablaRespuesta = parserReferencias(xmlString);
//
//			// 5 Liberacin de los recursos:
//			if (entity != null) {
//				entity.consumeContent();
//			} else {
//				throw new ExceptionHttpExchange("Leer REFERENCIAS",
//						"Imposible recuperar REFERENCIAS via HTTP (1)");
//			}
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 512 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar REFERENCIAS via HTTP (2): "
//							+ e.toString());
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 518 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar REFERENCIAS via HTTP (3): "
//							+ e.toString());
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 524 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar REFERENCIAS via HTTP (4): "
//							+ e.toString());
//		}
//
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Lee las referencias por parte, para no generar una conexion muy pesada,
//	 * iniciando en indice_inicial y finalizando en indice_final
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Objetos de conexin al web service
//	 * <p>
//	 * 3 Nuestra URL
//	 * <p>
//	 * 4 Instanciacin del objeto httppost
//	 * <p>
//	 * 5 Implementacin en el URL
//	 * <p>
//	 * 6 Lectura web
//	 * <p>
//	 * 7 Lectura de los datos a la cadena XML
//	 * <p>
//	 * 8 Liberacin de los recursos
//	 * <p>
//	 * 9 Parseo de los datos a una tabla de respuesta
//	 *
//	 * @param indice_inicial
//	 * @param indice_final
//	 * @return un HashMap con la parte correspondiente de las referencias
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public static HashMap<String, HashMap<String, String>> readParteReferencias(
//			int indice_inicial, int indice_final) throws ExceptionHttpExchange {
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Objetos de conexin al web service:
//		HttpClient httpclient = new DefaultHttpClient();
//
//		// 3 Nuestra URL:
//		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				3);
//
//		// 4 Instanciacin del objeto httppost:
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
//				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
//				ParametrosInventario.CODIGO_FONC_REFERENCIAS_POR_PARTES));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_opc,
//				String.valueOf(indice_inicial) + ","
//						+ String.valueOf(indice_final)));
//
//		// 5 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//			unEnEx.printStackTrace();
//		}
//
//		// 6 Lectura web
//		try {
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange("Recuperacion de REFERENCIAS",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//			// 7 Lectura de los datos a la cadena XML
//			String xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 780 xmlString " + xmlString);
//			// 8 Liberacin de los recursos:
//			entity.consumeContent();
//			// 9 Parseo de los datos a una tabla de respuesta
//			tablaRespuesta = parserReferencias(xmlString);
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (2)");
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (3)");
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (4)");
//		}
//
//		return tablaRespuesta;
//	}
//
//
//	/**
//	 * Lee los proveedores por parte, para no generar una conexion muy pesada,
//	 * iniciando en indice_inicial y finalizando en indice_final
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Objetos de conexin al web service
//	 * <p>
//	 * 3 Nuestra URL
//	 * <p>
//	 * 4 Instanciacin del objeto httppost
//	 * <p>
//	 * 5 Implementacin en el URL
//	 * <p>
//	 * 6 Lectura web
//	 * <p>
//	 * 7 Lectura de los datos a la cadena XML
//	 * <p>
//	 * 8 Liberacin de los recursos
//	 * <p>
//	 * 9 Parseo de los datos a una tabla de respuesta
//	 *
//	 * @param indice_inicial
//	 * @param indice_final
//	 * @return un HashMap con la parte correspondiente de los proveedores
//	 * @throws ExceptionHttpExchange
//	 */
//	@Nullable
//    public static HashMap<String, HashMap<String, String>> readParteProveedores(
//			int indice_inicial, int indice_final) throws ExceptionHttpExchange {
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Objetos de conexin al web service:
//		HttpClient httpclient = new DefaultHttpClient();
//
//		// 3 Nuestra URL:
//		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				3);
//
//		// 4 Instanciacin del objeto httppost:
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
//				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
//				ParametrosInventario.CODIGO_FONC_PROVEEDORES_CANTIDAD));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_opc,
//				String.valueOf(indice_inicial) + ","
//						+ String.valueOf(indice_final)));
//
//		// 5 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//			unEnEx.printStackTrace();
//		}
//
//		// 6 Lectura web
//		try {
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange("Recuperacion de PROVEEDORES",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//			// 7 Lectura de los datos a la cadena XML
//			String xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 890 xmlString " + xmlString);
//			// 8 Liberacin de los recursos:
//			entity.consumeContent();
//			// 9 Parseo de los datos a una tabla de respuesta
//			tablaRespuesta = parserProveedores(xmlString);
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer PROVEEDORES",
//					"Imposible recuperar operadores via HTTP (2)");
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (3)");
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer PROVEEDORES",
//					"Imposible recuperar operadores via HTTP (4)");
//		}
//
//		return tablaRespuesta;
//	}
//
//
//	/**
//	 * Funcion que lee las referencias totales DESDE EL USB (esta mal ubicada,
//	 * pero se prefirio dejarla por comodidad de cambio en la forma de leer)
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2Obtenemos la referencia al fichero XML de entrada
//	 * <p>
//	 * 3 Copiado desde el pen drive hasta la tablet
//	 * <p>
//	 * 4 Lectura de los datos desde el archivo a una cadena XML
//	 * <p>
//	 * 5 Parseo de la cadena XML a una tabla
//	 *
//	 * @param ctxt
//	 * @return un HashMap con las referencias
//	 */
//	@Nullable
//    public static HashMap<String, HashMap<String, String>> readReferenciasUSB(
//			Context ctxt) {
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2Obtenemos la referencia al fichero XML de entrada
//		try {
//
//			String xmlFile = ParametrosInventario.CARPETA_MAETABLET
//					+ ParametrosInventario.PREF_USB_IMPORT_MAESTRO_NOMBRE;
//			String xmlDest = Parametros.PREF_USB_IMPORT + "maestro.xml";
//
//			File archivo_fuente = new File(xmlFile);
//			File archivo_destino = new File(xmlDest);
//			// 3 Copiado desde el pen drive hasta la tablet
//			copyFile(archivo_fuente, archivo_destino);
//
//			String xmlString;
//
//			// 4 Lectura de los datos desde el archivo a una cadena XML
//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
//					.newInstance();
//			InputStream inputStream = new FileInputStream(new File(xmlDest));
//			Document doc = documentBuilderFactory
//					.newDocumentBuilder().parse(inputStream);
//			StringWriter stw = new StringWriter();
//			Transformer serializer = TransformerFactory.newInstance()
//					.newTransformer();
//			serializer.transform(new DOMSource(doc), new StreamResult(stw));
//			xmlString = stw.toString();
//			System.out.println("::: HttpReader 871 xmlString " + xmlString);
//			// 5 Parseo de la cadena XML a una tabla
//			tablaRespuesta = parserReferencias(xmlString);
//
//		} catch (UnsupportedEncodingException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 655 --]" + e.toString() + "__" + e.getMessage(), 4);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		} catch (FileNotFoundException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 662 --]" + e.toString() + "__" + e.getMessage(), 4);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 668 --]" + e.toString() + "__" + e.getMessage(), 4);
//			// TODO Auto-generated catch block
//
//			String msj = e.getMessage();
//			e.printStackTrace();
//		}
//
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Funcion que lee las referencias de un archivo XML con cierto formato y
//	 * las parsea en un HashMap. El archivo es solo una parte del maestro, el
//	 * nombre se pasa como parametro Esta funcion se usa para importar las
//	 * referencias por USB
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Obtenemos la referencia al fichero XML de entrada
//	 * <p>
//	 * 3 Leemos los datos del archivo en una cadena con formato XML
//	 * <p>
//	 * 4 Parseamos los datos del XML a una tabla
//	 *
//	 * @param ctxt
//	 * @param archivo
//	 * @return el HashMap con las referencias leidas y paresadas
//	 */
//	@Nullable
//    public static HashMap<String, HashMap<String, String>> readReferenciasVariosUSB(
//            Context ctxt, @NonNull File archivo) {
//		// 1 Variable de retorno:
//		HashMap<String, HashMap<String, String>> tablaRespuesta = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Obtenemos la referencia al fichero XML de entrada
//		try {
//
//			String xmlString = "";
//
//			// 3 Leemos los datos del archivo en una cadena con formato XML
//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
//					.newInstance();
//			InputStream inputStream = new FileInputStream(archivo);
//			Document doc = documentBuilderFactory
//					.newDocumentBuilder().parse(inputStream);
//			StringWriter stw = new StringWriter();
//			Transformer serializer = TransformerFactory.newInstance()
//					.newTransformer();
//			serializer.transform(new DOMSource(doc), new StreamResult(stw));
//			xmlString = stw.toString();
//			System.out.println("::: HttpReader 952 xmlString " + xmlString);
//			Log.v("yo", "Leida la cadena xml");
//
//			// 4 Parseamos los datos del XML a una tabla
//			Log.e("pasa por aca 2 ", tablaRespuesta.toString());
//
//			tablaRespuesta = parserReferencias(xmlString);
//
//			Log.v("yo", "Parseadas las referencias");
//		} catch (UnsupportedEncodingException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 720 --]" + e.toString() + "__" + e.getMessage(), 4);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		} catch (FileNotFoundException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 727 --]" + e.toString() + "__" + e.getMessage(), 4);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 734 --]" + e.toString() + "__" + e.getMessage(), 4);
//
//			String msj = e.getMessage();
//			e.printStackTrace();
//		}
//
//		return tablaRespuesta;
//	}
//
//	/**
//	 * Funcion accesoria que copia un archivo en un destino dado
//	 * <p>
//	 * 1 Verificamos la existencia de los archivos de destino y fuente
//	 * <p>
//	 * 2 Creamos los canales de archivo
//	 * <p>
//	 * 3 Creamos los Input y output stream
//	 * <p>
//	 * 4 Realizamos la transferencia de los datos
//	 *
//	 * @param sourceFile
//	 * @param destFile
//	 * @throws IOException
//	 */
//	private static void copyFile(@NonNull File sourceFile, @NonNull File destFile)
//			throws IOException {
//		// 1 Verificamos la existencia de los archivos de destino y fuente
//		if (!sourceFile.exists()) {
//			return;
//		}
//		if (!destFile.exists()) {
//			destFile.createNewFile();
//		}
//		// 2 Creamos los canales de archivo
//		FileChannel source = null;
//		FileChannel destination = null;
//		// 3 Creamos los Input y output stream
//		source = new FileInputStream(sourceFile).getChannel();
//		destination = new FileOutputStream(destFile).getChannel();
//		// 4 Realizamos la transferencia de los datos
//		if (destination != null && source != null) {
//			destination.transferFrom(source, 0, source.size());
//		}
//		if (source != null) {
//			source.close();
//		}
//		if (destination != null) {
//			destination.close();
//		}
//	}
//
//	/**
//	 * Devuelve cuantas referencias hay para saber como leerlas parcialmente
//	 * <p>
//	 * 1 Objetos de conexin al web service
//	 * <p>
//	 * 2 Nuestra URL
//	 * <p>
//	 * 3 Instanciacin del objeto httppost
//	 * <p>
//	 * 4 Implementacin en el URL
//	 * <p>
//	 * 5 Lectura web
//	 * <p>
//	 * 6 Lectura de los datos en formato XML string
//	 * <p>
//	 * 7 Liberacin de los recursos
//	 * <p>
//	 * 8 Parseo y retorno de la respuesta
//	 *
//	 * @return
//	 * @throws ExceptionHttpExchange
//	 */
//	public static int readCantidadReferencias() throws ExceptionHttpExchange {
//		// 1 Objetos de conexin al web service:
//		HttpClient httpclient = new DefaultHttpClient();
//
//		// 2 Nuestra URL:
//		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				2);
//
//		// 3 Instanciacin del objeto httppost:
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
//				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
//				ParametrosInventario.CODIGO_FONC_REFERENCIAS_CANTIDAD));
//
//		// 4 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 816 --]" + unEnEx.toString() + "__"
//					+ unEnEx.getMessage(), 4);
//			unEnEx.printStackTrace();
//		}
//
//		// 5 Lectura web
//		try {
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange(
//						"Recuperacion de la CANTIDAD DE ARTICULOS",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//			// 6 Lectura de los datos en formato XML string
//			String xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 1105 xmlString " + xmlString);
//			// 7 Liberacin de los recursos:
//			entity.consumeContent();
//			// 8 Parseo y retorno de la respuesta
//			return parserCantidadReferencias(xmlString);
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 838 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD ARTICULOS REFERENCIA",
//					"Imposible recuperar cantidad via HTTP (2)");
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[--844  --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD ARTICULOS REFERENCIA",
//					"Imposible recuperar cantidad via HTTP (3)");
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 850 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD ARTICULOS REFERENCIA",
//					"Imposible recuperar cantidad via HTTP (4)");
//		}
//	}
//
//
//	@Nullable
//    public static HashMap<String, String> readConfiguraciones() throws ExceptionHttpExchange {
//		HashMap<String, String> tablaRespuesta = new HashMap<String, String>();
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(2);
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft, Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc, Parametros.CODIGO_FONC_CONFIGURACIONES));
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//			unEnEx.printStackTrace();
//		}
//		try {
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange("Recuperacion de REFERENCIAS",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//			// 7 Lectura de los datos a la cadena XML
//			String xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 780 xmlString " + xmlString);
//			// 8 Liberacin de los recursos:
//			entity.consumeContent();
//			// 9 Parseo de los datos a una tabla de respuesta
//			tablaRespuesta = parserConfiguraciones(xmlString);
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 592 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (2)");
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 598 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (3)");
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 604 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange("Leer REFERENCIAS",
//					"Imposible recuperar operadores via HTTP (4)");
//		}
//		return tablaRespuesta;
//	}
//
//
//	/*Lee la cantidad de proveedores */
//	public static int readCantidadProveedores() throws ExceptionHttpExchange {
//		// 1 Objetos de conexin al web service:
//		HttpClient httpclient = new DefaultHttpClient();
//		// 2 Nuestra URL:
//		HttpPost httppost = new HttpPost(Parametros.PREF_URL_CONEXION_SERVIDOR);
//		List<NameValuePair> listaParametrosPost = new ArrayList<NameValuePair>(
//				2);
//		// 3 Instanciacin del objeto httppost:
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_soft,
//				Parametros.CODIGO_SOFT_DEBOINVENTARIO));
//		listaParametrosPost.add(new BasicNameValuePair(Parametros.codigo_fonc,
//				ParametrosInventario.CODIGO_FONC_REFERENCIAS_CANTIDAD));
//
//		// 4 Implementacin en el URL:
//		try {
//			httppost.setEntity(new UrlEncodedFormEntity(listaParametrosPost));
//		} catch (UnsupportedEncodingException unEnEx) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 816 --]" + unEnEx.toString() + "__"
//					+ unEnEx.getMessage(), 4);
//			unEnEx.printStackTrace();
//		}
//
//		// 5 Lectura web
//		try {
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//
//			if (entity == null) {
//				throw new ExceptionHttpExchange(
//						"Recuperacion de la CANTIDAD DE PROVEEDORES",
//						"La consulta HTTP al servidor no ha devuelto resultados (0)");
//			}
//			// 6 Lectura de los datos en formato XML string
//			String xmlString = EntityUtils.toString(entity);
//			System.out.println("::: HttpReader 1105 xmlString " + xmlString);
//			// 7 Liberacin de los recursos:
//			entity.consumeContent();
//			// 8 Parseo y retorno de la respuesta
//			return parserCantidadProveedores(xmlString);
//
//		} catch (ClientProtocolException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 838 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD PROVEEDORES ",
//					"Imposible recuperar cantidad via HTTP (2)");
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[--844  --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD PROVEEDORES",
//					"Imposible recuperar cantidad via HTTP (3)");
//		} catch (Exception e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 850 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			throw new ExceptionHttpExchange(
//					"Leer CANTIDAD PROVEEDORES",
//					"Imposible recuperar cantidad via HTTP (4)");
//		}
//	}
//
//
//
//	/**
//	 * Toma una cadena con informacion en formato XML y la parsea en un HashMap
//	 * <p>
//	 * 1 Construccin de la estructura de respuesta
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Recorrido del arbol de los datos XML
//	 * <p>
//	 * 4 Guardado de los datos en el hashmap
//	 * <p>
//	 * 5 Metemos el hashmap en la matriz grande
//	 *
//	 * @param xmlString
//	 * @return un HashMap con los articulos parseados
//	 * @throws Exception
//	 */
//	@Nullable
//    private HashMap<String, HashMap<String, String>> parserArticulos(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HttpReader 1163");
//		// 1 Construccin de la estructura de respuesta:
//		HashMap<String, HashMap<String, String>> matrizTodosArticulos;
//		matrizTodosArticulos = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			inStream.setCharacterStream(new StringReader(xmlString));
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 888 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 894 --]" + parConfExc.toString() + "__"
//					+ parConfExc.getMessage(), 4);
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 900 --]" + se.toString() + "__" + se.getMessage(), 4);
//			se.printStackTrace();
//			return null;
//		}
//
//		// 3 Recorrido del arbol de los datos XML:
//		NodeList listaArticulos = doc
//				.getElementsByTagName(Parametros.bal_xml_articulo_root);
//		for (int i = 0; i < listaArticulos.getLength(); i++) {
//			NodeList listaHijos = listaArticulos.item(i).getChildNodes();
//			HashMap<String, String> hashtmapUnArticulo = new HashMap<String, String>();
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//				String nombre = listaHijos.item(j).getNodeName();
//				String valor = listaHijos.item(j).getTextContent();
//				// 4 Guardado de los datos en el hashmap
//				hashtmapUnArticulo.put(ParametrosInventario.CONVERSOR_BALIZAS
//						.xml2bdd(nombre, ParametrosInventario.tabla_articulos),
//						valor);
//			}
//			// 5 Metemos el hashmap en la matriz grande
//			matrizTodosArticulos.put(hashtmapUnArticulo
//					.get(ParametrosInventario.bal_bdd_articulo_codigo_barra),
//					hashtmapUnArticulo);
//		}
//		return matrizTodosArticulos;
//	}
//
//	/**
//	 * Mtodo que desarma el archivo XML al formato String pasado en argumento y
//	 * devuelve una matriz LinkedHashMap<String,HashMap<String,String>> de todas
//	 * las entradas de la tabla INVENTARIO de la base de datos
//	 * <p>
//	 * 1 Variable de retorno
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Recorrido de los datos del docunento
//	 * <p>
//	 * &nbsp;&nbsp;3.1 Creamos el hashmap para un inventario
//	 * <p>
//	 * &nbsp;&nbsp;3.2 Agregamos ese inventario al hash map general de todos los
//	 * inventarios
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    private LinkedHashMap<String, String> parserInventarios(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HTTPREADER 1252 parserinventarios");
//		// 1 Variable de retorno:
////		LinkedHashMap<String, String>> matrizTodosInventarios = new LinkedHashMap<String, HashMap<String, String>>();
//		LinkedHashMap<String, String> matrizTodosInventarios = new LinkedHashMap<String,String>();
//		LinkedHashMap<String,String> matrizTodosInventariosDos = new LinkedHashMap<String,String>();
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			System.out.println("::: HTTPREADER 1266 xml "+xmlString);
//			inStream.setCharacterStream(new StringReader(xmlString));
////			System.out.println("::: HTTPREADER 1266 instream "+inStream);
////			SAXParserFactory spf = SAXParserFactory.newInstance();
////	            SAXParser sp = spf.newSAXParser();
////	            XMLReader xr = sp.getXMLReader();
////			 inStream.setCharacterStream(new StringReader(xmlString.toString()));
////	            Log.w("AndroidParseXMLActivity", "Parse2");
////	            xr.parse(inStream);
////	            Log.w("AndroidParseXMLActivity", "Parse3");
//			doc = db.parse(inStream);
////			System.out.println("::: HTTPREADER 1266 doc "+doc);
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 957 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 963 --]" + parConfExc.toString() + "__"
//					+ parConfExc.getMessage(), 4);
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 969 --]" + se.toString() + "__" + se.getMessage(), 4);
//			se.printStackTrace();
//			return null;
//		}
//		// 3 Recorrido de los datos del docunento
//		NodeList listaInventarios = doc
//				.getElementsByTagName(Parametros.bal_xml_inventario_root);
//
//		String tagNum = "";
//		String valTag = "";
//		String fechatag = "";
//		String descTag="";
//		String nombre = "";
//		String valor = "";
//		String todo = "";
//		String comparar = "";
//		int co = 0 ;
////		for (int i = 0; i < listaInventarios.getLength(); i++) {
////			Node inventario = listaInventarios.item(i);
////			NodeList datosInventario = inventario.getChildNodes();
////			for (int j = 0; j < datosInventario.getLength(); j++) {
////				Node dato = datosInventario.item(j);
////				NodeList datosInventarioTag = dato.getChildNodes();
////				for (int z = 0; z < datosInventarioTag.getLength(); z++) {
////					Node tag = datosInventarioTag.item(z);
////					if(tag.getNodeType()==Node.ELEMENT_NODE){
////					System.out.println("::: HttpReader NODEEEE "  + tag.getNodeName());
////					Node datoContenido = tag.getFirstChild();
////					valTag =tag.getNodeName();
////					if(datoContenido !=null && datoContenido.getNodeType()==Node.TEXT_NODE){
////						System.out.println("::: HttpReader Contenido nodo " + datoContenido.getNodeValue());
////						System.out.println("::: HttpReader Contenido valTAg " + valTag);
////						if(valTag.equals("N")){
////							tagNum=datoContenido.getNodeValue();
////							System.out.println("::::: HttpReader VALOR QUE NECESITOOOOO " + tagNum);
////						}
////					}
////				}
////				}
////			}
//			System.out.println("::: HttpReaderr 1360 " +listaInventarios);
//
//			/*Lo siguiente es para saber con que inventario se esta trabajando*/
//			boolean condicionRadio = ParametrosInventario.InventariosVentas;
//		int condR = 0;
//			if(condicionRadio == true){
//				condR=-1;
//
//			}else{
//				condR=-2;
//			}
//
//		for (int i = 0; i < listaInventarios.getLength(); i++) {
//			System.out.println("::: HttpReaderr 1363");
//
//			NodeList listaHijos = listaInventarios.item(i).getChildNodes();
//
//			HashMap<String, String> hashtmapUnInventario = new HashMap<String, String>();
//
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//
//				Node dato = listaHijos.item(j);
//				NodeList datosInventarioTag = dato.getChildNodes();
//
////				co++;
////				if(co==0 || (comparar!=tagNum)){
//
//					for (int z = 0; z < datosInventarioTag.getLength(); z++) {
//						nombre = datosInventarioTag.item(z).getNodeName();
//						valor = datosInventarioTag.item(z).getTextContent();
//
//						if(nombre.equals("N")){
//							tagNum=valor;
//						}
//						if(nombre.equals("D")){
//							descTag=valor;
//						}
//						if(nombre.equals("F")){
//							fechatag=valor;
//						}
//						if(nombre.equals("DEP")){
//							comparar=valor;
//						}
////						Log.e("nombre", nombre);
////						Log.e("valor", valor);
//
//
//					}
//					System.out.println("::: HttpReader 1398 nombre " + nombre + " valor " + valor);
//
//					if ( (!(tagNum.trim().length()==0)) && (!(descTag.trim().length()==0)) && (!(fechatag.trim().length()==0)) ) {
//
//									todo = descTag + " -" + fechatag;
////									todo = descTag + " -" + fechatag + " - D " + comparar;
//									int nn = Integer.parseInt(comparar);
//									System.out.println("::: HttpReader 1398 x entrar condR " + condR + " comparar " + nn);
//
//									if(condR==-1 && nn==0){
//										System.out.println("::: HttpReader 1398 ventas ");
//										hashtmapUnInventario.put(
//												ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
//														ParametrosInventario.tabla_inventarios), todo);
//
//										matrizTodosInventarios.put(tagNum,
//												todo);
//
//									}else if(condR==-2 && nn==1){
//										System.out.println("::: HttpReader 1398 deposito ");
//										hashtmapUnInventario.put(
//												ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
//														ParametrosInventario.tabla_inventarios), todo);
//
//										matrizTodosInventarios.put(tagNum,
//												todo);
//
//									}
////									hashtmapUnInventario.put(
////												ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,
////														ParametrosInventario.tabla_inventarios), todo);
////
////										matrizTodosInventarios.put(tagNum,
////												todo);
//								}
//			}
//
//			// 3.2 Agregamos ese inventario al hash map general de todos los
//			// inventarios
//
////			matrizTodosInventarios.put(hashtmapUnInventario
////					.get(ParametrosInventario.bal_bdd_inventario_numero),
////					hashtmapUnInventario);
////			System.out.println("MATRIZZZZZZZZ 3 :  " + matrizTodosInventarios);
//		}
//
//		System.out.println("::: HttpReader 1423 matriz " + matrizTodosInventarios);
//		return matrizTodosInventarios;
//	}
//
//	/**
//	 * Paresa la cadena XML pasada como parametros en la estructura de retorno
//	 * de detalles de inventarios
//	 * <p>
//	 * 1 Construccin de la estructura de respuesta
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Recorrido del arbol de los datos XML
//	 * <p>
//	 * &nbsp;&nbsp;3.1 Buscamos y guardamos los datos cada articlo
//	 * <p>
//	 * &nbsp;&nbsp;3.2 Si el id de inventario cambio, guardamos el LinkedHashMap
//	 * anterior con el numero de inventario y creamos uno nuevo
//	 * <p>
//	 * &nbsp;&nbsp;3.3 Obtenemos los codigos del articulo
//	 * <p>
//	 * &nbsp;&nbsp;3.4 Si no esta en la matriz lo metemos
//	 * <p>
//	 * &nbsp;&nbsp;3.5 Si esta le agregamos el codigo de barra nuevo
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    private LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> parserDetallesTodosInventarios(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HttpReader 1376");
//		// 1 Construccin de la estructura de respuesta:
//		LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>> matrizDetallesTodosInventarios;
//		matrizDetallesTodosInventarios = new LinkedHashMap<Integer, LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>>();
//
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			inStream.setCharacterStream(new StringReader(xmlString));
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1028 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1033 --]" + parConfExc.toString() + "__"
//					+ parConfExc.getMessage(), 4);
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1040 --]" + se.toString() + "__" + se.getMessage(), 4);
//			se.printStackTrace();
//			return null;
//		}
//		System.out.println("::: HttpReader 1504");
//		// 3 Recorrido del arbol de los datos XML:
//		NodeList listaArticulos = doc
//				.getElementsByTagName(Parametros.bal_xml_articulo_root);
//		int numeroInventarioActual = -1;
//		LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>> matrizTodosArticulosEsteInventario;
//		matrizTodosArticulosEsteInventario = new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>();
//
//		// int contador = 0;
//		System.out.println("::: HttpReader 1513");
//		for (int i = 0; i < listaArticulos.getLength(); i++) {
//			System.out.println("::: HttpReader 1515");
//			// 3.1 Buscamos y guardamos los datos cada articlo
//			NodeList listaHijos = listaArticulos.item(i).getChildNodes();
//			HashMap<String, String> hashmapArticulo = new HashMap<String, String>();
//
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//				System.out.println("::: HttpReader 1521");
//				String nombre = listaHijos.item(j).getNodeName();
//				String valor = listaHijos.item(j).getTextContent();
//				hashmapArticulo.put(ParametrosInventario.CONVERSOR_BALIZAS
//						.xml2bdd(nombre, ParametrosInventario.tabla_articulos),
//						valor);
//			}
//			System.out.println("::: HttpReader 1528");
//			// A ver si lo agregamos en el mismo LinkedHashMap o si lo ponemos
//			// en uno nuevo:
//			if (Integer.parseInt(hashmapArticulo
//					.get(ParametrosInventario.bal_bdd_articulo_inventario)) != numeroInventarioActual) {
//				System.out.println("::: HttpReader 1533");
//				// 3.2 Si el id de inventario cambio, guardamos el LinkedHashMap
//				// anterior con el numero de inventario y creamos uno nuevo:
//				if (numeroInventarioActual >= 0) {
//					System.out.println("::: HttpReader 1537");
//					matrizDetallesTodosInventarios.put(numeroInventarioActual,
//							matrizTodosArticulosEsteInventario);
//				}
//				numeroInventarioActual = Integer.parseInt(hashmapArticulo
//						.get(ParametrosInventario.bal_bdd_articulo_inventario));
//				matrizTodosArticulosEsteInventario = new LinkedHashMap<HashMap<String, Integer>, HashMap<String, String>>();
//			}
//			System.out.println("::: HttpReader 1545");
//			// 3.3 Obtenemos los codigos del articulo
//			HashMap<String, Integer> codigosArticulo = new HashMap<String, Integer>();
//			codigosArticulo
//					.put(ParametrosInventario.bal_bdd_articulo_sector,
//							Integer.parseInt(hashmapArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_sector)));
//			System.out.println("::: HttpReader 1552");
//			codigosArticulo
//					.put(ParametrosInventario.bal_bdd_articulo_codigo,
//							Integer.parseInt(hashmapArticulo
//									.get(ParametrosInventario.bal_bdd_articulo_codigo)));
//
//			// 3.4 Si no esta en la matriz lo metemos
//			if (matrizTodosArticulosEsteInventario.containsKey(codigosArticulo) == false) {
//				matrizTodosArticulosEsteInventario.put(codigosArticulo,
//						hashmapArticulo);
//			} else {
//				// 3.5 Si esta le agregamos el codigo de barra nuevo
//				String codbarNuevo = hashmapArticulo
//						.get(ParametrosInventario.bal_bdd_articulo_codigo_barra);
//				String codbarViejo = matrizTodosArticulosEsteInventario.get(
//						codigosArticulo).get(
//						ParametrosInventario.bal_bdd_articulo_codigo_barra);
//				matrizTodosArticulosEsteInventario.get(codigosArticulo).put(
//						ParametrosInventario.bal_bdd_articulo_codigo_barra,
//						codbarViejo + "," + codbarNuevo);
//			}
//		}
//		System.out.println("::: HttpReader 1574");
//		matrizDetallesTodosInventarios.put(numeroInventarioActual,
//				matrizTodosArticulosEsteInventario);
//		System.out.println("::: HttpReader 1577");
//		return matrizDetallesTodosInventarios;
//	}
//
//	/**
//	 * Paresa las referencias pasadas en formato XML String a un HashMap
//	 * <p>
//	 * 1 Construccin de la estructura de respuesta
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Recorrido del arbol de los datos XML
//	 * <p>
//	 * 4 Guardamos los datos de una referencia
//	 * <p>
//	 * 5 Agregamos esos datos a la matriz mayor
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    private static HashMap<String, HashMap<String, String>> parserReferencias(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HttpReader 1516");
//		Log.e("paso parsereferencias ", "paso por aca");
//		// 1 Construccin de la estructura de respuesta:
//		HashMap<String, HashMap<String, String>> matrizTodasReferencias;
//		matrizTodasReferencias = new HashMap<String, HashMap<String, String>>();
//
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			System.out.println("::: HttpReader 1516 xmlString " + xmlString);
//			inStream.setCharacterStream(new StringReader(xmlString));
////			System.out.println("::: HttpReader 1516 inStream " + inStream.getEncoding() );
////			System.out.println("::: HttpReader 1516 doc " + db.parse(inStream) );
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//			se.printStackTrace();
//			return null;
//		}
//		// 3 Recorrido del arbol de los datos XML:
//		NodeList listaReferencias = doc
//				.getElementsByTagName(Parametros.bal_xml_referencia_root);
//		System.out.println("::: HttpReader 1516 nodelist " + listaReferencias);
//		for (int i = 0; i < listaReferencias.getLength(); i++) {
//			NodeList listaHijos = listaReferencias.item(i).getChildNodes();
//			HashMap<String, String> hashtmapUnaReferencia = new HashMap<String, String>();
//			// 4 Guardamos los datos de una referencia
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//
//				String nombre = listaHijos.item(j).getNodeName();
//				System.out.println("::: HttpReader 1516 nombre " + nombre);
//				String valor = listaHijos.item(j).getTextContent();
//				System.out.println("::: HttpReader 1516 valor " + valor);
//
//				hashtmapUnaReferencia.put(ParametrosInventario.CONVERSOR_BALIZAS.xml2bdd(nombre,ParametrosInventario.tabla_referencias), valor);
//			}
//			// 5 Agregamos esos datos a la matriz mayor
//			matrizTodasReferencias.put(hashtmapUnaReferencia.get(ParametrosInventario.bal_bdd_referencia_codigo_barra) + i,hashtmapUnaReferencia);
//		}
//		System.out.println("::: HttpReader 1576 matriz " + matrizTodasReferencias);
//		return matrizTodasReferencias;
//	}
//
//
//	@Nullable
//    private static HashMap<String, String> parserConfiguraciones(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HttpReader 1516");
//		Log.e("paso parserConfig", "paso por aca");
//		// 1 Construccin de la estructura de respuesta:
//		HashMap<String, String> matrizTodasReferencias;
//		matrizTodasReferencias = new HashMap<String, String>();
//
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			System.out.println("::: HttpReader 1516 xmlString " + xmlString);
//			inStream.setCharacterStream(new StringReader(xmlString));
////			System.out.println("::: HttpReader 1516 inStream " + inStream.getEncoding() );
////			System.out.println("::: HttpReader 1516 doc " + db.parse(inStream) );
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//			se.printStackTrace();
//			return null;
//		}
//		// 3 Recorrido del arbol de los datos XML:
//		NodeList listaConfiguraciones = doc
//				.getElementsByTagName(Parametros.bal_xml_configuracion_root );
//		System.out.println("::: HttpReader 1516 nodelist " + listaConfiguraciones);
//		for (int i = 0; i < listaConfiguraciones.getLength(); i++) {
//			NodeList listaHijos = listaConfiguraciones.item(i).getChildNodes();
//			HashMap<String, String> hashtmapUnaReferencia = new HashMap<String, String>();
//			// 4 Guardamos los datos de una referencia
//			String variable = "";
//			String valor = "";
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//				String nodeName = listaHijos.item(j).getNodeName();
//				String nodeValue = listaHijos.item(j).getTextContent();
//				if(nodeName.equals("VARIABLE")) {
//					variable = nodeValue;
//				}else {
//					valor = nodeValue;
//				}
//			}
//			// 5 Agregamos esos datos a la matriz mayor
//			matrizTodasReferencias.put(variable, valor);
//		}
//		System.out.println("::: HttpReader 1576 matriz " + matrizTodasReferencias);
//		return matrizTodasReferencias;
//	}
//
//
//	/**
//	 * Paresa los proveedores pasados en formato XML String a un HashMap
//	 * <p>
//	 * 1 Construccin de la estructura de respuesta
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Recorrido del arbol de los datos XML
//	 * <p>
//	 * 4 Guardamos los datos de una referencia
//	 * <p>
//	 * 5 Agregamos esos datos a la matriz mayor
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	@Nullable
//    private static HashMap<String, HashMap<String, String>> parserProveedores(
//            @NonNull String xmlString) throws Exception {
//		System.out.println("::: HttpReader Proveedores 1900");
//		Log.e("paso proveedores ", "paso por aca");
//		// 1 Construccin de la estructura de respuesta:
//		HashMap<String, HashMap<String, String>> matrizTodosProveedores;
//		matrizTodosProveedores = new HashMap<String, HashMap<String, String>>();
//		// 2 Creacin de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			System.out.println("::: HttpReader 1914 xmlString " + xmlString);
//			inStream.setCharacterStream(new StringReader(xmlString));
//
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} catch (ParserConfigurationException parConfExc) {
//			parConfExc.printStackTrace();
//			return null;
//		} catch (SAXException se) {
//			se.printStackTrace();
//			return null;
//		}
//		// 3 Recorrido del arbol de los datos XML:
//		NodeList listaProveedores = doc
//				.getElementsByTagName(Parametros.bal_xml_proveedores_root);
//		System.out.println("::: HttpReader 1931 nodelist " + listaProveedores);
//		System.out.println("::: HttpReader 1931 nodelist " + listaProveedores.getLength());
//		for (int i = 0; i < listaProveedores.getLength(); i++) {
//			NodeList listaHijos = listaProveedores.item(i).getChildNodes();
//			HashMap<String, String> hashtmapUnProveedor = new HashMap<String, String>();
//			// 4 Guardamos los datos de una referencia
//			for (int j = 0; j < listaHijos.getLength(); j++) {
//
//				String nombre = listaHijos.item(j).getNodeName();
//				System.out.println("::: HttpReader 1939 nombre " + nombre);
//				String valor = listaHijos.item(j).getTextContent();
//				System.out.println("::: HttpReader 1941 valor " + valor);
//				// SE COMENTA LA SIGUIENTE LINEA POR QUE NO SE ENCUENTRA EL METODO xml2bddProv
//				//hashtmapUnProveedor.put(ParametrosInventario.CONVERSOR_BALIZAS.xml2bddProv(nombre,ParametrosInventario.tabla_proveedores), valor);
//			}
//			// 5 Agregamos esos datos a la matriz mayor
//			matrizTodosProveedores.put(hashtmapUnProveedor.get(ParametrosInventario.bal_bdd_proveedores_codigo) + i,hashtmapUnProveedor);
//		}
//		System.out.println("::: HttpReader 1948 matriz " + matrizTodosProveedores);
//		return matrizTodosProveedores;
//	}
//
//	/**
//	 * Paresa la cadena XML con la informacion de cantida de referencias a un
//	 * entero
//	 * <p>
//	 * 1 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Parsear
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	private static int parserCantidadReferencias(@NonNull String xmlString)
//			throws Exception {
//		System.out.println("::: HttpReader 1584");
//		// 1� Creaci�n de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//
//		// 2� Creaci�n de los elementos de trabajo con el archivo XML:
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			inStream.setCharacterStream(new StringReader(xmlString));
//			System.out.println("::: HTTPREADER 1598 xml "+xmlString);
//			System.out.println("::: HTTPREADER 1599 instream "+inStream);
////
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1180 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return 0;
//		} catch (ParserConfigurationException parConfExc) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1186 --]" + parConfExc.toString() + "__"
//					+ parConfExc.getMessage(), 4);
//			parConfExc.printStackTrace();
//			return 0;
//		} catch (SAXException se) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1192 --]" + se.toString() + "__" + se.getMessage(), 4);
//			se.printStackTrace();
//			return 0;
//		}
//
//		// 3� Parsear:
//		NodeList nudoCantidad = doc
//				.getElementsByTagName(Parametros.bal_xml_referencias_cantidad_total);
//		try {
//			Node noeud = nudoCantidad.item(0);
//			String resultado = noeud.getTextContent();
//			return Integer.parseInt(resultado);
//		} catch (Exception e) {
//			return 0;
//		}
//	}
//
//
//	/**
//	 * Paresa la cadena XML con la informacion de cantida de proveedores a un
//	 * entero
//	 * <p>
//	 * 1 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 2 Creacin de los elementos de trabajo con el archivo XML
//	 * <p>
//	 * 3 Parsear
//	 *
//	 * @param xmlString
//	 * @return
//	 * @throws Exception
//	 */
//	private static int parserCantidadProveedores(@NonNull String xmlString)
//			throws Exception {
//		System.out.println("::: HttpReader 1584 Proveedores");
//		// 1� Creaci�n de los elementos de trabajo con el archivo XML:
//		DocumentBuilderFactory factory = null;
//		DocumentBuilder db = null;
//		InputSource inStream = null;
//		Document doc = null;
//
//		// 2� Creaci�n de los elementos de trabajo con el archivo XML:
//		try {
//			factory = DocumentBuilderFactory.newInstance();
//			db = factory.newDocumentBuilder();
//			inStream = new InputSource();
//			inStream.setCharacterStream(new StringReader(xmlString));
//			System.out.println("::: HTTPREADER 1598 xml "+xmlString);
//			System.out.println("::: HTTPREADER 1599 instream "+inStream);
////
//			doc = db.parse(inStream);
//		} catch (IOException e) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1180 --]" + e.toString() + "__" + e.getMessage(), 4);
//			e.printStackTrace();
//			return 0;
//		} catch (ParserConfigurationException parConfExc) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1186 --]" + parConfExc.toString() + "__"
//					+ parConfExc.getMessage(), 4);
//			parConfExc.printStackTrace();
//			return 0;
//		} catch (SAXException se) {
//
//			log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
//			log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
//			log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
//			log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
//			log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
//			log.log("[-- 1192 --]" + se.toString() + "__" + se.getMessage(), 4);
//			se.printStackTrace();
//			return 0;
//		}
//
//		// 3� Parsear:
//		NodeList nudoCantidad = doc
//				.getElementsByTagName(Parametros.bal_xml_referencias_cantidad_total);
//		try {
//			Node noeud = nudoCantidad.item(0);
//			String resultado = noeud.getTextContent();
//			return Integer.parseInt(resultado);
//		} catch (Exception e) {
//			return 0;
//		}
//	}
//
//}
