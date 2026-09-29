package com.ccp.decorators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ccp.aop.CcpAllowNullReturn;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;


/**
 * Decorator over a file path of the file system. Wraps read, write, append,
 * ZIP compression, removal and conversion of the content to other framework types (JSON, list of JSONs).
 * Ensures the automatic creation of the parent directory when needed.
 */
public class CcpFileDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFileDecorator parent;
	/**
	 * Wraps the path and automatically resolves the decorator of the parent directory.
	 * @param content the file path
	 */
	protected CcpFileDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	@CcpAllowNullReturn
	private CcpFileDecorator getParent(String content) {
		
		File file = new File(content);
		String rawAbsolutePath = file.getAbsolutePath();
		String normalizedPath = rawAbsolutePath.replace('\\', File.separatorChar);
		File normalizedFile = new File(normalizedPath);
		File parentFile = normalizedFile.getParentFile();
		boolean hasNoParent = parentFile == null;
		if(hasNoParent) {
			return null;
		}
		String absolutePath = parentFile.getAbsolutePath();
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(absolutePath);
		return ccpFileDecorator;
	}

	/**
	 * Compresses the file or directory into a {@code .zip} file with the same name in the current directory.
	 */
	public CcpFileDecorator zip() {
		
		File fileToZip = tryToCreateParentFolder();
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream);) {
			CcpFileDecorator zippedFile = this.zip(fileToZip, zipOut);
			return zippedFile;
		} 
		
		
	}
	
	/**
	 * Returns only the file name (without the path).
	 */
	public String getName() {
		File file = tryToCreateParentFolder();
		String name = file.getName();
		return name;
	}
	/**
	 * Returns the complete absolute path of the file.
	 */
	public String getPath() {
		File file = tryToCreateParentFolder();
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	private CcpFileDecorator zip(File fileToZip, ZipOutputStream zipOut) throws IOException {
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
                CcpFileDecorator childFileDecorator = new CcpFileDecorator(childPath);
                childFileDecorator.zip(childFile, zipOut);
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
	 * Reads the whole file content as a UTF-8 string. Throws {@code CcpErrorFolderParentIsMissing} if the file does not exist.
	 */
	public  String getStringContent() {
		File file = tryToCreateParentFolder();
		boolean exists = file.exists();
		boolean fileIsMissing = false == exists;
		if(fileIsMissing) {
			CcpErrorFolderParentIsMissing fileMissingError = new CcpErrorFolderParentIsMissing(this);
			throw fileMissingError;
		}
		Path path = file.toPath();
		byte[] fileContent = Files.readAllBytes(path);
		String fileText = new String(fileContent, "UTF-8");
		return fileText;
	}
	/**
	 * Overwrites the file with the given content (clears it before writing).
	 * @param content the content to write
	 */
	public CcpFileDecorator write(String content) {
		this.reset();
		CcpFileDecorator writtenFile = this.append(content);
		return writtenFile;
		
	}
	
	/**
	 * Appends the content to the end of the file (creates the file if it does not exist).
	 * @param content the content to append
	 */
	public CcpFileDecorator append(String content) {
		File file = new File(this.content);
		boolean exists = file.exists();
		boolean fileIsMissing = false == exists;
		if (fileIsMissing) {
			file.createNewFile();
		}
		String contentWithLineBreak = content + "\n";
		byte[] bytes = (contentWithLineBreak).getBytes();
		Path path = Paths.get(this.content);
		Files.write(path, bytes, StandardOpenOption.APPEND);
		return this;
	}
	/**
	 * Erases the file content, leaving it empty (deletes and recreates it).
	 */
	public CcpFileDecorator reset() {

		File file = this.tryToCreateParentFolder();
		
		file.delete();
		file.createNewFile();
		return this;
	}

	private File tryToCreateParentFolder() {
		File file = new File(this.content);
		String parent = file.getParent();
		CcpFolderDecorator folder = new CcpFolderDecorator(parent);
		folder.createFolderIfNotExists();
		return file;
	}
	/**
	 * Reads all the lines of the file and returns them as a list of strings.
	 */
	public List<String> getLines(){
		String filePath = this.content;
		ArrayList<String> linesFromFile = new ArrayList<>();
		String line;
		try (FileReader fileReader = new FileReader(filePath); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			while ((line = bufferedReader.readLine()) != null) {
				linesFromFile.add(line);
			}
		}
		return linesFromFile;
	}


	/**
	 * Reads the file line by line, calling the callback {@code reader.onRead(line, index)} for each line.
	 * @param reader the callback to call for each line
	 */
	public  CcpFileDecorator readLines(FileLineReader reader){
		String line;
		try (FileReader fileReader = new FileReader(this.content); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			int k = 0;
			while ((line = bufferedReader.readLine()) != null) {
				reader.onRead(line, k++);
			}
			return this;
		}
	}
	
	
	public String toString() {
		File file = new File(this.content);
		String fileName = file.getName();
		return fileName;
	}

	/**
	 * Checks whether the file exists in the file system.
	 */
	public boolean exists() {
		File file = tryToCreateParentFolder();
		boolean exists = file.exists();
		return exists;
	}
	/**
	 * Returns {@code true} if the path exists and points to a file (not a directory).
	 */
	public boolean isFile() {
		boolean exists = this.exists();
		boolean doesNotExist = false == exists;
		if(doesNotExist) {
			return false;
		}
		File file = new File(this.content);
		boolean directory = file.isDirectory();
		if(directory) {
			return false;
		}
		return true;
	}
	/**
	 * Reinterprets the path as a directory.
	 */
	public CcpFolderDecorator asFolder() {
		CcpFolderDecorator ccpFolderDecorator = new CcpFolderDecorator(this.content);
		return ccpFolderDecorator;
	}
	
	/**
	 * Reads the file content and deserializes it as a single JSON.
	 */
	public CcpJsonRepresentation asSingleJson() {
		String fileText = this.getStringContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(fileText);
		return json;
	}
	
	/**
	 * Reads the file content and deserializes it as a list of JSON objects.
	 */
	public List<CcpJsonRepresentation> asJsonList(){
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String fileText = this.getStringContent();
		List<Map<String, Object>> list = jsonHandler.fromJson(fileText);
		var jsonMapsStream = list.stream();
		var jsonStream = jsonMapsStream.map(x -> new CcpJsonRepresentation(x));
		List<CcpJsonRepresentation> jsonList = jsonStream.collect(Collectors.toList());
		return jsonList;
	}
	
	/**
	 * Removes the file from the file system.
	 */
	public CcpFileDecorator remove() {

		File file = tryToCreateParentFolder();
		file.delete();
		return this;
	}
	
	/**
	 * Renames the file to the given new name and returns the decorator of the new file.
	 * @param newFileName the new file name
	 */
	public CcpFileDecorator rename(String newFileName) {
		
		File file = new File(this.content);
		File renamedFile = new File(newFileName);

		file.renameTo(renamedFile);
		
		CcpFileDecorator newFile = new CcpFileDecorator(newFileName);
		
		return newFile;
	}

	/**
	 * Implementation of {@code CcpDecorator}; returns the file path.
	 */
	public String getContent() {
		return this.content;
	}

}
