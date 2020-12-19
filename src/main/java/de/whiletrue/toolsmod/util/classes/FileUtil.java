package de.whiletrue.toolsmod.util.classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

public class FileUtil {

	private static FileUtil instance;
	
	private FileUtil() {}
	
	public static FileUtil getInstance() {
		if(instance==null)
			instance=new FileUtil();
		return instance;
	}
	
	/**
	 * Reads the content from the file and returns it
	 * @param location the location where the file is stored
	 * @return if loadedable the loaded content, otherwise empty
	 */
	public Optional<String> loadFromRSC(ResourceLocation location){
		try {
			//Loads the resource
			InputStream in = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream();
			//Reads all text
			return Optional.of(new BufferedReader(new InputStreamReader(in))
					.lines()
					.collect(Collectors.joining("\n")));
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	/**
	 * Wrapper for the {@link #loadFromRSC(ResourceLocation)} function with the json element
	 */
	public Optional<JsonElement> loadFromRSCAsJson(ResourceLocation location){
		return this.loadFromRSC(location).map(new JsonParser()::parse);
	}
	
	/**
	 * Reads the given amount of lines from the file
	 * @param file the file to read from
	 * @param amount the amount of lines
	 * @return optionally the lines that got read
	 */
	public Optional<String[]> readLines(String file,int amount){
		//End-lines
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file)))){
			String[] lines = new String[amount];
			
	        //Current scanned line
	        String ln;
	        
	        //Reads all lines
	        for(int i=0;i<amount;i++)
	        	if((ln=br.readLine())!=null)
	        		lines[i]=ln;
	        	else
	        		throw new Exception();

	        //Returns the content
	        return Optional.of(lines);
		} catch (Exception e) {
			//Something went wrong
			return Optional.empty();
		}
	}
	
	/**
	 * Loads a file from a remote location
	 * @param url the url to the file
	 * @return optionally the loaded content
	 */
	public Optional<JsonElement> loadRemoteFileAsJson(String url) {
		try(
			//Opens the stream
			InputStream in = new URL(url).openConnection().getInputStream();
			//Starts the reader	
			BufferedReader br = new BufferedReader(new InputStreamReader(in))
			){
	        //Returns the content
	        return Optional.of(new JsonParser().parse(br));
		} catch (Exception e) {
			//Something went wrong
			return Optional.empty();
		}
	}
	
	/**
	 * Loads a file from a remote location
	 * @param url the url to the file
	 * @return optionally the loaded content
	 */
	public Optional<String> loadRemoteFile(String url) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()))){
			
			//Final output
	        StringBuilder out = new StringBuilder();
	        //Current scanned line
	        String ln;
	        
	        //Reads the file
	        while((ln=br.readLine())!=null)
	        	out.append(ln);
	        
	        //Returns the content
	        return Optional.of(out.toString());
		} catch (Exception e) {
			//Something went wrong
			return Optional.empty();
		}
	}
	
	/**
	 * Loads a file as a string
	 * @param file the file
	 * @return optionally the loaded content
	 */
	public Optional<String> loadFile(String file){
		try{
			//Loads the data
			return Optional.of(new String(Files.readAllBytes(new File(file).toPath())));
		} catch (Exception e) {
			//Something went wrong
			return Optional.empty();
		}
	}
	
	/**
	 * Loads a file as a json element
	 * @param file the file
	 * @return optionally the loaded content
	 */
	public Optional<JsonElement> loadFileAsJson(String file){
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file)))){
			
			//Parses the file
			return Optional.of(
				new JsonParser().parse(br)
            );
		} catch (Exception e) {
			//Something went wrong
			return Optional.empty();
		}
	}
	
	/**
	 * Loads a file as a string from the resources
	 * @param file the file
	 * @return optionally the loaded content
	 */
	public Optional<String> loadClientFile(String file){
		//Loads the file
        try(
    		//Gets the file
        	InputStream in = Minecraft.getInstance().getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES,new ResourceLocation(file));
    		//Gets the reader
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
        	){
        	//Final output
        	StringBuilder out = new StringBuilder();
        	//Current scanned line
        	String ln;
        	
        	//Reads the file
        	while((ln=reader.readLine())!=null)
        		out.append(ln);
        	//Returns the content
        	return Optional.of(out.toString());
        }catch(Exception e) {
        	return Optional.empty();
        }
	}
	
	/**
	 * Loads a file as a json-element from the resources
	 * @param file the file
	 * @return optionally the loaded json-element
	 */
	public Optional<JsonElement> loadClientFileAsJson(String file){
		//Loads the alphabets
        try(
    		//Gets the file
        	InputStream in = Minecraft.getInstance().getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES,new ResourceLocation(file));
    		//Gets the reader
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
        	){
            return Optional.of(
            	new JsonParser().parse(reader)
            );
        }catch(Exception e) {
        	return Optional.empty();
        }
	}
	
	/**
	 * Writes an object to the given file
	 * @param file the file
	 * @param obj the object
	 */
	public boolean printToFile(String file,Object obj) {
		//Gets the file
		File f = new File(file);
		
		//Create path if not exists
		if(f.getParentFile()!=null && !f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		//Create file if no exists
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {}
		}
		
		//Print the content
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
			bw.write(obj.toString());
			bw.flush();
			return true;
		}catch(Exception e) {
			//Something went wrong
			return false;
		}
	}
	
}