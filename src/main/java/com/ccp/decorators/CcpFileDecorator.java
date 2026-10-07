package com.ccp.decorators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
 * Only the writing operations ({@code append}, {@code write}, {@code reset}) create the missing parent directories; the
 * text is written and read as UTF-8.
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
	 * Compresses the file (or the directory, recursively) into {@code <name>.zip} next to it (see
	 * {@link #zipNextToTheOriginal(File)}).
	 * @return this decorator
	 */
	public CcpFileDecorator zip() {
		File fileToZip = new File(this.content);
		zipNextToTheOriginal(fileToZip);
		return this;
	}

	/**
	 * Compresses the file, or the directory recursively, into {@code <name>.zip} in the same directory as it, with the
	 * entries named relative to that directory ({@code <name>/}, {@code <name>/child.txt}). Hidden entries are skipped.
	 * Until 2026-10-07 the zip was created in the working directory of the process and its entries were named by the full
	 * path ({@code C:/...}), so extracting it elsewhere rebuilt the whole tree of the original machine.
	 * @param original the file or directory
	 * @return the zip file
	 */
	static File zipNextToTheOriginal(File original) {
		File absoluteOriginal = original.getAbsoluteFile();
		File directory = absoluteOriginal.getParentFile();
		String originalName = absoluteOriginal.getName();
		File zipFile = new File(directory, originalName + ".zip");
		Path base = directory.toPath();
		try(FileOutputStream fileOutputStream = new FileOutputStream(zipFile); ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream)) {
			addToZip(absoluteOriginal, base, zipOut);
		}
		return zipFile;
	}
	
	/**
	 * Returns only the file name (without the path).
	 * @return the file name
	 */
	public String getName() {
		File file = new File(this.content);
		String name = file.getName();
		return name;
	}
	/**
	 * Returns the absolute path of the file.
	 * @return the absolute path
	 */
	public String getPath() {
		File file = new File(this.content);
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	/**
	 * Adds the file, or the directory and its children recursively, to the ZIP stream, named relative to the base.
	 * @param entry the file or directory to add
	 * @param base the directory the entry names are relative to
	 * @param zipOut the ZIP stream
	 * @throws IOException when reading a file or writing the stream fails
	 */
	private static void addToZip(File entry, Path base, ZipOutputStream zipOut) throws IOException {
		boolean hidden = entry.isHidden();
		if (hidden) {
			return;
		}
		Path entryPath = entry.toPath();
		Path relativePath = base.relativize(entryPath);
		String relativeName = relativePath.toString().replace('\\', '/');
		boolean isDirectory = entry.isDirectory();
		if (isDirectory) {
			ZipEntry directoryEntry = new ZipEntry(relativeName + "/");
			zipOut.putNextEntry(directoryEntry);
			zipOut.closeEntry();
			File[] children = entry.listFiles();
			for (File child : children) {
				addToZip(child, base, zipOut);
			}
			return;
		}
		ZipEntry fileEntry = new ZipEntry(relativeName);
		zipOut.putNextEntry(fileEntry);
		Files.copy(entryPath, zipOut);
		zipOut.closeEntry();
	}
	/**
	 * Reads the whole file content as UTF-8 text.
	 * @return the file content
	 * @throws CcpErrorFileIsMissing when the file does not exist
	 */
	public  String getStringContent() {
		File file = new File(this.content);
		boolean exists = file.exists();
		boolean fileIsMissing = false == exists;
		if(fileIsMissing) {
			CcpErrorFileIsMissing fileMissingError = new CcpErrorFileIsMissing(this);
			throw fileMissingError;
		}
		Path path = file.toPath();
		byte[] fileContent = Files.readAllBytes(path);
		String fileText = new String(fileContent, StandardCharsets.UTF_8);
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
	 * Appends the text followed by a line feed ({@code \n}) to the end of the file, as UTF-8, creating the file and its
	 * missing parent directories when needed. Until 2026-10-07 the bytes used the platform default charset (windows-1252
	 * on Windows with JDK 17), while {@link #getStringContent()} reads UTF-8, so accented text came back corrupted; and a
	 * missing parent directory raised an error instead of being created.
	 * @param content the content to append
	 * @return this decorator
	 */
	public CcpFileDecorator append(String content) {
		File file = this.tryToCreateParentFolder();
		boolean exists = file.exists();
		boolean fileIsMissing = false == exists;
		if (fileIsMissing) {
			file.createNewFile();
		}
		String contentWithLineBreak = content + "\n";
		byte[] bytes = contentWithLineBreak.getBytes(StandardCharsets.UTF_8);
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
	 * Creates the missing parent directories of the file. Only the writing methods ({@link #append}, {@link #write},
	 * {@link #reset}) call it; until 2026-10-07 the reading ones ({@code exists}, {@code getName}, {@code getPath},
	 * {@code getStringContent}, {@code remove}, {@code zip}) also did, so asking about a path created its directories.
	 * @return the file
	 */
	private File tryToCreateParentFolder() {
		File file = new File(this.content);
		File absoluteFile = file.getAbsoluteFile();
		String parent = absoluteFile.getParent();
		CcpFolderDecorator folder = new CcpFolderDecorator(parent);
		folder.createFolderIfNotExists();
		return file;
	}
	/**
	 * Reads every line of the file as UTF-8 (until 2026-10-07, the platform default charset).
	 * @return the lines, without terminators
	 */
	public List<String> getLines(){
		String filePath = this.content;
		ArrayList<String> linesFromFile = new ArrayList<>();
		String line;
		try (FileReader fileReader = new FileReader(filePath, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			while ((line = bufferedReader.readLine()) != null) {
				linesFromFile.add(line);
			}
		}
		return linesFromFile;
	}


	/**
	 * Reads the file line by line as UTF-8 (until 2026-10-07, the platform default charset), calling
	 * {@code reader.onRead(line, index)} for each line; the index starts at zero.
	 * @param reader the callback called for each line
	 * @return this decorator
	 */
	public  CcpFileDecorator readLines(FileLineReader reader){
		String line;
		try (FileReader fileReader = new FileReader(this.content, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
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
		File file = new File(this.content);
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

		File file = new File(this.content);
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
