package com.ccp.decorators;

import java.io.File;
import java.util.function.Consumer;

import com.ccp.aop.CcpAllowNullReturn;

/**
 * Decorator over a directory path of the file system. Offers operations to create subfolders and files, iterate over
 * the content, ZIP compression and removal of the directory with its direct files.
 */
public class CcpFolderDecorator implements CcpDecorator<String> {
	/** The directory path. */
	public final String content;
	/** The parent directory (absolute path), or {@code null} when the path has no parent. */
	public final CcpFolderDecorator parent;
	/**
	 * Wraps the path and resolves the parent directory.
	 * @param content the directory path
	 */
	protected CcpFolderDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	/**
	 * Resolves the parent directory of a path.
	 * @param content the directory path
	 * @return the parent directory, or {@code null} when the path has no parent
	 */
	@CcpAllowNullReturn
	private CcpFolderDecorator getParent(String content) {
		File file = new File(content);
		File parentFile = file.getParentFile();
		boolean hasNoParent = parentFile == null;
		if(hasNoParent) {
			return null;
		}
		String absolutePath = parentFile.getAbsolutePath();
		CcpFolderDecorator parentFolder = new CcpFolderDecorator(absolutePath);
		return parentFolder;
	}
	
	/**
	 * Reinterprets the path as a file.
	 * @return the file decorator over the same path
	 */
	public CcpFileDecorator asFile() {
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(this.content);
		return ccpFileDecorator;
	}

	/**
	 * Compresses the directory, recursively, into {@code <name>.zip} next to it, with the entries named relative to its
	 * parent directory (see {@code CcpFileDecorator.zipNextToTheOriginal}). Until 2026-10-07 the zip was created in the
	 * working directory of the process and its entries were named by the full path.
	 * @return this decorator
	 */
	public CcpFolderDecorator zip() {
		File folderToZip = new File(this.content);
		CcpFileDecorator.zipNextToTheOriginal(folderToZip);
		return this;
	}

	/**
	 * Returns only the directory name (without the parent path).
	 * @return the directory name
	 */
	public String getName() {
		File file = new File(this.content);
		String name = file.getName();
		return name;
	}
	
	/**
	 * Calls the consumer with a {@code CcpFolderDecorator} (absolute path) for each entry of the directory, files included.
	 * Does nothing when the directory does not exist.
	 * @param consumer the callback called for each entry
	 * @return this decorator
	 */
	public CcpFolderDecorator readFolders(Consumer<CcpFolderDecorator> consumer){
		File folder = new File(this.content);
		File[] files = folder.listFiles();
		boolean hasNoFiles = files == null;
		if(hasNoFiles) {
			return this;
		}
		for (File file : files) {
			String absolutePath = file.getAbsolutePath();
			CcpFolderDecorator entryFolder = new CcpFolderDecorator(absolutePath);
			consumer.accept(entryFolder);
		}
        return this;
	}
	
	/**
	 * Calls the consumer with a {@code CcpFileDecorator} (absolute path) for each entry of the directory, subdirectories
	 * included. Does nothing when the directory does not exist.
	 * @param consumer the callback called for each entry
	 * @return this decorator
	 */
	public CcpFolderDecorator readFiles(Consumer<CcpFileDecorator> consumer){
		File folder = new File(this.content);
		File[] files = folder.listFiles();
		boolean hasNoFiles = files == null;
		if(hasNoFiles) {
			return this;
		}
		for (File file : files) {
			String absolutePath = file.getAbsolutePath();
			CcpFileDecorator entryFile = new CcpFileDecorator(absolutePath);
			consumer.accept(entryFile);
		}
        return this;
	}

	
	/**
	 * Returns the directory name (without the parent path).
	 * @return the directory name
	 */
	public String toString() {
		File folder = new File(this.content);
		String folderName = folder.getName();
		return folderName;
	}

	/**
	 * Tells whether the directory exists.
	 * @return {@code true} when the path exists
	 */
	public boolean exists() {
		File file = new File(this.content);
		boolean exists = file.exists();
		return exists;
	}

	/**
	 * Creates a subdirectory with the given name (if it does not exist; the parent must exist) and returns its decorator.
	 * @param folderName the name of the subdirectory
	 * @return the decorator of the subdirectory
	 */
	public CcpFolderDecorator createNewFolderIfNotExists(String folderName) {
		String completePath = this.getCompletePath(folderName);
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator newFolder = new CcpFolderDecorator(completePath);
		return newFolder;
	}

	/**
	 * Creates the directory itself if it does not exist (its parent must exist).
	 * @return a decorator over the directory path followed by a separator
	 */
	public CcpFolderDecorator createNewFolderIfNotExists() {
		String completePath = this.getCompletePath("");
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator folder = new CcpFolderDecorator(completePath);
		return folder;
	}
	
	/**
	 * Creates the directory structure if needed and then an empty file inside the directory (an existing file keeps its
	 * content).
	 * @param fileName the name of the file
	 * @return the file decorator
	 */
	public CcpFileDecorator createNewFileIfNotExists(String fileName) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator file = new CcpFileDecorator(completePath);
		this.createFolderIfNotExists();
		CcpFileDecorator createdFile = file.append("");
		return createdFile;
	}
	
	/**
	 * Creates the directory and, recursively, every missing ancestor.
	 * @return always {@code true}
	 */
	public boolean createFolderIfNotExists() {
		boolean parentExists = this.parent.exists();
	
		boolean isNewFolder = false == parentExists;
		
		if(isNewFolder) {
			this.parent.createFolderIfNotExists();
		}
		this.createNewFolderIfNotExists();
		return true;
	}
	
	/**
	 * Writes the content into the file inside the directory, replacing the previous content.
	 * @param fileName the file name
	 * @param fileContent the content to write
	 * @return the file decorator
	 */
	public CcpFileDecorator writeInTheFile(String fileName, String fileContent) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(completePath);
		CcpFileDecorator writtenFile = ccpFileDecorator.write(fileContent);
		return writtenFile;
	}

	/**
	 * Joins the directory path and the name with the platform separator.
	 * @param fileName the name to append
	 * @return the complete path
	 */
	private String getCompletePath(String fileName) {
		String pathWithSeparator = this.content + File.separator;
		String completePath = pathWithSeparator + fileName;
		return completePath;
	}
	
	/**
	 * Deletes the directory with everything inside it, recursively; a directory that does not exist is a no-op, as in
	 * {@code CcpFileDecorator.remove()}. Until 2026-10-07 it deleted only the direct entries, so a non-empty subdirectory
	 * kept the directory in place with no error, and a missing directory raised a NullPointerException.
	 * @return this decorator
	 */
	public CcpFolderDecorator remove() {
		File folder = new File(this.content);
		this.removeRecursively(folder);
		return this;
	}

	/**
	 * Deletes the entry and, when it is a directory, everything inside it first.
	 * @param entry the file or directory
	 */
	private void removeRecursively(File entry) {
		File[] children = entry.listFiles();
		boolean isDirectoryWithChildren = children != null;

		if(isDirectoryWithChildren) {
			for (File child : children) {
				this.removeRecursively(child);
			}
		}
		entry.delete();
	}

	/**
	 * Returns the directory path.
	 * @return the directory path
	 */
	public String getContent() {
		return this.content;
	}
}
