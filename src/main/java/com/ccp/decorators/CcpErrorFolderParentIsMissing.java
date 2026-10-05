package com.ccp.decorators;

import java.io.File;

/** Raised when a file operation requires the parent folder of the file and that folder does not exist. */
@SuppressWarnings("serial")
public class CcpErrorFolderParentIsMissing extends RuntimeException {
	/**
	 * Builds the error naming the parent folder and the file.
	 * @param decorator the file whose parent folder is missing
	 */
	CcpErrorFolderParentIsMissing(CcpFileDecorator decorator) {
		super("in the file " + new File(decorator.content).getParentFile().getAbsolutePath() + " is missing the file: " + decorator.content);
	}
}
