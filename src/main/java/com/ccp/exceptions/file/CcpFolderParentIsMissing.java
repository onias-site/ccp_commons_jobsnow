package com.ccp.exceptions.file;

import java.io.File;

import com.ccp.decorators.CcpFileDecorator;

@SuppressWarnings("serial")
public class CcpFolderParentIsMissing extends RuntimeException {

	public CcpFolderParentIsMissing(CcpFileDecorator decorator) {
		super("in the file " + new File(decorator.content).getParentFile().getAbsolutePath() + " is missing the file: " + decorator.content);

	}
	
}
