package imb.ridiqirici.plugin.cordova.pc700print;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import com.zkc.pc700.helper.PrinterClassSerialPort;

public class PrintPc700 extends CordovaPlugin {
	public static final String PRINT_TEXT = "printText";
	public static final String PRINT_IMG = "printImg";
	protected static final String TAG = "Pc700PrintPlugin";
	private boolean veprimiKryer;

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		System.out.println(TAG + " Hyri execute!");
		this.veprimiKryer = true;
		if (PRINT_TEXT.equals(action)) {
			final String mesazhPrintim = args.getString(0);
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					if (mesazhPrintim != "") {
						System.out.println(TAG + " " + mesazhPrintim);
						veprimiKryer = printoTekstin(mesazhPrintim, callbackContext); 
						if (veprimiKryer) {
							veprimiKryer = true;
							System.out.println(TAG + " Printimi u krye me sukses!");
							Log.d(TAG, "Printimi u krye me sukses!");
							return;
						} else {
							veprimiKryer = false;
							System.out.println(TAG + " Ndodhi nje gabim gjate printimit!");
							Log.d(TAG, "Ndodhi nje gabim gjate printimit!");
							return;
						}
					} else {
						veprimiKryer = false;
						System.out.println(TAG + " Perdoruesi nuk ka specifikuar te dhena per tu printuar!");
						Log.d(TAG, " Perdoruesi nuk ka specifikuar te dhena per tu printuar!");
						callbackContext.error("Perdoruesi nuk ka specifikuar te dhena per tu printuar");
						return;
					}
				}
			});
		} else if (PRINT_IMG.equals(action)) {
			// TODO
		} else {
			this.veprimiKryer = false;
			System.out.println(TAG + " Veprim i pavlefshem : Eshte kaluar veprimi " + action + "!");
			Log.d(TAG, "Veprim i pavlefshem : Eshte kaluar veprimi " + action + "!");
			callbackContext.error("Veprim i pavlefshem!");
		}
		System.out.println(TAG + " Printoje me duket te hengert dheri!");
		return veprimiKryer;
	}

	public boolean printoTekstin(String stringaXPrintim, CallbackContext callbackContext) {
		System.out.println(TAG + " Hyri te printoTekstin o ti....");
		boolean pergjigja = true;
		try {

			PrinterClassSerialPort printerClass = new PrinterClassSerialPort();
			System.out.println(TAG + " " + printerClass.getState());

			if (!printerClass.IsOpen()) {
				System.out.println(TAG + " Po hapen portat...");
				pergjigja = printerClass.open();
				System.out.println(TAG + " U hapen portat...");
			}

			if (!pergjigja) {
				pergjigja = false;
				System.out.println(TAG + " Ndodhi nje problem gjate hapjes se portes seriale 38400!");
				callbackContext.error("Ndodhi nje problem gjate hapjes se portes seriale 38400!");
				return pergjigja;
			}
			System.out.println(TAG + " Po printohet");
			System.out.println(TAG + " " + stringaXPrintim);
			pergjigja = printerClass.printText(stringaXPrintim);
			System.out.println(TAG + " E ekzekutoi per nder printerClass.printText(stringaXPrintim)");
			if (!pergjigja) {
				/*
				 * if (printerClass.IsOpen()) { this.veprimiKryer =
				 * printerClass.close(); if (this.veprimiKryer) {
				 * this.veprimiKryer = false; System.out.println(TAG +
				 * " Printimi i tekstit nuk u krye me sukses!");
				 * mesazhPrintimGlob =
				 * "Printimi i tekstit nuk u krye me sukses! "; return
				 * this.veprimiKryer; } else { this.veprimiKryer = false;
				 * System.out.println(TAG + " Nuk u mbyll porta!");
				 * mesazhPrintimGlob = "Nuk u mbyll porta!"; return
				 * this.veprimiKryer; } }
				 */
				pergjigja = false;
				System.out.println(TAG + " Printimi i tekstit nuk u krye me sukses!");
				callbackContext.error("Printimi i tekstit nuk u krye me sukses!");
				return pergjigja;
			}
			//System.out.println(TAG + " Mos me thuaj qe erdhi deri ketu pa printuar .... :O");
			/*
			 * System.out.println(TAG + " " + printerClass.getState()); if
			 * (printerClass.IsOpen()) { this.veprimiKryer =
			 * printerClass.close(); if (!this.veprimiKryer) { this.veprimiKryer
			 * = false; System.out.println(TAG + " Nuk u mbyll porta!");
			 * mesazhPrintimGlob = "Nuk u mbyll porta!"; return
			 * this.veprimiKryer; }
			 * 
			 * }
			 */
			System.out.println(TAG + " Printimi i tekstit u krye me sukses! ");
			printerClass = null;
			callbackContext.success("Printimi i tekstit u krye me sukses! ");
			return pergjigja;
		} catch (Exception e) {
			pergjigja = false;
			System.out.println(TAG + " " + e.getMessage());
			Log.e(TAG, e.getMessage());
			callbackContext.error(
					"Gabim gjate printimit te tekstit! " + e.getMessage() + " " + e.toString() + " " + pergjigja);
			return pergjigja;
		}
	}

	/*
	 * public boolean printoTekstinMetoda2(String stringaXPrintim) {
	 * this.veprimiKryer = true; try { printerClass = new
	 * PrinterClassSerialPort();
	 * 
	 * if (printerClass.open()) { this.veprimiKryer =
	 * printerClass.printText(stringaXPrintim); if (!this.veprimiKryer) {
	 * this.veprimiKryer = false; mesazhPrintimGlob =
	 * "Printimi i tekstit nuk u krye me sukses!"; return this.veprimiKryer; }
	 * mesazhPrintimGlob = "Printimi i tekstit u krye me sukses! "; return
	 * this.veprimiKryer; } this.veprimiKryer = false; mesazhPrintimGlob =
	 * "Ndodhi nje problem gjate hapjes se portes seriale 38400!"; return
	 * this.veprimiKryer; } catch (Exception e) { this.veprimiKryer = false;
	 * mesazhPrintimGlob = "Gabim gjate printimit te tekstit! " + e.getMessage()
	 * + " " + e.toString() + " " + this.veprimiKryer; Log.e(TAG,
	 * e.getMessage()); return this.veprimiKryer; } }
	 */
}
