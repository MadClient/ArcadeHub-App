package com.yunluo.android.arcadehub.interfac;

public interface OnNetPlayListener {
	
	/**
	 * Request Connect
	 * @param info
	 */
	public void onRequestConnected(String info);
	
	/**
	 * Refute Connect
	 * @param info
	 */
	public void onRefuteConnected(String info);
	
	/**
	 * Request Transfer
	 * @param info
	 */
	public void onRequestTransfer(String info);
	
	/**
	 * Agree Transfer
	 */
	public void onAgreeTransfer();
	
	/**
	 * Refute Transfer
	 */
	public void onRefuteTransfer();
	
	/**
	 * Finish Transfer
	 * @param info
	 */
	public void onFinishTransfer(String info);
	
	/**
	 * Start LAN Game
	 * @param info
	 */
	public void onStartWIFIGame(String info);
	
	/**
	 * Start BT Game
	 * @param info
	 */
	public void onStartBTGame(String info);
	
	/**
	 * Add Net-Play Information
	 * @param os
	 * @param ip
	 * @param cpu
	 * @param ram
	 * @param rom
	 */
	public void onAddNetPlayInformation(int os, String ip, int cpu, int ram, String rom);
	
	/**
	 * Exit game
	 */
	public void onFinish();
	
}
