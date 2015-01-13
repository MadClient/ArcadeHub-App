package com.yunluo.android.arcadehub.push;

/**
 * push listener
 * 
 * @author Madhouse
 */
public interface OnPushResponseListener {
	
	/**
	 * Header Data
	 * 
	 * @param data
	 */
	public void doPushHeaderResponse(byte[] data);
	
	/**
	 * Body Data
	 * @param data
	 */
	public void doPushBodyResponse(byte[] data);
	
	/**
	 * Error
	 */
	public void doErrorResponse();
	
}
