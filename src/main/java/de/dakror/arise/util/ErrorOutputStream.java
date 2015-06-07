/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
