package com.gajos.ftp;

public enum FTPMessageType {
	CodeResponse(0), FilePath(1), IPAddress(2), PortNumber(3), Number(4), File(4096);
	
    private final int value;
    private FTPMessageType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
}
