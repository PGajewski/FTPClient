package com.gajos.ftp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class FTPClientBoxController {
	
	/**
	 * Address of server.
	 */
	@FXML TextField address;
	
	/**
	 * IP protocol server.
	 */
	@FXML TextField port;
	
	/**
	 * Hostname of server in project.
	 */
	@FXML TextField hostname;
	
	/**
	 * Button to finish modify endpoint.
	 */
	@FXML Button finishButton;
	
	/**
	 * Constructor for create new endpoint.
	 */
	FTPClientBoxController() {
		
	}
	
	/**
	 * Constructor for modifying FTPCllient.
	 * @param client for modify.
	 */
	FTPClientBoxController(FTPClient client) {
		
		//Set actual values.
		hostname.setText(client.getHostname());
		port.setText(Integer.toString(client.getPort()));
		address.setText(client.getIpAddress().getHostAddress());
	}
}
