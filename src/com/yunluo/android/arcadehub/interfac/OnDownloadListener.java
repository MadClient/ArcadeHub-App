package com.yunluo.android.arcadehub.interfac;

public interface OnDownloadListener {

	/**
	 * downloaded
	 */
	public void downloaded(String id);
	
	/**
	 * downloading
	 */
	public void downloading(String id, int precent, long length);
	
	/**
	 * download error
	 */
	public void downloadError(String id);
	
}
