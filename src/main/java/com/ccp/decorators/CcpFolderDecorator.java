package com.ccp.decorators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ccp.aop.CcpAllowNullReturn;

/**
 * Decorator over a directory path in the file system. Offers operations to create subfolders and files,
 * iterate over the content, ZIP compression and recursive removal.
 */
public class CcpFolderDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFolderDecorator parent;
	/**
	 * Wraps the path and resolves the parent directory.
	 * @param content the directory path
	 */
	protected CcpFolderDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

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
	 */
	public CcpFileDecorator asFile() {
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(this.content);
		return ccpFileDecorator;
	}

	/**
	 * Compresses the whole directory into a {@code .zip} file with the same name.
	 */
	public CcpFolderDecorator zip() {
		
		File fileToZip = new File(this.content);
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream);) {
			CcpFolderDecorator zippedFolder = this.zip(fileToZip, zipOut);
			return zippedFolder;
		}
	}

	/**
	 * Returns only the directory name (without the parent path).
	 */
	public String getName() {
		File file = new File(this.content);
		String name = file.getName();
		return name;
	}

	private CcpFolderDecorator zip(File fileToZip, ZipOutputStream zipOut) throws IOException {
		boolean hidden = fileToZip.isHidden();
       if (hidden) {
            return this;
        }
        boolean isDirectory = fileToZip.isDirectory();
        if (isDirectory) {
            String trailingSlash = "/";
        	   boolean endsWith = this.content.endsWith("/");
        	   if (endsWith) {
        		trailingSlash = ""; 
            } 
            String directoryEntryName = this.content + trailingSlash;
            ZipEntry directoryEntry = new ZipEntry(directoryEntryName);
			zipOut.putNextEntry(directoryEntry);
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                String childPathPrefix = this.content + "/";
                String childFileName = childFile.getName();
                String childPath = childPathPrefix + childFileName;
                CcpFolderDecorator childFolder = new CcpFolderDecorator(childPath);
                childFolder.zip(childFile, zipOut);
            }
            return this;
        }
        try(FileInputStream fileInputStream = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(this.content);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            return this;
		}
    }
	
	/**
	 * Iterates over each entry of the directory and calls the {@code consumer} passing a {@code CcpFolderDecorator} for each entry.
	 * @param consumer the callback to call for each entry
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
	 * Iterates over each entry of the directory and calls the {@code consumer} with a {@code CcpFileDecorator} for each entry.
	 * @param consumer the callback to call for each file
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

	
	public String toString() {
		File folder = new File(this.content);
		String folderName = folder.getName();
		return folderName;
	}

	/**
	 * Checks whether the directory exists.
	 */
	public boolean exists() {
		File file = new File(this.content);
		boolean exists = file.exists();
		return exists;
	}

	/**
	 * Creates a subdirectory with the given name (if it does not exist) and returns the decorator of the new directory.
	 * @param folderName the name of the subdirectory to create
	 */
	public CcpFolderDecorator createNewFolderIfNotExists(String folderName) {
		String completePath = this.getCompletePath(folderName);
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator newFolder = new CcpFolderDecorator(completePath);
		return newFolder;
	}

	/**
	 * Creates the directory itself if it does not exist.
	 */
	public CcpFolderDecorator createNewFolderIfNotExists() {
		String completePath = this.getCompletePath("");
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator folder = new CcpFolderDecorator(completePath);
		return folder;
	}
	
	/**
	 * Creates an empty file inside the directory (ensuring that the folder structure exists) and returns its decorator.
	 * @param fileName the name of the file to create
	 */
	public CcpFileDecorator createNewFileIfNotExists(String fileName) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator file = new CcpFileDecorator(completePath);
		this.createFolderIfNotExists();
		CcpFileDecorator createdFile = file.append("");
		return createdFile;
	}
	
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
	 * Writes {@code fileContent} into the file {@code fileName} inside the directory and returns the file decorator.
	 * @param fileName the file name
	 * @param fileContent the content to write
	 */
	public CcpFileDecorator writeInTheFile(String fileName, String fileContent) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(completePath);
		CcpFileDecorator writtenFile = ccpFileDecorator.write(fileContent);
		return writtenFile;
	}

	private String getCompletePath(String fileName) {
		String pathWithSeparator = this.content + File.separator;
		String completePath = pathWithSeparator + fileName;
		return completePath;
	}
	
	/**
	 * Removes every file of the directory and then the directory itself.
	 */
	public CcpFolderDecorator remove() {
		File folder = new File(this.content);
		String[]entries = folder.list();
		
		for(String fileName: entries){
		    String path = folder.getPath();
			File currentFile = new File(path, fileName);
		    currentFile.delete();
		}
		
		folder.delete();
		
		return this;
	}

	/**
	 * Implementation of {@code CcpDecorator}; returns the directory path.
	 */
	public String getContent() {
		return this.content;
	}
}
