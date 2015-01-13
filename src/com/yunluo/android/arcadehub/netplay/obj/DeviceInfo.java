package com.yunluo.android.arcadehub.netplay.obj;

import android.bluetooth.BluetoothDevice;

public class DeviceInfo {

	private String name = null;
	private String desc = null;
	private int os = 0;
	private String ip = null;
	private int cpu = 0;
	private int ram = 0;
	private String deviceName = null;
	private BluetoothDevice device = null;
	private int id = 0;
	
	public int getOs() {
		return os;
	}
	public void setOs(int os) {
		this.os = os;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	public BluetoothDevice getDevice() {
		return device;
	}
	
	@Override
	public String toString() {
		return "DeviceInfo [name=" + name + ", desc=" + desc + ", os=" + os
				+ ", ip=" + ip + ", cpu=" + cpu + ", ram=" + ram + ", device="
				+ device + "]";
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceName() {
		return deviceName;
	}
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
	
	
}
