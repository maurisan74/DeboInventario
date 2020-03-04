package com.focasoftware.deboinventario;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Clase que contiene una gran variedad de parametros comunes para todas las
 * aplicaciones que se utilizan para controlar el funcionamiento del sistema
 * correspondiente
 *
 * @author GuillermoR
 *
 */
public class Parametros {


	/**
	 * PARAMETROS A CARGAR AL ARRANQUE:
	 */
	public static String PREF_URL_CONEXION_SERVIDOR = "http://webservice.php";
	public static int Pref_id_Local = 0;
	public static String PREF_WIFI_PRIVILEGIADO = "FocaSF";
	public static String PREF_NUMERO_DE_TERMINAL = "0";
	public static String PREF_USB_IMPORT = "/udisk/";
	public static String PREF_USB_EXPORT = "/udisk/";
	public static String PREF_OPERADOR_ID = "-1";
	public static boolean PREF_CAMARA = false;
	public static String PREF_ULTIMA_FECHA_OPERACION = new SimpleDateFormat("yyyyMMdd").format(new Date());
	public static String PREF_CANT_FOTOS = "2";
	public static boolean PREF_LOG_EVENTOS = false;
	public static boolean PREF_LOG_PROCESOS = false;
	public static boolean PREF_LOG_MENSAJES = false;
	public static boolean PREF_LOG_EXCEPCIONES = false;
	public static boolean PREF_HAB_IMPRESION = true;
	public static String MAC_BLUETOOH = "";

	public static boolean PREF_STOCK = true;


	public final static String sdcard = Environment
			.getExternalStorageDirectory().toString();
	public final static String softDeboSancion = "/DeboSancion";
	public static String carpeta_sancion_log_tablet = sdcard + softDeboSancion + "/logTablet/";
	public static String carpeta_sancion_log_Datos = sdcard + softDeboSancion + "/logDatos/";

	public final static String preferencia_wifi = "WIFI_PREFERIDO";
	public final static String preferencia_idtablet = "ID_TABLET";
	public final static String preferencia_id_operador = "ID_OPERADOR";
	public final static String preferencia_fecha_ultima_op = "FECHA_ULT_OP";
	public final static String preferencia_servidor = "URL_SERVIDOR";
	public final static String preferencia_LOCAL = "ID_LOCAL_ACTUAL";
	public final static String preferencia_admin_log = "ADMIN_LOG";
	public final static String preferencia_admin_pwd = "ADMIN_PASS";
	public final static String preferencia_max_cant_fotos = "MAXIMA_FOTOS";
	public final static String preferencia_usb_uri_import = "USB_IMPORT";
	public final static String preferencia_usb_uri_export = "USB_EXPORT";
	public final static String preferencia_usb_uri_maestros = "MAESTROS";
	public final static String preferencia_usb_uri_log_eventos = "LOGEVENTOS";
	public final static String preferencia_usb_uri_log_datos = "LOGDATOS";
	public final static String preferencias_Camara = "camara";
	public final static String preferencias_logEvento = "logEvento";
	public final static String preferencias_logProcesos = "logProcesos";
	public final static String preferencias_logMensajes = "logMensajes";
	public final static String preferencias_logExcepciones = "logExcepciones";
	public final static String preferencias_habilitar_impresion = "preferenciaImpresion";
	public final static String preferencias_logTablet = "logTablet";
	public final static String preferencias_logDatos = "logDatos";
	public final static String preferencias_mac_bluetooth = "mac_bluetooth";
	public final static String preferencias_inventario_venta = "inventario_venta";
	public final static String preferencias_inventario_deposito = "inventario_deposito";
	public final static String habilitar_scanner_camara = "habilitar_scanner_camara";
	public final static String preferencia_lectura_entrada = "preferencia_lectura_entrada";
	public final static String preferencia_productos_no_contabilizados = "preferencia_productos_no_contabilizados";

	public final static String preferencia_inventario_venta = "preferencia_inventario_venta";

	public final static String preferencias_stockToma = "stockToma";

	public final static int ARCHIVO_TAMANO_MIN = 50; // en bytes
	public final static long TREINTA_DIAS_MILLISEC = new Long("2592000000");

	public final static String ADMIN_LOGIN_FOCA = "foca";
	public final static String ADMIN_PASSWORD_FOCA = "foca";

	public static String ADMIN_LOGIN = "foca";
	public static String ADMIN_PASSWORD = "foca";

	public final static int ID_RADIOBUTTON = 20000;
	public final static int ID_CHECKBOX = 30000;
	public final static int ID_EDITTEXT = 40000;

	public final static String extra_login = "LOGIN";
	public final static String extra_password = "PASSWORD";
	public final static String extra_uri_usb = "URI_USB";
	public final static String extra_usb_inventario = "USB_INVENTARIO";
	public final static String extra_lista_actualizaciones = "LISTA_ACTUALIZACIONES";
	public final static String extra_foto_uri = "FOTO_URI";

	public final static String codigo_soft = "a"; // define el software
	// (deboAgua, deboSancion,
	// deboInventario)
	public final static String codigo_fonc = "b"; // define el dato querido
	// (rutas, operadores,
	// sanciones, articulos,
	// etc.)
	public final static String codigo_tab = "c"; // define el número de ID de la
	// tablet quien trae los datos
	public final static String codigo_opc = "r"; // define los opciones
	// eventuales (numero de los
	// inventarios o rutas
	// deseadas)
	public final static String codigo_post = "p";
	public final static String codigo_foto = "i";
	public final static String codigo_text = "l";
	public final static String codigo_xfile = "x";

	public final static String CODIGO_FONC_EXPORT_DATOS = "11";
	public final static String CODIGO_FONC_EXPORT_FOTOS = "12";
	public final static String CODIGO_FONC_EXPORT_LOGS = "13";
	public final static String CODIGO_FONC_EXPORT_LIBERACION = "15";
	public final static String CODIGO_FONC_CONTROLAR_ADMIN = "21";
	public final static String CODIGO_FONC_RECOVERY = "99";

	public final static int FONCION_CONTROLAR_ADMIN = 21;

	public final static String CODIGO_SOFT_DEBOAGUA = "1";
	public final static String CODIGO_SOFT_DEBOSANCION = "2";
	public final static String CODIGO_SOFT_DEBOINVENTARIO = "3";
	public final static String CODIGO_SOFT_DEBORECOVERY = "0";
	public final static String CODIGO_SOFT_DEBOCOMPRA = "5";

	public final static String CODIGO_SOFT_DEBOPISCINA = "11";
	// public final static String CODIGO_SOFT_DEBO??? = "X";

	public final static int REQUEST_WIFI = 58;
	public final static int REQUEST_WIFI_IMPORT = 581;
	public final static int REQUEST_WIFI_EXPORT = 582;
	public final static int REQUEST_CAMBIO_ADMIN_LOGIN = 59;
	public final static int REQUEST_PREFERENCIAS = 57;
	public final static int REQUEST_USB = 56;
	public final static int REQUEST_FOTO = 55;


	// /////////////////////////////
	// BALIZAS USB //
	// /////////////////////////////
	//
	// <I/><D/><F/>
	// <A>
	// <S/><C/><CB/><N/><Q/>
	// </A>
	//
	/*
	 * public final static String bal_usb_madre = "USB"; public final static
	 * String bal_usb_articulo = "A"; public final static String
	 * bal_usb_cantidad = "Q"; public final static String bal_usb_codigo = "C";
	 * public final static String bal_usb_codigo_barra = "CB"; public final
	 * static String bal_usb_descripcion = "D"; public final static String
	 * bal_usb_fecha_creacion = "F"; public final static String
	 * bal_usb_inventario = "I"; public final static String bal_usb_nombre =
	 * "N"; public final static String bal_usb_precio_venta = "PV"; public final
	 * static String bal_usb_precio_costo = "PC"; public final static String
	 * bal_usb_sector = "S"; public final static String bal_usb_uri_foto = "UF";
	 */

	// //////////////////////////////
	// BALIZAS HTTP //
	// //////////////////////////////
	//
	/*
	 * public final static String bal_xml_madre = "USB"; public final static
	 * String bal_xml_articulo = "A"; public final static String
	 * bal_xml_cantidad = "Q"; public final static String bal_xml_codigo = "C";
	 * public final static String bal_xml_codigo_barra = "CB"; public final
	 * static String bal_xml_descripcion = "D"; public final static String
	 * bal_xml_fecha_creacion = "F"; public final static String
	 * bal_xml_inventario = "I"; public final static String bal_xml_nombre =
	 * "N"; public final static String bal_xml_precio_venta = "PV"; public final
	 * static String bal_xml_precio_costo = "PC"; public final static String
	 * bal_xml_sector = "S"; public final static String bal_xml_uri_foto = "UF";
	 */

	// WEBSERVICE HTTP - XML //
	/* Articulos: */
	public final static String bal_xml_articulo_root = "ART";

	public final static String bal_xml_articulo_sector = "S";
	public final static String bal_xml_articulo_codigo = "C";
	public final static String bal_xml_articulo_codigo_barra = "CB";
	public final static String bal_xml_articulo_inventario = "I";
	public final static String bal_xml_articulo_descripcion = "D";
	public final static String bal_xml_articulo_precio_venta = "PV";
	public final static String bal_xml_articulo_precio_costo = "PC";
	public final static String bal_xml_articulo_foto = "FO";
	public final static String bal_xml_articulo_cantidad = "Q";
	public final static String bal_xml_articulo_fechaInicio = "FI";
	public final static String bal_xml_articulo_fechaFin = "FF";
	public final static String bal_xml_articulo_existencia_venta = "EV";
	public final static String bal_xml_articulo_existencia_deposito = "ED";

	public final static String bal_xml_articulo_codigo_barra_completo = "CBC";

	public final static String bal_xml_articulo_balanza = "UV";
	public final static String bal_xml_articulo_decimales = "DE";
	/* Canastos: */
	public final static String bal_xml_canasto_root = "CAN";

	public final static String bal_xml_canasto_id = "ID";
	public final static String bal_xml_canasto_estado = "E";
	public final static String bal_xml_canasto_fecha = "F";
	public final static String bal_xml_canasto_tip = "TIP";
	public final static String bal_xml_canasto_tipo_comprobante = "TCO";
	public final static String bal_xml_canasto_sucursal = "S";
	public final static String bal_xml_canasto_num_comprobante = "NCO";
	public final static String bal_xml_canasto_proveedor = "PRO";
	public final static String bal_xml_canasto_operador = "O";
	public final static String bal_xml_canasto_forma_pago = "PAG";
	public final static String bal_xml_proveedores_root = "PROVEEDOR";

	/* Compras: */
	public final static String bal_xml_compra_root = "CMP";

	public final static String bal_xml_compra_codigo_barra = "CB";

	/*se crea para generar un nuevo campo de codigo de barra completo*/


	public final static String bal_xml_compra_descripcion = "D";
	public final static String bal_xml_compra_canasto = "C";
	public final static String bal_xml_compra_operador = "O";
	public final static String bal_xml_compra_cantidad = "Q";
	public final static String bal_xml_compra_fecha = "F";

	/* Entradas: */
	public final static String bal_xml_entrada_root = "ENT";

	public final static String bal_xml_entrada_fecha = "FE";
	public final static String bal_xml_entrada_ope_id = "I";
	public final static String bal_xml_entrada_ope_nombre = "NO";
	public final static String bal_xml_entrada_res_codope = "CO";
	public final static String bal_xml_entrada_res_documento = "DO";
	public final static String bal_xml_entrada_res_apellido = "A";
	public final static String bal_xml_entrada_res_nombre = "NR";
	public final static String bal_xml_entrada_res_estatus = "E";
	public final static String bal_xml_entrada_res_fecha = "FN";
	public final static String bal_xml_entrada_res_construccion = "CN";
	public final static String bal_xml_entrada_res_calle = "CA";
	public final static String bal_xml_entrada_res_altura = "AL";
	public final static String bal_xml_entrada_res_manzana = "M";
	public final static String bal_xml_entrada_res_lote = "L";
	public final static String bal_xml_entrada_res_piso = "P";
	public final static String bal_xml_entrada_res_dpto = "DP";
	public final static String bal_xml_entrada_res_telefono = "T";
	public final static String bal_xml_entrada_res_habilitado = "H";
	public final static String bal_xml_entrada_revisacion = "R";

	/* Inventarios: */
	public final static String bal_xml_inventario_root = "VI_INVENTARIOS";

	public final static String bal_xml_inventario_numero = "I";
	public final static String bal_xml_inventario_descripcion = "D";
	public final static String bal_xml_inventario_fechaInicio = "FI";
	public final static String bal_xml_inventario_fechaFin = "FF";
	public final static String bal_xml_inventario_cantidad = "C";
	public final static String bal_xml_inventario_lugar = "L";
	public final static String bal_xml_inventario_prodcont = "PC";
	public final static String bal_usb_inventario_prodcont = "PC";
	public final static String bal_xml_compra_proveedores_root = "COMPRA_PROVEEDORES";
	public final static String bal_usb_proveedores_root = "PROVEEDOR";
	public final static String bal_usb_proveedores_codigo = "COD";
	public final static String bal_usb_proveedores_descripcion = "DESC";
	public final static String bal_xml_proveedores_codigo = "COD";
	public final static String bal_xml_proveedores_descripcion = "DESC";
	public final static String bal_usb_compra_proveedores_root = "COMPRA_PROVEEDORES";
	public final static String bal_usb_compra_proveedores_codigo = "COD_PROV";
	public final static String bal_usb_compra_proveedores_inv = "INV";
	public final static String bal_xml_compra_proveedores_codigo = "COD_PROV";
	public final static String bal_xml_compra_proveedores_inv = "INV";

	/* Operadores: */
	public final static String bal_xml_operador_root = "OPE";

	public final static String bal_xml_operador_id = "ID";
	public final static String bal_xml_operador_nombre = "N";
	public final static String bal_xml_operador_contrasena = "C";
	public final static String bal_xml_operador_derecho_compra = "D";
	public final static String bal_xml_operador_habilitado = "H";

	/* Proveedores: */
	public final static String bal_xml_proveedor_root = "PRO";

	public final static String bal_xml_proveedor_cuit = "C";
	public final static String bal_xml_proveedor_nombre = "N";
	public final static String bal_xml_proveedor_telefono = "T";

	/* Referencias: */
	public final static String bal_xml_referencia_root = "REF";
	public final static String bal_xml_referencias_cantidad_total = "CNT_VALIDOS";

	/*
	*****public final static String bal_xml_referencias_exisventa = "REF_ExiVen";
	public final static String bal_xml_referencias_exisdeposito = "REF_ExiDep";
*/
	/* Residentes: */
	public final static String bal_xml_residente_root = "RES";
	public final static String bal_xml_residente_cantidad_total = "CAN";

	public final static String bal_xml_residente_codope = "COPE";
	public final static String bal_xml_residente_documento = "DOC";
	public final static String bal_xml_residente_apellido = "APE";
	public final static String bal_xml_residente_nombre = "NOM";
	public final static String bal_xml_residente_estatus = "EST";
	public final static String bal_xml_residente_fecha = "FEC";
	public final static String bal_xml_residente_construccion = "CTR";
	public final static String bal_xml_residente_calle = "CAL";
	public final static String bal_xml_residente_altura = "ALT";
	public final static String bal_xml_residente_manzana = "MZN";
	public final static String bal_xml_residente_lote = "LOT";
	public final static String bal_xml_residente_piso = "PIS";
	public final static String bal_xml_residente_dpto = "DPT";
	public final static String bal_xml_residente_telefono = "TEL";
	public final static String bal_xml_residente_habilitado = "HAB";
	public final static String bal_xml_residente_tipo = "TIP";
	public final static String bal_xml_residente_zona = "ZON";

	/* Revisaciones */
	public final static String bal_xml_revisacion_root = "REV";

	public final static String bal_xml_revisacion_fecha = "FEC";
	public final static String bal_xml_revisacion_residente_doc = "R_D";
	public final static String bal_xml_revisacion_vencimiento = "VEC";
	public final static String bal_xml_revisacion_medico = "MED";
	public final static String bal_xml_revisacion_operador = "OPE";
	public final static String bal_xml_revisacion_observacion = "OBS";

	// USB //
	/* Articulos: */
	public final static String bal_usb_articulo_root = "ART";

	public final static String bal_usb_articulo_sector = "S";
	public final static String bal_usb_articulo_codigo = "C";

	/*Trae los valores de los decimales y del pesaje*/
	public final static String bal_usb_articulo_balanza = "UV";
	public final static String bal_usb_articulo_decimales = "DE";
	public final static String bal_usb_articulo_codigo_barra_completo = "CBC";

	public final static String bal_usb_articulo_codigo_barra = "CB";
	public final static String bal_usb_articulo_inventario = "I";
	public final static String bal_usb_articulo_descripcion = "D";
	public final static String bal_usb_articulo_precio_venta = "PV";
	public final static String bal_usb_articulo_precio_costo = "PC";
	public final static String bal_usb_articulo_foto = "UF";
	public final static String bal_usb_articulo_cantidad = "Q";
	public final static String bal_usb_articulo_fechaInicio = "FI";
	public final static String bal_usb_articulo_fechaFin = "FF";
	public final static String bal_usb_articulo_existencia_venta = "EV";
	public final static String bal_usb_articulo_existencia_deposito = "ED";

	/* Inventarios: */
	public final static String bal_usb_inventario_root = "INVENTARIO";

	public final static String bal_usb_inventario_numero = "I";
	public final static String bal_usb_inventario_descripcion = "D";
	public final static String bal_usb_inventario_fechaInicio = "FI";
	public final static String bal_usb_inventario_fechaFin = "FF";
	public final static String bal_usb_inventario_cantidad = "C";
	public final static String bal_usb_inventario_lugar = "L";

	/* Canastos: */
	public final static String bal_usb_canasto_root = "CAN";

	public final static String bal_usb_canasto_id = "ID";
	public final static String bal_usb_canasto_estado = "EST";
	public final static String bal_usb_canasto_fecha = "FEC";
	public final static String bal_usb_canasto_tip = "TIP";
	public final static String bal_usb_canasto_tipo_comprobante = "TCO";
	public final static String bal_usb_canasto_sucursal = "SUC";
	public final static String bal_usb_canasto_num_comprobante = "NCO";
	public final static String bal_usb_canasto_proveedor = "PRO";
	public final static String bal_usb_canasto_operador = "OPE";
	public final static String bal_usb_canasto_forma_pago = "PAG";

	/* Compras: */
	public final static String bal_usb_compra_root = "CMP";

	public final static String bal_usb_compra_codigo_barra = "CB";
	public final static String bal_usb_compra_codigo_barra_completo = "CBC";
	public final static String bal_usb_compra_descripcion = "D";
	public final static String bal_usb_compra_canasto = "C";
	public final static String bal_usb_compra_operador = "O";
	public final static String bal_usb_compra_cantidad = "Q";
	public final static String bal_usb_compra_fecha = "F";

	/* Entradas: */
	public final static String bal_usb_entrada_root = "ENT";

	public final static String bal_usb_entrada_fecha = "FE";
	public final static String bal_usb_entrada_ope_id = "I";
	public final static String bal_usb_entrada_ope_nombre = "NO";
	public final static String bal_usb_entrada_res_codope = "CO";
	public final static String bal_usb_entrada_res_documento = "DO";
	public final static String bal_usb_entrada_res_apellido = "A";
	public final static String bal_usb_entrada_res_nombre = "NR";
	public final static String bal_usb_entrada_res_estatus = "E";
	public final static String bal_usb_entrada_res_fecha = "FN";
	public final static String bal_usb_entrada_res_construccion = "CN";
	public final static String bal_usb_entrada_res_calle = "CA";
	public final static String bal_usb_entrada_res_altura = "AL";
	public final static String bal_usb_entrada_res_manzana = "M";
	public final static String bal_usb_entrada_res_lote = "L";
	public final static String bal_usb_entrada_res_piso = "P";
	public final static String bal_usb_entrada_res_dpto = "DP";
	public final static String bal_usb_entrada_res_telefono = "T";
	public final static String bal_usb_entrada_res_habilitado = "H";
	public final static String bal_usb_entrada_revisacion = "R";

	/* Operadores: */
	public final static String bal_usb_operador_root = "OPE";

	public final static String bal_usb_operador_id = "I";
	public final static String bal_usb_operador_nombre = "N";
	public final static String bal_usb_operador_contrasena = "C";
	public final static String bal_usb_operador_derecho_compra = "D";
	public final static String bal_usb_operador_habilitado = "H";

	/* Referencias: */
	public final static String bal_usb_referencia_root = "REF";

	/* Residentes: */
	public final static String bal_usb_residente_root = "RES";

	public final static String bal_usb_residente_codope = "CO";
	public final static String bal_usb_residente_documento = "DO";
	public final static String bal_usb_residente_apellido = "A";
	public final static String bal_usb_residente_nombre = "N";
	public final static String bal_usb_residente_estatus = "E";
	public final static String bal_usb_residente_fecha = "F";
	public final static String bal_usb_residente_construccion = "CN";
	public final static String bal_usb_residente_calle = "CA";
	public final static String bal_usb_residente_altura = "AL";
	public final static String bal_usb_residente_manzana = "M";
	public final static String bal_usb_residente_lote = "L";
	public final static String bal_usb_residente_piso = "P";
	public final static String bal_usb_residente_dpto = "DP";
	public final static String bal_usb_residente_telefono = "T";
	public final static String bal_usb_residente_habilitado = "H";
	public final static String bal_usb_residente_tipo = "TI";
	public final static String bal_usb_residente_zona = "ZO";

	/* Revisaciones */
	public final static String bal_usb_revisacion_root = "REV";

	public final static String bal_usb_revisacion_fecha = "F";
	public final static String bal_usb_revisacion_residente_doc = "RD";
	public final static String bal_usb_revisacion_vencimiento = "V";
	public final static String bal_usb_revisacion_medico = "M";
	public final static String bal_usb_revisacion_operador = "OP";
	public final static String bal_usb_revisacion_observacion = "OB";
	public final static String CODIGO_FONC_EXPORT_COMPRAS = "17";
	public final static String bal_xml_articulo_subtotal = "ST";
	public final static String bal_usb_articulo_subtotal = "ST"; // se agrega subtotal
	public final static String bal_xml_articulo_depsn = "DEPSN";
	public final static String bal_usb_articulo_depsn = "DEPSN";
	public final static String preferencias_balanza = "balanza";
	public static boolean PREF_BAL = true;
	public final static String CODIGO_FONC_CONFIGURACIONES = "18";
	public final static String bal_xml_configuracion_root = "SETTING";
}
