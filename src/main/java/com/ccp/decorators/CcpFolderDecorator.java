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
 * Decorator sobre um caminho de diretório no sistema de arquivos. Oferece operações de criação de subpastas e arquivos,
 * iteração sobre conteúdo, compactação ZIP e remoção recursiva.
 */
public class CcpFolderDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFolderDecorator parent;
	/**
	 * Encapsula o caminho e resolve o diretório pai.
	 * @param content o caminho do diretório
	 */
	protected CcpFolderDecorator(String content) {
		this.parent = this.getParent(content);
		this.content = content;
	}

	@CcpAllowNullReturn
	private CcpFolderDecorator getParent(String content) {
		File file = new File(content);
		File parentFile = file.getParentFile();
		boolean parentFileIgual = parentFile == null;
		if(parentFileIgual) {
			return null;
		}
		String absolutePath = parentFile.getAbsolutePath();
		CcpFolderDecorator ccpFileDecorator = new CcpFolderDecorator(absolutePath);
		return ccpFileDecorator;
	}
	
	/**
	 * Reinterpreta o caminho como um arquivo.
	 */
	public CcpFileDecorator asFile() {
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(this.content);
		return ccpFileDecorator;
	}

	/**
	 * Compacta o diretório inteiro em um arquivo {@code .zip} com o mesmo nome.
	 */
	public CcpFolderDecorator zip() {
		
		File fileToZip = new File(this.content);
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fos = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			CcpFolderDecorator zip = this.zip(fileToZip, zipOut);
			return zip;
		}
	}

	/**
	 * Retorna apenas o nome do diretório (sem o caminho pai).
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
        boolean directory = fileToZip.isDirectory();
        if (directory) {
            String terminacao = "/";
        	   boolean endsWith = this.content.endsWith("/");
        	   if (endsWith) {
        		terminacao = ""; 
            } 
            String contentMais = this.content + terminacao;
            ZipEntry e = new ZipEntry(contentMais);
			zipOut.putNextEntry(e);
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                String contentMais2 = this.content + "/";
                String childFileName = childFile.getName();
                String contentMais2Mais = contentMais2 + childFileName;
                CcpFolderDecorator ccpFolderDecorator = new CcpFolderDecorator(contentMais2Mais);
                ccpFolderDecorator.zip(childFile, zipOut);
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
		}
    }
	
	/**
	 * Itera sobre cada entrada do diretório e chama o {@code consumer} passando um {@code CcpFolderDecorator} para cada entrada.
	 * @param consumer o callback a ser chamado para cada entrada
	 */
	public CcpFolderDecorator readFolders(Consumer<CcpFolderDecorator> consumer){
		File file2 = new File(this.content);
		File[] files = file2.listFiles();
		boolean filesIgual = files == null;
		if(filesIgual) {
			return this;
		}
		for (File file : files) {
			String absolutePath = file.getAbsolutePath();
			CcpFolderDecorator f = new CcpFolderDecorator(absolutePath);
			consumer.accept(f);
		}
        return this;
	}
	
	/**
	 * Itera sobre cada entrada do diretório e chama o {@code consumer} com um {@code CcpFileDecorator} para cada entrada.
	 * @param consumer o callback a ser chamado para cada arquivo
	 */
	public CcpFolderDecorator readFiles(Consumer<CcpFileDecorator> consumer){
		File file3 = new File(this.content);
		File[] files = file3.listFiles();
		boolean filesIgual2 = files == null;
		if(filesIgual2) {
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
		File file4 = new File(this.content);
		String file4Name = file4.getName();
		return file4Name;
	}

	/**
	 * Verifica se o diretório existe.
	 */
	public boolean exists() {
		File file = new File(this.content);
		boolean exists = file.exists();
		return exists;
	}

	/**
	 * Cria um subdiretório com o nome fornecido (se não existir) e retorna o decorator do novo diretório.
	 * @param folderName o nome do subdiretório a criar
	 */
	public CcpFolderDecorator createNewFolderIfNotExists(String folderName) {
		String completePath = this.getCompletePath(folderName);
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator ccpFolderDecorator2 = new CcpFolderDecorator(completePath);
		return ccpFolderDecorator2;
	}

	/**
	 * Cria o próprio diretório se não existir.
	 */
	public CcpFolderDecorator createNewFolderIfNotExists() {
		String completePath = this.getCompletePath("");
		File file = new File(completePath);
		file.mkdir();
		CcpFolderDecorator ccpFolderDecorator3 = new CcpFolderDecorator(completePath);
		return ccpFolderDecorator3;
	}
	
	/**
	 * Cria um arquivo vazio dentro do diretório (garantindo que a estrutura de pastas exista) e retorna seu decorator.
	 * @param fileName o nome do arquivo a criar
	 */
	public CcpFileDecorator createNewFileIfNotExists(String fileName) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator file = new CcpFileDecorator(completePath);
		this.createFolderIfNotExists();
		CcpFileDecorator append = file.append("");
		return append;
	}
	
	public boolean createFolderIfNotExists() {
		boolean exists2 = this.parent.exists();
	
		boolean isNewFolder = false == exists2;
		
		if(isNewFolder) {
			this.parent.createFolderIfNotExists();
		}
		this.createNewFolderIfNotExists();
		return true;
	}
	
	/**
	 * Escreve {@code fileContent} no arquivo {@code fileName} dentro do diretório e retorna o decorator do arquivo.
	 * @param fileName o nome do arquivo
	 * @param fileContent o conteúdo a escrever
	 */
	public CcpFileDecorator writeInTheFile(String fileName, String fileContent) {
		String completePath = this.getCompletePath(fileName);
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(completePath);
		CcpFileDecorator write = ccpFileDecorator.write(fileContent);
		return write;
	}

	private String getCompletePath(String fileName) {
		String contentMais3 = this.content + File.separator;
		String contentMais3Mais = contentMais3 + fileName;
		return contentMais3Mais;
	}
	
	/**
	 * Remove todos os arquivos do diretório e depois o próprio diretório.
	 */
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

	/**
	 * Implementação de {@code CcpDecorator}; retorna o caminho do diretório.
	 */
	public String getContent() {
		return this.content;
	}
}
