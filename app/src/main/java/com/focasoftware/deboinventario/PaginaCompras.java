package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/*
 * Created by Usuario on 14/05/2016.
 */
public class PaginaCompras  extends Activity implements DialogPersoSimple {
    /*
     * Cantidad de lineas que tiene la tabla que muestra
     */
    private final static int NUMERO_LINEAS_EN_TABLA = 8;
    private static final int SCAN_BARCODE = 0;
    // TOUCH
    private int event_triggered = -1;
    private float y_delta = 0;
    private float y_inicial = 0;
    private float UMBRAL_DESPLAZAMIENTO_MINIMO = 10;
    @NonNull
    private Context ctxt = this;
    private BaseDatos bdd;
    private Intent intentPadre;
    private int parametroPrimeraSeleccion = 0;
    float corregir;
    String corregirDecimales;
    /*
     * Numero de inventario que estamos viendo
     */
    private int inventario_numero_en_curso;
    /*
     * Articulos que tiene el inventario
     */
    @Nullable
    private ArrayList<ArticuloVisible> listaArticulosCompleta;
    /*
     * Objeto que almacena el inventario en curso
     */
    @Nullable
    private Inventario inventarioEnCurso;
    /*
     * ArrayList que contiene las tablas de los codigos de barras. El orden se
     * preserva como a la insersin. Esta tabla se tiene que ordenar al igual
     * que la listaArticulosCompleta.
     */
    private ArrayList<ArrayList<String>> listaCodigosDeBarrasOrdenados;
    @NonNull
    private ArrayList<Integer> listaSectoresFiltrados = new ArrayList<Integer>();
    @NonNull
    private ArrayList<Integer> copia_listaSectoresFiltrados = new ArrayList<Integer>();
    private double precioFiltro = (double) 0;
    private double copia_precioFiltro = 0;
    private Dialog dialogo;
    /**
     * Variable de filtro : +1 => articulos ya inventariados // 0 => no ver nada
     * // -1 => articulos no inventariados
     */
    private int inventarioFiltro = 0;
    private int copia_inventarioFiltro = 0;
    private int modo_mas_1 = 0;
    /*
     * Elementos de la UI
     */
    private Button boton_salir;
    private ImageView boton_busqueda, boton_lectora;
    private TextView textview_titulo;
    private TextView encabezado_codigo, encabezado_descripcion,
            encabezado_precio_venta, encabezado_cantidad,encabezado_accion;
    private ImageView filtro_codigo, filtro_precio, filtro_cantidad;
    private ImageView flecha_arriba, flecha_abajo;

    private LinearLayout asensor_layout;
    private TableLayout tabla_articulos;
    private ProgressBar loadingBar;
    @Nullable
    private EditText editTT = null;
    @Nullable
    private EditText edittextBusqueda = null;
    // Cosas del dinamico
    private TextView textVFechaInicio;
    private TextView textVCantArticulos;
    private TextView textVMensaje;
    private RadioButton radioBVenta;
    private RadioButton radioBDepo;
    private TextView textVentas;
    private TextView textDeposito;
    private AlertDialog alert;
    private DialogPersoComplexCantidadMasMenos dialogoMasMenos;
    @Nullable
    private DialogPersoComplexCantidadModificacion dialogoModificacion;
    private DialogPersoComplexBusqueda dialogoBusqueda;
    private boolean fueCanceladoDialogoBusqueda = false;
    @Nullable
    private DialogPersoComplexResultados dialogoResultados;
    private boolean fueCanceladoDialogoResultados = false;
    private int respuestaSeleccionada = -99;
    private DialogPersoComplexEditTextOkCancel dialogoNombreArticuloNuevo;

    private int columna_ordonante = -1;
    @NonNull
    private String bufferLectoraCB = "";
    @Nullable
    private HashMap<Integer, Integer> articulo_resultado_busqueda = null;
    /*
     * Indice al cual apunta la primera linea de la tabla visual de los 8
     * articulos. Puede ser diferente de la linea del articulo enfocado.
     */
    private int indice_primera_linea = 0;
    /*
     * Indice de la tablaArticulosCompleta donde se encuentra el articulo
     * enfocado.
     */
    private int indice_on_focus = -1;
    private int numero_articulos_visibles = 0;
    private boolean dedoEnContacto = false;
    private float dedoInicialX, dedoFinalX, dedoInicialY, dedoFinalY;
    private int altura_linea_asensor = 300;
    private int numero_lineas_asensor_text = 0;
    // private float umbralUnitario = (float)10.0;
    private float valor_antes_modificar = -1;
    /*Damian variables*/
    private int columna_codigo = 0;
    private int columna_descripcion = 1;
    private int columna_precio = 2;
    private int columna_cantidad = 3;
    //private int columna_existencia = 4;
    private int columna_secreta = 7;
    // PArametros del mensaje
    private final int MENSAJE_NO_ACTIVO_MAS_1 = 0;
    private final int MENSAJE_ACTIVO_MAS_1 = 1;
    private ImageView Ic_Lectora_Din;
    /**
     * Dialogs de mensajes
     */
    private ProgressDialog aguarde;
    private Dialog dialogAguarde;
    private EditText TextcodigoBarras;
    private int numeroInventario;
    public CheckBox checkBoxT;
    public RadioButton radioVen;
    public RadioButton radioDep;
    public RadioButton valorRadio;
    private AlertDialog.Builder dialogoFin;
    /*
     * Considera la creacion de inventarios dinamicos de venta y deposito
     * 1 Seteamos el inventario en curso a -1 por que siempre empieza mostrando
     * el inventario de venta
     * 2 Recuperamos la lista de todos los datos que mostrar en pantalla:
     * 3 Si no hay nada que mostrar avisamos
     * 4 Iniciamos la UI
     * 5 Cargar los HANDLERS:
     * 6 Refrescar encabezado
     * 7 Refrescar mensaje inferior
     * 8 Aviso por si no sabe que hacer para que lea con CB
     * 9 Carga los articulos en la tabla
     */
    @NonNull
    GestorLogEventos log = new GestorLogEventos();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBoxT=(CheckBox)findViewById(R.id.checkBoxToma);
  //    boolean condicionRadio = ParametrosInventario.InventariosVentas;
        int condR = -3;
  //    boolean condicionb = ParametrosInventario.StockToma;
  //    boolean condicionBalanza = ParametrosInventario.balanza;
  //    int condi = -3;
        setContentView(R.layout.xml_pagina_compras);
        PreferenciasInventario.cargarPreferencias(ctxt);
        log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
        log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
        log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
        log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
        log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
        log.log("[-- 223 --]" + "Inicica Inventario Compras", 2);
        // BUNDLES:
        intentPadre = getIntent();
        Bundle bundle = getIntent().getExtras();
        // 1 Seteamos el inventario en curso a -1 por que siempre empieza
        // mostrando el inventario de venta
        if (bundle != null) {
            inventario_numero_en_curso = bundle
                    .getInt(ParametrosInventario.extra_numeroInventarioCompra);
        } else {
            inventario_numero_en_curso = -3;
        }

        this.numeroInventario = inventario_numero_en_curso;

        // BASE DE DATOS:
        bdd = new BaseDatos(ctxt);

        // 2 Recuperamos la lista de todos los datos que mostrar en pantalla:
        try {
            inventarioEnCurso = bdd.selectInventarioConNumero(inventario_numero_en_curso);
            listaArticulosCompleta = bdd.selectArticulosConNumeroInventario(inventario_numero_en_curso);
        } catch (ExceptionBDD e) {

            log.log("[-- 246 --]" + e.toString(), 4);
            e.printStackTrace();
            showSimpleDialogOK("Error", e.toString()).show();
        }

        // 3 Si no hay nada que mostrar avisamos
        if (listaArticulosCompleta.size() <= 0 && inventario_numero_en_curso > 0) {
            showSimpleDialogSiNo("ERROR", "El inventario no contiene ningun articulo\n\n Desea continuar?", ComprasMainBoard.class).show();
        }

        // 4 Iniciamos la UI
        // Recuperamos los elementos de la parte grfica:

        Ic_Lectora_Din = (ImageView) findViewById(R.id.Ic_Lectora_Din);

        textview_titulo = (TextView) findViewById(R.id.PID_tit_pagina);
        encabezado_codigo = (TextView) findViewById(R.id.PID_encabezado_codigo);
        encabezado_descripcion = (TextView) findViewById(R.id.PID_encabezado_descripcion);
        encabezado_precio_venta = (TextView) findViewById(R.id.PID_encabezado_precio);
        encabezado_cantidad = (TextView) findViewById(R.id.PID_encabezado_cantidad);
        encabezado_accion = (TextView) findViewById(R.id.PID_accion_eliminar);

        filtro_cantidad = (ImageView) findViewById(R.id.PID_encab_cantidad);
        filtro_codigo = (ImageView) findViewById(R.id.PID_encab_codigo);
        filtro_precio = (ImageView) findViewById(R.id.PID_encab_precio);

        flecha_abajo = (ImageView) findViewById(R.id.PID_boton_down);
        flecha_arriba = (ImageView) findViewById(R.id.PID_boton_up);

        boton_salir = (Button) findViewById(R.id.PID_boton_salir);
        boton_busqueda = (ImageView) findViewById(R.id.PID_boton_busqueda);
        boton_lectora = (ImageView) findViewById(R.id.PID_boton_lectora);

        asensor_layout = (LinearLayout) findViewById(R.id.PID_layout_asensor);
        tabla_articulos = (TableLayout) findViewById(R.id.PID_tabla_articulos);

        loadingBar = (ProgressBar) findViewById(R.id.PID_loadingbar);

        textVCantArticulos = (TextView) findViewById(R.id.PID_textViewCantArticulos);
        textVFechaInicio = (TextView) findViewById(R.id.PID_textViewFechaInicio);
        textVMensaje = (TextView) findViewById(R.id.PID_textViewMensajeInforme);

        radioBVenta = (RadioButton) findViewById(R.id.PID_radioVenta);
        radioBDepo = (RadioButton) findViewById(R.id.PID_radioDepo);

        textVentas = (TextView) findViewById(R.id.seleccion_vta);
        textDeposito = (TextView) findViewById(R.id.seleccion_dep);

        TextcodigoBarras = (EditText) findViewById(R.id.TextcodigoBarras);
        if (ParametrosInventario.LecturaEntrada) {
            TextcodigoBarras.setVisibility(View.VISIBLE);
        } else {
            TextcodigoBarras.setVisibility(View.GONE);
        }

        if (!ParametrosInventario.InventariosVentas) {
            radioBVenta.setVisibility(View.INVISIBLE);
            textVentas.setVisibility(View.INVISIBLE);
            radioBDepo.setVisibility(View.INVISIBLE);
            textDeposito.setVisibility(View.INVISIBLE);
        }
        if (!ParametrosInventario.InventariosDeposito) {
            radioBVenta.setVisibility(View.INVISIBLE);
            textVentas.setVisibility(View.INVISIBLE);
            radioBDepo.setVisibility(View.INVISIBLE);
            textDeposito.setVisibility(View.INVISIBLE);
        }

        System.out.println("::: PaginaCompras radioBVenta="+radioBVenta);
        System.out.println("::: PaginaCompras radioBDepo="+radioBDepo);

        loadingBar.setVisibility(View.GONE);

        // 5 Cargar los HANDLERS:

        cargar_handlers();
        // 6 Refrescar encabezado
        refrescarEncabezado(inventario_numero_en_curso);
        System.out.println("::: PaginaCompras 531 salio refrescarencabezado 8");
        // 7 Refrescar mensaje inferior
        refrescarMensaje();

        log.log("[-- 307 --]"
                + "Lea un articulo con la lectora de codigo de barra para modificar"
                + " su cantidad en el inventario", 3);

        // Esto no esta funcionando? habria que ver cuando son muchos articulos
        aguarde = ProgressDialog.show(ctxt, "Aguarde",
                "Aguarde que se carguen los articulos");

        // 9 Carga los articulos en la tabla
        cargarArticulosEnTabla();

        aguarde.dismiss();

        if (!ParametrosInventario.CamHabScanner) {
            Ic_Lectora_Din.setVisibility(View.INVISIBLE);
        }

        // Verificacin de configuracin de ariculos no contabilizados
        if (ParametrosInventario.ProductosNoContabilizados == 2) {
            // 8 Aviso por si no sabe que hacer
            Toast.makeText(
                    ctxt,
                    "Atencion: los productos no contabilizados se guardaran como: Cantidad cero. Si desea cambiarlo hagalo desde los parametros.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Funcion para refrescar los textView de cantidad y fecha
     *
     * @param inventario_numero
     */
    private void refrescarEncabezado(int inventario_numero) {
        System.out.println("::: PagInvDin 436 RefrescarEncabezado");
        int cantArticulos = listaArticulosCompleta.size();
        textVCantArticulos.setText("Cantidad de Articulos: " + cantArticulos);
        textVFechaInicio.setText("Fecha Inicio: "
                + inventarioEnCurso.getFechaInicio());
        System.out.println("::: entro a refrescar encabezado");
    }

    /**
     * Funcion para refrescar el mensaje inferior
     *
     * * @param codigoMensaje
     */
    private void refrescarMensaje() {
        System.out.println("::: PaginaInventarioDinamico RefrescarMensaje");
        String msj = "Esperando codigo de barra.";
        if (modo_mas_1 == 1) {

            log.log("[-- 342 --]"
                            + "Esperando Codigo de Barra. Activo escaneo por articulo (+1)",
                    3);
            msj = "Esperando Codigo de Barra. Activo escaneo por articulo (+1)";
        } else {

            log.log("[-- 346 --]"
                            + "Esperando Codigo de Barra. NO activo escaneo por articulo (+1)",
                    3);
            msj = "Esperando Codigo de Barra. NO activo escaneo por articulo (+1)";
        }

        textVMensaje.setText(msj);
        textVMensaje.setVisibility(View.VISIBLE);
    }

    /**
     * Actualiza toda la informacion del inventario, articulos y deberia
     * refrescar la pantalla acorde a esta info
     * <p>
     * 1 Seteamos el nro de inventario en curso
     * <p>
     * 2 Lo buscamos en la BD
     * <p>
     * 3 Cargamos sus articulos en la lista
     * <p>
     * 4 Verificamos si no hay nada que mostrar
     * <p>
     * 5 Refrescar encabezado
     * <p>
     * 6 Refrescar mensaje inferior
     * <p>
     * 7 Refrescamos la tabla central
     *
     * @param nroInventario
     */
    private void actualizarPaginaInventario(int nroInventario) {
// Buscar el nuevo inventario y Actualizar la lista de productos Recuperamos la lista de todos los datos que mostrar en pantalla:
// Seteamos el nro de inventario en curso
        inventario_numero_en_curso = nroInventario;
        listaArticulosCompleta = null;
        try {
            inventarioEnCurso = bdd.selectInventarioConNumero(inventario_numero_en_curso);
            listaArticulosCompleta = bdd.selectArticulosConNumeroInventario(inventario_numero_en_curso);
        } catch (ExceptionBDD e) {
            log.log("[-- 389 --]" + e.toString(), 4);
            e.printStackTrace();
            showSimpleDialogOK("Error", e.toString()).show();
        }
        // Verificamos si no hay nada que mostrar
        if (listaArticulosCompleta.size() <= 0
                && inventario_numero_en_curso > 0) {
            showSimpleDialogSiNo(
                    "ERROR",
                    "El inventario no contiene ningun articulo\n\n Desea continuar?",
                    ComprasMainBoard.class).show();
        }

        // Actualizar Interfaz grafica
        // Refrescar encabezado
        refrescarEncabezado(inventario_numero_en_curso);
        // 6 Refrescar mensaje inferior
        refrescarMensaje();
        aguarde = ProgressDialog.show(ctxt, "Aguarde",
                "Aguarde que se carguen los articulos");
        // 7 Refrescamos la tabla central
        refreshTablaCentral();
        aguarde.dismiss();
    }

    private void cargarArticulosEnTabla() {
        int con = 0;
        try {
            indice_primera_linea = 0;
            int paridad = 0; // numeroLinea % 2
            listaCodigosDeBarrasOrdenados = new ArrayList<ArrayList<String>>();

            numero_articulos_visibles = 0;

            // 1 Construimos la tabla - Linea por linea:
            for (int numLinea = 0; numLinea < NUMERO_LINEAS_EN_TABLA; numLinea++) {
                con = numLinea;
//
                // 1.1 Recuperamos la linea y la cargamos con el layout
                // inflater:
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // ViewFlipper vf = (ViewFlipper) findViewById(idflipper);
                TableRow linea = (TableRow) inflater.inflate(
                        R.layout.z_lineaarticulo_inventario_compras, null);
//                TableRow linea = (TableRow) inflater.inflate(
//                        R.layout.z_lineaarticulo_inventario_dinamico, null);

                tabla_articulos.addView(linea);

                ArticuloVisible a = null;
                Referencia r = null;
                try {
                    a = listaArticulosCompleta.get(numLinea);
                } catch (Exception e) {
                    a = new ArticuloVisible(false);
                    linea.setVisibility(View.INVISIBLE);
                }
                // 1.3 Configuramos los elementos (4 casillas) de la linea:
                TextView casilla_codigo = (TextView) linea
                        .findViewById(R.id.ZLIN_codigo);
                TextView casilla_descripcion = (TextView) linea
                        .findViewById(R.id.ZLIN_descripcion);
                TextView casilla_precio = (TextView) linea
                        .findViewById(R.id.ZLIN_precio);
                TextView casilla_exisventa = (TextView) linea
                        .findViewById(R.id.ZLIN_existente);
                EditText casilla_subtotal = (EditText) linea
                        .findViewById(R.id.ZLIN_subtotal);
                EditText casilla_cantidad = (EditText) linea
                        .findViewById(R.id.ZLIN_cantidad);
                ImageView casilla_accion = (ImageView) linea
                        .findViewById(R.id.id_quitar);
                TextView casilla_secreta = (TextView) linea
                        .findViewById(R.id.ZLIN_secreto);

                // 1.4 Calculo de la cantidad de elementos visibles:
                if (a.esVisible() == true) {
                    numero_articulos_visibles++;
                }

                // 1.5 Configuramos los valores de casillas:
				/* Primera casilla: CODIGO del ARTICULO */
                if (a.getSector() >= 0) {
                    casilla_codigo.setText(String.valueOf(a.getSector()) + "-"
                            + String.valueOf(a.getCodigo()));
                } else {
                    casilla_codigo.setText("Nvo. "
                            + String.valueOf(Math.abs(a.getSector())));
                }
				/* Segunda casilla: DESCRIPCION del ARTICULO */
                casilla_descripcion.setText(a.getDescripcion());
				/* Tercera casilla: PRECIO DE VENTA del ARTICULO */
                casilla_precio
                        .setText("$ "
                                + new DecimalFormat("0.00").format(a
                                .getPrecio_venta()));

				/* Cuarta casilla: CANTIDAD del ARTICULO */
                System.out.println("////////////GET CANTIDAD///////////" + a.getCantidad());
                System.out.println("////////////GET SUBTOTAL///////////" + a.getSubtotal());
                if (a.getSubtotal() >= 0) {
                    casilla_subtotal.setText(String.valueOf(a.getSubtotal()));
                } else {
                    casilla_subtotal.setText("0");
                }


                if (a.getCantidad() >= 0) {
                    casilla_cantidad.setText(String.valueOf(a.getCantidad()));
                } else {
                    casilla_cantidad.setText("No Tomado");
                }

                if (ParametrosInventario.InventariosVentas == true) {
                    casilla_exisventa.setText(String.valueOf(a.getExis_venta()));
                }else{
                    casilla_exisventa.setText(String.valueOf(a.getExis_deposito()));
                }
				/* Quinta casilla: INDICE ARTICULO */
                casilla_secreta.setText(String.valueOf(numLinea));
                // Colorear la linea:
                //colorearLinea(linea, paridad);

                // 1.6 Pegamos LISTENERS a la linea.
                casilla_cantidad.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("::: PaginaInvDinamico 684 PASO Dinamico 1 ingresa al onclick");
                        bufferLectoraCB = "";
                        if (indice_on_focus >= 0) {
                            deseleccionarLineaParticular(indice_on_focus);
                        } else if (indice_on_focus < 0) {
                            EditText editT = (EditText) v;
                            TableRow tableR = (TableRow) editT.getParent();
                            TextView textV = (TextView) tableR.getChildAt(7);
                            int indice_articulo = Integer.parseInt(String
                                    .valueOf(textV.getText()));
                            seleccionarMostrarIndiceArticulo(indice_articulo);
                        }
                    }
                });

                casilla_subtotal.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("::: Click Subtotal" + indice_on_focus);
                        bufferLectoraCB = "";
                        if (indice_on_focus >= 0) {
                            deseleccionarLineaParticular_subtotal(indice_on_focus);
                        } else if (indice_on_focus < 0) {
                            EditText editT = (EditText) v;
                            TableRow tableR = (TableRow) editT.getParent();
                            TextView textV = (TextView) tableR.getChildAt(7);
                            int indice_articulo = Integer.parseInt(String
                                    .valueOf(textV.getText()));
                            System.out.println("::: INDICE ARTICULO" + indice_articulo);
                            seleccionarMostrarIndiceArticulo_subtotal(indice_articulo);
                        }
                    }
                });

                casilla_accion.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("::: PaginaCompras 684 1 ingresa al onclick DE CASILLA ACCION");
                        bufferLectoraCB = "";
                        if (indice_on_focus >= 0) {
                            deseleccionarLineaParticular(indice_on_focus);
                        } else if (indice_on_focus < 0) {
                            ImageView imagV = (ImageView) v;
                            TableRow tableR = (TableRow) imagV.getParent();
                            TextView textV = (TextView) tableR.getChildAt(7);
                            int indice_articulo = Integer.parseInt(String
                                    .valueOf(textV.getText()));
                            seleccionarMostrarIndiceArticuloBorrar(indice_articulo,inventario_numero_en_curso);
                            deseleccionarLineaParticularBorrar(indice_on_focus);
                        System.out.println("::: PaginaCompras 572 PaginaCompras CASILLA ACCION indice_articulo " + indice_articulo);
                        }
                    }
                });

                // Pegamos LISTENERS a la linea.
                // Touch <=> grag and drop:
                linea.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            event_triggered = GestionarioClicks.TOUCH_TRIGGERED;
                            y_delta = 0;
                            y_inicial = event.getRawY();
                            // Log.v("on move","Y INICIAL = " + y_inicial);
                            return false;
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE
                                && indice_on_focus < 0
                                && (event_triggered == GestionarioClicks.TOUCH_TRIGGERED || event_triggered == GestionarioClicks.SLIP_TRIGGERED)) {
                            if (Math.abs(event.getRawY() - y_inicial) < 5) {
                                return true;
                            }
                            event_triggered = GestionarioClicks.SLIP_TRIGGERED;
                            float y_actual = event.getRawY();
                            y_delta += -1 * (y_actual - y_inicial);
                            y_inicial = y_actual;
                            // Obtener el signo de y_delta y el movimiento de
                            // lineas:
                            int y_move;
                            if (y_delta >= 0) {
                                y_move = (int) Math.floor((float) y_delta
                                        / (float) UMBRAL_DESPLAZAMIENTO_MINIMO);
                                y_delta = Math.abs(y_delta)
                                        % UMBRAL_DESPLAZAMIENTO_MINIMO;
                            } else {
                                y_move = (int) Math.ceil((float) y_delta
                                        / (float) UMBRAL_DESPLAZAMIENTO_MINIMO);
                                y_delta = -1
                                        * (Math.abs(y_delta) % UMBRAL_DESPLAZAMIENTO_MINIMO);
                            }
                            // Log.v("on move","mouvement = " + y_move);
                            moverTablaArticulos(y_move);

                            SystemClock.sleep(20);
                            return true;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (event_triggered == GestionarioClicks.TOUCH_TRIGGERED) {
                                // Si el evento de soltar el boton ocurre sin
                                // desplazamiento, es un click:
                                event_triggered = GestionarioClicks.CLICK_TRIGGERED;
                                bufferLectoraCB = "";
                                if (indice_on_focus >= 0) {
                                    deseleccionarLineaParticular(indice_on_focus);
                                } else if (indice_on_focus < 0) {
                                    seleccionarMostrarIndiceArticulo(Integer.parseInt(String.valueOf(((TextView) ((TableRow) v)
                                            .getChildAt(columna_secreta))
                                            .getText())));
                                }
                            } else {
                                event_triggered = GestionarioClicks.CLICK_TRIGGERED;
                            }
                            event_triggered = -1;
                            y_delta = 0;
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                // Long Click <=> pide para suprimir el articulo:
                linea.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(@NonNull View linea) {
                        // Si otro evento se dispar antes del long click, no se
                        // procesa el LongClick:
                        if (event_triggered != GestionarioClicks.TOUCH_TRIGGERED) {
                            return true;
                        } else {
                            event_triggered = GestionarioClicks.LONGCLICK_TRIGGERED;
                        }
                        // Sino:
                        bufferLectoraCB = "";
                        int indice_articulo_apuntado = Integer.parseInt(String.valueOf(((TextView) ((TableRow) linea)
                                .getChildAt(columna_secreta)).getText()));
                        ArticuloVisible a = listaArticulosCompleta
                                .get(indice_articulo_apuntado);

                        Intent intentDetallesArticulo = new Intent(
                                PaginaCompras.this,
                                DetallesArticuloDinamico.class)
                                .putExtra(ParametrosInventario.extra_sector,
                                        a.getSector())
                                .putExtra(ParametrosInventario.extra_codigo,
                                        a.getCodigo())
                                .putExtra(
                                        ParametrosInventario.extra_inventario,
                                        inventario_numero_en_curso);
                        startActivityForResult(intentDetallesArticulo,
                                ParametrosInventario.REQUEST_DETALLES_ART);
                        return true;
                    }
                });

                // 1.7 Modificamos la lista de codigos de barras, agregando los
                // CB del articulo en la linea
                listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());

                paridad = (paridad + 1) % 2;
            } // end for

            // 2 Seguimos las otras listas fuera de la tabla visible de 8
            // lineas:
            for (int i2 = NUMERO_LINEAS_EN_TABLA; i2 < listaArticulosCompleta
                    .size(); i2++) {
                con = i2;
                ArticuloVisible a = listaArticulosCompleta.get(i2);
                // Calculo de la cantidad de elementos visibles:
                if (a.esVisible() == true) {
                    numero_articulos_visibles++;
                }
                listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());

            }

        } catch (Exception e) {

            log.log("[-- 712 --]" + e.toString(), 4);
            e.printStackTrace();
            loadingBar.setVisibility(View.GONE);
            showSimpleDialogOK(
                    "ERROR de carga",
                    e.toString()
                            + "\n\nUn error occurio en la carga del presente inventario, articulo "
                            + String.valueOf(con)
                            + ".\n\n Por favor, quite y reintente carga los datos.")
                    .show();
        }
    }

    private void refreshTablaCentral() {
        try {
            // Parametros:
            int paridad;
            // Cargamos la barra de carga:
            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.bringToFront();
            // 1 Actualizamos la lista de los codigos de barras:
            listaCodigosDeBarrasOrdenados = new ArrayList<ArrayList<String>>();
            numero_articulos_visibles = 0;
            for (int i = 0; i < listaArticulosCompleta.size(); i++) {
                ArticuloVisible a = listaArticulosCompleta.get(i);
                listaCodigosDeBarrasOrdenados.add(a.getCodigos_barras());
                if (a.esVisible() == true) {
                    numero_articulos_visibles++;
                }
            }
            // 2 Modificamos la tabla visual de 8 lineas (recuperamos los
            // proximos indices desde el actual)
            ArrayList<Integer> listaProximosIndex = null;
            // if (listaArticulosCompleta.size() > 8) {
            if (indice_on_focus < 0) {
                listaProximosIndex = getProximosIndicesArticulosVisibles(
                        indice_primera_linea, false);
            } else {
                listaProximosIndex = getProximosIndicesArticulosVisibles(
                        indice_on_focus, true);
            }
            paridad = (listaProximosIndex.get(0)) % 2;
/*
* Comienzo de lo que carga los articulos
* 
* */
            // 3 Para cada indice proximo
            for (int index : listaProximosIndex) {
                // 3.1 Recuperamos el id de cada TableRow de cada linea, y la
                // linea asociada:
                TableRow linea = (TableRow) tabla_articulos
                        .getChildAt(listaProximosIndex.indexOf((int) index));
                linea.setVisibility(View.VISIBLE);
                // 3.2 Modificamos los datos:
                TextView casilla_codigo = (TextView) linea.getChildAt(0);
                TextView casilla_descripcion = (TextView) linea.getChildAt(1);
                TextView casilla_precio = (TextView) linea.getChildAt(2);
                EditText casilla_cantidad = (EditText) linea.getChildAt(3);
				/*Damian 09/05*/
                EditText casilla_subtotal = (EditText) linea.getChildAt(4);
                TextView casilla_exisventa = (TextView) linea.getChildAt(5);
                ImageView casilla_accion = (ImageView) linea.getChildAt(6);
                TextView casilla_secreta = (TextView) linea.getChildAt(7);
                // 3.3 Recuperamos los detalles del articulo que va a figurar en
                // esta linea:
                ArticuloVisible a = listaArticulosCompleta.get(index);
                // 3.4 Configuramos los valores de casillas:
				/* Primera casilla: CODIGO del ARTICULO */
                if (a.getSector() >= 0) {
                    casilla_codigo.setText(String.valueOf(a.getSector()) + "-"
                            + String.valueOf(a.getCodigo()));
                } else {
                    casilla_codigo.setText("Nvo. "
                            + String.valueOf(Math.abs(a.getSector())));
                }
				/* Segunda casilla: DESCRIPCION del ARTICULO */
                casilla_descripcion.setText(a.getDescripcion());
				/* Tercera casilla: PRECIO DE VENTA del ARTICULO */
                casilla_precio
                        .setText("$ "
                                + new DecimalFormat("0.00").format(a
                                .getPrecio_venta()));
				/* Cuarta casilla: CANTIDAD del ARTICULO */
                boolean condicionBalanza = ParametrosInventario.balanza;

                if(condicionBalanza == true){

                    String longitudA = String.valueOf(a.getCodigos_barras_completo());
                    if(longitudA.length()<4){
                        if(a.getBalanza()==8 && a.getCantidad()== -1.0 && a.getDecimales()!=3){
                            System.out.println(":: PID Opc 1");
                            //	corregir = Float.parseFloat(a.getCodigos_barras_completo().get(0).substring(7,12));
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()==8 && a.getCantidad()!= -1.0 && a.getDecimales()!=3){
                            System.out.println(":: PID Opc 2");
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()!=8 ){
                            System.out.println(":: PID Opc 3");
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()==8 && a.getCantidad()== -1.0 && a.getDecimales()==3){
                            System.out.println(":: PID Opc 4");
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()==8 && a.getCantidad()!= -1.0 && a.getDecimales()==3){
                            System.out.println(":: PID Opc 5");
                            corregir = a.getCantidad();
                        }
                    }else{
                        if(a.getBalanza()==8 && a.getCantidad()== -1.0 && a.getDecimales()!=3){
                            System.out.println(":: PID Opc 6");
                            corregir = Float.parseFloat(a.getCodigos_barras_completo().get(0).substring(7,12));
                        }else if(a.getBalanza()==8 && a.getCantidad()!= -1.0 && a.getDecimales()!=3){
                            System.out.println(":: PID Opc 7");
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()!=8 ){
                            System.out.println(":: PID Opc 8");
                            corregir = a.getCantidad();
                        }else if(a.getBalanza()==8 && a.getCantidad()== -1.0 && a.getDecimales()==3){
                            System.out.println(":: PID Opc 9");
                            corregirDecimales = a.getCodigos_barras_completo().get(0).substring(7,12);
                            String parte1 = a.getCodigos_barras_completo().get(0).substring(7,9);
                            String parte2 = a.getCodigos_barras_completo().get(0).substring(9,12);
                            corregir = Float.parseFloat(parte1 +"."+ parte2);
                        }else if(a.getBalanza()==8 && a.getCantidad()!= -1.0 && a.getDecimales()==3){
                            System.out.println(":: PID Opc 10");
                            corregir = a.getCantidad();
                        }
                    }

                    if (corregir >= 0) {
                        casilla_cantidad.setText(String.valueOf(corregir));
                    } else {
                        casilla_cantidad.setText("No Tomado");
                    }
                }else{
                    if (a.getCantidad() >= 0) {
                        casilla_cantidad.setText(String.valueOf(a.getCantidad()));
                    } else {
                        casilla_cantidad.setText("No Tomado");
                    }
                }

				/*Damian 09/05*/
                if (ParametrosInventario.InventariosVentas == true) {
                    casilla_exisventa.setText(String.valueOf(a.getExis_venta()));
                }else{
                    casilla_exisventa.setText(String.valueOf(a.getExis_deposito()));
                }

                if ((a.getSubtotal()) >= 0) {
                    casilla_subtotal.setText(String.valueOf(a.getSubtotal()));
                } else {
                    casilla_subtotal.setText("0");
                }

				/* Quinta casilla: INDICE ARTICULO */
                casilla_secreta.setText(String.valueOf(index));
                casilla_secreta.setVisibility(View.GONE);
                // Colorear la linea:
                colorearLinea(linea, paridad);
                paridad = (paridad + 1) % 2;
            } // end for
/*
 * Fin de lo que carga los articulos
 * */
            // 4 Si no tenemos las 8 lineas necesarias, escondemos las ultimas:
            for (int j = listaProximosIndex.size(); j < NUMERO_LINEAS_EN_TABLA; j++) {
                // Recuperamos el id de cada TableRow de cada linea, y la linea
                // asociada:
                try {
                    TableRow linea = (TableRow) tabla_articulos
                            .getChildAt((int) j);
                    linea.setVisibility(View.GONE);
                } catch (Exception e) {

                    log.log("[-- 865 --]" + e.toString(), 4);
                }
            }
            // Parametros globales:
            loadingBar.setVisibility(View.GONE);
            refreshAsensor();
            // aguarde.dismiss();

        } catch (Exception e) {

            log.log("[-- 878 --]" + e.toString(), 4);
            e.printStackTrace();
            loadingBar.setVisibility(View.GONE);
            TableRow linea;
            // Entra aca cuando no hay articulos, probar en manejar ese caso
            // desde aca
            for (int i = 0; i < tabla_articulos.getChildCount(); i++) {
                linea = (TableRow) tabla_articulos.getChildAt(i);
                linea.setVisibility(View.INVISIBLE);
            }

            // showSimpleDialogOK("ERROR de carga", e.toString() +
            // "\n\nUn error occuri en la carga del presente inventario, articulo "+
            // String.valueOf(con)
            // +".\n\n Por favor, quite y reintente carga los datos.").show();
        }
    }

    /**
     * Refrezca el ascensor del costado
     */
    private void refreshAsensor() {
        System.out.println("::: PaginaInventarioDinamico refres Ascensor");
        numero_lineas_asensor_text = (int) ((float) getPosicionRelativa(
                indice_primera_linea, false)
                * (float) (altura_linea_asensor - 30) / (float) (numero_articulos_visibles - 1));

        if (numero_lineas_asensor_text < 0) {
            numero_lineas_asensor_text = 0;
        } else if (numero_lineas_asensor_text > altura_linea_asensor) {
            numero_lineas_asensor_text = altura_linea_asensor;
        }

        TextView txv = (TextView) (asensor_layout.getChildAt(0));
        txv.setLines(numero_lineas_asensor_text);
    }

    /**
     * Refrezca el ascensor del costado
     *
     * @param delta
     */
    private void refreshAsensor(float delta) {
        System.out.println("::: PaginaInventarioDinamico refres ascensor con delta");
        int indice_anterior = (int) ((float) (numero_lineas_asensor_text * numero_articulos_visibles) / (float) (altura_linea_asensor));

        numero_lineas_asensor_text = (int) delta;

        int indice_nuevo = (int) ((float) (numero_lineas_asensor_text * numero_articulos_visibles) / (float) (altura_linea_asensor));

        moverTablaArticulos(indice_nuevo - indice_anterior);

    }

    /**
     * Pinta la linea segun la paridad que se le pase
     *
     * @param linea
     * @param paridad
     */
    private void colorearLinea(@NonNull TableRow linea, int paridad) {
        System.out.println("::: pinto");
        // Cortamos la linea en sus 4 casillas:
        TextView casilla_codigo = (TextView) linea.getChildAt(0);
        TextView casilla_descripcion = (TextView) linea.getChildAt(1);
        TextView casilla_precio = (TextView) linea.getChildAt(2);
        //	TextView casilla_exisventa = (TextView) linea.getChildAt(3);
        EditText casilla_cantidad = (EditText) linea.getChildAt(3);
        EditText casilla_subtotal = (EditText) linea.getChildAt(4);

        TextView casilla_exisventa = (TextView) linea.getChildAt(5);
		/*Damian 09/05*/
        //TextView casilla_exisventa = (TextView) linea.getChildAt(5);

        // 1ra CASILLA - CODIGO:
        if (paridad == 1) {
            casilla_codigo.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
        } else {
            casilla_codigo.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro));
        }

        // 2da CASILLA - DESCRIPCIN:
        if (paridad == 1) {
            casilla_descripcion.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
        } else {
            casilla_descripcion.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro));
        }

        // 3ra CASILLA - PRECIO DE VENTA:
        if (paridad == 1) {
            casilla_precio.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
        } else {
            casilla_precio.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro));
        }

        // 4ta CASILLA - ESTADO INVENTARIO:
        if (paridad == 1) {
            casilla_cantidad.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
        } else {
            casilla_cantidad.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro));
        }
		/*Damian 09/05*/
        // 5ta CASILLA - EXISTENCIA INVENTARIO:
        if (paridad == 1) {
            casilla_exisventa.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
        } else {
            casilla_exisventa.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro));
        }

        System.out.println(":::Pagina inventario pinta paridad");

    }

    /**
     * Busca el indice de el articulo en la tabla cuyo Codigo de barra es cb
     *
     * @param cb
     * @throws ExceptionBDD
     */
    private void getIndiceArticuloConCB(@NonNull final String cb) throws ExceptionBDD {
        System.out.println("::: PaginaInventarioDinamico getIndiceArticulo");
        // Toast.makeText(ctxt, cb, Toast.LENGTH_SHORT).show();
        loadingBar.setVisibility(View.VISIBLE);
        loadingBar.bringToFront();

        int indice_articulo_encontrado = -1;

        // Buscamos el codigo de barras:
        for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
            if (tablaCodigosUnArticulo.contains((String) cb)) {
                // Obtenemos el indice de la linea seleccionada:
                indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
                        .indexOf(tablaCodigosUnArticulo);
                break;
            } // end if
        } // end for

        if (indice_articulo_encontrado < 0) {
            BaseDatos bdd = new BaseDatos(ctxt);
            System.out.println("::: PaginaInventarioDinamico 3887 que antes 4");
            Articulo articulo = bdd.selectReferenciaConCodigoBarra(cb);
            System.out.println("::: PaginaInventarioDinamico 3887 que sigue 4");

        }

        // Tenemos el indice de la posicion del articulo, y luego su visibilidad
        // :
        if (indice_articulo_encontrado < 0) {
            // No encontramos nada, el codigo de barra no referencia ningun
            // articulo del inventario:
            showSimpleDialogTimer(
                    "Articulo no encontrado: " + cb,
                    "El codigo de barra no referencia ningun articulo en el presente inventario!\n\n("
                            + cb + ")", 4000).show();
            loadingBar.setVisibility(View.GONE);
            return;
        }

        // A ver si el articulo es visible:
        if (listaArticulosCompleta.get(indice_articulo_encontrado).esVisible() == false) {
            showSimpleDialogOK(
                    "Articulo oculto",
                    "El codigo de barras leido hace referencia a un articulo bloqueado por un filtro!")
                    .show();
            loadingBar.setVisibility(View.GONE);
            return;
        } else { // Si visible...
            seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
            loadingBar.setVisibility(View.GONE);
        }
    } // end funcion

    private void seleccionarMostrarIndiceArticuloBorrar(int indice_articulo,int inventario_numero_en_curso) {
        System.out.println("::: PaginaInventarioDinamico 1272 Seleccionar mostrarIndice");
        System.out.println("::: PaginaCompras 1049  Borrar ");
        if (indice_articulo < 0) {
            return;
        } else if (indice_articulo == 0) {
            indice_primera_linea = indice_articulo;
        } else {
            indice_primera_linea = indice_articulo - 1;
        }
        // 1 Seteamos el indice_on_focus al indice pasado
        indice_on_focus = indice_articulo;

        BaseDatos bdd = new BaseDatos(ctxt);

        // 2 Refresh de las tablas:
       // refreshTablaCentral();
       for (int j = 0; j < NUMERO_LINEAS_EN_TABLA; j++) {
            TableRow linea = (TableRow) tabla_articulos.getChildAt(j);
            if (Float.parseFloat(String.valueOf(((TextView) (linea
                    .getChildAt(7))).getText())) == indice_articulo) {
                System.out.println("::: Paso 4");
                //linea.setBackgroundColor(getResources().getColor(
                  //      R.color.anaranjado_verde));
                final TextView textcod = (TextView) linea.getChildAt(0);

                // edittext.setFocusable(true);
                textcod.requestFocus();
                textcod.setFocusableInTouchMode(true);
               // textcod.setBackgroundColor(getResources().getColor(
                 //       R.color.anaranjado_verde));
               // textcod.setTextColor(getResources().getColor(R.color.white));

                System.out.println("::: Paso 5");
                try {
                    //parametroPrimeraSeleccion
                    System.out.println("::: String.valueOf(edittextcod.getText()) " +String.valueOf(textcod.getText()));
                    String[] separated = String.valueOf(textcod.getText()).split("-");
                    separated[0] = separated[0].trim();
                    separated[1] = separated[1].trim();
                    final int cod_sector = Integer.parseInt(separated[0]);
                    final int cod_art = Integer.parseInt(separated[1]);

                    bdd.borrarArticuloCompra(cod_sector,cod_art, inventario_numero_en_curso);
                    actualizarPaginaInventario(inventario_numero_en_curso);
                    //refreshTablaCentral();
                   //if (modo_mas_1 == 1) {
                   //     return;
                   // }
             /*       final View.OnClickListener listenerNegativo = new View.OnClickListener() {

                        public void onClick(View v) {
                            log.log("[-- 1115 --]"
                                            + "Se presiona para abrir el cuadro de dialogo de sumar, restar y modificar",
                                    0);
                            dialogoModificacion.cancel();
                        }
                    };*/
             /*       View.OnClickListener listener_sumar = new View.OnClickListener() {

                        public void onClick(View v) {
                            log.log("[-- 1166--]"
                                            + "Se abre el cuador de dialogo para la suma",
                                    0);
                            // Abrir un dialogo para sumar valores:
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {
                                    log.log("[-- 1173 --]"
                                                    + "Se presiona para editar texto",
                                            2);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    System.out.println("::: Que tiene edittext " + editTT);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);
                                    SystemClock.sleep(20);
                                    String valor_a_sumar = dialogoModificacion
                                            .get_nuevo_valor();
                                    System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
                                    edittext.setText(String.valueOf(cantidad_producto
                                            + Float.parseFloat(valor_a_sumar)));
                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_SUMAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {
                                        public void onShow(
                                                DialogInterface dialog) {
                                            System.out.println("::: Entro en el siguiente");
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            System.out.println("::: Entro en el siguiente editTT" + editTT);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                            System.out.println("::: Entro en el siguiente mgr " + mgr);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {

                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        public void onCancel(
                                                DialogInterface dialog) {
                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };*/
              /*      View.OnClickListener listener_restar = new View.OnClickListener() {

                        public void onClick(View v) {
                            // Abrir un dialogo para restar valores:
                            log.log("[-- 1235 --]" + "Se presiono restar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {
                                    log.log("[-- 1240 --]"
                                                    + "Se presiona para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);
                                    SystemClock.sleep(20);
                                    String valor_a_restar = dialogoModificacion
                                            .get_nuevo_valor();
                                    float nuevo_valor = Math
                                            .max(0,
                                                    cantidad_producto
                                                            - Float
                                                            .parseFloat(valor_a_restar));
                                    edittext.setText(String
                                            .valueOf(nuevo_valor));
                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_RESTAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {

                                        public void onShow(

                                                DialogInterface dialog) {
                                            log.log("[-- 1273 --]"
                                                            + "Se dispara el evento OnShow de restar",
                                                    4);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1288 --]"
                                                            + "Se cierra el cuadro de dialogo de restar",
                                                    0);
                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        public void onCancel(
                                                DialogInterface dialog) {
                                            log.log("[-- 1311 --]"
                                                            + "Se cierra restar, se abre el cuadro Modicacion",
                                                    0);
                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };*/
 /*   View.OnClickListener listener_modificar = new View.OnClickListener() {
                        public void onClick(View v) {
                            // Abrir dialogo para ingresar nuevo valor:
                            log.log("[-- 1338 --]" + "Se presiono modificar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {
                                public void onClick(View v) {
                                    log.log("[-- 1343 --]"
                                                    + "Se presiono para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);
                                    SystemClock.sleep(20);
                                    String valor_string = (dialogoModificacion)
                                            .get_nuevo_valor();
                                    edittext.setText(String
                                            .valueOf(valor_string));
                                    dialogoModificacion.dismiss();
                                }
                            };
                            View.OnClickListener listenerReset = new View.OnClickListener() {
                                public void onClick(View v) {
                                    log.log("[-- 1165 --]"
                                            + "Se presiono par resetear", 0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);
                                    SystemClock.sleep(20);
                                    // Caso de reset: se escribe "No tomado":
                                    edittext.setText("");
                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_MODIFICAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, listenerReset);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {
                                        public void onShow(
                                                DialogInterface dialog) {
                                            log.log("[-- 1390 --]"
                                                            + "Se dispara el evento OnShow Modificar",
                                                    0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1404 --]"
                                                            + "Se cierra el cuadro de dialogo de modificar, se abre Cuadro de Modificacion",
                                                    0);
                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {
                                            log.log("[-- 1426 --]"
                                                            + "Se cancelo modificar, se abre el cuadro de Modificacion",
                                                    0);
                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };*/
      /*              View.OnClickListener listener_cancelar = new View.OnClickListener() {
                        public void onClick(View v) {
                            log.log("[-- 1450 --]" + "Se presiono cancelar", 0);
                            // Es un simple cancel:
                            dialogoMasMenos.cancel();
                        }
                    };*/
            /*        dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
                            ctxt, "Articulo ya inventariado",
                            (float) cantidad_producto, listener_sumar, listener_restar,
                            listener_modificar, listener_cancelar);
                    dialogoMasMenos
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    log.log("[-- 1465 --]"
                                            + "se cierra SumarRestar", 0);
                                    deseleccionarLineaParticular(indice_on_focus);
                                }
                            });*/
            /*        dialogoMasMenos
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    deseleccionarLineaParticular(indice_on_focus);
                                }
                            });*/
                    //dialogoMasMenos.show();

                } catch (Exception e) {
                    log.log("[-- 1493 --]" + e.toString(), 4);
               /*     edittext.setText("");
                    edittext.requestFocus();
                    // Toast.makeText(ctxt, "Clavier apparait maintenant",
                    // Toast.LENGTH_LONG).show();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);*/
                }
                break;
            }
        }
    }

    /**
     * Funcion que se ejecuta para seleccionar el articulo de la tabla cuyo
     * indice es indice_articulo y poder modificar el valor de la cantidad
     *
     * @param indice_articulo
     *            <p>
     *            1 Seteamos el indice_on_focus al indice pasado
     *            <p>
     *            2 Refresh de las tablas
     *            <p>
     *            3 Buscamos la linea que modificar
     *            <p>
     *            &nbsp; &nbsp;3.1 Modificamos la cantidad:si es modo ms uno le
     *            sumamos uno, si no creamos el dialog para modificar, sumar o
     *            restar
     *
     */
    private void seleccionarMostrarIndiceArticulo(int indice_articulo) {
        System.out.println("::: PaginaInventarioDinamico 1272 Seleccionar mostrarIndice");

        if (indice_articulo < 0) {
            return;
        } else if (indice_articulo == 0) {
            indice_primera_linea = indice_articulo;
        } else {
            indice_primera_linea = indice_articulo - 1;
        }
        // }
        // 1 Seteamos el indice_on_focus al indice pasado
        indice_on_focus = indice_articulo;
        // 2 Refresh de las tablas:
        refreshTablaCentral();

        // 3 Buscamos la linea que modificar:
        for (int j = 0; j < NUMERO_LINEAS_EN_TABLA; j++) {
            TableRow linea = (TableRow) tabla_articulos.getChildAt(j);
/*			if (Integer.parseInt(String.valueOf(((TextView) (linea
*/
            if (Float.parseFloat(String.valueOf(((TextView) (linea
                    .getChildAt(7))).getText())) == indice_articulo) {
                System.out.println("::: Paso 4");
                linea.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                final EditText edittext = (EditText) linea.getChildAt(3);
                // edittext.setFocusable(true);
                edittext.requestFocus();
                edittext.setFocusableInTouchMode(true);
                // InputMethodManager mgr = (InputMethodManager)
                // getSystemService(Context.INPUT_METHOD_SERVICE);
                // mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
                //
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                edittext.setTextColor(getResources().getColor(R.color.white));
                System.out.println("::: Paso 5");
                try {

//Pasa este valor numerico entonces pregunta por modificar la cantidad 0.00
// ver alguna validacion para evitar esto y revisar por q en el otro proceso no lo hace
                    //parametroPrimeraSeleccion
                    System.out.println("::: 1087 PAGINA COMPRAS ACA NO DEBE FALLAR Y SIGUE " + edittext.getText());
                    final float cantidad_producto = Float.parseFloat(String
                            .valueOf(edittext.getText()));
//String valor_validar = String.valueOf(edittext.getText());
                    //float valor_validar = String.valueOf(edittext.getText());
//                    if(cantidad_producto==0.0){
                    //                       System.out.println("::: 1087 PAGINA COMPRAS ACA SERIA 0.0" );
                    //                   }else if(cantidad_producto==0){
                    //                       System.out.println("::: 1087 PAGINA COMPRAS ACA SERIA SOLAMENTE 0" );
                    //                  }
                    if(cantidad_producto == 0.0){
                        //   log.log("[-- 1493 --]" + e.toString(), 4);
                        edittext.setText("");
                        edittext.requestFocus();
                        // Toast.makeText(ctxt, "Clavier apparait maintenant",
                        // Toast.LENGTH_LONG).show();
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);

                        break;
                    }


                    // 3.1 Modificamos la cantidad
                    // Aca, el articulo est y tiene cantidad.
                    // Si estamos en modo +1, no mostramos le menu 3M:
                    System.out.println("::: PID modo_mas_1 " + modo_mas_1);
                    if (modo_mas_1 == 1) {
                        return;
                    }
                    // Si la linea enfocada ya contiene un valor numrico,
                    // abrimos un dialog para preguntar si queremos sumar,
                    // restar, modificar y cancelar:
                    final View.OnClickListener listenerNegativo = new View.OnClickListener() {

                        public void onClick(View v) {

                            log.log("[-- 1115 --]"
                                            + "Se presiona para abrir el cuadro de dialogo de sumar, restar y modificar",
                                    0);

                            dialogoModificacion.cancel();
                        }
                    };

                    // ***************
                    // * SUMAR *
                    // ***************
                    View.OnClickListener listener_sumar = new View.OnClickListener() {

                        public void onClick(View v) {

                            log.log("[-- 1166--]"
                                            + "Se abre el cuador de dialogo para la suma",
                                    0);
                            // Abrir un dialogo para sumar valores:
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1173 --]"
                                                    + "Se presiona para editar texto",
                                            2);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    System.out.println("::: Que tiene edittext " + editTT);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_a_sumar = dialogoModificacion
                                            .get_nuevo_valor();
                                    System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
                                    edittext.setText(String.valueOf(cantidad_producto
                                            + Float.parseFloat(valor_a_sumar)));

                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_SUMAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {
                                        public void onShow(
                                                DialogInterface dialog) {
                                            System.out.println("::: Entro en el siguiente");
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            System.out.println("::: Entro en el siguiente editTT" + editTT);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                            System.out.println("::: Entro en el siguiente mgr " + mgr);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {

                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {

                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };

                    // ****************
                    // * RESTAR *
                    // ****************
                    View.OnClickListener listener_restar = new View.OnClickListener() {

                        public void onClick(View v) {
                            // Abrir un dialogo para restar valores:

                            log.log("[-- 1235 --]" + "Se presiono restar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1240 --]"
                                                    + "Se presiona para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_a_restar = dialogoModificacion
                                            .get_nuevo_valor();
                                    float nuevo_valor = Math
                                            .max(0,
                                                    cantidad_producto
                                                            - Float
                                                            .parseFloat(valor_a_restar));
                                    edittext.setText(String
                                            .valueOf(nuevo_valor));

                                    dialogoModificacion.dismiss();
                                }
                            };

                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_RESTAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {

                                        public void onShow(

                                                DialogInterface dialog) {
                                            log.log("[-- 1273 --]"
                                                            + "Se dispara el evento OnShow de restar",
                                                    4);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);

                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1288 --]"
                                                            + "Se cierra el cuadro de dialogo de restar",
                                                    0);

                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {

                                            log.log("[-- 1311 --]"
                                                            + "Se cierra restar, se abre el cuadro Modicacion",
                                                    0);

                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };

                    // *******************
                    // * MODIFICAR *
                    // *******************
                    View.OnClickListener listener_modificar = new View.OnClickListener() {

                        public void onClick(View v) {
                            // Abrir dialogo para ingresar nuevo valor:
                            log.log("[-- 1338 --]" + "Se presiono modificar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1343 --]"
                                                    + "Se presiono para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_string = (dialogoModificacion)
                                            .get_nuevo_valor();
                                    edittext.setText(String
                                            .valueOf(valor_string));

                                    dialogoModificacion.dismiss();
                                }
                            };

                            View.OnClickListener listenerReset = new View.OnClickListener() {

                                public void onClick(View v) {
                                    log.log("[-- 1165 --]"
                                            + "Se presiono par resetear", 0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    // Caso de reset: se escribe "No tomado":
                                    edittext.setText("");

                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_MODIFICAR,
                                    cantidad_producto, listenerPositivo,
                                    listenerNegativo, listenerReset);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {

                                        public void onShow(
                                                DialogInterface dialog) {
                                            log.log("[-- 1390 --]"
                                                            + "Se dispara el evento OnShow Modificar",
                                                    0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1404 --]"
                                                            + "Se cierra el cuadro de dialogo de modificar, se abre Cuadro de Modificacion",
                                                    0);
                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {
                                            log.log("[-- 1426 --]"
                                                            + "Se cancelo modificar, se abre el cuadro de Modificacion",
                                                    0);
                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };
                    View.OnClickListener listener_cancelar = new View.OnClickListener() {
                        public void onClick(View v) {
                            log.log("[-- 1450 --]" + "Se presiono cancelar", 0);
                            // Es un simple cancel:
                            dialogoMasMenos.cancel();
                        }
                    };
                    dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
                            ctxt, "Articulo ya inventariado",
                            (float) cantidad_producto, listener_sumar, listener_restar,
                            listener_modificar, listener_cancelar);
                    dialogoMasMenos
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    log.log("[-- 1465 --]"
                                            + "se cierra SumarRestar", 0);

                                    deseleccionarLineaParticular(indice_on_focus);
                                }
                            });
                    dialogoMasMenos
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                public void onCancel(DialogInterface dialog) {
                                    deseleccionarLineaParticular(indice_on_focus);
                                }
                            });

                    dialogoMasMenos.show();

                } catch (Exception e) {

                    log.log("[-- 1493 --]" + e.toString(), 4);
                    edittext.setText("");
                    edittext.requestFocus();

                    // Toast.makeText(ctxt, "Clavier apparait maintenant",
                    // Toast.LENGTH_LONG).show();

                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
                }

                break;
            }
        }

    }


    private void seleccionarMostrarIndiceArticulo_subtotal(int indice_articulo) {
        System.out.println("::: PaginaInventarioDinamico 1272 Seleccionar mostrarIndice");

        if (indice_articulo < 0) {
            return;
        } else if (indice_articulo == 0) {
            indice_primera_linea = indice_articulo;
        } else {
            indice_primera_linea = indice_articulo - 1;
        }
        System.out.println("::: indice_articulo" + indice_articulo);
        // }
        // 1 Seteamos el indice_on_focus al indice pasado
        indice_on_focus = indice_articulo;
        // 2 Refresh de las tablas:
        refreshTablaCentral();

        // 3 Buscamos la linea que modificar:
        for (int j = 0; j < NUMERO_LINEAS_EN_TABLA; j++) {
            TableRow linea = (TableRow) tabla_articulos.getChildAt(j);
/*			if (Integer.parseInt(String.valueOf(((TextView) (linea
*/
            if (Float.parseFloat(String.valueOf(((TextView) (linea
                    .getChildAt(7))).getText())) == indice_articulo) {
                System.out.println("::: Paso 4");
                linea.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                final EditText edittext = (EditText) linea.getChildAt(4);
                // edittext.setFocusable(true);
                edittext.requestFocus();
                edittext.setFocusableInTouchMode(true);
                // InputMethodManager mgr = (InputMethodManager)
                // getSystemService(Context.INPUT_METHOD_SERVICE);
                // mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
                //
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                edittext.setTextColor(getResources().getColor(R.color.white));
                System.out.println("::: Paso 5");
                try {

//Pasa este valor numerico entonces pregunta por modificar la cantidad 0.00
// ver alguna validacion para evitar esto y revisar por q en el otro proceso no lo hace
                    //parametroPrimeraSeleccion
                    System.out.println("::: 1087 PAGINA COMPRAS ACA NO DEBE FALLAR Y SIGUE " + edittext.getText());
                    final float subtotal_producto = Float.parseFloat(String
                            .valueOf(edittext.getText()));
                    System.out.println("*** SUB TOTAL " + subtotal_producto);
//String valor_validar = String.valueOf(edittext.getText());
                    //float valor_validar = String.valueOf(edittext.getText());
//                    if(cantidad_producto==0.0){
                    //                       System.out.println("::: 1087 PAGINA COMPRAS ACA SERIA 0.0" );
                    //                   }else if(cantidad_producto==0){
                    //                       System.out.println("::: 1087 PAGINA COMPRAS ACA SERIA SOLAMENTE 0" );
                    //                  }

                    /*
                    if(subtotal_producto == 0.0){
                        //   log.log("[-- 1493 --]" + e.toString(), 4);
                        edittext.setText("");
                        edittext.requestFocus();
                        // Toast.makeText(ctxt, "Clavier apparait maintenant",
                        // Toast.LENGTH_LONG).show();
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);

                        break;
                    }*/


                    // 3.1 Modificamos la cantidad
                    // Aca, el articulo est y tiene cantidad.
                    // Si estamos en modo +1, no mostramos le menu 3M:
                    System.out.println("::: PID modo_mas_1 " + modo_mas_1);
                    if (modo_mas_1 == 1) {
                        return;
                    }
                    // Si la linea enfocada ya contiene un valor numrico,
                    // abrimos un dialog para preguntar si queremos sumar,
                    // restar, modificar y cancelar:
                    final View.OnClickListener listenerNegativo = new View.OnClickListener() {

                        public void onClick(View v) {

                            log.log("[-- 1115 --]"
                                            + "Se presiona para abrir el cuadro de dialogo de sumar, restar y modificar",
                                    0);

                            dialogoModificacion.cancel();
                        }
                    };

                    // ***************
                    // * SUMAR *
                    // ***************
                    View.OnClickListener listener_sumar = new View.OnClickListener() {

                        public void onClick(View v) {

                            log.log("[-- 1166--]"
                                            + "Se abre el cuador de dialogo para la suma",
                                    0);
                            // Abrir un dialogo para sumar valores:
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1173 --]"
                                                    + "Se presiona para editar texto",
                                            2);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    System.out.println("::: Que tiene edittext****************** " + editTT);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_a_sumar = dialogoModificacion
                                            .get_nuevo_valor();
                                    System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
                                    edittext.setText(String.valueOf(subtotal_producto
                                            + Float.parseFloat(valor_a_sumar)));

                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_SUMAR,
                                    subtotal_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {
                                        public void onShow(
                                                DialogInterface dialog) {
                                            System.out.println("::: Entro en el siguiente");
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);

                                            System.out.println("::: Entro en el siguiente editTT" + editTT);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            System.out.println("********** " + R.id.Z_DIALOG_cantidad_nueva);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                            System.out.println("::: Entro en el siguiente mgr " + mgr);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {

                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {

                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };

                    // ****************
                    // * RESTAR *
                    // ****************
                    View.OnClickListener listener_restar = new View.OnClickListener() {

                        public void onClick(View v) {
                            // Abrir un dialogo para restar valores:

                            log.log("[-- 1235 --]" + "Se presiono restar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1240 --]"
                                                    + "Se presiona para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_a_restar = dialogoModificacion
                                            .get_nuevo_valor();
                                    float nuevo_valor = Math
                                            .max(0,
                                                    subtotal_producto
                                                            - Float
                                                            .parseFloat(valor_a_restar));
                                    edittext.setText(String
                                            .valueOf(nuevo_valor));

                                    dialogoModificacion.dismiss();
                                }
                            };

                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_RESTAR,
                                    subtotal_producto, listenerPositivo,
                                    listenerNegativo, null);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {

                                        public void onShow(

                                                DialogInterface dialog) {
                                            log.log("[-- 1273 --]"
                                                            + "Se dispara el evento OnShow de restar",
                                                    4);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);

                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1288 --]"
                                                            + "Se cierra el cuadro de dialogo de restar",
                                                    0);

                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {

                                            log.log("[-- 1311 --]"
                                                            + "Se cierra restar, se abre el cuadro Modicacion",
                                                    0);

                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };

                    // *******************
                    // * MODIFICAR *
                    // *******************
                    View.OnClickListener listener_modificar = new View.OnClickListener() {

                        public void onClick(View v) {
                            // Abrir dialogo para ingresar nuevo valor:
                            log.log("[-- 1338 --]" + "Se presiono modificar", 0);
                            View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1343 --]"
                                                    + "Se presiono para editar el formulario",
                                            0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    String valor_string = (dialogoModificacion)
                                            .get_nuevo_valor();
                                    edittext.setText(String
                                            .valueOf(valor_string));

                                    dialogoModificacion.dismiss();
                                }
                            };

                            View.OnClickListener listenerReset = new View.OnClickListener() {

                                public void onClick(View v) {
                                    log.log("[-- 1165 --]"
                                            + "Se presiono par resetear", 0);
                                    editTT = (EditText) dialogoModificacion
                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    mgr.hideSoftInputFromWindow(
                                            editTT.getWindowToken(), 0);

                                    SystemClock.sleep(20);

                                    // Caso de reset: se escribe "No tomado":
                                    edittext.setText("");

                                    dialogoModificacion.dismiss();
                                }
                            };
                            dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                    ctxt, DialogPerso.OPERACION_MODIFICAR,
                                    subtotal_producto, listenerPositivo,
                                    listenerNegativo, listenerReset);
                            dialogoModificacion
                                    .setOnShowListener(new DialogInterface.OnShowListener() {

                                        public void onShow(
                                                DialogInterface dialog) {
                                            log.log("[-- 1390 --]"
                                                            + "Se dispara el evento OnShow Modificar",
                                                    0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.showSoftInput(
                                                    editTT,
                                                    InputMethodManager.SHOW_FORCED);
                                        }
                                    });
                            dialogoModificacion
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(
                                                DialogInterface dialog) {
                                            log.log("[-- 1404 --]"
                                                            + "Se cierra el cuadro de dialogo de modificar, se abre Cuadro de Modificacion",
                                                    0);
                                            dialogoMasMenos.dismiss();
                                        }
                                    });
                            dialogoModificacion
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(
                                                DialogInterface dialog) {
                                            log.log("[-- 1426 --]"
                                                            + "Se cancelo modificar, se abre el cuadro de Modificacion",
                                                    0);
                                        }
                                    });
                            dialogoModificacion.show();
                        }
                    };
                    View.OnClickListener listener_cancelar = new View.OnClickListener() {
                        public void onClick(View v) {
                            log.log("[-- 1450 --]" + "Se presiono cancelar", 0);
                            // Es un simple cancel:
                            dialogoMasMenos.cancel();
                        }
                    };
                    dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
                            ctxt, "Modificar Subtotal", //Articulo ya inventariado
                            (float) subtotal_producto, listener_sumar, listener_restar,
                            listener_modificar, listener_cancelar);
                    dialogoMasMenos
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    log.log("[-- 1465 --]"
                                            + "se cierra SumarRestar", 0);

                                    deseleccionarLineaParticular_subtotal(indice_on_focus);
                                }
                            });
                    dialogoMasMenos
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                public void onCancel(DialogInterface dialog) {
                                    deseleccionarLineaParticular_subtotal(indice_on_focus);
                                }
                            });

                    dialogoMasMenos.show();

                } catch (Exception e) {

                    log.log("[-- 1493 --]" + e.toString(), 4);
                    edittext.setText("");
                    edittext.requestFocus();

                    // Toast.makeText(ctxt, "Clavier apparait maintenant",
                    // Toast.LENGTH_LONG).show();

                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
                }

                break;
            }
        }

    }



    public void borrar_articulo(View view) {

        System.out.println("ACA SI ENTRA");

        //String convertir_id = String.valueOf(id_inventario);
        //Intent i = new Intent(ComprasMainBoard.this, ProveedorBusqueda.class);
        //i.putExtra("proveedor", convertir_id);
        //startActivity(i);
    }


    /**
     * Funcion que se ejecuta para seleccionar el articulo de la tabla cuyo
     * indice es indice_articulo y poder modificar el valor de la cantidad,
     * teniendo en cuenta tambien el parametro de balanza activado
     *
     * @param indice_articulo
     *            <p>
     *            1 Seteamos el indice_on_focus al indice pasado
     *            <p>
     *            2 Refresh de las tablas
     *            <p>
     *            3 Buscamos la linea que modificar
     *            <p>
     *            &nbsp; &nbsp;3.1 Modificamos la cantidad:si es modo ms uno le
     *            sumamos uno, si no creamos el dialog para modificar, sumar o
     *            restar
     *
     */
    private void seleccionarMostrarIndiceArticuloBalanza(int indice_articulo, @NonNull String cb) {
        System.out.println("::: PaginaInventarioDinamico Balanza 1864 Seleccionar mostrarIndice");
        if (indice_articulo < 0) {
            return;
        } else if (indice_articulo == 0) {
            indice_primera_linea = indice_articulo;
        } else {
            indice_primera_linea = indice_articulo - 1;
        }
        // 1 Seteamos el indice_on_focus al indice pasado
        indice_on_focus = indice_articulo;
        // 2 Refresh de las tablas:
        refreshTablaCentral();
        // 3 Buscamos la linea que modificar:
        for (int j = 0; j < NUMERO_LINEAS_EN_TABLA; j++) {
            TableRow linea = (TableRow) tabla_articulos.getChildAt(j);
            if (Float.parseFloat(String.valueOf(((TextView) (linea
                    .getChildAt(7))).getText())) == indice_articulo) {
                System.out.println("::: Paso 4");
                linea.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                final EditText edittext = (EditText) linea.getChildAt(3);
                edittext.requestFocus();
                edittext.setFocusableInTouchMode(true);
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.anaranjado_verde));
                edittext.setTextColor(getResources().getColor(R.color.white));
                try {
                    boolean condicionBalanza = ParametrosInventario.balanza;
                    float cantidad_actual = 0;
                    if(condicionBalanza == true){
                        int pesable = Integer.parseInt(cb.substring(0, 2));
                        if(pesable == 20){
                            if(parametroPrimeraSeleccion == 1){
                                System.out.println("::: EXISTE EN EL INV");
                                //quiere decir que ya existe
                                final float cantidad_producto = Float.parseFloat(String
                                        .valueOf(edittext.getText()));
                                // 3.1 Modificamos la cantidad
                                // Aca, el articulo est y tiene cantidad.
                                // Si estamos en modo +1, no mostramos le menu 3M:
                                System.out.println("::: PID modo_mas_1 " + modo_mas_1);
                                if (modo_mas_1 == 1) {
                                    return;
                                }
                                // Si la linea enfocada ya contiene un valor numrico,
                                // abrimos un dialog para preguntar si queremos sumar,
                                // restar, modificar y cancelar:
                                final View.OnClickListener listenerNegativo = new View.OnClickListener() {

                                    public void onClick(View v) {
                                        log.log("[-- 1115 --]"
                                                        + "Se presiona para abrir el cuadro de dialogo de sumar, restar y modificar",
                                                0);
                                        dialogoModificacion.cancel();
                                    }
                                };
                                // ***************
                                // * SUMAR *
                                // ***************
                                View.OnClickListener listener_sumar = new View.OnClickListener() {

                                    public void onClick(View v) {

                                        log.log("[-- 1166--]"
                                                        + "Se abre el cuador de dialogo para la suma",
                                                0);
                                        // Abrir un dialogo para sumar valores:
                                        View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                            public void onClick(View v) {

                                                log.log("[-- 1173 --]"
                                                                + "Se presiona para editar texto",
                                                        2);
                                                editTT = (EditText) dialogoModificacion
                                                        .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                System.out.println("::: Que tiene edittext " + editTT);
                                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                mgr.hideSoftInputFromWindow(
                                                        editTT.getWindowToken(), 0);

                                                SystemClock.sleep(20);

                                                String valor_a_sumar = dialogoModificacion
                                                        .get_nuevo_valor();
                                                System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
                                                edittext.setText(String.valueOf(cantidad_producto
                                                        + Float.parseFloat(valor_a_sumar)));

                                                dialogoModificacion.dismiss();
                                            }
                                        };
                                        dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                                ctxt, DialogPerso.OPERACION_SUMAR,
                                                cantidad_producto, listenerPositivo,
                                                listenerNegativo, null);
                                        dialogoModificacion
                                                .setOnShowListener(new DialogInterface.OnShowListener() {
                                                    public void onShow(
                                                            DialogInterface dialog) {
                                                        System.out.println("::: Entro en el siguiente");
                                                        editTT = (EditText) dialogoModificacion
                                                                .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                        System.out.println("::: Entro en el siguiente editTT" + editTT);
                                                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        mgr.showSoftInput(
                                                                editTT,
                                                                InputMethodManager.SHOW_FORCED);
                                                        System.out.println("::: Entro en el siguiente mgr " + mgr);
                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                    public void onDismiss(
                                                            DialogInterface dialog) {
                                                        dialogoMasMenos.dismiss();
                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                    public void onCancel(
                                                            DialogInterface dialog) {
                                                    }
                                                });
                                        dialogoModificacion.show();
                                    }
                                };

                                // ****************
                                // * RESTAR *
                                // ****************
                                View.OnClickListener listener_restar = new View.OnClickListener() {

                                    public void onClick(View v) {
                                        // Abrir un dialogo para restar valores:

                                        log.log("[-- 1235 --]" + "Se presiono restar", 0);
                                        View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                            public void onClick(View v) {

                                                log.log("[-- 1240 --]"
                                                                + "Se presiona para editar el formulario",
                                                        0);
                                                editTT = (EditText) dialogoModificacion
                                                        .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                mgr.hideSoftInputFromWindow(
                                                        editTT.getWindowToken(), 0);

                                                SystemClock.sleep(20);

                                                String valor_a_restar = dialogoModificacion
                                                        .get_nuevo_valor();
                                                float nuevo_valor = Math
                                                        .max(0,
                                                                cantidad_producto
                                                                        - Float
                                                                        .parseFloat(valor_a_restar));
                                                edittext.setText(String
                                                        .valueOf(nuevo_valor));

                                                dialogoModificacion.dismiss();
                                            }
                                        };

                                        dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                                ctxt, DialogPerso.OPERACION_RESTAR,
                                                cantidad_producto, listenerPositivo,
                                                listenerNegativo, null);
                                        dialogoModificacion
                                                .setOnShowListener(new DialogInterface.OnShowListener() {

                                                    public void onShow(

                                                            DialogInterface dialog) {
                                                        log.log("[-- 1273 --]"
                                                                        + "Se dispara el evento OnShow de restar",
                                                                4);
                                                        editTT = (EditText) dialogoModificacion
                                                                .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        mgr.showSoftInput(
                                                                editTT,
                                                                InputMethodManager.SHOW_FORCED);

                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                    public void onDismiss(
                                                            DialogInterface dialog) {
                                                        log.log("[-- 1288 --]"
                                                                        + "Se cierra el cuadro de dialogo de restar",
                                                                0);
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
                                                        dialogoMasMenos.dismiss();
                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                    public void onCancel(
                                                            DialogInterface dialog) {

                                                        log.log("[-- 1311 --]"
                                                                        + "Se cierra restar, se abre el cuadro Modicacion",
                                                                0);
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
                                                    }
                                                });
                                        dialogoModificacion.show();
                                    }
                                };

                                // *******************
                                // * MODIFICAR *
                                // *******************
                                View.OnClickListener listener_modificar = new View.OnClickListener() {

                                    public void onClick(View v) {
                                        // Abrir dialogo para ingresar nuevo valor:
                                        log.log("[-- 1338 --]" + "Se presiono modificar", 0);
                                        View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                            public void onClick(View v) {

                                                log.log("[-- 1343 --]"
                                                                + "Se presiono para editar el formulario",
                                                        0);
                                                editTT = (EditText) dialogoModificacion
                                                        .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                mgr.hideSoftInputFromWindow(
                                                        editTT.getWindowToken(), 0);

                                                SystemClock.sleep(20);

                                                String valor_string = (dialogoModificacion)
                                                        .get_nuevo_valor();
                                                edittext.setText(String
                                                        .valueOf(valor_string));

                                                dialogoModificacion.dismiss();
                                            }
                                        };

                                        View.OnClickListener listenerReset = new View.OnClickListener() {

                                            public void onClick(View v) {
                                                log.log("[-- 1165 --]"
                                                        + "Se presiono par resetear", 0);
                                                editTT = (EditText) dialogoModificacion
                                                        .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                mgr.hideSoftInputFromWindow(
                                                        editTT.getWindowToken(), 0);

                                                SystemClock.sleep(20);

                                                // Caso de reset: se escribe "No tomado":
                                                edittext.setText("");

                                                dialogoModificacion.dismiss();
                                            }
                                        };
                                        dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                                ctxt, DialogPerso.OPERACION_MODIFICAR,
                                                cantidad_producto, listenerPositivo,
                                                listenerNegativo, listenerReset);
                                        dialogoModificacion
                                                .setOnShowListener(new DialogInterface.OnShowListener() {

                                                    public void onShow(
                                                            DialogInterface dialog) {
                                                        log.log("[-- 1390 --]"
                                                                        + "Se dispara el evento OnShow Modificar",
                                                                0);
                                                        editTT = (EditText) dialogoModificacion
                                                                .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        mgr.showSoftInput(
                                                                editTT,
                                                                InputMethodManager.SHOW_FORCED);
                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                    public void onDismiss(
                                                            DialogInterface dialog) {
                                                        log.log("[-- 1404 --]"
                                                                        + "Se cierra el cuadro de dialogo de modificar, se abre Cuadro de Modificacion",
                                                                0);
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
                                                        dialogoMasMenos.dismiss();
                                                    }
                                                });
                                        dialogoModificacion
                                                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                    public void onCancel(
                                                            DialogInterface dialog) {
                                                        log.log("[-- 1426 --]"
                                                                        + "Se cancelo modificar, se abre el cuadro de Modificacion",
                                                                0);
											/*
											 * editTT = (EditText)
											 * dialogoModificacion
											 * .findViewById(R
											 * .id.Z_DIALOG_cantidad_nueva);
											 * InputMethodManager mgr =
											 * (InputMethodManager)
											 * getSystemService
											 * (Context.INPUT_METHOD_SERVICE);
											 * mgr
											 * .hideSoftInputFromWindow(editTT
											 * .getWindowToken(), 0);
											 */
                                                    }
                                                });
                                        dialogoModificacion.show();
                                    }
                                };
                                View.OnClickListener listener_cancelar = new View.OnClickListener() {

                                    public void onClick(View v) {

                                        log.log("[-- 1450 --]" + "Se presiono cancelar", 0);
                                        // Es un simple cancel:
                                        dialogoMasMenos.cancel();
                                    }
                                };

                                dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
                                        ctxt, "Articulo ya inventariado",
                                        (float) cantidad_producto, listener_sumar, listener_restar,
                                        listener_modificar, listener_cancelar);
                                dialogoMasMenos
                                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                            public void onDismiss(DialogInterface dialog) {

                                                log.log("[-- 1465 --]"
                                                        + "se cierra SumarRestar", 0);
									/*
									 * editTT = (EditText)
									 * dialogoModificacion.findViewById
									 * (R.id.Z_DIALOG_cantidad_nueva);
									 * InputMethodManager mgr =
									 * (InputMethodManager)
									 * getSystemService(Context
									 * .INPUT_METHOD_SERVICE);
									 * mgr.hideSoftInputFromWindow
									 * (editTT.getWindowToken(), 0);
									 */

                                                deseleccionarLineaParticular(indice_on_focus);
                                            }
                                        });
                                dialogoMasMenos
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                            public void onCancel(DialogInterface dialog) {
                                                deseleccionarLineaParticular(indice_on_focus);
                                            }
                                        });
                                dialogoMasMenos.show();
                            }else{
                                return;
                            }
                        }else{
                            final float cantidad_producto = Float.parseFloat(String
                                    .valueOf(edittext.getText()));
                            // 3.1 Modificamos la cantidad
                            // Aca, el articulo est y tiene cantidad.
                            // Si estamos en modo +1, no mostramos le menu 3M:
                            System.out.println("::: PID modo_mas_1 " + modo_mas_1);
                            if (modo_mas_1 == 1) {
                                return;
                            }
                            // Si la linea enfocada ya contiene un valor numrico,
                            // abrimos un dialog para preguntar si queremos sumar,
                            // restar, modificar y cancelar:
                            final View.OnClickListener listenerNegativo = new View.OnClickListener() {

                                public void onClick(View v) {
                                    log.log("[-- 1115 --]"
                                                    + "Se presiona para abrir el cuadro de dialogo de sumar, restar y modificar",
                                            0);
                                    dialogoModificacion.cancel();
                                }
                            };

                            // ***************
                            // * SUMAR *
                            // ***************
                            View.OnClickListener listener_sumar = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1166--]"
                                                    + "Se abre el cuador de dialogo para la suma",
                                            0);
                                    // Abrir un dialogo para sumar valores:
                                    View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                        public void onClick(View v) {

                                            log.log("[-- 1173 --]"
                                                            + "Se presiona para editar texto",
                                                    2);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            System.out.println("::: Que tiene edittext " + editTT);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.hideSoftInputFromWindow(
                                                    editTT.getWindowToken(), 0);

                                            SystemClock.sleep(20);

                                            String valor_a_sumar = dialogoModificacion
                                                    .get_nuevo_valor();
                                            System.out.println("::: Que tiene valor_a_sumar " + valor_a_sumar);
                                            edittext.setText(String.valueOf(cantidad_producto
                                                    + Float.parseFloat(valor_a_sumar)));

                                            dialogoModificacion.dismiss();
                                        }
                                    };
                                    dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                            ctxt, DialogPerso.OPERACION_SUMAR,
                                            cantidad_producto, listenerPositivo,
                                            listenerNegativo, null);
                                    dialogoModificacion
                                            .setOnShowListener(new DialogInterface.OnShowListener() {
                                                public void onShow(
                                                        DialogInterface dialog) {
                                                    System.out.println("::: Entro en el siguiente");
                                                    editTT = (EditText) dialogoModificacion
                                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                    System.out.println("::: Entro en el siguiente editTT" + editTT);
                                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    mgr.showSoftInput(
                                                            editTT,
                                                            InputMethodManager.SHOW_FORCED);
                                                    System.out.println("::: Entro en el siguiente mgr " + mgr);
                                                }
                                            });
                                    dialogoModificacion
                                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                public void onDismiss(
                                                        DialogInterface dialog) {

                                                    dialogoMasMenos.dismiss();
                                                }
                                            });
                                    dialogoModificacion
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                public void onCancel(
                                                        DialogInterface dialog) {

                                                }
                                            });
                                    dialogoModificacion.show();
                                }
                            };

                            // ****************
                            // * RESTAR *
                            // ****************
                            View.OnClickListener listener_restar = new View.OnClickListener() {

                                public void onClick(View v) {
                                    // Abrir un dialogo para restar valores:

                                    log.log("[-- 1235 --]" + "Se presiono restar", 0);
                                    View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                        public void onClick(View v) {

                                            log.log("[-- 1240 --]"
                                                            + "Se presiona para editar el formulario",
                                                    0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.hideSoftInputFromWindow(
                                                    editTT.getWindowToken(), 0);

                                            SystemClock.sleep(20);

                                            String valor_a_restar = dialogoModificacion
                                                    .get_nuevo_valor();
                                            float nuevo_valor = Math
                                                    .max(0,
                                                            cantidad_producto
                                                                    - Float
                                                                    .parseFloat(valor_a_restar));
                                            edittext.setText(String
                                                    .valueOf(nuevo_valor));

                                            dialogoModificacion.dismiss();
                                        }
                                    };

                                    dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                            ctxt, DialogPerso.OPERACION_RESTAR,
                                            cantidad_producto, listenerPositivo,
                                            listenerNegativo, null);
                                    dialogoModificacion
                                            .setOnShowListener(new DialogInterface.OnShowListener() {

                                                public void onShow(

                                                        DialogInterface dialog) {
                                                    log.log("[-- 1273 --]"
                                                                    + "Se dispara el evento OnShow de restar",
                                                            4);
                                                    editTT = (EditText) dialogoModificacion
                                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    mgr.showSoftInput(
                                                            editTT,
                                                            InputMethodManager.SHOW_FORCED);

                                                }
                                            });
                                    dialogoModificacion
                                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                public void onDismiss(
                                                        DialogInterface dialog) {
                                                    log.log("[-- 1288 --]"
                                                                    + "Se cierra el cuadro de dialogo de restar",
                                                            0);

                                                    dialogoMasMenos.dismiss();
                                                }
                                            });
                                    dialogoModificacion
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                public void onCancel(
                                                        DialogInterface dialog) {

                                                    log.log("[-- 1311 --]"
                                                                    + "Se cierra restar, se abre el cuadro Modicacion",
                                                            0);

                                                }
                                            });
                                    dialogoModificacion.show();
                                }
                            };

                            // *******************
                            // * MODIFICAR *
                            // *******************
                            View.OnClickListener listener_modificar = new View.OnClickListener() {

                                public void onClick(View v) {
                                    // Abrir dialogo para ingresar nuevo valor:
                                    log.log("[-- 1338 --]" + "Se presiono modificar", 0);
                                    View.OnClickListener listenerPositivo = new View.OnClickListener() {

                                        public void onClick(View v) {

                                            log.log("[-- 1343 --]"
                                                            + "Se presiono para editar el formulario",
                                                    0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.hideSoftInputFromWindow(
                                                    editTT.getWindowToken(), 0);

                                            SystemClock.sleep(20);

                                            String valor_string = (dialogoModificacion)
                                                    .get_nuevo_valor();
                                            edittext.setText(String
                                                    .valueOf(valor_string));

                                            dialogoModificacion.dismiss();
                                        }
                                    };

                                    View.OnClickListener listenerReset = new View.OnClickListener() {

                                        public void onClick(View v) {
                                            log.log("[-- 1165 --]"
                                                    + "Se presiono par resetear", 0);
                                            editTT = (EditText) dialogoModificacion
                                                    .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            mgr.hideSoftInputFromWindow(
                                                    editTT.getWindowToken(), 0);

                                            SystemClock.sleep(20);

                                            // Caso de reset: se escribe "No tomado":
                                            edittext.setText("");

                                            dialogoModificacion.dismiss();
                                        }
                                    };
                                    dialogoModificacion = new DialogPersoComplexCantidadModificacion(
                                            ctxt, DialogPerso.OPERACION_MODIFICAR,
                                            cantidad_producto, listenerPositivo,
                                            listenerNegativo, listenerReset);
                                    dialogoModificacion
                                            .setOnShowListener(new DialogInterface.OnShowListener() {

                                                public void onShow(
                                                        DialogInterface dialog) {
                                                    log.log("[-- 1390 --]"
                                                                    + "Se dispara el evento OnShow Modificar",
                                                            0);
                                                    editTT = (EditText) dialogoModificacion
                                                            .findViewById(R.id.Z_DIALOG_cantidad_nueva);
                                                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    mgr.showSoftInput(
                                                            editTT,
                                                            InputMethodManager.SHOW_FORCED);
                                                }
                                            });
                                    dialogoModificacion
                                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                                public void onDismiss(
                                                        DialogInterface dialog) {
                                                    log.log("[-- 1404 --]"
                                                                    + "Se cierra el cuadro de dialogo de modificar, se abre Cuadro de Modificacion",
                                                            0);

                                                    dialogoMasMenos.dismiss();
                                                }
                                            });
                                    dialogoModificacion
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                public void onCancel(
                                                        DialogInterface dialog) {
                                                    log.log("[-- 1426 --]"
                                                                    + "Se cancelo modificar, se abre el cuadro de Modificacion",
                                                            0);

                                                }
                                            });
                                    dialogoModificacion.show();
                                }
                            };
                            View.OnClickListener listener_cancelar = new View.OnClickListener() {

                                public void onClick(View v) {

                                    log.log("[-- 1450 --]" + "Se presiono cancelar", 0);
                                    // Es un simple cancel:
                                    dialogoMasMenos.cancel();
                                }
                            };

                            dialogoMasMenos = new DialogPersoComplexCantidadMasMenos(
                                    ctxt, "Articulo ya inventariado",
                                    (float) cantidad_producto, listener_sumar, listener_restar,
                                    listener_modificar, listener_cancelar);
                            dialogoMasMenos
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                        public void onDismiss(DialogInterface dialog) {

                                            log.log("[-- 1465 --]"
                                                    + "se cierra SumarRestar", 0);

                                            deseleccionarLineaParticular(indice_on_focus);
                                        }
                                    });
                            dialogoMasMenos
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                        public void onCancel(DialogInterface dialog) {
                                            deseleccionarLineaParticular(indice_on_focus);
                                        }
                                    });
                            dialogoMasMenos.show();
                        }
                    }else{
                        System.out.println("::: PID 3 que trae " + Float.parseFloat(String
                                .valueOf(edittext.getText())));
                        cantidad_actual = Float.parseFloat(String
                                .valueOf(edittext.getText()));
                    }

                } catch (Exception e) {

                    log.log("[-- 1493 --]" + e.toString(), 4);
                    edittext.setText("");
                    edittext.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
                }

                break;
            }
        }

    }
    private void deseleccionarLineaParticularBorrar(int indice_focused) {
        deseleccionarLineaParticular_borrar(indice_focused, true);
    }
    private void deseleccionarLineaParticular_subtotal(int indice_focused) {
        deseleccionarLineaParticularSubtotal(indice_focused, true);
    }

    /**
     * Deselecciona una linea indicada por el indice, al deseleccionar debemos
     * grabar la informacion actualizada en la BD
     *
     * @param indice_focused
     */
    private void deseleccionarLineaParticular(int indice_focused) {
        System.out.println("::: PaginaCompras  deseleccionarLinea");
        deseleccionarLineaParticular(indice_focused, true);
    }

    private void deseleccionarLineaParticular_borrar(int indice_focused,
                                              boolean grabar_si_no) {
        if (indice_focused >= 0) {
            // 1 Buscamos la linea que modificar:
            TableRow lineaFocused = null;
            int numeroLineaFocused = 0;
            for (int j = 0; j < tabla_articulos.getChildCount(); j++) {
                lineaFocused = (TableRow) tabla_articulos.getChildAt(j);
                if (Integer.parseInt(String.valueOf(((TextView) (lineaFocused
                        .getChildAt(7))).getText())) == indice_focused) {
                    numeroLineaFocused = j;
                    break;
                }
            }
            // 2 Cambiamos el color
            lineaFocused.setBackgroundColor(getResources().getColor(
                    R.color.verde_claro));
            EditText edittext = (EditText) lineaFocused.getChildAt(3);
            edittext.setFocusable(false);
            if ((indice_primera_linea % 2) == (numeroLineaFocused % 2)) {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro));
            } else {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro_mas));
            }
            edittext.setTextColor(getResources().getColor(
                    R.color.anaranjado_verde));
            tabla_articulos.requestFocus();
            indice_on_focus = -1;
            // se esconde el teclado? para que
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                // Si decidimos no grabar el valor, entonces reponemos el viejo
                // valor del edittext como antes:
                // edittext.setText(String.valueOf(valor_antes_modificar));
                // valor_antes_modificar = -1;
        }
    }
    /**
     * Deselecciona la linea indicada por el indice y avisa si hay que grabar la
     * informacion o no, al deseleccionar debemos,si se pide, grabar la
     * informacion actualizada en la BD
     * <p>
     * 1 Buscamos la linea que modificar
     * <p>
     * 2 Cambiamos el color
     * <p>
     * 3 Modificamos el objeto ARTICULO y su valor en la base de datos (si el
     * argumento booleano "grabar_si_no" lo permite y si no es un valor erroneo,
     * muy grande o invalido. Si el valor es invalido se pone -1 o el valor
     * anterior
     *
     * @param indice_focused
     * @param grabar_si_no
     */
    private void deseleccionarLineaParticular(int indice_focused,
                                              boolean grabar_si_no) {
        System.out.println("::: PaginaInventarioDinamico deseleccionarLinea int");
        if (indice_focused >= 0) {
            // 1 Buscamos la linea que modificar:
            TableRow lineaFocused = null;
            int numeroLineaFocused = 0;

            for (int j = 0; j < tabla_articulos.getChildCount(); j++) {
                lineaFocused = (TableRow) tabla_articulos.getChildAt(j);

                if (Integer.parseInt(String.valueOf(((TextView) (lineaFocused
                        .getChildAt(7))).getText())) == indice_focused) {
                    numeroLineaFocused = j;
                    break;
                }
            }

            // 2 Cambiamos el color
            lineaFocused.setBackgroundColor(getResources().getColor(
                    R.color.verde_claro));

            EditText edittext = (EditText) lineaFocused.getChildAt(3);
            ImageView viewIma = (ImageView) lineaFocused.getChildAt(6);
            edittext.setFocusable(false);
            if ((indice_primera_linea % 2) == (numeroLineaFocused % 2)) {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro));
            } else {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro_mas));
                viewIma.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro_mas));

            }
            edittext.setTextColor(getResources().getColor(
                    R.color.anaranjado_verde));
            viewIma.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
            tabla_articulos.requestFocus();

            indice_on_focus = -1;

            // se esconde el teclado? para que
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edittext.getWindowToken(), 0);

            // 3 Modificamos el objeto ARTICULO y su valor en la base de datos
            // (si
            // el argumento booleano "grabar_si_no" lo permite):
            if (grabar_si_no == true) {
                String valorDelEdittext = "";
                try {
                    valorDelEdittext = String.valueOf(edittext.getText())
                            .trim();
                    System.out.println(":::No tomado, toma el valor con trim + "+ valorDelEdittext);
                } catch (Exception e) {

                    log.log("[-- 1588 --]" + e.toString(), 4);
                    // Si excepcion levantada, es que el contenido del Edittext
                    // es malo, ponemos valor del articulo a -1 (sin hacer):
                    listaArticulosCompleta.get(indice_focused).setCantidad(-1);
                    showSimpleDialogOK("Error", "Valor incorrecto (1)").show();
                    edittext.setText("No Tomado");
                    return;
                }
                System.out.println(":::Ingresa al no tomado");

                if (valorDelEdittext.length() == 0) {
                    listaArticulosCompleta.get(indice_focused).setCantidad(-1);
                    edittext.setText("No Tomado");
                    // return;
                } else {
                    try {
                        System.out.println("::: Aca hace le proceso de guardado");
                        float cantidad = Float.parseFloat(valorDelEdittext);
                        listaArticulosCompleta.get(indice_focused).setCantidad(
                                cantidad);
                        if (listaArticulosCompleta.get(indice_focused)
                                .getFechaInicio().length() == 0) {
                            listaArticulosCompleta.get(indice_focused)
                                    .setFechaInicio(
                                            new SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm:ss")
                                                    .format(new Date()));
                        }
                        listaArticulosCompleta.get(indice_focused).setFechaFin(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .format(new Date()));
                    } catch (Exception e) {

                        log.log("[-- 1619 --]" + e.toString(), 4);
                        // Si excepcion levantada, es que el contenido del
                        // Edittext no es un Integer, en este caso: -1
                        listaArticulosCompleta.get(indice_focused).setCantidad(
                                -1);
                        showSimpleDialogOK("Error",
                                "Valor incorrecto, ingrese un entero").show();
                        edittext.setText("No Tomado");
                        return;
                    }
                }

                bdd = new BaseDatos(ctxt);
                try {
                    // Cuidado, si el nuevo valor es "No Tomado" (cantidad Q =
                    // -1), hay que suprimir la fecha en la BDD:
                    bdd.updateArticulo(listaArticulosCompleta
                            .get(indice_focused));
                } catch (ExceptionBDD e) {

                    log.log("[-- 1639 --]" + e.toString(), 4);
                    e.printStackTrace();
                    showSimpleDialogOK("Error", e.toString()).show();
                }
            } else {
                // Si decidimos no grabar el valor, entonces reponemos el viejo
                // valor del edittext como antes:
                edittext.setText(String.valueOf(valor_antes_modificar));
                valor_antes_modificar = -1;
            }
        }
    }




    private void deseleccionarLineaParticularSubtotal(int indice_focused,
                                              boolean grabar_si_no) {
        System.out.println("::: PaginaInventarioDinamico deseleccionarLinea int");
        if (indice_focused >= 0) {
            // 1 Buscamos la linea que modificar:
            TableRow lineaFocused = null;
            int numeroLineaFocused = 0;

            for (int j = 0; j < tabla_articulos.getChildCount(); j++) {
                lineaFocused = (TableRow) tabla_articulos.getChildAt(j);

                if (Integer.parseInt(String.valueOf(((TextView) (lineaFocused
                        .getChildAt(7))).getText())) == indice_focused) {
                    numeroLineaFocused = j;
                    break;
                }
            }

            // 2 Cambiamos el color
            lineaFocused.setBackgroundColor(getResources().getColor(
                    R.color.verde_claro));

            EditText edittext = (EditText) lineaFocused.getChildAt(4);
            ImageView viewIma = (ImageView) lineaFocused.getChildAt(6);
            edittext.setFocusable(false);
            if ((indice_primera_linea % 2) == (numeroLineaFocused % 2)) {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro));
            } else {
                edittext.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro_mas));
                viewIma.setBackgroundColor(getResources().getColor(
                        R.color.verde_oscuro_mas));

            }
            edittext.setTextColor(getResources().getColor(
                    R.color.anaranjado_verde));
            viewIma.setBackgroundColor(getResources().getColor(
                    R.color.verde_oscuro_mas));
            tabla_articulos.requestFocus();

            indice_on_focus = -1;

            // se esconde el teclado? para que
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edittext.getWindowToken(), 0);

            // 3 Modificamos el objeto ARTICULO y su valor en la base de datos
            // (si
            // el argumento booleano "grabar_si_no" lo permite):
            System.out.println(listaArticulosCompleta+"***"+grabar_si_no + "******************************");
            if (grabar_si_no == true) {
                String valorDelEdittext = "";
                try {
                    valorDelEdittext = String.valueOf(edittext.getText())
                            .trim();
                    System.out.println(":::No tomado, toma el valor con trim + "+ valorDelEdittext);
                } catch (Exception e) {

                    log.log("[-- 1588 --]" + e.toString(), 4);
                    // Si excepcion levantada, es que el contenido del Edittext
                    // es malo, ponemos valor del articulo a -1 (sin hacer):
                    listaArticulosCompleta.get(indice_focused).setSubtotal(-1);
                    showSimpleDialogOK("Error", "Valor incorrecto (1)").show();
                    edittext.setText("0");
                    return;
                }
                System.out.println(":::Ingresa al no tomado");

                if (valorDelEdittext.length() == 0) {
                    listaArticulosCompleta.get(indice_focused).setSubtotal(-1);
                    edittext.setText("0");
                    // return;
                } else {
                    try {
                        System.out.println("::: Aca hace le proceso de guardado");
                        float subtotal = Float.parseFloat(valorDelEdittext);
                        listaArticulosCompleta.get(indice_focused).setSubtotal(
                                subtotal);
                        if (listaArticulosCompleta.get(indice_focused)
                                .getFechaInicio().length() == 0) {
                            listaArticulosCompleta.get(indice_focused)
                                    .setFechaInicio(
                                            new SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm:ss")
                                                    .format(new Date()));
                        }
                        listaArticulosCompleta.get(indice_focused).setFechaFin(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .format(new Date()));
                    } catch (Exception e) {

                        log.log("[-- 1619 --]" + e.toString(), 4);
                        // Si excepcion levantada, es que el contenido del
                        // Edittext no es un Integer, en este caso: -1
                        listaArticulosCompleta.get(indice_focused).setSubtotal(
                                -1);
                        showSimpleDialogOK("Error",
                                "Valor incorrecto, ingrese un entero").show();
                        edittext.setText("0");
                        return;
                    }
                }

                bdd = new BaseDatos(ctxt);
                System.out.println(listaArticulosCompleta + "******************************");
                try {
                    // Cuidado, si el nuevo valor es "No Tomado" (cantidad Q =
                    // -1), hay que suprimir la fecha en la BDD:

                    bdd.updateArticulo(listaArticulosCompleta
                            .get(indice_focused));
                } catch (ExceptionBDD e) {

                    log.log("[-- 1639 --]" + e.toString(), 4);
                    e.printStackTrace();
                    showSimpleDialogOK("Error", e.toString()).show();
                }
            } else {
                // Si decidimos no grabar el valor, entonces reponemos el viejo
                // valor del edittext como antes:
                edittext.setText(String.valueOf(valor_antes_modificar));
                valor_antes_modificar = -1;
            }
        }
    }
    /**
     * Muestra un dialog con la opcion de OK
     */

    public AlertDialog showSimpleDialogOK(String titulo, String mensaje) {
        System.out.println("::: PaginaInventarioDinamico showSimpledialog");
        log.log("[-- 1658 --]" + "titulo: " + titulo + ", \n mensaje: "
                + mensaje, 3);
        AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
        dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = dialogoSimple.create();
        return alert;
    }

    /**
     * Muestra un dialogo por un tiempo determinado
     *
     * @param titulo
     * @param mensaje
     * @param timer_ms
     * @return
     */
    public AlertDialog showSimpleDialogTimer(String titulo, String mensaje,
                                             final int timer_ms) {
        System.out.println("::: PaginaInventarioDinamico show2");
        log.log("[-- 1680--]" + "titulo: " + titulo + ", \n mensaje: "
                + mensaje + ", \n timer: " + timer_ms, 3);

        AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
        dialogoSimple.setCancelable(false).setTitle(titulo).setMessage(mensaje)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alert = dialogoSimple.create();

        ShowDialogTimer unDialogoTimer = new ShowDialogTimer();
        unDialogoTimer.execute(ctxt);

        return alert;
    }

    /**
     * Muestra un dialog con opcion Si o No, en el caso afirmativo procedemos a
     * la Activity definida por "clase"
     */

    public AlertDialog showSimpleDialogSiNo(String titulo, String mensaje,
                                            final Class<?> clase) {

        log.log("[-- 1705 --]" + "titulo: " + titulo + ", \n mensaje: "
                + mensaje, 3);
        AlertDialog.Builder dialogoSimple = new AlertDialog.Builder(this);
        dialogoSimple
                .setCancelable(false)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int id) {

                        log.log("[-- 1714 --]" + "Se presiono que si", 0);
                        Intent i = new Intent(PaginaCompras.this,
                                clase);
                        startActivity(i);
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int id) {

                        log.log("[-- 1725 --]" + "Se presiono que no", 0);
                        Intent i = new Intent(PaginaCompras.this,
                                clase);
                        startActivity(i);
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alert = dialogoSimple.create();
        return alert;
    }

    /**
     * Funcion para filtrar los articulos a mostrar, evalua los valores de
     * banderas segun el filtro que se aplique
     * <p>
     * 1 Configuramos la visibilidad de todos los articulos
     * <p>
     * &nbsp; &nbsp;1.1 SI hay filtro por sector Y SI el sector del articulo no
     * est en la lista, visibilidad = false
     * <p>
     * &nbsp; &nbsp;1.2 SI hay filtro por precio Y SI el precio no corresponde a
     * los criterios, visibilidad = false
     * <p>
     * &nbsp; &nbsp;1.3 SI hay filtro por inventario hecho o no Y SI el estado
     * no corresponde a los criterios, visibilidad = false
     * <p>
     * 2 Deseleccionamos la linea en focus
     */
    private void filtrar() {
        numero_articulos_visibles = 0;
        // 1 Configuramos la visibilidad de todos los articulos:
        for (ArticuloVisible a : listaArticulosCompleta) {

            // 1.1 SI hay filtro por sector Y SI el sector del articulo no est
            // en
            // la lista, visibilidad = false:
            if (listaSectoresFiltrados.size() > 0
                    && listaSectoresFiltrados.contains((Object) a.getSector()) == false) {
                a.setVisibilidad(false);
                continue;
            }

            // 1.2 SI hay filtro por precio Y SI el precio no corresponde a los
            // criterios, visibilidad = false:
            if (precioFiltro != 0) {
                if (precioFiltro > 0 && a.getPrecio_venta() < precioFiltro) {
                    a.setVisibilidad(false);
                    continue;
                } else if (precioFiltro < 0
                        && a.getPrecio_venta() > Math.abs(precioFiltro)) {
                    a.setVisibilidad(false);
                    continue;
                }
            }

            // 1.3 SI hay filtro por inventario hecho o no Y SI el estado no
            // corresponde a los criterios, visibilidad = false:
            if (inventarioFiltro != 0) {
                if (inventarioFiltro > 0 && a.getCantidad() < 0) {
                    a.setVisibilidad(false);
                    continue;
                } else if (inventarioFiltro < 0 && a.getCantidad() >= 0) {
                    a.setVisibilidad(false);
                    continue;
                }
            }
            a.setVisibilidad(true);
            numero_articulos_visibles++;
        } // end for
        // 2 Deseleccionamos la linea en focus
        deseleccionarLineaParticular(indice_on_focus);
        try {
            indice_primera_linea = getIndiceArticuloVisibleMasCercano(0);
        } catch (Exception e) {
            indice_primera_linea = 0;
            loadingBar.setVisibility(View.GONE);
            showSimpleDialogOK("ERROR de filtraje", "Imposible filtrar").show();
            return;
        }
        refreshTablaCentral();
        loadingBar.setVisibility(View.GONE);
    }

    /**
     * Muestra el menu del filtro a aplicar, actualiza las banderas
     * correspondientes?
     * <p>
     * 1 Trabajamos con copias de las 3 variables de filtro, asi en caso de
     * "CANCEL" no modificamos nada
     * <p>
     * 2 Segun el tipo de filtro guardamos la informacion y las banderas del
     * mismo
     * <p>
     * 3 Aplicar modificaciones desde las variables filtro hasta las
     * <p>
     * 4 Mostrar el icono de filtro
     * <p>
     * 5 Actualizar el filtrado en la UI llamando a filtrar()
     *
     * @param tipoFiltro
     */
    public void showMenuFiltro(int tipoFiltro) {
        System.out.println("::: PaginaInventarioDinamico mostrarFiltrar");
        log.log("[-- 1829 --]" + "Se inicia el menu de filtrado", 2);
        loadingBar.setVisibility(View.VISIBLE);
        loadingBar.bringToFront();
        dialogo = new Dialog(ctxt);
        LinearLayout layoutMenu = new LinearLayout(ctxt);
        layoutMenu.setOrientation(LinearLayout.VERTICAL);

        // 1Trabajamos con copias de las 3 variables de filtro, asi en caso de
        // "CANCEL" no modificamos nada:
        copia_listaSectoresFiltrados.clear();
        for (int i : listaSectoresFiltrados) {
            copia_listaSectoresFiltrados.add(i);
        }
        copia_precioFiltro = precioFiltro;
        copia_inventarioFiltro = inventarioFiltro;

        // 2 Segun el tipo de filtro guardamos la informacion y las banderas
        // del mismo
        if (tipoFiltro == ParametrosInventario.filtro_sector) {

            dialogo.setTitle("Filtrar por sector");
            for (int sector : getListaTodosSectores()) {
                CheckBox chkb = new CheckBox(ctxt);
                chkb.setText("Sector " + String.valueOf(sector));
                chkb.setTextSize(ParametrosInventario.TALLA_TEXTO);
                chkb.setId(Parametros.ID_CHECKBOX + sector);
                if (listaSectoresFiltrados.contains(sector) == true) {
                    chkb.setChecked(true);
                }
                chkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(@NonNull CompoundButton checkbox,
                                                 boolean isChecked) {

                        log.log("[-- 1863 --]" + "Se cambia de estado chkb", 0);
                        if (isChecked == true) {
                            copia_listaSectoresFiltrados.add(checkbox.getId()
                                    - Parametros.ID_CHECKBOX);
                        } else {
                            copia_listaSectoresFiltrados
                                    .remove((Object) (checkbox.getId() - Parametros.ID_CHECKBOX));
                        }
                    }
                });
                layoutMenu.addView(chkb);
            }
        } else if (tipoFiltro == ParametrosInventario.filtro_precio) {
            System.out.println("::: PaginaInventarioDinamico r1");
            dialogo.setTitle("Filtrar por precio");

            RadioButton rdioSup = new RadioButton(ctxt);
            rdioSup.setText("Precios superiores a: ");
            rdioSup.setTextSize(ParametrosInventario.TALLA_TEXTO);
            rdioSup.setId(1 + Parametros.ID_RADIOBUTTON);

            RadioButton rdioInf = new RadioButton(ctxt);
            rdioInf.setText("Precios inferiores a: ");
            rdioInf.setTextSize(ParametrosInventario.TALLA_TEXTO);
            rdioInf.setId(-1 + Parametros.ID_RADIOBUTTON);

            RadioButton rdioSin = new RadioButton(ctxt);
            rdioSin.setText("Sin filtrar");
            rdioSin.setTextSize(ParametrosInventario.TALLA_TEXTO);
            //rdioSin.setId(Parametros.ID_RADIOBUTTON);

            EditText valorUmbral = new EditText(ctxt);
            valorUmbral.setSingleLine(true);
            valorUmbral.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
            //valorUmbral.setId(Parametros.ID_EDITTEXT);
            valorUmbral.setTextSize(ParametrosInventario.TALLA_TEXTO);

            if (precioFiltro < 0) {
                rdioInf.setChecked(true);
                valorUmbral.setEnabled(true);
                valorUmbral
                        .setText(String.valueOf(Math.abs((int) precioFiltro)));
                copia_precioFiltro = -1;
            } else if (precioFiltro > 0) {
                rdioSup.setChecked(true);
                valorUmbral.setEnabled(true);
                valorUmbral
                        .setText(String.valueOf(Math.abs((int) precioFiltro)));
                copia_precioFiltro = 1;
            } else if (precioFiltro == 0) {
                rdioSin.setChecked(true);
                valorUmbral.setEnabled(false);
                valorUmbral.setText("");
                copia_precioFiltro = 0;
            }

            rdioInf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton view,
                                             boolean isChecked) {
                    System.out.println("::: PaginaInventarioDinamico r2");
                    if (isChecked == true) {

                        log.log("[-- 1924 --]" + "Cambia de estado rdioInf", 0);
                        copia_precioFiltro = -1;

                        RadioButton rbSup = (RadioButton) dialogo
                                .findViewById(1 + Parametros.ID_RADIOBUTTON);
                        rbSup.setChecked(false);

                        RadioButton rbSin = (RadioButton) dialogo
                                .findViewById(0 + Parametros.ID_RADIOBUTTON);
                        rbSin.setChecked(false);

                    //    EditText valorUmbral = (EditText) dialogo
                     //           .findViewById(Parametros.ID_EDITTEXT);
                     //   valorUmbral.setEnabled(true);
                    }
                }
            });

            rdioSup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton view,
                                             boolean isChecked) {
                    System.out.println("::: PaginaInventarioDinamico r3");
                    if (isChecked == true) {

                        log.log("[-- 1948 --]" + "Cambia de estado rdioSup", 0);
                        copia_precioFiltro = 1;

                        RadioButton rbInf = (RadioButton) dialogo
                                .findViewById(-1 + Parametros.ID_RADIOBUTTON);
                        rbInf.setChecked(false);

                        RadioButton rbSin = (RadioButton) dialogo
                                .findViewById(0 + Parametros.ID_RADIOBUTTON);
                        rbSin.setChecked(false);

//                        EditText valorUmbral = (EditText) dialogo
  //                              .findViewById(Parametros.ID_EDITTEXT);
    //                    valorUmbral.setEnabled(true);
                    }
                }
            });

            rdioSin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton view,
                                             boolean isChecked) {
                    System.out.println("::: PaginaInventarioDinamico r4");
                    if (isChecked == true) {
                        log.log("[-- 1971 --]" + "Cambia de estado rdioSin", 0);
                        copia_precioFiltro = 0;

                        RadioButton rbSup = (RadioButton) dialogo
                                .findViewById(1 + Parametros.ID_RADIOBUTTON);
                        rbSup.setChecked(false);

                        RadioButton rbInf = (RadioButton) dialogo
                                .findViewById(-1 + Parametros.ID_RADIOBUTTON);
                        rbInf.setChecked(false);

//                        EditText valorUmbral = (EditText) dialogo
  //                              .findViewById(Parametros.ID_EDITTEXT);
  //                      valorUmbral.setEnabled(false);
                        copia_precioFiltro = 0;
                    }
                }
            });

            layoutMenu.addView(rdioSin);
            layoutMenu.addView(rdioSup);
            layoutMenu.addView(rdioInf);
            layoutMenu.addView(valorUmbral);
        } else if (tipoFiltro == ParametrosInventario.filtro_inventario) {
            dialogo.setTitle("Filtrar por estado del inventario");

            System.out.println("::: PaginaInventarioDinamico r5");

            RadioButton rdioSin = new RadioButton(ctxt);
            rdioSin.setText("Sin filtrar");
            rdioSin.setTextSize(ParametrosInventario.TALLA_TEXTO);
            //   rdioSin.setId(Parametros.ID_RADIOBUTTON);

            RadioButton rdioHecho = new RadioButton(ctxt);
            rdioHecho.setText("Articulos ya inventariados");
            rdioHecho.setTextSize(ParametrosInventario.TALLA_TEXTO);
            rdioHecho.setId(Parametros.ID_RADIOBUTTON + 2);

            RadioButton rdioVirgen = new RadioButton(ctxt);
            rdioVirgen.setText("Articulos NO inventariados");
            rdioVirgen.setTextSize(ParametrosInventario.TALLA_TEXTO);
            rdioVirgen.setId(Parametros.ID_RADIOBUTTON - 2);

            if (copia_inventarioFiltro == 1) {
                rdioHecho.setChecked(true);
            } else if (copia_inventarioFiltro == -1) {
                rdioVirgen.setChecked(true);
            } else if (copia_inventarioFiltro == 0) {
                rdioSin.setChecked(true);
            }

            rdioVirgen
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        public void onCheckedChanged(CompoundButton view,
                                                     boolean isChecked) {

                            System.out.println("::: PaginaInventarioDinamico r7");

                            log.log("[-- 2026 --]"
                                    + "Cambia de estado rdioVirgen", 4);
                            if (isChecked == true) {
                                RadioButton rbSin = (RadioButton) dialogo
                                        .findViewById(0 + Parametros.ID_RADIOBUTTON);
                                rbSin.setChecked(false);
                                RadioButton rbHecho = (RadioButton) dialogo
                                        .findViewById(2 + Parametros.ID_RADIOBUTTON);
                                rbHecho.setChecked(false);

                                copia_inventarioFiltro = -1;
                            }
                        }
                    });

            rdioHecho
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton view,
                                                     boolean isChecked) {
                            System.out.println("::: PaginaInventarioDinamico r8");
                            if (isChecked == true) {

                                log.log("[-- 2046 --]"
                                        + "Cambia de estado rdioHecho", 0);
                                RadioButton rbSin = (RadioButton) dialogo
                                        .findViewById(0 + Parametros.ID_RADIOBUTTON);
                                rbSin.setChecked(false);
                                RadioButton rbVirgen = (RadioButton) dialogo
                                        .findViewById(-2
                                                + Parametros.ID_RADIOBUTTON);
                                rbVirgen.setChecked(false);

                                copia_inventarioFiltro = 1;
                            }
                        }
                    });

            rdioSin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton view,
                                             boolean isChecked) {
                    System.out.println("::: PaginaInventarioDinamico r9");

                    if (isChecked == true) {

                        log.log("[-- 2065 --]" + "Cambia de estado rdioSin", 0);
                        RadioButton rbHecho = (RadioButton) dialogo
                                .findViewById(2 + Parametros.ID_RADIOBUTTON);
                        rbHecho.setChecked(false);
                        RadioButton rbVirgen = (RadioButton) dialogo
                                .findViewById(-2 + Parametros.ID_RADIOBUTTON);
                        rbVirgen.setChecked(false);

                        copia_inventarioFiltro = 0;
                    }
                }
            });

            layoutMenu.addView(rdioSin);
            layoutMenu.addView(rdioHecho);
            layoutMenu.addView(rdioVirgen);
        }

        LinearLayout layoutBotones = new LinearLayout(ctxt);
        Button boton_cancel = new Button(ctxt);
        boton_cancel.setText("Cancel");
        boton_cancel.setTextSize(ParametrosInventario.TALLA_TEXTO);
        Button boton_validar = new Button(ctxt);
        boton_validar.setText("Validar");
        boton_validar.setTextSize(ParametrosInventario.TALLA_TEXTO);
        boton_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogo.cancel();
                loadingBar.setVisibility(View.GONE);
            }
        });
        boton_validar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                System.out.println("::: PaginaInventarioDinamico r10");

                log.log("[-- 2101 --]" + "Se presiono el boton validar", 0);
                dialogo.dismiss();
                loadingBar.setVisibility(View.VISIBLE);
                loadingBar.bringToFront();

                // 3 Aplicar modificaciones desde las variables filtro hasta
                // las
                // variables reales:
                listaSectoresFiltrados.clear();
                for (int j : copia_listaSectoresFiltrados) {
                    listaSectoresFiltrados.add(j);
                }

                inventarioFiltro = copia_inventarioFiltro;

                // 4 Mostrar el icono de filtro:
                if (listaSectoresFiltrados.size() > 0) {
                    filtro_codigo.setVisibility(View.VISIBLE);
                } else {
                    filtro_codigo.setVisibility(View.INVISIBLE);
                }

                if (precioFiltro != 0) {
                    filtro_precio.setVisibility(View.VISIBLE);
                } else {
                    filtro_precio.setVisibility(View.INVISIBLE);
                }

                if (inventarioFiltro != 0) {
                    filtro_cantidad.setVisibility(View.VISIBLE);
                } else {
                    filtro_cantidad.setVisibility(View.INVISIBLE);
                }

                // 5 Actualizar el filtrado en la UI
                filtrar();

            }
        });
        layoutBotones.addView(boton_validar);
        layoutBotones.addView(boton_cancel);
        layoutBotones.setGravity(Gravity.CENTER_VERTICAL);
        layoutMenu.addView(layoutBotones);
        dialogo.setContentView(layoutMenu);
        dialogo.show();
    }

    /**
     * Devuelve todos los sectores posibles para el filtrado
     *
     * @return
     */
    @NonNull
    private ArrayList<Integer> getListaTodosSectores() {
        System.out.println("::: PaginaInventarioDinamico Devolver sectores para el filtrado");
        ArrayList<Integer> listaRespuesta = new ArrayList<Integer>();

        for (Articulo articulo : listaArticulosCompleta) {
            if (listaRespuesta.contains((int) articulo.getSector()) == false) {
                listaRespuesta.add(articulo.getSector());
            }
        }

        return listaRespuesta;
    }

    /**
     * Esta foncion se usa despues de un cambio de orden o la aplicacin de un
     * filtro para encontrar el articulo VISIBLE mas cercano al indice anterior,
     * dado que el articulo que se presenta en el indice enfocado anteriormente
     * puede no ser visible como su predecesor.
     *
     * Hacemos una busqueda al indice. Si no esta visible, miramos el siguiente.
     * Si no esta visible, buscamos a la posicion anterior de la posicion
     * inicial. El indice varia segun +1, -2, +3, -4, +5, -6, etc...
     *
     * <p>
     * 1 Verifica que haya al menos un articulo visible
     * <p>
     * 2 Mientras no se encuentre el articulo visible en la lista de todos los
     * articulos
     * <p>
     * &nbsp; &nbsp;2.1 Actualiza el indice segun el paso
     * <p>
     * &nbsp; &nbsp;3.1 Verifica que este el articulo visible en la lista con
     * ese indice
     *
     * @param indice_anterior
     * @return indice del articulo visible ms cercano por arriba o abajo
     * @throws Exception
     */
    private int getIndiceArticuloVisibleMasCercano(int indice_anterior)
            throws Exception {
        System.out.println("::: PaginaInventarioDinamico mascercano");
        ArticuloVisible a = new ArticuloVisible(false);
        int indice = indice_anterior;
        int paso = -1;

        // 1 Verifica que haya al menos un articulo visible
        boolean hayMinimoUnArticuloVisible = false;
        for (ArticuloVisible av : listaArticulosCompleta) {
            if (av.esVisible() == true) {
                hayMinimoUnArticuloVisible = true;
                break;
            }
        }

        if (hayMinimoUnArticuloVisible == false) {
            // Toast.makeText(ctxt, "Ningun articulo visible en la lista",
            // Toast.LENGTH_SHORT).show();
            throw new Exception("Ningun articulo visible en la lista");

        }
        System.out.println("::: PaginaInventarioDinamico mascercano 4");

        do {
            paso++;
            // 2 Actualiza el indice segun el paso
            if ((paso % 2) == 1) {
                indice = indice + paso;
            } else {
                indice = indice - paso;
            }

            System.out.println("::: PaginaInventarioDinamico mascercano 5");

            try {
                // 3 Verifica que este el articulo en la lista con ese indice
                a = listaArticulosCompleta.get(indice);
            } catch (Exception e) {

                log.log("[-- 2241 --]" + e.toString(), 4);
            }

            System.out.println("::: PaginaInventarioDinamico mascercano 6");

        } while (a.esVisible() == false);

        return indice;
    }

    /**
     * Idem anterior pero busca para arriba Buscamos al articulo visible
     * anterior al nuestro en el cual el operador ha clicado.Es decir que
     * hacemos una busqueda al indice anterior, de -1 en -1 hasta topar con un
     * indice correspondiendo a un articulo visible. Si topamos con 0 antes de
     * encontrar un articulo visible, es que indice_anterior es el primer
     * articulo visible de la lista.
     *
     * @param indice_anterior
     * @return
     * @throws Exception
     */
    private int getIndiceArticuloVisibleMasCercanoPorArriba(int indice_anterior)
            throws Exception {
        System.out.println("::: PaginaInventarioDinamico indiceArticuloVisiblecercano");
        ArticuloVisible a = new ArticuloVisible(false);
        int indice = indice_anterior;

        if (indice == 0) {
            return getIndiceArticuloVisibleMasCercano(0);
        }

        indice--;
        a = listaArticulosCompleta.get(indice);

        while (a.esVisible() == false) {
            indice--;

            try {
                a = listaArticulosCompleta.get(indice);
            } catch (Exception e) {
                log.log("[-- 2279 --]" + e.toString(), 4);
                return indice_anterior;
            }
        }

        return indice;
    }

    /**
     * Busca los indices de los proximos articulos visibles a partir del indice
     * dado
     * <p>
     * 1 Buscamos el articulo visible mas cercano a nuestra incice-posicion
     * actual
     * <p>
     * 2 Buscar los 7 elementos que siguen. Atencin, puede no haber 7, puede
     * haber menos
     * <p>
     * 3 Actualizamos el indice de la primera linea
     *
     * @param indice_actual
     * @param hay_indice_on_focus
     * @return una lista con indices de los articulos visibles en la lista
     * @throws Exception
     */
    @NonNull
    private ArrayList<Integer> getProximosIndicesArticulosVisibles(
            int indice_actual, boolean hay_indice_on_focus) throws Exception {
        System.out.println("::: PaginaInventarioDinamico 2525");
        // 1 Buscamos el articulo visible mas cercano a nuestra
        // incice-posicion actual:
        int indice_inicial = 0;
        if (hay_indice_on_focus == false) {
            // try {
            indice_inicial = getIndiceArticuloVisibleMasCercano(indice_actual);
            // }catch(Exception e) {
            // //No hay articulos visibles en la lista. que hacemos en este
            // caso?
            //
            // }
        } else {
            indice_inicial = getIndiceArticuloVisibleMasCercanoPorArriba(indice_actual);
        }

        // 2 Buscar los 7 elementos que siguen:
        // Atencin, puede no haber 7, puede haber menos:
        ArrayList<Integer> listaRespuesta = new ArrayList<Integer>();
        listaRespuesta.add(indice_inicial);
        int index = indice_inicial + 1;

        while (listaRespuesta.size() < NUMERO_LINEAS_EN_TABLA) {
            try {
                ArticuloVisible av = listaArticulosCompleta.get(index);
                if (av.esVisible() == true) {
                    listaRespuesta.add(index);
                }
                index++;

            } catch (Exception e) {

                log.log("[-- 2337 --]" + e.toString(), 4);
                // Error: si llegamos aca, es que estamos al fin de
                // listaArticulosCompleta, entonces paramos.
                break;
            }
        }
        // 3 Actualizamos el indice de la primera linea
        indice_primera_linea = listaRespuesta.get(0);
        // Fin: que sea el break o que hayamos alcanzado los 8 elementos,
        // devolvemos resultado:
        return listaRespuesta;
    }

    /**
     * Funcion para mover la tabla de articulos tantas unidades como lo indica
     * su argumento
     *
     * @param unidades
     */
    private void moverTablaArticulos(int unidades) {
        System.out.println("::: PaginaInventarioDinamico 2577");
        if (unidades != 0) {
            deseleccionarLineaParticular(indice_on_focus);

            int contador = Math.abs(unidades);
            int paso = 0;
            if (unidades > 0) {
                paso = 1;
            } else {
                paso = -1;
            }
            int indice_busqueda = indice_primera_linea;

            try {
                while (contador > 0) {
                    indice_busqueda += paso;
                    ArticuloVisible av = listaArticulosCompleta
                            .get(indice_busqueda);
                    if (av.esVisible() == true) {
                        contador--;
                        indice_primera_linea = indice_busqueda;
                    }
                }
            } catch (Exception e) {

                log.log("[-- 2381 --]" + e.toString(), 4);
                // Bloc catch: hemos sobrepasado el indice mas grande de la
                // tabla (si unidades>0),
                // o hemos alcanzado el indice -1 de la tabla y seguimos
                // buscando
                // => NO HAY POSIBILIDAD DE BAJAR O SUBIR MAS, NOS PARAMOS AL
                // EXTREMO VISIBLE:
                // (indice_primera_linea ya est actualizado de todos modos)
            }

            refreshTablaCentral();
        }
    }

    /**
     * A partir de un indice de un elemento de la tabla_articulos_completa
     * VISIBLE, devuelve su posicion relativa con respecto a los solos elementos
     * VISIBLES de la tabla (EN BASE 0!). <br/>
     * <br/>
     * Nota: esta funcin puede actualizar el numero_articulos_visibles si
     * pedido en parametro <br/>
     * <br/>
     * Cuidado: la tabla debe tener como minimo 1 elemento visible: el
     * parametro.
     *
     */
    private int getPosicionRelativa(int indice_elemento, boolean actualizarFull) {
        System.out.println("::: PaginaInventarioDinamico 2630");
        int resultado = 0;

        // Actualizacin parcial:
        if (actualizarFull == false) {
            for (int k = 0; k < indice_elemento; k++) {
                ArticuloVisible av = listaArticulosCompleta.get(k);
                if (av.esVisible() == true) {
                    resultado++;
                }
            }
            resultado++; // 1 vez ms para llegar hasta el elemento buscado
        }
        // Actualizacin completa:
        else {
            numero_articulos_visibles = 0;
            for (int k = 0; k < listaArticulosCompleta.size(); k++) {
                ArticuloVisible av = listaArticulosCompleta.get(k);
                if (av.esVisible() == true) {
                    numero_articulos_visibles++;
                    if (k <= indice_elemento) {
                        resultado++;
                    }
                }
            }
        } // end if

        return resultado - 1;
    }


    void mostrarMensaje(int valorRecibido){
        if (ParametrosInventario.InventariosDeposito) {

            if(valorRecibido == 0){
                Toast.makeText(ctxt,
                        "El articulo no tiene habilitado el deposito",
                        Toast.LENGTH_LONG).show();
            }

        }

    }


    private boolean buscarArticulosNoTomados(int inventario_curso) throws ExceptionBDD {
        boolean result;
        BaseDatos bdd = new BaseDatos(ctxt);
            if (bdd.buscarArticulosNoTomadosBD(inventario_curso)== true) {
                result = true;
            }else{
                result = false;
            }
        return result;
    }
    /**
     * Funcion accesoria para cargar los handlers de todos los elementos que lo
     * necesiten
     */
    private void cargar_handlers() {
        // HANDLERS:
        // Boton de SALIDA:
        System.out.println("::: PaginaInventarioDinamico 2667");
        boton_salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(@NonNull View v) {
                try {
            System.out.println("::: PaginaCompras 3181 inventario en curso salir " + inventario_numero_en_curso);
                    ArrayList<HashMap<Integer, Object>> hay_notomados = null;
                    bdd = new BaseDatos(ctxt);
                    BaseDatos bdd = new BaseDatos(ctxt);
                    System.out.println("::: 2 ComprasMainBoard CUAL ABREEEE " + v.getId());
                    boolean hay_no_tomados = false;
                    if (buscarArticulosNoTomados(inventario_numero_en_curso)) {
                        System.out.println("::: BD PaginaCompras HAY ARTIIII NMO TOMADOOSOSOS");
                        dialogoFin = new AlertDialog.Builder(PaginaCompras.this);
                        dialogoFin
                                .setTitle("Articulos no contados")
                                .setMessage(
                                        "Se encuentran articulos con stock en '0' o 'No Tomado' .\n"
                                                + "No seran cargados en la compra al momento de exportar.")
//                                .setTitle("Fin de las mediciones")
//                                .setMessage(
  //                                      "Todas los inventarios han sido procesados exitosamente.\n"
    //                                            + "Por favor, dirijase hacia el central de control para descargar los datos de los inventarios al servidor.")
                                .setCancelable(false)
                                .setNeutralButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(@NonNull DialogInterface dialog,
                                                                int which) {
                                                dialog.dismiss();
                                                    setResult(RESULT_OK, intentPadre);
                                                   finish();
                                            }
                                        });
                        AlertDialog alert = dialogoFin.create();
                        alert.show();
                    }else{
                        if (indice_on_focus >= 0) {
                            TableRow linea = (TableRow) tabla_articulos
                                    .getChildAt(indice_on_focus);
                            EditText edittext = (EditText) linea.getChildAt(3);
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(edittext.getWindowToken(),
                                    0);
                            deseleccionarLineaParticular(indice_on_focus);
                        }
                        setResult(RESULT_OK, intentPadre);
                        finish();
                    }

                } catch (Exception e) {

                    log.log("[-- 2461 --]" + e.toString(), 4);
                    e.printStackTrace();
                } catch (ExceptionBDD exceptionBDD) {
                    exceptionBDD.printStackTrace();
                } finally {
                //    setResult(RESULT_OK, intentPadre);
                 //   finish();
                }
            }
        });

        Ic_Lectora_Din.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode(Ic_Lectora_Din);
            }
        });

        // Boton de BUSQUEDA: Hay que modificarlo para que busque en las
        // referencias si el articulo no esta en la lista
        boton_busqueda.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Deseleccionamos la linea actual:
                deseleccionarLineaParticular(indice_on_focus);

                // Construccin de la lista de los nombre de todos los
                // productos:
                ArrayList<String> lista_todos_nombres = new ArrayList<String>();

                for (ArticuloVisible articulo : listaArticulosCompleta) {
                    if (articulo.esVisible()) {
                        lista_todos_nombres.add(articulo.getDescripcion());
                    }
                }

                // ////////////////////////
                // BOTON CANCELAR //
                // ////////////////////////
                View.OnClickListener listenerCancelar = new View.OnClickListener() {

                    public void onClick(View v) {
                        edittextBusqueda = (EditText) dialogoBusqueda
                                .findViewById(R.id.Z_DIALOG_editext);
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(
                                edittextBusqueda.getWindowToken(), 0);
                        SystemClock.sleep(20);
                        dialogoBusqueda.cancel();
                    }
                };

                // //////////////////////
                // BOTON BUSCAR //
                // //////////////////////
                View.OnClickListener listenerBusqueda = new View.OnClickListener() {

                    public void onClick(View v) {
                        // 1) Recuperamos el string buscado:
                        String busqueda = dialogoBusqueda.get_busqueda();

                        // 2) Corremos la busqueda en la base de datos:
                        ArrayList<HashMap<Integer, Object>> lista_resultados = null;
                        bdd = new BaseDatos(ctxt);
                        try {
                            // Esto va a cambiar, se buscaran articulos en las
                            // referencias para agregarlo
                            // lista_resultados =
                            // bdd.buscar(inventario_numero_en_curso, busqueda);
                            // Deberia devolverme una lista de articulos
                            lista_resultados = bdd
                                    .buscarEnReferencias(busqueda);
                            //
                        } catch (ExceptionBDD e) {

                            log.log("[-- 2529 --]" + e.toString(), 4);
                            e.printStackTrace();

                            if (e.getCodigo() == ExceptionBDD.ERROR_NO_RESULT_UNEXPECTED) {
                                Toast.makeText(ctxt,
                                        "El proceso de busqueda ha fracasado",
                                        Toast.LENGTH_LONG).show();
                                return;
                            } else if (e.getCodigo() == ExceptionBDD.ERROR_TOO_MANY_RESULTS) {
                                Toast.makeText(
                                        ctxt,
                                        "La busqueda devolvio demasiado articulos, por favor afine el termino",
                                        Toast.LENGTH_LONG).show();
                                return;
                            } else if (e.getCodigo() == ExceptionBDD.ERROR_TIPO_SELECT) {
                                Toast.makeText(
                                        ctxt,
                                        "El proceso de busqueda ha fracasado con el siguiente error: "
                                                + e.toString(),
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        // 3) Escondemos el teclado virtual:
                        edittextBusqueda = (EditText) dialogoBusqueda.findViewById(R.id.Z_DIALOG_editext);
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edittextBusqueda.getWindowToken(), 0);

                        // 4) Cerrar el menu anterior:
                        dialogoBusqueda.cancel();

                        // Nueva version, MODIFICAR para que inserte en la bd y
                        // actualize todo
                        View.OnClickListener listenerEleccionRespuesta = new View.OnClickListener() {

                            public void onClick(View v) {

                                log.log("[-- 2639 --]"
                                                + "Se hizo clic en Elegicon Respeusta",
                                        0);
                                // toDo;
                                LinearLayout tableR = (LinearLayout) v;
                                articulo_resultado_busqueda = dialogoResultados.get_codigos_articulo_seleccionado();
                                // int indice =
                                // get_indice_con_articulos(articulo_resultado_busqueda);
                                // buscar el articulo en las referencias
                                int codigo = articulo_resultado_busqueda.get(ParametrosInventario.clave_art_codigo);
                                int sector = articulo_resultado_busqueda.get(ParametrosInventario.clave_art_sector);

                                ArticuloVisible art = null;
                                try {
                                    art = bdd.buscarArticuloEnReferencias(codigo, sector);
                                } catch (ExceptionBDD e1) {

                                    log.log("[-- 2658 --]" + e1.toString(), 4);
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                    Toast.makeText(
                                            ctxt,
                                            "El proceso de busqueda del articulo "
                                                    + "ha fracasado con el siguiente error: "
                                                    + e1.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                                if (art != null) {

                                    boolean esta = false;
                                    for (ArticuloVisible aux : listaArticulosCompleta) {
                                        if (aux.getCodigo() == art.getCodigo()
                                                && aux.getSector() == art
                                                .getSector()) {
                                            esta = true;
                                            break;
                                        }
                                    }

                                    // Si no esta en la lista de articulos
                                    if (!esta) {
                                        // Modificar valores necesarios
                                        art.setInventario(inventario_numero_en_curso);
                                        // Agregarlo a la lista
                                        listaArticulosCompleta.add(art);
                                        // Meterlo en la BD
                                        try {
//											System.out.println("::::: LLAMADO 1 " + art.getDepsn());
                                            bdd.insertArticuloEnBdd(art);
                                            mostrarMensaje(art.getDepsn());
                                        } catch (ExceptionBDD e) {

                                            log.log("[-- 2691 --]"
                                                    + e.toString(), 4);
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                            Toast.makeText(
                                                    ctxt,
                                                    "El proceso de actualizacion del articulo "
                                                            + "ha fracasado con el siguiente error: "
                                                            + e.toString(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        // Refrescar la tabla
                                        actualizarPaginaInventario(inventario_numero_en_curso);
                                        dialogoResultados.cancel();
                                        // Mostrarlo en la lista
                                        int indice = get_indice_con_articulos(articulo_resultado_busqueda);
                                        seleccionarMostrarIndiceArticulo(indice);


                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(
                                                InputMethodManager.SHOW_FORCED,
                                                0);
                                        // asd;

                                    } else {
                                        // Si esta en la lista
                                        // toDo;
                                        int indice = get_indice_con_articulos(articulo_resultado_busqueda);
                                        // indice_on_focus=indice;
                                        // o, o ambas
                                        // deseleccionarLineaParticular(indice);

                                        // dialogoResultados.dismiss();
                                        dialogoResultados.cancel();
                                        // Mostrarlo en la lista
                                        seleccionarMostrarIndiceArticulo(indice);

                                    }
                                } else {
                                    Toast.makeText(
                                            ctxt,
                                            "No se encontro el articulo con codigo = "
                                                    + codigo + ", y sector = "
                                                    + sector, Toast.LENGTH_LONG)
                                            .show();
                                }

                            }
                        };

                        View.OnClickListener listenerCancel = new View.OnClickListener() {

                            public void onClick(View v) {

                                log.log("[-- 2773 --]" + "Se presiono cancelar",
                                        0);
                                dialogoResultados.cancel();

                                dialogoBusqueda.show();

								/*
								 * edittextBusqueda = (EditText)
								 * dialogoBusqueda.
								 * findViewById(R.id.Z_DIALOG_editext);
								 * InputMethodManager mgr = (InputMethodManager)
								 * getSystemService
								 * (Context.INPUT_METHOD_SERVICE);
								 * mgr.showSoftInput(edittextBusqueda,
								 * InputMethodManager.SHOW_FORCED);
								 */
                            }
                        };

                        dialogoResultados = new DialogPersoComplexResultados(
                                ctxt, lista_resultados,
                                listenerEleccionRespuesta, listenerCancel);
                        dialogoResultados
                                .setOnShowListener(new DialogInterface.OnShowListener() {

                                    public void onShow(DialogInterface dialog) {
                                        fueCanceladoDialogoResultados = false;
                                    }
                                });
                        dialogoResultados
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                    public void onDismiss(DialogInterface dialog) {
                                        respuestaSeleccionada = -99;
                                        if (fueCanceladoDialogoResultados == false) {
                                            // Segun el indice devuelto,
                                            // hacemos:
                                            int indice_nuevo = indice_on_focus;

                                            if (indice_on_focus >= 0) {
                                                seleccionarMostrarIndiceArticulo(indice_nuevo);
                                            } else {
                                                showSimpleDialogTimer(
                                                        "Articulo oculto",
                                                        "El resultado de su busqueda es un articulo bloqueado por un filtro!",
                                                        5000).show();
                                            }
                                        }
                                    }
                                });
                        dialogoResultados
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                    public void onCancel(DialogInterface dialog) {

                                        log.log("[-- 2827 --]"
                                                        + "Se presiono cancelar en Dialogo Resultados",
                                                0);
                                        fueCanceladoDialogoResultados = true;
                                    }
                                });
                        dialogoResultados.show();
                    }
                };

                dialogoBusqueda = new DialogPersoComplexBusqueda(
                        ctxt,
                        "Buscar un articulo",
                        "Esta herramienta de busqueda le permitira encontrar un articulo en particular. \n\n"
                                + "Por favor, ingrese el nombre del articulo deseado en el campo situado mas abajo. \n\n",
                        inventario_numero_en_curso, listenerBusqueda,
                        listenerCancelar);
                dialogoBusqueda
                        .setOnShowListener(new DialogInterface.OnShowListener() {

                            public void onShow(DialogInterface dialog) {

                                log.log("[-- 2847 --]"
                                                + "Se muestra el cuador de dialogo de Busqueda",
                                        0);
                                edittextBusqueda = (EditText) dialogoBusqueda
                                        .findViewById(R.id.Z_DIALOG_editext);
                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgr.showSoftInput(edittextBusqueda,
                                        InputMethodManager.SHOW_FORCED);
                            }
                        });
                dialogoBusqueda.show();

            }
        });

        boton_busqueda.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, @NonNull MotionEvent event) {

                log.log("[-- 2864 --]" + "Se presiona el boton de busqueda", 0);
                ImageView imgV = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imgV.setBackgroundColor(getResources().getColor(
                            R.color.amarillo_oscuro_mas));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imgV.setBackgroundColor(getResources().getColor(
                            android.R.color.transparent));
                }
                return false;
            }
        });

        // Click on boton lectora (activacion de modo +1):
        boton_lectora.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {

                log.log("[-- 2883 --]" + "Se presiona la lectora ", 0);
                ImageView imageV = (ImageView) v;
                if (modo_mas_1 == 0) {
                    log.log("[-- 2886 --]" + "modo_mas_1 paso a 1", 2);
                    modo_mas_1 = 1;
                    imageV.setBackgroundColor(getResources().getColor(
                            R.color.anaranjado_oscuro));
                } else {

                    log.log("[-- 2892--]" + "modo_mas_1 paso a 0", 4);
                    modo_mas_1 = 0;
        //            imageV.setBackgroundColor(android.R.color.transparent);
                }
                refrescarMensaje();
                return true;
            }
        });


        // Touch on flecha:
        flecha_arriba.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, @NonNull MotionEvent event) {

                log.log("[-- 2906 --]" + "Se presiono la flecha arriba", 0);
                ImageView imgV = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imgV.setImageDrawable(getResources().getDrawable(
                            R.drawable.boton_up_select));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imgV.setImageDrawable(getResources().getDrawable(
                            R.drawable.boton_up));
                }
                return false;
            }
        });

        flecha_arriba.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                log.log("[-- 2923 --]"
                                + "Se presiono la fecha riiba, se mueve la tabla Articulos",
                        0);
                moverTablaArticulos(-1);
            }
        });

        flecha_abajo.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, @NonNull MotionEvent event) {

                log.log("[-- 2932 --]" + "Se presiono la flecha abajo", 0);
                ImageView imgV = (ImageView) v;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imgV.setImageDrawable(getResources().getDrawable(
                            R.drawable.boton_down_select));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imgV.setImageDrawable(getResources().getDrawable(
                            R.drawable.boton_down));
                }
                return false;
            }
        });

        flecha_abajo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                log.log("[-- 2942 --]" + "Se presiono la flecha abajo, se mueve la tabla de Articulos", 0);
                moverTablaArticulos(1);
            }
        });

        // Touch on asensor:
        asensor_layout.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
                // Caso cuando el dedo toca la pantalla:
                if (event.getAction() == MotionEvent.ACTION_DOWN && dedoEnContacto == false) {
                    dedoEnContacto = true;
                    dedoInicialY = event.getY();
                }
                // Caso de soltar el touch:
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    dedoEnContacto = false;
                }

                else if (event.getAction() == MotionEvent.ACTION_MOVE && dedoEnContacto == true) {
                    dedoFinalY = event.getY();
                    float deltaY = dedoFinalY;
                    refreshAsensor(deltaY);
                    SystemClock.sleep(20);
                }
                return true;
            }
        });

        // Ordenar por CODIGO:
        encabezado_codigo.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

                log.log("[-- 2988 --]" + "Se presiono para ordenar por codigo", 0);
                bufferLectoraCB = "";
                System.out.println("::: PaginaInventarioDinamico 3235");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(getResources().getColor(R.color.anaranjado_verde));
                    deseleccionarLineaParticular(indice_on_focus);
                }
                return false;
            }
        });

        encabezado_codigo.setOnClickListener(new View.OnClickListener() {

            public void onClick(@NonNull View view) {

                log.log("[-- 3003 --]"
                        + "Se presiono para ordenar por codigo, onclic", 0);
                view.setBackgroundColor(getResources().getColor(
                        R.color.verde_claro));

                if (columna_ordonante != 0) {
                    Collections.sort(listaArticulosCompleta,
                            Articulo.ORDEN_CODIGO);
                } else {
                    Collections.reverse(listaArticulosCompleta);
                }
                columna_ordonante = 0;
                indice_primera_linea = 0;
                refreshTablaCentral();
            }
        });

        encabezado_codigo
                .setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick(View v) {
                        System.out.println("::: PaginaInventarioDinamico 3270");
                        log.log("[-- 3024 --]"
                                + "Se presiono para filtrar, longClic", 0);
                        showMenuFiltro(ParametrosInventario.filtro_sector);
                        encabezado_codigo.setBackgroundColor(getResources()
                                .getColor(R.color.verde_claro));
                        return true;
                    }
                });

        // Ordenar por DESCRIPCION:
        encabezado_descripcion.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

                log.log("[-- 3037 --]"
                        + "Se presiono para ordenar por descripcion", 0);
                bufferLectoraCB = "";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(getResources().getColor(
                            R.color.anaranjado_verde));
                    deseleccionarLineaParticular(indice_on_focus);
                }
                return false;
            }
        });

        encabezado_descripcion.setOnClickListener(new View.OnClickListener() {

            public void onClick(@NonNull View view) {
                log.log("[-- 3051 --]"
                                + "Se presiono para ordenar por descripcion, efectivamente",
                        0);
                view.setBackgroundColor(getResources().getColor(
                        R.color.verde_claro));

                if (columna_ordonante != 1) {
                    Collections
                            .sort(listaArticulosCompleta, Articulo.ORDEN_NOM);
                } else {
                    Collections.reverse(listaArticulosCompleta);
                }
                columna_ordonante = 1;
                indice_primera_linea = 0;
                refreshTablaCentral();
            }
        });

        // Ordenar por PRECIO DE VENTA:
        encabezado_precio_venta.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

                log.log("[-- 3072 --]"
                        + "Se presiono para ordenar por precio de venta", 0);
                bufferLectoraCB = "";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(getResources().getColor(
                            R.color.anaranjado_verde));
                    deseleccionarLineaParticular(indice_on_focus);
                }
                return false;
            }
        });

        encabezado_precio_venta.setOnClickListener(new View.OnClickListener() {

            public void onClick(@NonNull View view) {
                log.log("[-- 3086 --]"
                                + "Se presiono para ordenar por precio de venta, onclic",
                        0);
                view.setBackgroundColor(getResources().getColor(
                        R.color.verde_claro));

                if (columna_ordonante != 2) {
                    Collections.sort(listaArticulosCompleta,
                            Articulo.ORDEN_PRECIO_VENTA);
                } else {
                    Collections.reverse(listaArticulosCompleta);
                }
                columna_ordonante = 2;
                indice_primera_linea = 0;
                refreshTablaCentral();
            }
        });

        encabezado_precio_venta
                .setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick(View v) {
                        log.log("[-- 3106 --]"
                                + "Se abre el menu de filtro, longClic", 0);
                        showMenuFiltro(ParametrosInventario.filtro_precio);
                        encabezado_precio_venta
                                .setBackgroundColor(getResources().getColor(
                                        R.color.verde_claro));
                        return true;
                    }
                });

        // Ordenar por CANTIDADES:
        encabezado_cantidad.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {

                log.log("[-- 3120 --]"
                        + "Se presiono para ordenar por cantidades", 0);
                bufferLectoraCB = "";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(getResources().getColor(
                            R.color.anaranjado_verde));
                    deseleccionarLineaParticular(indice_on_focus);
                }
                return false;
            }
        });

        encabezado_cantidad.setOnClickListener(new View.OnClickListener() {

            public void onClick(@NonNull View view) {
                view.setBackgroundColor(getResources().getColor(
                        R.color.verde_claro));
                if (columna_ordonante != 3) {
                    Collections.sort(listaArticulosCompleta,
                            Articulo.ORDEN_CANTIDAD);
                } else {
                    Collections.reverse(listaArticulosCompleta);
                }
                log.log("[-- 3142 --]"
                        + "Se presiono par ordenar por cantidades, onclc", 0);
                columna_ordonante = 3;
                indice_primera_linea = 0;
                refreshTablaCentral();
            }
        });

        encabezado_cantidad
                .setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick(View v) {

                        log.log("[-- 3154 --]"
                                        + "Se presiono para ordenar cantidades, longclic",
                                0);
                        deseleccionarLineaParticular(indice_on_focus);
                        showMenuFiltro(ParametrosInventario.filtro_inventario);
                        encabezado_cantidad.setBackgroundColor(getResources()
                                .getColor(R.color.verde_claro));
                        return true;
                    }
                });

        encabezado_accion.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                log.log("[-- 3120 --]"
                        + "Se presiono para ordenar por cantidades", 0);
                bufferLectoraCB = "";
                //if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                  //  view.setBackgroundColor(getResources().getColor(
                    //        R.color.anaranjado_verde));
                    //deseleccionarLineaParticular(indice_on_focus);
                //}
                return false;
            }
        });
        // Handlers de los radio button

        radioBVenta
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        System.out.println("::: PaginaInventarioDinamico venta");

                        log.log("[-- 3171 --]" + "Cambia de estado radioVenta",
                                0);
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            actualizarPaginaInventario(ParametrosInventario.ID_INV_DIN_VTA);

                        }

                    }
                });

        radioBDepo
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        System.out.println("::: PaginaInventarioDinamico Deposito");
                        log.log("[-- 3186 --]" + "Cambia de estado radioDep", 0);
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            actualizarPaginaInventario(ParametrosInventario.ID_INV_DIN_DEP);

                        }

                    }
                });

        System.out.println("::: XXXXXXXXXXXXXXXXXXXXXXXXXXXXX V ::: "+radioBVenta+" Dep " +radioBDepo);
    }

    /**
     * Creamos el thread que va mostrar y destruir el menu
     *
     * @author GuillermoR
     *
     */
    protected class ShowDialogTimer extends AsyncTask<Context, Integer, String> {

        @Nullable
        protected String doInBackground(Context... arg0) {
            SystemClock.sleep(3000);
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                alert.dismiss();

            } catch (Exception ex) {

                log.log("[-- 3216--]" + ex.toString(), 4);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Funcin para buscar el indice de un articulo en la lista por codigo y
     * sector
     * <p>
     * Iteramos la lista hasta encontrar la coincidencia
     *
     * @param hashmap
     *            : contiene el codigo y sector de un articulo
     * @return Devuelve el indice de el articulo cuyo codigo y sector se pasan
     *         en el parametro
     */
    private int get_indice_con_articulos(@NonNull HashMap<Integer, Integer> hashmap) {
        System.out.println("::: PaginaInventarioDinamico GetIndice");
        int posicion = -1;
        int index = 0;
        int codigo, sector;
        int codigoArt, sectorArt;
        // Iteramos la lista hasta encontrar la coincidencia
        for (ArticuloVisible av : listaArticulosCompleta) {
            codigo = hashmap.get(ParametrosInventario.clave_art_codigo);
            sector = hashmap.get(ParametrosInventario.clave_art_sector);
            codigoArt = av.getCodigo();
            sectorArt = av.getSector();
            if (av.esVisible() && codigoArt == codigo && sectorArt == sector) {
                posicion = index;
                break;
            }
            index++;
        }

        if (posicion < 0) {
            return -1;
        } else {
            return posicion;
        }
    }



    /**
     * Funcion que se llama luego de que se levanta la tecla.Esto procesa todos
     * los eventos de teclado y codigo de barra.
     *
     */

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        System.out.println("::: PaginaInventarioDinamico onkeyuo");
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            /**
             * Si se presion la tecla ABAJO: mover la tabla una unidad para
             * abajo
             */
            bufferLectoraCB = "";
            moverTablaArticulos(1);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            /**
             * Si se presion la tecla ARRIBA: mover la tabla una unidad para
             * arriba
             */
            bufferLectoraCB = "";
            moverTablaArticulos(-1);
        } else if (indice_on_focus < 0) {

            /**
             * Si ninguna linea tiene el focus procesamos eso y lo metemos en el
             * buffer de lectura de CB si es un caracter
             */
            char car = event.getNumber();
            if (Character.isDigit(car) == true) {
                log.log("key ingresado en el buffer: " + event.getKeyCode()
                        + " = " + car, 3);
                bufferLectoraCB += String.valueOf(car);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                /**
                 * Se ley un codigo completo de CB, se lo procesa y vacia el
                 * buffer
                 */
                log.log("[-- 3539 --]" + "Codigo de Barras: " + bufferLectoraCB
                        + ", from lectora: " + 0, 3);
                try {
                    processArticuloConCB(bufferLectoraCB, true);
                } catch (ExceptionBDD e) {
                    // TODO Auto-generated catch block
//					e.printStackTrace();


                    // 2.2 Si el articulo es nuevo / desconocido:
                    // 2.2.1 Abrimos un dialogo para pedir nombre del articulo
                    // nuevo:
                    log.log("[-- 3520 --]" + e.toString(), 4);
                    View.OnClickListener listenerNegativo = new View.OnClickListener() {

                        public void onClick(View v) {

                            log.log("[-- 3525 --]"
                                    + "Se abre el cuadro para un articulo nuevo", 0);
                            dialogoNombreArticuloNuevo.cancel();
                            loadingBar.setVisibility(View.GONE);
                        }
                    };

                    View.OnClickListener listenerPositivo = new View.OnClickListener() {

                        public void onClick(View v) {
                            log.log("[-- 3534 --]"
                                            + "Se abre el cuadro para un articulo nuevo, onclic",
                                    0);
                            // 2.2.2 Crear articulo visible nuevo no existnte en las
                            // referencias de la BDD:
                            ArticuloVisible a_v = new ArticuloVisible(true);

                            ArrayList<String> lista_cb = new ArrayList<String>();
                            lista_cb.add(bufferLectoraCB);
                            a_v.setCodigos_barras(lista_cb);

                            if (a_v.getFechaInicio().length() == 0) {
                                a_v.setFechaInicio(new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss").format(new Date()));
                            }

                            a_v.setFechaFin(new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss").format(new Date()));
                            a_v.setInventario(inventario_numero_en_curso);
                            if (modo_mas_1 == 1) {
                                a_v.setCantidad(1);
                            } else {
                                a_v.setCantidad(-1);
                            }

                            // 2.2.3 Modificar el nombre/descripcin del artculo:
                            String n_texto = dialogoNombreArticuloNuevo.get_text();
                            if (n_texto.length() > 0) {
                                a_v.setDescripcion(n_texto);
                            } else {
                                a_v.setDescripcion("#");
                            }

                            // 2.2.4 Agregamos este articulo en el listado de los
                            // articulos:
                            listaArticulosCompleta.add(a_v);

                            // 2.2.5 Agregar este articulo a las tablas de referncia
                            // y articulos
                            try {
                                BaseDatos bdd2 = new BaseDatos(ctxt);

                                // Primero se debe insertar el articulo nuevo en las
                                // referenicias
                                // Aqui se crea con nro de sector y de codigo nuevo
                                bdd2.insertReferenciaNuevaEnBdd(a_v);
                                // bdd2.insertArticuloNuevoEnBdd(a_v);
                                // Directamente insertamos un articulo ya creado en
                                // BDD
                                System.out.println("::::: LLAMADO 3");
                                bdd2.insertArticuloEnBdd(a_v);

                                // Agregar este articulo a la base, tabla RFERENCIAS

                            } catch (ExceptionBDD e1) {

                                log.log("[-- 3587 --]" + e1.toString(), 4);
                                e1.printStackTrace();
                            }

                            // 2.2.6 Refrescamos el encabezado

                            refrescarEncabezado(inventario_numero_en_curso);
                            System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 6");
                            // 2.2.7 Enfocamos el articulo (es el ltimo dado que no
                            // lo
                            // hemos encontrado en la lista):
                            dialogoNombreArticuloNuevo.dismiss();

                            seleccionarMostrarIndiceArticulo(listaArticulosCompleta
                                    .size() - 1);
                        }
                    };

                    dialogoNombreArticuloNuevo = new DialogPersoComplexEditTextOkCancel(
                            ctxt,
                            "Nuevo Articulo Escaneado",
                            "El articulo escaneado no ha podido ser identificado.\n"
                                    + "Si lo desea, usted puede ingresar una breve descripcion del producto:",
                            DialogPerso.IMAGEN_ARTICULO, DialogPerso.INPUT_LETRAS,
                            listenerPositivo, listenerNegativo);
                    dialogoNombreArticuloNuevo.show();
                }
                refrescarEncabezado(inventario_numero_en_curso);
                System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 2 ");
                bufferLectoraCB = "";
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                /**
                 * Volvemos a la pagina anterior
                 */
                bufferLectoraCB = "";
                deseleccionarLineaParticular(indice_on_focus);
                setResult(RESULT_OK, intentPadre);
                finish();

            }
        } else if (indice_on_focus >= 0) { // Si una linea tiene foco
            // bufferLectoraCB = "";
            char car = event.getNumber();
            if (Character.isDigit(car) == true) {
                // Toast.makeText(ctxt, String.valueOf(car),
                // Toast.LENGTH_SHORT).show();
                if (bufferLectoraCB.length() <= 0) {
                    /**
                     * Para guardar el valor por si se lee como cantidad un
                     * valor no valido
                     */
                    valor_antes_modificar = listaArticulosCompleta.get(
                            indice_on_focus).getCantidad();
                }
                bufferLectoraCB += String.valueOf(car);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                bufferLectoraCB = "";
                deseleccionarLineaParticular(indice_on_focus);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                if (bufferLectoraCB.length() <= 0) {
                    /**
                     * Se presion enter pero no se ha leido por CB
                     */
                    deseleccionarLineaParticular(indice_on_focus);
                    refrescarEncabezado(inventario_numero_en_curso);
                    System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 3");
                } else if (bufferLectoraCB.length() > ParametrosInventario.TAMANO_MAX_CANTIDAD) {
                    /**
                     * Cuando se leyo algo en la cantidad que no es valido
                     */
                    showSimpleDialogOK(
                            "Error al leer cantidad",
                            "La cantidad leida tiene mas digitos de "
                                    + "los permitidos,por favor reingrese la cantidad")
                            .show();
                    deseleccionarLineaParticular(indice_on_focus, false);
                    // deseleccionarLineaParticular(indice_on_focus, true);
                } else {
                    deseleccionarLineaParticular(indice_on_focus);
                    // processArticuloConCB(bufferLectoraCB, true);
                    bufferLectoraCB = "";
                }
                bufferLectoraCB = "";
            } else {
                bufferLectoraCB = "";
                super.onKeyUp(keyCode, null);
            }
        }
        System.out.println("::: PaginaInventarioDinamico 3009 return true");
        return true;
    }

    /**
     * Procesa el CB leido para buscarlo en la lista o aadirlo si no esta o
     * crear uno nuevo si no existe en las referencias
     * <p>
     * Al leer un cdigo de barras, buscamos el articulo en el listado de los
     * articulos del inventario, es el caso cuando un articulo ya estuvo
     * cargado, y vuelve a pasar por la lectora...
     * </p>
     * <p>
     * Si todava no est el articulo en la tabla, lo agregamos.
     *
     * <p>
     * 1 Buscamos el codigo de barras en la tabla de los articulos con su
     * codigo de barra
     * <p>
     * 2 Si el articulo no ha sido escaneado todava, entonces agregamos el
     * articulo que no est en la tabla
     * <p>
     * &nbsp; &nbsp;2.1 Vemos si el codigo de barras referencia un articulo
     * conocido en la tabla de los articulos
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.1.1 Agregamos este articulo en el listado de
     * los articulos
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.1.2 Agregar este articulo a la base, tabla
     * ARTICULOS
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.1.3 Refrescar el encabezado
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.1.4 Enfocamos el articulo (es el ltimo dado
     * que no lo hemos encontrado ya en la lista)
     * <p>
     * &nbsp; &nbsp;2.2 Si el articulo es nuevo / desconocido
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.1 Abrimos un dialogo para pedir nombre del
     * articulo nuevo
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.2 Si se acepta la creacion -> Crear
     * articulo visible nuevo no existnte en las referencias de la BDD
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.3 Modificar el nombre/descripcin del
     * artculo
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.4 Agregamos este articulo en el listado de
     * los articulos
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.5 Agregar este articulo a las tablas de
     * referncia y articulos
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.6 Refrescamos el encabezado
     * <p>
     * &nbsp; &nbsp;&nbsp; &nbsp;2.2.7 Enfocamos el articulo (es el ltimo dado
     * que no lo hemos encontrado en la lista)
     * <p>
     * 3 Si el articulo ya existe
     * <p>
     * &nbsp; &nbsp;3.1 Si hemos leido con la lectora en modo NO "+1", abrimos
     * abrimos el cartel "MasMenosModif"
     * <p>
     * &nbsp; &nbsp;3.2 Si hemos leido con la lectora en modo "+1", agregamos +1
     * a la cantidad
     *
     *
     * @param cb
     * @param from_lectoraCB
     * @throws ExceptionBDD
     */
    private void processArticuloConCB(@NonNull final String cb, boolean from_lectoraCB)
            throws ExceptionBDD {
        System.out.println("::: PaginaInventarioDinamico ArticuloConCB");
        boolean condicionBalanza = ParametrosInventario.balanza;
        Articulo artic = null;
        if(condicionBalanza == true){
            int sSubCadena = Integer.parseInt(cb.substring(0,2));
            if(sSubCadena == 20){
                String buscarBD = cb.substring(2,7);
                System.out.println("::: PaginaInventarioDinamico 4861 balanza busca BD " +buscarBD);
                artic = bdd.selectReferenciaConCodigoBarra(cb);
            }else{
                System.out.println("::: PaginaInventarioDinamico 3887 que antes 2");
                artic = bdd.selectReferenciaConCodigoBarra(cb);
            }
        }
        TextcodigoBarras.setText("");
        log.log("[-- 3539 --]" + "Codgio de Barras: " + cb + ", from lectora: "
                + from_lectoraCB, 3);
//		loadingBar.setVisibility(View.VISIBLE);
//		loadingBar.bringToFront();
        // Se considera que aqui el orden de clasificacion puede ser violado,
        // desactivamos el eventual
        // ordenamiento corriente:
        columna_ordonante = -1;
        int indice_articulo_encontrado = -1;
        if(condicionBalanza == true){
            for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
                int valorCortado1 = Integer.parseInt(cb.substring(0,2));
                String valorCortado2 = cb.substring(2,7);
                if (valorCortado1 == 20) {
                    parametroPrimeraSeleccion = 0;
                    if (tablaCodigosUnArticulo.contains((String) valorCortado2)) {
                        parametroPrimeraSeleccion = 1;
                        indice_articulo_encontrado = listaCodigosDeBarrasOrdenados.indexOf(tablaCodigosUnArticulo);
                        break;
                    }
                }else{
                    if (tablaCodigosUnArticulo.contains((String) cb)) {
                        indice_articulo_encontrado = listaCodigosDeBarrasOrdenados.indexOf(tablaCodigosUnArticulo);
                        break;
                    }
                }
            } // end for
        }else{
            System.out.println("::: PaginaInventarioDinamico 3960");
            for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
                if (tablaCodigosUnArticulo.contains((String) cb)) {
                    // Obtenemos el indice de la linea seleccionada:
                    indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
                            .indexOf(tablaCodigosUnArticulo);
                    break;
                } // end if
            } // end for
        }
        // 1 Buscamos el codigo de barras en la tabla de los articulos con su
        // codigo de barra:
//		for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
//			if (tablaCodigosUnArticulo.contains((String) cb)) {
//				// Obtenemos el indice de la linea seleccionada:
//				indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
//						.indexOf(tablaCodigosUnArticulo);
//				break;
//			} // end if
//		} // end for
        // 2 Si el articulo no ha sido escaneado todava:
        if (indice_articulo_encontrado < 0) {
            BaseDatos bdd = new BaseDatos(ctxt);
            ArticuloVisible a = null;
            try {
                System.out.println("::: PID 4922");
                // 2.1 Entonces agregamos el articulo que no est en la tabla:
                // Vemos si el codigo de barras referencia un articulo conocido
                // en la tabla de los articulos:
                // Si se encuentra el articulo en la base de datos...:
                Articulo articulo = bdd.selectReferenciaConCodigoBarra(cb);
                Articulo artComprobacion = bdd.selectArticuloConCodigos(
                        articulo.getSector(), articulo.getCodigo(),
                        this.numeroInventario);
                boolean seguir = true;
                if (artComprobacion != null) {
                    for (ArrayList<String> tablaCodigosUnArticulo : listaCodigosDeBarrasOrdenados) {
                        if (tablaCodigosUnArticulo.contains(artComprobacion
                                .getCodigos_barras_string())) {
                            // Obtenemos el indice de la linea seleccionada:
                            indice_articulo_encontrado = listaCodigosDeBarrasOrdenados
                                    .indexOf(tablaCodigosUnArticulo);
                            seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                            seguir = false;
                            break;
                        } // end if
                    } // end for
                }
                // Articulo articulo =
                // bdd.selectReferenciaArticuloConCodigoBarra(cb);
                System.out.println("::: PID 3856");
                if (seguir) {
                    a = new ArticuloVisible(articulo);

                    if(ParametrosInventario.InventariosDeposito == true && a.getDepsn()==1){
                        if (modo_mas_1 == 1) {
                            if(condicionBalanza==true){
                                if(a.getBalanza()==8 && a.getDecimales()==3){
                                    String sumar1 = cb.substring(7,9);
                                    String sumar2 = cb.substring(9,12);
                                    String numSumar = sumar1 +"."+ sumar2;
                                    float sumarD = Float.parseFloat(numSumar);
                                    a.setCantidad(sumarD);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()==8 && a.getDecimales()!=3){
                                    float sumar = Integer.parseInt(cb.substring(7,12));
                                    a.setCantidad(sumar);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()!=8){
                                    a.setCantidad(1);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }
                            }else{
                                a.setCantidad(1);
                            }
                        } else {
                            a.setCantidad(-1);
                        }
                        if (a.getFechaInicio().length() == 0) {
                            a.setFechaInicio(new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss").format(new Date()));
                        }
                        a.setFechaFin(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date()));
                        a.setInventario(inventario_numero_en_curso);

                        // 2.1.1 Agregamos este articulo en el listado de los
                        // articulos:

                        listaArticulosCompleta.add(a);

                        // 2.1.2 Agregar este articulo a la base, tabla ARTICULOS:
                        System.out.println("::::: LLAMADO 2");

                        bdd.insertArticuloEnBdd(a);

                        // 2.1.3 Para que se refresce el encabezado
                        refrescarEncabezado(inventario_numero_en_curso);
                        // 2.1.4 Enfocamos el articulo (es el ltimo dado que no lo
                        // hemos
                        // encontrado ya en la lista):
                        //seleccionarMostrarIndiceArticulo(listaArticulosCompleta
                        //		.size() - 1);
                        seleccionarMostrarIndiceArticuloBalanza(listaArticulosCompleta
                                .size() - 1,cb);
                        if (modo_mas_1 == 1) {
                            deseleccionarLineaParticular(indice_on_focus);
                        }
                    }else if(ParametrosInventario.InventariosDeposito == true && a.getDepsn()==0){
                        mostrarMensaje(a.getDepsn());
                    }else if(ParametrosInventario.InventariosVentas == true){
                        if (modo_mas_1 == 1) {
                            if(condicionBalanza==true){
                                if(a.getBalanza()==8 && a.getDecimales()==3){
                                    String sumar1 = cb.substring(7,9);
                                    String sumar2 = cb.substring(9,12);
                                    String numSumar = sumar1 +"."+ sumar2;
                                    float sumarD = Float.parseFloat(numSumar);
                                    a.setCantidad(sumarD);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()==8 && a.getDecimales()!=3){
                                    float sumar = Integer.parseInt(cb.substring(7,12));
                                    a.setCantidad(sumar);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()!=8){
                                    a.setCantidad(1);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }
                            }else{
                                a.setCantidad(1);
                            }
                        } else {
                            if(condicionBalanza==true){
                                if(a.getBalanza()==8 && a.getDecimales()==3){
                                    String sumar1 = cb.substring(7,9);
                                    String sumar2 = cb.substring(9,12);
                                    String numSumar = sumar1 +"."+ sumar2;
                                    float sumarD = Float.parseFloat(numSumar);
                                    a.setCantidad(sumarD);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()==8 && a.getDecimales()!=3){
                                    float sumar = Integer.parseInt(cb.substring(7,12));
                                    a.setCantidad(sumar);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }else if(a.getBalanza()!=8){
                                    a.setCantidad(-1);
                                    seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                                    deseleccionarLineaParticular(indice_articulo_encontrado);
                                }
                            }else{
                                a.setCantidad(-1);
                                deseleccionarLineaParticular(indice_articulo_encontrado);
                            }

                            //		a.setCantidad(-1);

                        }

                        if (a.getFechaInicio().length() == 0) {

                            a.setFechaInicio(new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss").format(new Date()));
                        }

                        a.setFechaFin(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date()));

                        a.setInventario(inventario_numero_en_curso);

                        // 2.1.1 Agregamos este articulo en el listado de los
                        // articulos:

                        listaArticulosCompleta.add(a);

                        // 2.1.2 Agregar este articulo a la base, tabla ARTICULOS:
                        System.out.println("::::: LLAMADO 2");

                        bdd.insertArticuloEnBdd(a);

                        // 2.1.3 Para que se refresce el encabezado
                        refrescarEncabezado(inventario_numero_en_curso);
                        System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 5");
                        // 2.1.4 Enfocamos el articulo (es el ltimo dado que no lo
                        // hemos
                        // encontrado ya en la lista):
                        //	seleccionarMostrarIndiceArticulo(listaArticulosCompleta
                        //				.size() - 1);
                        seleccionarMostrarIndiceArticuloBalanza(listaArticulosCompleta
                                .size() - 1,cb);

                        if (modo_mas_1 == 1) {
                            deseleccionarLineaParticular(indice_on_focus);
                        }
                    }
                }
            } catch (ExceptionBDD e) {

                // 2.2 Si el articulo es nuevo / desconocido:
                // 2.2.1 Abrimos un dialogo para pedir nombre del articulo
                // nuevo:
                log.log("[-- 3520 --]" + e.toString(), 4);
                View.OnClickListener listenerNegativo = new View.OnClickListener() {

                    public void onClick(View v) {

                        log.log("[-- 3525 --]"
                                + "Se abre el cuadro para un articulo nuevo", 0);
                        dialogoNombreArticuloNuevo.cancel();
                        loadingBar.setVisibility(View.GONE);
                    }
                };

                View.OnClickListener listenerPositivo = new View.OnClickListener() {

                    public void onClick(View v) {
                        log.log("[-- 3534 --]"
                                        + "Se abre el cuadro para un articulo nuevo, onclic",
                                0);
                        // 2.2.2 Crear articulo visible nuevo no existnte en las
                        // referencias de la BDD:
                        ArticuloVisible a_v = new ArticuloVisible(true);

                        ArrayList<String> lista_cb = new ArrayList<String>();
                        lista_cb.add(cb);
                        a_v.setCodigos_barras(lista_cb);

                        if (a_v.getFechaInicio().length() == 0) {
                            a_v.setFechaInicio(new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss").format(new Date()));
                        }

                        a_v.setFechaFin(new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss").format(new Date()));
                        a_v.setInventario(inventario_numero_en_curso);
                        if (modo_mas_1 == 1) {
                            a_v.setCantidad(1);
                        } else {
                            a_v.setCantidad(-1);
                        }

                        // 2.2.3 Modificar el nombre/descripcin del artculo:
                        String n_texto = dialogoNombreArticuloNuevo.get_text();
                        if (n_texto.length() > 0) {
                            a_v.setDescripcion(n_texto);
                        } else {
                            a_v.setDescripcion("#");
                        }

                        // 2.2.4 Agregamos este articulo en el listado de los
                        // articulos:
                        listaArticulosCompleta.add(a_v);

                        // 2.2.5 Agregar este articulo a las tablas de referncia
                        // y articulos
                        try {
                            BaseDatos bdd2 = new BaseDatos(ctxt);

                            // Primero se debe insertar el articulo nuevo en las
                            // referenicias
                            // Aqui se crea con nro de sector y de codigo nuevo
                            bdd2.insertReferenciaNuevaEnBdd(a_v);
                            // bdd2.insertArticuloNuevoEnBdd(a_v);
                            // Directamente insertamos un articulo ya creado en
                            // BDD
                            System.out.println("::::: LLAMADO 3");
                            bdd2.insertArticuloEnBdd(a_v);

                            // Agregar este articulo a la base, tabla RFERENCIAS

                        } catch (ExceptionBDD e1) {

                            log.log("[-- 3587 --]" + e1.toString(), 4);
                            e1.printStackTrace();
                        }

                        // 2.2.6 Refrescamos el encabezado

                        refrescarEncabezado(inventario_numero_en_curso);
                        System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 6");
                        // 2.2.7 Enfocamos el articulo (es el ltimo dado que no
                        // lo
                        // hemos encontrado en la lista):
                        dialogoNombreArticuloNuevo.dismiss();

                        seleccionarMostrarIndiceArticulo(listaArticulosCompleta
                                .size() - 1);
                    }
                };

                dialogoNombreArticuloNuevo = new DialogPersoComplexEditTextOkCancel(
                        ctxt,
                        "Nuevo Articulo Escaneado",
                        "El articulo escaneado no ha podido ser identificado.\n"
                                + "Si lo desea, usted puede ingresar una breve descripcion del producto:",
                        DialogPerso.IMAGEN_ARTICULO, DialogPerso.INPUT_LETRAS,
                        listenerPositivo, listenerNegativo);
                dialogoNombreArticuloNuevo.show();
            }

            // Enfocamos el articulo (es el ltimo dado que no lo hemos
            // encontrado ya en la lista):
            // seleccionarMostrarIndiceArticulo(listaArticulosCompleta.size() -
            // 1);

        }
        // 3 Si el articulo ya existe:
        // Enfocamos y sumamos 1 unidad
        else {
            // Estratgia: (3 casos)
            // 1) Si hemos leido el CB con boton "+Nuevo", abrimos el cartel
            // "MasMenosModif"
            // 2) Si hemos leido con la lectora en modo NO "+1", abrimos abrimos
            // el cartel "MasMenosModif"
            // 3) Si hemos leido con la lectora en modo "+1", agregamos +1 a la
            // cantidad sin dar foco
            //
            loadingBar.setVisibility(View.GONE);
            ArticuloVisible a_v = listaArticulosCompleta
                    .get(indice_articulo_encontrado);
            if (a_v.esVisible() == false) {
                showSimpleDialogOK(
                        "Articulo Oculto",
                        "El articulo a inventariar esta "
                                + "oculto por un filtro").show();
            } else {

                // En el caso 3, agregamos + 1 a la cantidad de
                // listaArticuloCompleta, damos y sacamos el foco:
                if (modo_mas_1 == 1) {

                    if(condicionBalanza==true){

                        if(a_v.getBalanza()==8 && a_v.getDecimales()==3){
                            System.out.println("::: PaginaInventarioDinamico 4766 balanza 8 y decimal 3");
                            //if (a_v.getCantidad() >= 0) {
                            String sumar1 = cb.substring(7,9);
                            String sumar2 = cb.substring(9,12);
                            String numSumar = sumar1 +"."+ sumar2;
                            float sumarD = Float.parseFloat(numSumar);
                            float suma_actual = 0;
                            if(a_v.getCantidad()==-1){
                                suma_actual = 0;
                            }else{
                                suma_actual = a_v.getCantidad();
                            }

                            a_v.setCantidad(suma_actual + sumarD);
                            //	seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                            seleccionarMostrarIndiceArticuloBalanza(indice_articulo_encontrado,cb);
                            deseleccionarLineaParticular(indice_articulo_encontrado);

                        }else if(a_v.getBalanza()==8 && a_v.getDecimales()!=3){
                            if (a_v.getCantidad() >= 0) {
                                float sumar = Integer.parseInt(cb.substring(7,12));
                                a_v.setCantidad(a_v.getCantidad() + sumar);
                            } else {
                                a_v.setCantidad(1);
                            }
                            deseleccionarLineaParticular(indice_articulo_encontrado);
                            seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                            deseleccionarLineaParticular(indice_articulo_encontrado);
                        }else if(a_v.getBalanza()!=8){
                            System.out.println("::: PaginaInventarioDinamico 4792 balanza!=8");
                            if (a_v.getCantidad() >= 0) {
                                a_v.setCantidad(a_v.getCantidad() + 1);
                            } else {
                                a_v.setCantidad(1);
                            }
                            System.out.println("::: PaginaInventarioDinamico 4800 llama a seleccionar");
                            seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                            deseleccionarLineaParticular(indice_articulo_encontrado);
                        }
                    }else{

                        if (a_v.getCantidad() >= 0) {
                            a_v.setCantidad(a_v.getCantidad() + 1);
                        } else {
                            a_v.setCantidad(1);
                        }
                        seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                        deseleccionarLineaParticular(indice_articulo_encontrado);

                    }

                }
                // En los casos 1 y 2 damos el foco a la linea:
                else {
                    //seleccionarMostrarIndiceArticulo(indice_articulo_encontrado);
                    seleccionarMostrarIndiceArticuloBalanza(indice_articulo_encontrado, cb);
                }
            }
        }

    } // end funcion

    /**/
	/*Creo q se encargaba de obtener el codigo de barra y el pesaje en el string completo de 13 caracteres*/
	/**/
    transient Object[] array;
    int size;

    public boolean containsCasero(@Nullable Object object) {
        Object[] a = array;
        int s = size;
        if (object != null) {
            for (int i = 2; i < 7; i++) {
                if (object.equals(a[i])) {
                    return true;
                }
            }
        } else {
            for (int i = 2; i < 7; i++) {
                if (a[i] == null) {
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * Evaluamos si se volvio del detalle, si se elimno el articulo
     * <p>
     * Si se volvio de eliminacin
     * <p>
     * Lo eliminamos de la lista
     * <p>
     * Actualizamos la pagina
     */

    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        try {
            System.out.println("::: PaginaInventarioDinamico onActivityResult");
            super.onActivityResult(requestCode, resultCode, data);
            Bundle bundle = data.getExtras();

            if (requestCode == SCAN_BARCODE) {
                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");

                    String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                    log.log("[-- 3539 --]" + "Codgio de Barras: "
                            + contents.trim() + ", from lectora: " + 1, 3);
                    processArticuloConCB(contents.trim(), true);
                    deseleccionarLineaParticular(indice_on_focus);
                    refrescarEncabezado(inventario_numero_en_curso);
                    // ... Usar o mostrar el cdigo de barras
                    System.out.println("::: PaginaInventarioDinamico 4380");
                } else if (resultCode == RESULT_CANCELED) {
                    // El escaneo ha fallado. Volver a probar
                    // ...
                }
            }

            if (resultCode == ParametrosInventario.RETURN_ART_ELIM) {
                // Se elimino el articulo
                int codigo = bundle.getInt(ParametrosInventario.extra_codigo);
                int sector = bundle.getInt(ParametrosInventario.extra_sector);
                String codBarString = bundle
                        .getString(ParametrosInventario.extra_codBar);
                ArrayList<String> codsBar = getCodigosBarra(codBarString);
                // Lo eliminamos de la lista
                eliminiarArticuloLista(codigo, sector, codsBar);
                // Actualizamos la pagina
                refreshTablaCentral();
                refrescarEncabezado(inventario_numero_en_curso);
                System.out.println("::: PaginaInventarioDinamico 531 salio refrescarencabezado 7");
            } else if (resultCode == RESULT_OK) {
                /**
                 * No hubieron cambios
                 */
            } else if (resultCode == ParametrosInventario.RETURN_ART_ELIM_FALLO) {
                /**
                 * No se pudo eliminar el articulo
                 */
            }
        } catch (Exception e) {
            refreshTablaCentral();
        } catch (ExceptionBDD e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            loadingBar.setVisibility(View.GONE);
        }
    }

    /*
	 * 
	 * Funcion accesoria: Elimina el articulo de la lista y los CB de la lista
	 * 
	 * @param codigo
	 * 
	 * @param sector
	 * 
	 * @param cb
	 */
    private void eliminiarArticuloLista(int codigo, int sector,
                                        ArrayList<String> cb) {
        System.out.println("::: PaginaInventarioDinamico EliminarARticulo");
        int indice = 0;
        // Eliminar el articulo de la lista
        ArticuloVisible av;
        // Ubicar el indice
        for (int i = 0; i < listaArticulosCompleta.size(); i++) {
            av = listaArticulosCompleta.get(i);
            if (av.getCodigo() == codigo && av.getSector() == sector) {
                indice = i;
                break;
            }
        }
        listaArticulosCompleta.remove(indice);

        // No seria necesario
        // ArrayList<String> cbsActual;
        // //Eliminar de la lista de cb
        // for (int i=0;i<listaCodigosDeBarrasOrdenados.size();i++) {
        // cbsActual=listaCodigosDeBarrasOrdenados.get(i);
        // for(String cbAct:cb) {
        // if(cbsActual.contains(cbAct)){
        // indice=i;
        // break;
        // }
        // }
        // }
        // listaCodigosDeBarrasOrdenados.remove(indice);
    }

    /**
     * Funcion accesoria: Separa los codigos de barra que estan en una cadena
     * separada por comas
     */
    @NonNull
    private ArrayList<String> getCodigosBarra(@NonNull String codBarString) {
        System.out.println("::: PaginaInventarioDinamico 4459");
        ArrayList<String> codigosBarra = new ArrayList<String>();
        String[] cbsSplited = codBarString.split(",");

        for (int i = 0; i < cbsSplited.length; i++) {
            codigosBarra.add(cbsSplited[i]);
        }

        return codigosBarra;
    }

    public void scanBarcode(View button) {

        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE",
                "ONE_D_MODE");
        startActivityForResult(intent, SCAN_BARCODE);
    }

}