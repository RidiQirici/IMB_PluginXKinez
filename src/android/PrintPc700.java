package imb.ridiqirici.plugin.cordova.pc700print;

import org.apache.cordova.CordovaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.zkc.helper.printer.PrintService;
import com.zkc.helper.printer.PrinterClass;
import com.zkc.pc700.helper.PrinterClassSerialPort;

public class PrintPc700 extends CordovaPlugin{
	public static final String PRINT_TEXT = "printText";
	public static final String PRINT_IMG = "printImg";
	protected static final String TAG = "Pc700PrintPlugin";
	private CallbackContext mesazhi;
	private boolean veprimiKryer;
	static PrinterClassSerialPort printerClass = null;
	private Thread autoprint_Thread;
	private String perPrintim;
	private boolean printimi = true;
	private String mesazhPrintim = "";

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.mesazhi = callbackContext;
		this.veprimiKryer = true;

		if (PRINT_TEXT.equals(action)) {
			mesazhPrintim = args.getString(0);
			if (!mesazhPrintim.isEmpty()) {

				Thread th = new Thread(new Runnable() {
					public void run() {
						veprimiKryer = printoTekstin(mesazhPrintim);
						try {
							Thread.sleep(500);
							//th.interrupt();
						} catch (InterruptedException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				});
				th.start();

				if(veprimiKryer)
				{
					this.veprimiKryer = true;
					System.out.println(TAG + " Printimi u krye me sukses!");
					Log.d(TAG, "Printimi u krye me sukses!");
					this.mesazhi.success("Printimi u krye me sukses!");

				}
				else
				{
					this.veprimiKryer = false;
					System.out.println(TAG + " Ndodhi nje gabim gjate printimit!");
					Log.d(TAG, "Ndodhi nje gabim gjate printimit!");
					this.mesazhi.error("Ndodhi nje gabim gjate printimit!");

				}

			} else {
				this.veprimiKryer = false;
				System.out.println(TAG + " Ndodhi nje JSON Exception!");
				Log.d(TAG, "Ndodhi nje JSON Exception ");
				this.mesazhi.error("Perdoruesi nuk ka specifikuar te dhena per tu printuar");

			}
		} else if (PRINT_IMG.equals(action)) {
			//TODO
		} else {
			this.veprimiKryer = false;
			System.out.println(TAG + " Veprim i pavlefshem : Eshte kaluar veprimi " + action + "!");
			Log.d(TAG, "Veprim i pavlefshem : Eshte kaluar veprimi " + action + "!");
			this.mesazhi.error("Veprim i pavlefshem!");

		}
		return veprimiKryer;
	}

	public boolean printoTekstin(String stringaXPrintim) {
		this.veprimiKryer = true;
		try {

			printerClass = new PrinterClassSerialPort();
			System.out.println(TAG + printerClass.getState());

			if (!printerClass.IsOpen())
				printerClass.open();

			if (!this.veprimiKryer)
			{
				this.veprimiKryer = false;
				System.out.println(TAG + " Ndodhi nje problem gjate hapjes se portes seriale 38400!");
				this.mesazhi.error("Ndodhi nje problem gjate hapjes se portes seriale 38400!");
				return this.veprimiKryer;
			}


			this.veprimiKryer = printerClass.printText(stringaXPrintim);

			if (!this.veprimiKryer)
			{
				printerClass.close();
				this.veprimiKryer = false;
				System.out.println(TAG + " Printimi i tekstit nuk u krye me sukses!");
				this.mesazhi.error("Printimi i tekstit nuk u krye me sukses! ");
				return this.veprimiKryer;
			}
			printerClass.close();
			this.mesazhi.success("Printimi i tekstit u krye me sukses! ");
			return this.veprimiKryer;
		} catch (Exception e) {
			this.veprimiKryer = false;
			System.out.println(TAG + " " +  e.getMessage());
			Log.e(TAG, e.getMessage());
			this.mesazhi.error("Gabim gjate printimit te tekstit! " + e.getMessage() + " " + e.toString() + " " + this.veprimiKryer );

			return this.veprimiKryer;
		}
	}

	public boolean printoTekstinMetoda2(String stringaXPrintim) {
		this.veprimiKryer = true;
		try {
			printerClass = new PrinterClassSerialPort();
			this.veprimiKryer = printerClass.open();

			if (printerClass.setSerialPortBaudrate(38400))
			{
				this.veprimiKryer = printerClass.printText(stringaXPrintim);
				if (!this.veprimiKryer)
				{
					this.veprimiKryer = false;
					this.mesazhi.error("Printimi i tekstit nuk u krye me sukses! ");
					return this.veprimiKryer;
				}
				this.mesazhi.success("Printimi i tekstit u krye me sukses! ");
				return this.veprimiKryer;
			}
			this.veprimiKryer = false;
			this.mesazhi.error("Ndodhi nje problem gjate hapjes se portes seriale 38400!");
			return this.veprimiKryer;
		} catch (Exception e) {
			this.veprimiKryer = false;
			this.mesazhi.error("Gabim gjate printimit te tekstit! " + e.getMessage() + " " + e.toString() + " " + this.veprimiKryer );
			Log.e(TAG, e.getMessage());
			return this.veprimiKryer;
		}
	}
}

