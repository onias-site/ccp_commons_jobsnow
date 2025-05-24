package com.ccp.decorators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CcpFolderDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFolderDecorator parent;
	protected CcpFolderDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	private CcpFolderDecorator getParent(String content) {
		File file = new File(content);
		File parentFile = file.getParentFile();
		if(parentFile == null) {
			return null;
		}
		String absolutePath = parentFile.getAbsolutePath();
		CcpFolderDecorator ccpFileDecorator = new CcpFolderDecorator(absolutePath);
		return ccpFileDecorator;
	}
	
	public CcpFileDecorator asFile() {
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(this.content);
		return ccpFileDecorator;
	}

	public CcpFolderDecorator zip() {
		
		File fileToZip = new File(this.content);
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fos = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			CcpFolderDecorator zip = this.zip(fileToZip, zipOut);
			return zip;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		File file = new File(this.content);
		String name = file.getName();
		return name;
	}

	private CcpFolderDecorator zip(File fileToZip, ZipOutputStream zipOut) throws IOException {
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
                new CcpFolderDecorator(this.content + "/" + childFile.getName()).zip(childFile, zipOut);
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
	
	public CcpFolderDecorator readFolders(Consumer<CcpFolderDecorator> consumer){
		File[] files = new File(this.content).listFiles();
		if(files == null) {
			return this;
		}
		for (File file : files) {
			String absolutePath = file.getAbsolutePath();
			CcpFolderDecorator f = new CcpFolderDecorator(absolutePath);
			consumer.accept(f);
		}
        return this;
	}
	
	public CcpFolderDecorator readFiles(Consumer<CcpFileDecorator> consumer){
		File[] files = new File(this.content).listFiles();
		if(files == null) {
			return this;
		}
		for (File file : files) {
			String absolutePath = file.getAbsolutePath();
			CcpFileDecorator f = new CcpFileDecorator(absolutePath);
			consumer.accept(f);
		}
        return this;
	}

	
	public String toString() {
		return new File(this.content).getName();
	}

	public boolean exists() {
		File file = new File(this.content);
		return file.exists();
	}

	public CcpFolderDecorator createNewFolderIfNotExists(String folderName) {
		String path = this.content + "\\" + folderName;
		new File(path).mkdir();
		return new CcpFolderDecorator(path);
	}
	
	public CcpFileDecorator createNewFileIfNotExists(String fileName) {
		String path = this.content +"\\"+ fileName;
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(path);
		CcpFileDecorator append = ccpFileDecorator.append("");
		return append;
	}
	
	public CcpFileDecorator writeInTheFile(String fileName, String fileContent) {
		return new CcpFileDecorator(this.content +"\\" + fileName).write(fileContent);
	}
	
	public CcpFolderDecorator remove() {
		File index = new File(this.content);
		String[]entries = index.list();
		
		for(String fileName: entries){
		    String path = index.getPath();
			File currentFile = new File(path, fileName);
		    currentFile.delete();
		}
		
		index.delete();
		
		return this;
	}

	public String getContent() {
		return this.content;
	}
}
