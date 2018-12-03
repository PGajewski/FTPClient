package com.gajos.ftp;

import java.io.IOException;
import java.io.OutputStream;

import javafx.scene.control.TextArea;

public class ControlOutputStream extends OutputStream {
    private TextArea textArea;

    public ControlOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
    	textArea.appendText(String.valueOf((char)b));
    }
}
