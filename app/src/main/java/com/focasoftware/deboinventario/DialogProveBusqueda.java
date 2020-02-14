package com.focasoftware.deboinventario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by Usuario on 19/04/2016.
 */
public class DialogProveBusqueda extends Dialog{
    /**
     * Variable para almacenar la busqueda que se quiere realizar
     */
    @Nullable
    private EditText campoBusqueda = null;
    public DialogProveBusqueda (
            @NonNull Context context,
            String titulo,
            String mensaje,
            // int numeroProveedor,
            View.OnClickListener listenerBuscarPro
    ){
        super(context);
        final Activity owner = (Activity) context;

        super.setContentView(R.layout.z_dialogpersocomplexbusquedaprov);
        //4 Mensaje:
//        TextView tv_mensaje = (TextView) super.findViewById(R.id.Z_DIALOG_mensaje);
  //      tv_mensaje.setText(mensaje);
        //5 Edittext:
//        campoBusqueda = (EditText) super.findViewById(R.id.Z_DIALOG_editext);

        //Button boton_cancelar = (Button) super.findViewById(R.id.Z_DIALOG_boton_cancelar);
        //ImageView boton_buscar = (ImageView) super.findViewById(R.id.Z_DIALOG_boton_buscar);
        //ID_BUSCAR_PROV
        Button boton_buscar_proveedor = (Button) super.findViewById(R.id.ID_BUSCAR_PROV);
        final GestorLogEventos log = new GestorLogEventos();
        log.setUbicacion(ParametrosInventario.CARPETA_LOGTABLET);
        log.tipo_0 = Parametros.PREF_LOG_EVENTOS;
        log.tipo_2 = Parametros.PREF_LOG_PROCESOS;
        log.tipo_3 = Parametros.PREF_LOG_MENSAJES;
        log.tipo_4 = Parametros.PREF_LOG_EXCEPCIONES;
        log.log("[-- 83 --]" + "Inicio de Dialog Person Comple Bsqueda", 2 );
      //  boton_cancelar.setOnClickListener(listenerCancelarPro);
      //  boton_buscar.setOnClickListener(listenerBuscarPro);
        boton_buscar_proveedor.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                ImageView imageV = (ImageView) v;
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_down));
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    imageV.setImageDrawable(owner.getResources().getDrawable(R.drawable.boton_buscar_up));
                }
                log.log("[-- 99 --]" + "Click en el boton buscar", 0);
                return false;
            }
        });
    }
    @NonNull
    public String get_busqueda() {
        return String.valueOf(campoBusqueda.getText());
    }
}