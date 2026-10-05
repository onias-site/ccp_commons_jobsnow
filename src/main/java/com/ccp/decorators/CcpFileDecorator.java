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
 * Decorator over a file path of the file system. Wraps reading, writing, appending, ZIP compression, removal and
 * conversion of the content into framework types (JSON, list of JSONs).
 * <p>
 * Most operations ({@code exists}, {@code getName}, {@code getPath}, {@code reset}, {@code remove}, {@code zip},
 * {@code getStringContent}) create the missing parent directories as a side effect before acting.
 */
public class CcpFileDecorator implements CcpDecorator<String> {
	/** The file path. */
	public final String content;
	/** The parent directory (absolute path), or {@code null} when the path has no parent. */
	public final CcpFileDecorator parent;
	/**
	 * Wraps the path and automatically resolves the decorator of the parent directory.
	 * @param content the file path
	 */
	protected CcpFileDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	/**
	 * Resolves the parent directory of a path, after making it absolute.
	 * @param content the file path
	 * @return the parent directory, or {@code null} when the path has no parent
	 */
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
	 * Compresses the file (or the directory, recursively) into a {@code <name>.zip} file created in the working directory
	 * of the process. Hidden entries are skipped; entry names keep the path given to this decorator.
	 * @return this decorator
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
	 * @return the file name
	 */
	public String getName() {
		File file = tryToCreateParentFolder();
		String name = file.getName();
		return name;
	}
	/**
	 * Returns the absolute path of the file.
	 * @return the absolute path
	 */
	public String getPath() {
		File file = tryToCreateParentFolder();
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	/**
	 * Adds the file, or the directory and its children recursively, to the ZIP stream.
	 * @param fileToZip the file or directory to add
	 * @param zipOut the ZIP stream
	 * @return this decorator
	 * @throws IOException when reading a file or writing the stream fails
	 */
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
	 * Reads the whole file content as UTF-8 text.
	 * @return the file content
	 * @throws CcpErrorFolderParentIsMissing when the file does not exist
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
	 * Replaces the file content with the given text followed by a line feed.
	 * @param content the content to write
	 * @return this decorator
	 */
	public CcpFileDecorator write(String content) {
		this.reset();
		CcpFileDecorator writtenFile = this.append(content);
		return writtenFile;
		
	}
	
	/**
	 * Appends the text followed by a line feed ({@code \n}) to the end of the file, creating the file when it does not
	 * exist (its parent directory must exist). The bytes use the platform default charset.
	 * @param content the content to append
	 * @return this decorator
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
	 * Empties the file (deletes and recreates it), creating the parent directories when needed.
	 * @return this decorator
	 */
	public CcpFileDecorator reset() {

		File file = this.tryToCreateParentFolder();
		
		file.delete();
		file.createNewFile();
		return this;
	}

	/**
	 * Creates the missing parent directories of the file.
	 * @return the file
	 */
	private File tryToCreateParentFolder() {
		File file = new File(this.content);
		String parent = file.getParent();
		CcpFolderDecorator folder = new CcpFolderDecorator(parent);
		folder.createFolderIfNotExists();
		return file;
	}
	/**
	 * Reads every line of the file (platform default charset).
	 * @return the lines, without terminators
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
	 * Reads the file line by line (platform default charset), calling {@code reader.onRead(line, index)} for each line;
	 * the index starts at zero.
	 * @param reader the callback called for each line
	 * @return this decorator
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
	
	
	/**
	 * Returns the file name (without the path).
	 * @return the file name
	 */
	public String toString() {
		File file = new File(this.content);
		String fileName = file.getName();
		return fileName;
	}

	/**
	 * Tells whether the path exists (file or directory), creating the missing parent directories as a side effect.
	 * @return {@code true} when the path exists
	 */
	public boolean exists() {
		File file = tryToCreateParentFolder();
		boolean exists = file.exists();
		return exists;
	}
	/**
	 * Tells whether the path exists and is not a directory.
	 * @return {@code true} when the path is an existing file
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
	 * @return the folder decorator over the same path
	 */
	public CcpFolderDecorator asFolder() {
		CcpFolderDecorator ccpFolderDecorator = new CcpFolderDecorator(this.content);
		return ccpFolderDecorator;
	}
	
	/**
	 * Reads the file content and deserializes it as a single JSON object.
	 * @return the JSON
	 */
	public CcpJsonRepresentation asSingleJson() {
		String fileText = this.getStringContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(fileText);
		return json;
	}
	
	/**
	 * Reads the file content and deserializes it as a list of JSON objects, using the registered {@code CcpJsonHandler}.
	 * @return the list of JSONs
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
	 * Deletes the file (a no-op when it does not exist).
	 * @return this decorator
	 */
	public CcpFileDecorator remove() {

		File file = tryToCreateParentFolder();
		file.delete();
		return this;
	}
	
	/**
	 * Renames (moves) the file to the given path and returns the decorator of the new path. A failure to rename is
	 * silently ignored.
	 * @param newFileName the new path of the file
	 * @return the decorator of the new path
	 */
	public CcpFileDecorator rename(String newFileName) {
		
		File file = new File(this.content);
		File renamedFile = new File(newFileName);

		file.renameTo(renamedFile);
		
		CcpFileDecorator newFile = new CcpFileDecorator(newFileName);
		
		return newFile;
	}

	/**
	 * Returns the file path.
	 * @return the file path
	 */
	public String getContent() {
		return this.content;
	}

}
