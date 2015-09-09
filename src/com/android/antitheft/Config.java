package com.android.antitheft;

/**
 * 
 * @author mikalackis
 *
 */
public class Config {
	
	public static final boolean DEBUG = true;
	
	public static final String LOCK_SCREEN_PASS = "6969";
	
	public static final String KEY_LAYOUT_NORMAL = "Generic.kl";
	
	public static final String KEY_LAYOUT_SCRAMBLED = "Generic_locked.kl";
	
	public static enum ANTITHEFT_STATE {
		LOCKDOWN(1),
		NORMAL(0);
		
		private int mState;
		
		private ANTITHEFT_STATE(final int state){
			this.mState = state;
		}
		
		public int getState(){
			return mState;
		}
	}

}
