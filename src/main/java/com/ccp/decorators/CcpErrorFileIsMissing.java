package com.ccp.decorators;

/**
 * Raised when the file read does not exist. Until 2026-10-07 this case raised {@code CcpErrorFolderParentIsMissing},
 * whose message pointed at the parent folder, which at that point had just been created by the read itself.
 */
@SuppressWarnings("serial")
public class CcpErrorFileIsMissing extends RuntimeException {
	/**
	 * Builds the error naming the missing file.
	 * @param decorator the missing file
	 */
	CcpErrorFileIsMissing(CcpFileDecorator decorator) {
		super("The file is missing: " + decorator.content);
	}
}
