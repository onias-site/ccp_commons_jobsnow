package com.ccp.decorators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


/**
 * Decorator sobre um caminho de arquivo do sistema de arquivos. Encapsula operações de leitura, escrita, acréscimo,
 * compactação ZIP, remoção e conversão do conteúdo para outros tipos do framework (JSON, lista de JSONs).
 * Garante criação automática do diretório pai quando necessário.
 */
public class CcpFileDecorator implements CcpDecorator<String> {
	public final String content;
	public final CcpFileDecorator parent;
	/**
	 * Encapsula o caminho e resolve automaticamente o decorator do diretório pai.
	 * @param content o caminho do arquivo
	 */
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

	/**
	 * Compacta o arquivo ou diretório em um arquivo {@code .zip} com o mesmo nome no diretório corrente.
	 */
	public CcpFileDecorator zip() {
		
		File fileToZip = tryToCreateParentFolder();
		
		String fileName = fileToZip.getName();
		
		try(FileOutputStream fos = new FileOutputStream(fileName + ".zip");ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			CcpFileDecorator zip = this.zip(fileToZip, zipOut);
			return zip;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna apenas o nome do arquivo (sem o caminho).
	 */
	public String getName() {
		File file = tryToCreateParentFolder();
		String name = file.getName();
		return name;
	}
	/**
	 * Retorna o caminho absoluto completo do arquivo.
	 */
	public String getPath() {
		File file = tryToCreateParentFolder();
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
	/**
	 * Lê todo o conteúdo do arquivo como string UTF-8. Lança {@code CcpErrorFolderParentIsMissing} se o arquivo não existir.
	 */
	public  String getStringContent() {
		File file = tryToCreateParentFolder();
		boolean fileIsMissing = false == file.exists();
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
	/**
	 * Sobrescreve o arquivo com o conteúdo fornecido (limpa antes de escrever).
	 * @param content o conteúdo a ser escrito
	 */
	public CcpFileDecorator write(String content) {
		this.reset();
		CcpFileDecorator append = this.append(content);
		return append;
		
	}
	
	/**
	 * Acrescenta o conteúdo ao final do arquivo (cria o arquivo se não existir).
	 * @param content o conteúdo a ser acrescentado
	 */
	public CcpFileDecorator append(String content) {
		try {
			File file = new File(this.content);
			if (false == file.exists()) {
				file.createNewFile();
			}
			byte[] bytes = (content + "\n").getBytes();
			Files.write(Paths.get(this.content), bytes, StandardOpenOption.APPEND);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e); 
		}
	}
	/**
	 * Apaga o conteúdo do arquivo, deixando-o vazio (apaga e recria).
	 */
	public CcpFileDecorator reset() {

		File f = this.tryToCreateParentFolder();
		
		f.delete();
		try {
			f.createNewFile();
			return this;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("The file '" + this.content + "' does not exist", e);
		}catch (IOException e) {
			throw new RuntimeException("The file '" + this.content + "' has an error", e);
		}
	}

	private File tryToCreateParentFolder() {
		File f = new File(this.content);
		String parent = f.getParent();
		CcpFolderDecorator folder = new CcpFolderDecorator(parent);
		folder.createFolderIfNotExists();
		return f;
	}
	/**
	 * Lê todas as linhas do arquivo e as retorna como lista de strings.
	 */
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

	/**
	 * Lê o arquivo linha a linha, chamando o callback {@code reader.onRead(linha, índice)} para cada linha.
	 * @param reader o callback a ser chamado para cada linha
	 */
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

	/**
	 * Verifica se o arquivo existe no sistema de arquivos.
	 */
	public boolean exists() {
		File file = tryToCreateParentFolder();
		return file.exists();
	}
	/**
	 * Retorna {@code true} se o caminho existe e aponta para um arquivo (não um diretório).
	 */
	public boolean isFile() {
		if(false == this.exists()) {
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
	 * Reinterpreta o caminho como um diretório.
	 */
	public CcpFolderDecorator asFolder() {
		return new CcpFolderDecorator(this.content);
	}
	
	/**
	 * Lê o conteúdo do arquivo e o desserializa como um único JSON.
	 */
	public CcpJsonRepresentation asSingleJson() {
		String string = this.getStringContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(string);
		return json;
	}
	
	/**
	 * Lê o conteúdo do arquivo e o desserializa como uma lista de objetos JSON.
	 */
	public List<CcpJsonRepresentation> asJsonList(){
		CcpJsonHandler dependency = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String string = this.getStringContent();
		List<Map<String, Object>> list = dependency.fromJson(string);
		List<CcpJsonRepresentation> collect = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		return collect;
	}
	
	/**
	 * Remove o arquivo do sistema de arquivos.
	 */
	public CcpFileDecorator remove() {

		File file = tryToCreateParentFolder();
		file.delete();
		return this;
	}
	
	/**
	 * Renomeia o arquivo para o novo nome informado e retorna o decorator do novo arquivo.
	 * @param newFileName o novo nome do arquivo
	 */
	public CcpFileDecorator rename(String newFileName) {
		
		File file = new File(this.content);
		
		file.renameTo(new File(newFileName));
		
		CcpFileDecorator newFile = new CcpFileDecorator(newFileName);
		
		return newFile;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o caminho do arquivo.
	 */
	public String getContent() {
		return this.content;
	}

	@SuppressWarnings("serial")
	public static class CcpErrorFolderParentIsMissing extends RuntimeException {
		private CcpErrorFolderParentIsMissing(CcpFileDecorator decorator) {
			super("in the file " + new File(decorator.content).getParentFile().getAbsolutePath() + " is missing the file: " + decorator.content);
		}
	}
}
