package com.focasoftware.deboinventario;

import android.os.Environment;

import java.io.File;

/**
 * Clase donde se almacenan los parmetros generales de la aplicacin. Contiene
 * en su mayora campos estticos para poder acceder a ellos desde toda la
 * aplicacin.
 *
 * @author GuillermoR
 *
 */
public class ParametrosInventario {
	/**
	 * Parametro de control para el modo debug IMPORTANTE SU ESTADO
	 */
	public static boolean MODO_DEBUG = false;
	public static boolean PREF_CAMARA = false;

	public static String ID_TABLET = "99";

	public final static String no_disponible = "N/D";
	public final static String tablet_mostrar_existencia = "tablet_mostrar_existencia";
	public static boolean mostrar_existencia = true;

	public static final String intent_codigo = "INTENT_CODIGO";
	public final static String foto_uri = "FOTO_URI";
	public final static String foto_calidad = "FOTO_CALIDAD";
	public final static int PREF_MAX_COMPRA_ABIERTAS = 10;
	public final static int MAX_SQL_RESPUESTAS = 200;

	public final static int TAMANO_MAX_CANTIDAD = 8;

	public final static int TAMANO_PAQUETES_XML = 400;

	public final static int INVENTARIO_ABIERTO = 1;
	public final static int INVENTARIO_CERRADO = 0;

	public static final float TALLA_TEXTO = 16;

	public static final int REQUEST_CODIGO_BARRA = 310;
	public static final int REQUEST_FOTO = 320;
	public static final int REQUEST_INVENTARIO = 330;
	public static final int REQUEST_INVENTARIO_DINAMICO = 340;
	public static final int REQUEST_INVENTARIO_COMPRAS = 345;
	public static final int REQUEST_DETALLES_ART = 350;

	public static final int filtro_sector = 7001;
	public static final int filtro_inventario = 7002;
	public static final int filtro_precio = 7003;

	public static final String preferencias_camara_scanner = "preferencias_camara_scanner";

	public static final String preferencias_stock_alatoma = "preferencias_stock_alatoma";

	public static final String URL_CARPETA_FOTOS = "/data/data/com.focasoftware.deboinventario/fotos/";
	public static final String URL_CARPETA_USB = "/data/data/com.focasoftware.deboinventario/usb/";
	public static final String URL_CARPETA_USB_EXPORT = "/data/data//com.focasoftware.deboinventario/usb/export/";
	public static final String URL_CARPETA_USB_IMPORT = "/data/data/com.focasoftware.deboinventario/usb/import/";
	public static final String URL_CARPETA_DATABASES = "/data/data/com.focasoftware.deboinventario/databases/";

	public final static String URL_ARCHIVO_LOG = "/data/data/com.focasoftware.deboinventario/log.txt";
	public final static String URL_COPIA_XML_EXPORT = "/data/data/com.focasoftware.deboinventario/deboInventarioExport.xml";
	public final static String URI_USB = "/data/data/com.focasoftware.deboinventario/test/";

	public static File SdCard = Environment.getExternalStorageDirectory();
	public static String Stringsdcard = SdCard.toString();
	public static String CARPETA_DEBOINVENTARIO = Stringsdcard + "/deboInventario/";
	public static String CARPETA_ATABLET = Stringsdcard + "/deboInventario/aTablet/";
	public static String CARPETA_DESDETABLET = Stringsdcard + "/deboInventario/desdeTablet/";
	public static String CARPETA_MAETABLET = Stringsdcard + "/deboInventario/maeTablet/";
	public static String CARPETA_LOGTABLET = Stringsdcard + "/deboInventario/logTablet/";
	public static String CARPETA_LOGDATOS = Stringsdcard + "/deboInventario/logDatos/";

	public static boolean InventariosVentas = true;
	public static boolean InventariosDeposito = false;
	public static boolean CamHabScanner = true;
	public static boolean LecturaEntrada = false;
	public static int ProductosNoContabilizados = 1;
	public static boolean StockToma = true;

	public static boolean balanza = true;

	public static int InventarioVentas = 1;

	public static int radVen = 1;

	public static int radDep = 2;

	public static int StockalaToma = 1;

	public static String PREF_IMPORT = CARPETA_ATABLET;
	public static String PREF_USB_EXPORT = CARPETA_DESDETABLET;
	public static String Dispositivo_Import = "Dispositivo";
	public static String Dispositivo_Export = "Dispositivo";

	// public final static String PREF_USB_IMPORT_MAESTRO =
	// "/udisk/deboInventario/maeTablet/";
	// public final static String PREF_FLASH_IMPORT_MAESTRO =
	// "/flash/deboInventario/maeTablet/";
	// public final static String PREF_SDCARD_IMPORT_MAESTRO =
	// CARPETA_MAETABLET;
	// public final static String UNIDAD_ALMACENAMIENTO_FLASH = "/flash";
	// public final static String UNIDAD_ALMACENAMIENTO_UDISK = "/udisk";
	public final static String UNIDAD_ALMACENAMIENTO_SDCARD = Stringsdcard;
	public final static String PREF_USB_IMPORT_MAESTRO_NOMBRE = "maestro.xml";

	public final static int BDD_VERSION = 20;
	public final static String VERSION = BDD_VERSION + ".0.15";
	public final static String BDD_NOMBRE = "DB_INVENT";

	public static final String elementos_seleccionados = "INVENTARIOS_SELECCIONADOS";

	// Codigos de fonciones:
	public final static int FONCION_CARGAR_INVENTARIOS = 31;
	public final static int FONCION_CARGAR_ARTICULOS = 32;
	public final static int FONCION_CARGAR_REFERENCIAS = 37;
	public final static int FONCION_CARGAR_REFERENCIAS_POR_PARTES = 38;
	public final static int FONCION_CARGAR_FOTO = 33;
	// public final static int FONCION_CARGAR_TODOS_OPERADORES = 11;
	// public final static int FONCION_CARGAR_DETALLES_RUTA = 12;

	// BUNDLES Y EXTRAS:
	public final static String extra_numeroInventario = "NUMERO_INVENTARIO";
	public final static String extra_numeroInventarioDinVta = "NUMERO_INVENTARIO_DINAMICO_VENTA";
	public final static String extra_numeroInventarioDinDepo = "NUMERO_INVENTARIO_DINAMICO_DEPOSITO";
	public final static String extra_numeroInventarioCompra = "NUMERO_INVENTARIO_DINAMICO_COMPRA";
	public final static String extra_bandera_invs_dinamicos = "CARGA_NUEVOS_INVS_DINAMICOS";
	public final static String extra_listaArticulos = "LISTA_ARTICULOS";
	public final static String extra_codigo = "ART_CODIGO";
	public final static String extra_sector = "ART_SECTOR";
	public final static String extra_codBar = "ART_COD_BAR";
	public final static String extra_inventario = "ART_I";

	public final static int extra_valor_bandera_invs_dinamicos_si = 1;
	public final static int extra_valor_bandera_invs_dinamicos_no = 0;

	// Codigos de ID:
	// public final static int ID_CURSORS = 11000;
	public final static int ID_BOTONES = 12000;
	public final static int ID_CHECKBOXES = 13000;
	public final static int ID_LINEAS = 14000;
	public final static int ID_LINEA_DINAMICO = -1;

	// ID inventarios dinmicos
	public final static int ID_INV_DIN_VTA = -1;
	public final static int ID_INV_DIN_DEP = -2;
	public final static int ID_INV_COMPRAS = -3;

	public final static int COD_LUGAR_INVENTARIO_VENTA = 0;
	public final static int COD_LUGAR_INVENTARIO_DEPO = 1;

	// PREFERENCIAS:
	public final static String preferencias_en_curso = "EN_CURSO";

	// CLAVES PARA HASHMAP:
	public final static int clave_art_sector = 1;
	public final static int clave_art_codigo = 2;
	public final static int clave_art_nombre = 3;


	// CLAVES PARA HASHMAP Proveedores:
	public final static int clave_prov_cod = 1;
	public final static int clave_prov_desc = 2;

	// /////////////////////////
	// BALIZAS //
	// /////////////////////////

	/* GLOBAL: */
	public final static String bal_xml_export_cabecera = "TABLET_EXPORT_INVENTARIO";

	/* BASE DE DATOS: */
	/* Articulos: */
	public final static String tabla_articulos = "ARTICULOS";

	public final static String bal_bdd_articulo_sector = "ART_SEC";
	public final static String bal_bdd_articulo_codigo = "ART_COD";
	public final static String bal_bdd_articulo_balanza = "ART_BAL";
	public final static String bal_bdd_articulo_decimales = "ART_DE";
	public final static String bal_bdd_articulo_codigo_barra = "ART_CB";
	public final static String bal_bdd_articulo_codigo_barra_completo = "ART_CBC";
	public final static String bal_bdd_articulo_inventario = "ART_I";
	public final static String bal_bdd_articulo_descripcion = "ART_DESC";
	public final static String bal_bdd_articulo_precio_venta = "ART_PRE_VTA";
	public final static String bal_bdd_articulo_precio_costo = "ART_PRE_COS";
	public final static String bal_bdd_articulo_foto = "ART_FOTO";
	public final static String bal_bdd_articulo_cantidad = "ART_Q";
	public final static String bal_bdd_articulo_subtotal = "ART_SUBTOT";

	public final static String bal_xml_export_fec_ope = "FEC_OPE";

	public final static String bal_bdd_articulo_pesaje = "ART_P";

	public final static String bal_bdd_articulo_fechaInicio = "ART_FEI";
	public final static String bal_bdd_articulo_fechaFin = "ART_FEF";

	public final static String bal_bdd_articulo_existencia_venta = "ART_EV";
	public final static String bal_bdd_articulo_existencia_deposito = "ART_ED";
	public final static String bal_bdd_articulo_depsn = "ART_DEPSN";


	/* Inventarios: */
	public final static String tabla_inventarios = "INVENTARIOS";

	public final static String bal_bdd_inventario_numero = "INV_NUM";
	public final static String bal_bdd_inventario_prodcont = "INV_PRODCONT";

//	public final static String bal_bdd_inventario_dep = "INV_DEP";

	public final static String bal_bdd_inventario_descripcion = "INV_DESC";
	public final static String bal_bdd_inventario_fechaInicio = "INV_FEI";
	public final static String bal_bdd_inventario_fechaFin = "INV_FEF";
	public final static String bal_bdd_inventario_estado = "INV_EST";
	public final static String bal_bdd_inventario_cantidad = "INV_CAN";
	public final static String bal_bdd_inventario_lugar = "INV_LUG";
	public final static String bal_bdd_inventario_clase = "INV_CLA";

	/* Proveedores: */
	public final static String tabla_proveedores = "PROVEEDORES";
	public final static String tabla_compra_proveedor = "COMPRA_PROVEEDOR";

	public final static String bal_bdd_proveedores_codigo = "PROV_COD";
	public final static String bal_bdd_proveedores_descripcion = "PROV_DESC";
	public final static String bal_bdd_compraproveedor_inventario = "COMPRA_INV_COD";
	public final static String bal_bdd_compraproveedor_codigo = "COMPRA_PROVE_COD";

	/* Articulos: */
	public final static String tabla_referencias = "REFERENCIAS";

	/*Referencias para el codigo de barra completo, decimales, y valor de unidad/balanza */
	public final static String bal_bdd_referencia_decimales = "REF_DE";
	public final static String bal_bdd_referencia_codigo_barra_completo = "REF_CBC";
	public final static String bal_bdd_referencia_balanza = "REF_BAL";
	public final static String bal_bdd_referencia_sector = "REF_SEC";
	public final static String bal_bdd_referencia_codigo = "REF_COD";
	public final static String bal_bdd_referencia_codigo_barra = "REF_CB";
	public final static String bal_bdd_referencia_descripcion = "REF_DESC";
	public final static String bal_bdd_referencia_precio_venta = "REF_PRE_VTA";
	public final static String bal_bdd_referencia_precio_costo = "REF_PRE_COS";
	public final static String bal_bdd_referencia_foto = "REF_FOTO";
	public final static String bal_bdd_referencia_cantidad = "REF_Q";
	public final static String bal_bdd_referencia_existencia_venta = "REF_EV";
	public final static String bal_bdd_referencia_existencia_deposito = "REF_ED";
	public final static String bal_bdd_referencia_depsn = "REF_DEPSN";

	public final static String tabla_local = "LOCAL";

	public final static String bal_bdd_local_idLocal = "id_local";
	public final static String bal_bdd_local_nombre = "nombre";
	public final static String bal_bdd_local_descripcion = "descripcion";

	public static BalizasConversor CONVERSOR_BALIZAS = new BalizasConversor();
	static {

		CONVERSOR_BALIZAS.put(tabla_inventarios,
				Parametros.bal_xml_inventario_root,
				Parametros.bal_usb_inventario_root);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_numero,
				Parametros.bal_xml_inventario_numero,
				Parametros.bal_usb_inventario_numero);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_prodcont,
				Parametros.bal_xml_inventario_prodcont,
				Parametros.bal_usb_inventario_prodcont);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_descripcion,
				Parametros.bal_xml_inventario_descripcion,
				Parametros.bal_usb_inventario_descripcion);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_fechaInicio,
				Parametros.bal_xml_inventario_fechaInicio,
				Parametros.bal_usb_inventario_fechaInicio);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_fechaFin,
				Parametros.bal_xml_inventario_fechaFin,
				Parametros.bal_usb_inventario_fechaFin);
		CONVERSOR_BALIZAS.put(bal_bdd_inventario_lugar,
				Parametros.bal_xml_inventario_lugar,
				Parametros.bal_usb_inventario_lugar);

		CONVERSOR_BALIZAS.put(bal_bdd_articulo_balanza,
				Parametros.bal_xml_articulo_balanza,
				Parametros.bal_usb_articulo_balanza);

		CONVERSOR_BALIZAS.put(bal_bdd_articulo_decimales,
				Parametros.bal_xml_articulo_decimales,
				Parametros.bal_usb_articulo_decimales);

		CONVERSOR_BALIZAS.put(bal_bdd_articulo_codigo_barra,
				Parametros.bal_xml_articulo_codigo_barra,
				Parametros.bal_usb_articulo_codigo_barra);


		CONVERSOR_BALIZAS.put(tabla_articulos,
				Parametros.bal_xml_articulo_root,
				Parametros.bal_usb_articulo_root);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_sector,
				Parametros.bal_xml_articulo_sector,
				Parametros.bal_usb_articulo_sector);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_codigo,
				Parametros.bal_xml_articulo_codigo,
				Parametros.bal_usb_articulo_codigo);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_codigo_barra,
				Parametros.bal_xml_articulo_codigo_barra,
				Parametros.bal_usb_articulo_codigo_barra);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_inventario,
				Parametros.bal_xml_articulo_inventario,
				Parametros.bal_usb_articulo_inventario);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_descripcion,
				Parametros.bal_xml_articulo_descripcion,
				Parametros.bal_usb_articulo_descripcion);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_precio_venta,
				Parametros.bal_xml_articulo_precio_venta,
				Parametros.bal_usb_articulo_precio_venta);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_precio_costo,
				Parametros.bal_xml_articulo_precio_costo,
				Parametros.bal_usb_articulo_precio_costo);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_foto,
				Parametros.bal_xml_articulo_foto,
				Parametros.bal_usb_articulo_foto);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_cantidad,
				Parametros.bal_xml_articulo_cantidad,
				Parametros.bal_usb_articulo_cantidad);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_subtotal,
				Parametros.bal_xml_articulo_subtotal,
				Parametros.bal_usb_articulo_subtotal);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_fechaInicio,
				Parametros.bal_xml_articulo_fechaInicio,
				Parametros.bal_usb_articulo_fechaInicio);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_fechaFin,
				Parametros.bal_xml_articulo_fechaFin,
				Parametros.bal_usb_articulo_fechaFin);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_existencia_venta,
				Parametros.bal_xml_articulo_existencia_venta,
				Parametros.bal_usb_articulo_existencia_venta);
		CONVERSOR_BALIZAS.put(bal_bdd_articulo_existencia_deposito,
				Parametros.bal_xml_articulo_existencia_deposito,
				Parametros.bal_usb_articulo_existencia_deposito);


		CONVERSOR_BALIZAS.put(tabla_referencias,
				Parametros.bal_xml_referencia_root,
				Parametros.bal_usb_referencia_root);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_sector,
				Parametros.bal_xml_articulo_sector,
				Parametros.bal_usb_articulo_sector);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_codigo,
				Parametros.bal_xml_articulo_codigo,
				Parametros.bal_usb_articulo_codigo);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_codigo_barra,
				Parametros.bal_xml_articulo_codigo_barra,
				Parametros.bal_usb_articulo_codigo_barra);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_descripcion,
				Parametros.bal_xml_articulo_descripcion,
				Parametros.bal_usb_articulo_descripcion);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_precio_venta,
				Parametros.bal_xml_articulo_precio_venta,
				Parametros.bal_usb_articulo_precio_venta);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_precio_costo,
				Parametros.bal_xml_articulo_precio_costo,
				Parametros.bal_usb_articulo_precio_costo);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_foto,
				Parametros.bal_xml_articulo_foto,
				Parametros.bal_usb_articulo_foto);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_cantidad,
				Parametros.bal_xml_articulo_cantidad,
				Parametros.bal_usb_articulo_cantidad);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_existencia_venta,
				Parametros.bal_xml_articulo_existencia_venta,
				Parametros.bal_usb_articulo_existencia_venta);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_existencia_deposito,
				Parametros.bal_xml_articulo_existencia_deposito,
				Parametros.bal_usb_articulo_existencia_deposito);
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_depsn,
				Parametros.bal_xml_articulo_depsn,
				Parametros.bal_usb_articulo_depsn);
		/*Balizas para el pesaje y la cantidad de decimales*/
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_decimales,
				Parametros.bal_xml_articulo_decimales,
				Parametros.bal_usb_articulo_decimales);

		CONVERSOR_BALIZAS.put(bal_bdd_referencia_balanza,
				Parametros.bal_xml_articulo_balanza,
				Parametros.bal_usb_articulo_balanza);

		/*Se crea campo nuevo codigo barra completo*/
		CONVERSOR_BALIZAS.put(bal_bdd_referencia_codigo_barra_completo,
				Parametros.bal_xml_articulo_codigo_barra_completo,
				Parametros.bal_usb_articulo_codigo_barra_completo);

		/*    ----------------        */
		CONVERSOR_BALIZAS.put(tabla_proveedores,
				Parametros.bal_xml_proveedores_root,
				Parametros.bal_usb_proveedores_root);
		CONVERSOR_BALIZAS.put(bal_bdd_proveedores_codigo,
				Parametros.bal_xml_proveedores_codigo,
				Parametros.bal_usb_proveedores_codigo);
		CONVERSOR_BALIZAS.put(bal_bdd_proveedores_descripcion,
				Parametros.bal_xml_proveedores_descripcion,
				Parametros.bal_usb_proveedores_descripcion);

		CONVERSOR_BALIZAS.put(tabla_compra_proveedor,
				Parametros.bal_xml_compra_proveedores_root,
				Parametros.bal_usb_compra_proveedores_root);
		CONVERSOR_BALIZAS.put(bal_bdd_compraproveedor_codigo,
				Parametros.bal_xml_compra_proveedores_codigo,
				Parametros.bal_usb_compra_proveedores_codigo );
		CONVERSOR_BALIZAS.put(bal_bdd_compraproveedor_inventario,
				Parametros.bal_xml_compra_proveedores_inv,
				Parametros.bal_usb_compra_proveedores_inv);

	}


	// COMUNICACION HTTP:
	public final static String CODIGO_FONC_INVENTARIOS = "1";
	public final static String CODIGO_FONC_ARTICULOS = "3";
	public final static String CODIGO_FONC_FOTO = "4";
	public final static String CODIGO_FONC_REFERENCIAS_CANTIDAD = "5";
	public final static String CODIGO_FONC_REFERENCIAS = "7";
	public final static String CODIGO_FONC_REFERENCIAS_POR_PARTES = "8";
	public final static String CODIGO_FONC_EXPORT_DATOS = "11";
	public final static String CODIGO_FONC_EXPORT_FOTOS = "12";
	public final static String CODIGO_FONC_EXPORT_LOG = "13";
	public final static String CODIGO_FONC_PROVEEDORES_CANTIDAD = "16";
	//public final static String CODIGO_FONC_EXPORT_COMPRAS = "17";

	public final static int RETURN_ART_ELIM = 10;

	public final static int RETURN_ART_ELIM_FALLO = 20;

}