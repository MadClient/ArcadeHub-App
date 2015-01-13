package com.yunluo.android.arcadehub.interfac;

public interface OnOptionListener {

	/**
	 * goldfinger
	 */
	public void doCheat();

	/**
	 * pause/resume
	 */
	public void doFrezee();

	/**
	 * load game
	 */
	public void doLoadGame();
	
	/**
	 * save game
	 */
	public void doSaveGame();

	/**
	 * setting
	 */
	public void doSettings();

	/**
	 * help
	 */
	public void doHelp();

	/**
	 * about
	 */
	public void doAbout();
	
	/**
	 * macro key
	 */
	public void doKeyMacro();
	/**
     * cobination key
     */
    public void doKeyCombination();
}
