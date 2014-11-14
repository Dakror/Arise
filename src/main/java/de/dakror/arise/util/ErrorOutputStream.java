package de.dakror.arise.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class ErrorOutputStream extends PrintStream {
	PrintStream os;
	
	public ErrorOutputStream(PrintStream os, File file) throws FileNotFoundException {
		super(file);
		this.os = os;
	}
	
	public void close() {
		super.close();
		os.close();
	}
	
	public void flush() {
		super.flush();
		os.flush();
	}
	
	public synchronized void write(byte[] b) throws IOException {
		super.write(b);
		os.write(b);
	}
	
	public synchronized void write(byte[] b, int off, int len) {
		super.write(b, off, len);
		os.write(b, off, len);
	}
	
	public synchronized void write(int b) {
		super.write(b);
		os.write(b);
	}
}
