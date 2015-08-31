package com.android.antitheft.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.R;
import com.android.antitheft.WhosThatService;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseObject;

public class CheckPasswordActivity extends Activity {

	private static final String TAG = "CheckPasswordActivity";

	private EditText txtPassword;
	private Button btnCheck;
	private Button btnScrambleButtons;
	private Button btnRevertButtons;

	private ServiceConnection whosThatServiceConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.check_password_activity);

		txtPassword = (EditText) findViewById(R.id.txt_password);

		btnCheck = (Button) findViewById(R.id.btn_check);
		btnCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkPassword();
			}
		});

		btnScrambleButtons = (Button) findViewById(R.id.btn_scramble_buttons);
		btnScrambleButtons.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				copyScrambledLayout();
			}
		});

		btnRevertButtons = (Button) findViewById(R.id.btn_revert_buttons);
		btnRevertButtons.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

	}

	private void copyScrambledLayout() {
		/*
		 * $ su # mount -o remount,rw /system # cp /sdcard/Generic.kl
		 * /system/usr/keylayout # mount -o remount,ro /system
		 */
		String device = null;
		boolean foundSystem = false;
		try {
			Process process = Runtime.getRuntime().exec("mount");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = stdInput.readLine()) != null) {
				String[] array = line.split(" ");
				device = array[0];
				if ((array[1].equals("on") && array[2].equals("/system"))
						|| array[1].equals("/system")) {
					foundSystem = true;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Problem remounting /system", e);
		}

		if (foundSystem && device != null) {
			final String mountDev = device;
			Process process;
			try {
				Log.i(TAG, "Executing commands");
				process = Runtime.getRuntime().exec("su");
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
				os.writeBytes("mount -o remount,rw " + mountDev + " /system\n");
				os.writeBytes("cat /sdcard/Generic.kl > /system/usr/keylayout/Genericbla.kl\n");
				os.writeBytes("mount -o remount,ro " + mountDev + " /system\n");
				os.writeBytes("exit\n");
				Log.i(TAG, "Wrote last commands");
				try {
					process.waitFor();
					if (process.exitValue() != 255) {
						Toast.makeText(this, "Copy OK", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(this, "Copy KO", Toast.LENGTH_LONG)
								.show();
						;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i(TAG, "All clear");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void revertScrambledLayout() {
		/*
		 * $ su # mount -o remount,rw /system # cp /sdcard/Generic.kl
		 * /system/usr/keylayout # mount -o remount,ro /system
		 */
		String device = null;
		boolean foundSystem = false;
		try {
			Process process = Runtime.getRuntime().exec("mount");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = stdInput.readLine()) != null) {
				String[] array = line.split(" ");
				device = array[0];
				if ((array[1].equals("on") && array[2].equals("/system"))
						|| array[1].equals("/system")) {
					foundSystem = true;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Problem remounting /system", e);
		}

		if (foundSystem && device != null) {
			final String mountDev = device;
			Process process;
			try {
				process = Runtime.getRuntime().exec("su");
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
				os.writeBytes("mount -o remount,rw " + mountDev + " /system\n");
				os.writeBytes("cat /sdcard/keypad_8960.kl > /system/usr/keylayout/keypad_8960.kl\n");
				os.writeBytes("mount -o remount,ro " + mountDev + " /system\n");
				os.writeBytes("exit\n");
				try {
					process.waitFor();
					if (process.exitValue() != 255) {
						Toast.makeText(this, "Copy OK", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(this, "Copy KO", Toast.LENGTH_LONG)
								.show();
						;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkPassword() {
		String password = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANTITHEFT_PASSWORD);
		String value = txtPassword.getText().toString();
		if (value.equals(password)) {
			performRebootSequence();
		} else {
			Toast.makeText(this, "Wrong password mate...", Toast.LENGTH_LONG)
					.show();
			whoIsBuggingMe();
		}
		finish();
	}

	private void performRebootSequence() {
		try {
			IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager
					.getService(Context.POWER_SERVICE));
			pm.reboot(true, null, false);
		} catch (RemoteException e) {
			Log.e(TAG, "PowerManager service died!", e);
			return;
		}
	}

	private void whoIsBuggingMe() {
		Intent intent = new Intent(getApplicationContext(),
				WhosThatService.class);
		intent.putExtra(WhosThatService.SERVICE_PARAM,
				WhosThatService.CAMERA_VIDEO);
		// whosThatServiceConnection = new ServiceConnection() {
		// @Override
		// public void onServiceConnected(ComponentName name, IBinder service) {
		// Messenger messenger = new Messenger(service);
		// Message msg = Message.obtain(null, 0);
		// msg.what=0;
		// msg.arg1 = msg.arg2 = 0;
		// try {
		// messenger.send(msg);
		// } catch (RemoteException e) {
		// }
		// }
		// @Override
		// public void onServiceDisconnected(ComponentName name) {}
		// };
		// bindServiceAsUser(
		// intent, whosThatServiceConnection, Context.BIND_AUTO_CREATE,
		// UserHandle.CURRENT);
		startService(intent);
	}

}
