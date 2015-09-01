package com.android.antitheft.lockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

import com.android.internal.widget.LockPatternUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LockPatternUtilsHelper extends LockPatternUtils{
	
    private static final String SYSTEM_DIRECTORY = "/system/";
    private static final String LOCK_PATTERN_FILE = "gesture.key";
    private static final String LOCK_PASSWORD_FILE = "password.key";
	
	private LockPatternUtilsHelper instance;
	
	private Context mContext;
	
	public LockPatternUtilsHelper(Context context){
		super(context);
		mContext = context;
	}
	
	public void performAdminLock(String message,String password){
		clearLock(false);
		saveLockPassword(password,
				DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
		KeyguardManager keyguardManager = (KeyguardManager)mContext.getSystemService(Activity.KEYGUARD_SERVICE); 
	}
	
	public byte[] getUnlockPassword() {
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPasswordFilename = dataSystemDirectory + LOCK_PASSWORD_FILE;
		try{
			RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename, "r");
			final byte[] stored = new byte[(int) raf.length()];
			return stored;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] getUnlockPattern() {
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPatternFilename = dataSystemDirectory + LOCK_PATTERN_FILE;
		try{
			RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "r");
	        final byte[] stored = new byte[(int) raf.length()];
			return stored;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void savePassword(byte[] pass){
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPasswordFilename = dataSystemDirectory + LOCK_PASSWORD_FILE;
		resetPassFile();
		try{
			// Write the hash to file
	        RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename, "rw");
	        // Truncate the file if pattern is null, to clear the lock
	        if (pass == null) {
	            raf.setLength(0);
	        } else {
	            raf.write(pass, 0, pass.length);
	        }
	        raf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void resetPassFile(){
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPasswordFilename = dataSystemDirectory + LOCK_PASSWORD_FILE;

		try{
			// Write the hash to file
	        RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename, "rw");
	        // Truncate the file if pattern is null, to clear the lock
	            raf.setLength(0);
	        raf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void resetPatternFile(){
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPatternFilename = dataSystemDirectory + LOCK_PATTERN_FILE;
		
		try{
			// Write the hash to file
	        RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "rw");
	        // Truncate the file if pattern is null, to clear the lock
	            raf.setLength(0);
	        raf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void savePattern(byte[] pattern){
		String dataSystemDirectory = android.os.Environment.getDataDirectory()
				.getAbsolutePath() + SYSTEM_DIRECTORY;
		String sLockPatternFilename = dataSystemDirectory + LOCK_PATTERN_FILE;
		resetPatternFile();
		try{
			// Write the hash to file
	        RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "rw");
	        // Truncate the file if pattern is null, to clear the lock
	        if (pattern == null) {
	            raf.setLength(0);
	        } else {
	            raf.write(pattern, 0, pattern.length);
	        }
	        raf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
