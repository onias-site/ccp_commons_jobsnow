package com.ccp.decorators;

/** Callback that receives, one at a time, the lines of a text file being read. */
public interface FileLineReader {
	/**
	 * Handles one line of the file.
	 * @param fileLine the content of the line, without the line terminator
	 * @param lineNumber the position of the line in the file
	 */
	void onRead(String fileLine, int lineNumber);
}
