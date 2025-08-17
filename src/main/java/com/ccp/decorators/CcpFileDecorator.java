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

import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;


public class CcpFileDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFileDecorator parent;
	protected CcpFileDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	private CcpFileDecorator getParent(String content) {
		
		File file = new File(content);
		String ap = file.getAbsolutePath();
		String replace = ap.replace('\\', File.separatorChar);
		File file2 = new File(replace);
		File parentFile = file2.getParentFile();
		if(parentFile == null) {
			return null;
		}
		String absolutePath = parentFile.getAbsolutePath();
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(absolutePath);
		return ccpFileDecorator;
	}

	public CcpFileDecorator zip() {
		
		File fileToZip = tryToCreateFolder();
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fos = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			CcpFileDecorator zip = this.zip(fileToZip, zipOut);
			return zip;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		File file = tryToCreateFolder();
		String name = file.getName();
		return name;
	}
	public String getPath() {
		File file = tryToCreateFolder();
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	private CcpFileDecorator zip(File fileToZip, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return this;
        }
        if (fileToZip.isDirectory()) {
            String terminacao = "/";
        	if (this.content.endsWith("/")) {
        		terminacao = ""; 
            } 
            ZipEntry e = new ZipEntry(this.content + terminacao);
			zipOut.putNextEntry(e);
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                new CcpFileDecorator(this.content + "/" + childFile.getName()).zip(childFile, zipOut);
            }
            return this;
        }
        try(FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(this.content);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
			return this;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
	public  String getStringContent() {
		File file = tryToCreateFolder();
		boolean fileIsMissing = file.exists() == false;
		if(fileIsMissing) {
			throw new CcpErrorFolderParentIsMissing(this);
		}
		try {
			Path path = file.toPath();
			byte[] fileContent = Files.readAllBytes(path);
			String string = new String(fileContent, "UTF-8");
			return string;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public CcpFileDecorator write(String content) {
		this.reset();
		CcpFileDecorator append = this.append(content);
		return append;
		
	}
	
	public CcpFileDecorator append(String content) {
		try {
			File file = new File(this.content);
			if (file.exists() == false) {
				file.createNewFile();
			}
			byte[] bytes = (content + "\n").getBytes();
			Files.write(Paths.get(this.content), bytes, StandardOpenOption.APPEND);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public CcpFileDecorator reset() {

		File f = this.tryToCreateFolder();
		
		f.delete();
		try {
			f.createNewFile();
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File tryToCreateFolder() {
		File f = new File(this.content);
		File parentFile = f.getParentFile();
		boolean alreadyCreated = parentFile.exists();

		if(alreadyCreated) {
			return f;
		}
		
		parentFile.mkdir();
		
		return f;
	}
	public List<String> getLines(){
		String filePath = this.content;
		ArrayList<String> linesFromFile = new ArrayList<>();
		String line;
		try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
			while ((line = br.readLine()) != null) {
				linesFromFile.add(line);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return linesFromFile;
	}
	public static interface FileLineReader {
		void onRead(String fileLine, int lineNumber);
	}

	public  CcpFileDecorator readLines(FileLineReader reader){
		String line;
		try (FileReader fr = new FileReader(this.content); BufferedReader br = new BufferedReader(fr)) {
			int k = 0;
			while ((line = br.readLine()) != null) {
				reader.onRead(line, k++);
			}
			return this;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public String toString() {
		return new File(this.content).getName();
	}

	public boolean exists() {
		File file = tryToCreateFolder();
		return file.exists();
	}
	public boolean isFile() {
		if(this.exists() == false) {
			return false;
		}
		File file = new File(this.content);
		boolean directory = file.isDirectory();
		if(directory) {
			return false;
		}
		return true;
	}
	public CcpFolderDecorator asFolder() {
		return new CcpFolderDecorator(this.content);
	}
	
	public CcpJsonRepresentation asSingleJson() {
		String string = this.getStringContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(string);
		return json;
	}
	
	public List<CcpJsonRepresentation> asJsonList(){
		CcpJsonHandler dependency = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String string = this.getStringContent();
		List<Map<String, Object>> list = dependency.fromJson(string);
		List<CcpJsonRepresentation> collect = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		return collect;
	}
	
	public CcpFileDecorator remove() {
		
		File file = tryToCreateFolder();
		file.delete();
		return this;
	}
	
	public CcpFileDecorator rename(String newFileName) {
		
		File file = new File(this.content);
		
		file.renameTo(new File(newFileName));
		
		CcpFileDecorator newFile = new CcpFileDecorator(newFileName);
		
		return newFile;
	}

	public String getContent() {
		return this.content;
	}
}
