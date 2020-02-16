package com.focasoftware.deboinventario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Activity que permite al usuario tomar una foto
 * 
 * @author GuillermoR
 * 
 */
public class TomarFoto extends Activity implements SurfaceHolder.Callback {

	/**
	 * Variable que almacena informaci�n de contexto de la activity
	 */
	@NonNull
    private Context ctxt = this;
	/**
	 * Informacion del intent que llamo a esta actividad
	 */
	private Intent intentPadre;
	/**
	 * SurfaceView donde se previsualiza la foto
	 */
	private SurfaceView mSurfaceView;
	/**
	 * SurfaceHolder que contiene la SurfaceView de la foto
	 */
	private SurfaceHolder mSurfaceHolder;
	/**
	 * Variable tipo Camera para tomar la foto
	 */
	private Camera mCamera;
	/**
	 * Variable accesoria
	 */
	private boolean mPreviewRunning;
	/**
	 * Variable accesoria para almacenar el nombre de la foto tomada
	 */
	@Nullable
    private String nombreFoto;

	/**
	 * Ejecutado al crear la Activity, inicializa la UI y carga los handlers
	 * <p>
	 * 1� Recuperamos el Activity padre
	 * <p>
	 * 2� Recuperamos datos de la UI
	 * <p>
	 * 3� Recuperamos la Camara y ponemos el preview en la surface
	 * correspondiente
	 * <p>
	 * 4� Handler del boton para cancelar la toma de foto
	 */

	

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// 1� Recuperamos el Activity padre:
		intentPadre = getIntent();
		nombreFoto = intentPadre.getExtras().getString(
				Parametros.extra_foto_uri);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		try {
			// requestWindowFeature(Window.FEATURE_NO_TITLE);
			// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			// WindowManager.LayoutParams.FLAG_FULLSCREEN);
			// 2� Recuperamos datos de la UI
			setContentView(R.layout.xml_tomafoto);

			mSurfaceView = (SurfaceView) findViewById(R.id.cuadroImagen);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// 3� Recuperamos la Camara y ponemos el preview en la surface
			// correspondiente
			Date date1 = new Date();
			mCamera = Camera.open();
			Date date2 = new Date();
			mCamera.setPreviewDisplay(mSurfaceHolder);
			Date date3 = new Date();

			Toast.makeText(
					ctxt,
					"Camara lista ("
							+ String.valueOf(date2.getTime() - date1.getTime())
							+ "/"
							+ String.valueOf(date3.getTime() - date2.getTime())
							+ ")", Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(ctxt, "Error launching camera",
			// Toast.LENGTH_LONG).show();
		}

		// 4� Handler del boton para cancelar la toma de foto
		Button boton = (Button) findViewById(R.id.botonCamera);
		boton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// mCamera.takePicture(null, null, mPictureCallback);
				setResult(RESULT_CANCELED, intentPadre);
				terminar();
				finish();
			}
		});
	}

	/**
	 * Al presionar Back, se toma la foto
	 */
	@Override
	public void onBackPressed() {
		mCamera.takePicture(null, null, mPictureCallback);
		return;
	}

	/**
	 * Evalua el evento onKeyDown y verifica si es home, menu, o volumenes para
	 * tomar la foto
	 * <p>
	 * 1� Tomar la foto
	 */
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		try {
			if ((keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0)
					|| (keyCode == KeyEvent.KEYCODE_MENU && event
							.getRepeatCount() == 0)
					|| (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event
							.getRepeatCount() == 0)
					|| (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event
							.getRepeatCount() == 0)) {
				// 1� Tomar la foto
				try {
					mCamera.takePicture(null, null, mPictureCallback);
				} catch (Exception e) {
				}

				return true;
			}
		} catch (Exception e) {
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Metodo al ejecutarse cuando se crea la surface
	 * <p>
	 * 1� Abrir la camara
	 * <p>
	 * 2� Empezar la previsualizacion
	 */

	public void surfaceCreated(SurfaceHolder holder) {
		try {

			// 1� Abrir la camara
			mCamera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 2� Empezar la previsualizacion
		mCamera.startPreview();
	}

	/**
	 * Metodo a llamarse cuando cambia la superficie se modifican los parametros
	 * de la camara para los nuevos, se trata de refrescar la camara
	 * <p>
	 * 1� Si se esta viendo preview, se detiene
	 * <p>
	 * 2� Se setean los nuevos parametros de la camara
	 * <p>
	 * 3� Volvemos a mostrar el preview
	 * 
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// 1� Si se esta viendo preview, se detiene
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		// 2� Se setean los nuevos parametros de la camara
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);

		// 3� Volvemos a mostrar el preview
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(ctxt, "Error refreshing camera", Toast.LENGTH_LONG)
					.show();
		}

		mCamera.startPreview();
		mPreviewRunning = true;
	}

	/**
	 * Cuando se destruye la superficie se para el preview y se libera la camara
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redefinimos que vamos a hacer cuando se toma la foto: la vamos a guardar
	 * en una carpeta predefinida :
	 * data/data/nombre.del.paquete/fotos/nombreDeLaFoto
	 */
    @NonNull
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(@NonNull byte[] imageData, Camera c) {
			try {
				Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length);

				String pack1 = getApplicationContext().getPackageName();

				// nombreFoto = "testtest.jpg";
				// File file0 = new File(ParametrosSancion.URL_CARPETA_FOTOS);
				File file0 = new File("/data/data/" + pack1 + "/fotos/");
				file0.mkdirs();
				File file1 = new File(file0.getAbsolutePath() + "/"
						+ nombreFoto);
				if (file1.exists() == true) {
					file1.delete();
				}
				file1.createNewFile();
				// FileOutputStream fOut1 =
				// ctxt.openFileOutput(Parametros.URL_CARPETA_FOTOS +
				// nombreFoto, Context.MODE_WORLD_WRITEABLE);
				OutputStream fOut1 = new FileOutputStream(file1);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut1);
				fOut1.flush();
				fOut1.close();

				setResult(RESULT_OK, intentPadre);
			} catch (Exception e) {
				e.printStackTrace();
				setResult(RESULT_CANCELED, intentPadre);
				Toast.makeText(ctxt, e.toString(), Toast.LENGTH_LONG).show();
			} finally {
				terminar();
			}
		}
	};

	/**
	 * Funcion para cerrar la camara, detener el preview y liberarla
	 */
	private void terminar() {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
		finish();
	}

}
