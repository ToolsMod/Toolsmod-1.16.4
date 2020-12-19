package de.whiletrue.toolsmod.util.classes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import de.whiletrue.toolsmod.util.PlayerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class MojangUtils {

	private static MojangUtils instance;
	
	private MojangUtils() {}
	
	public static MojangUtils getInstance() {
		if(instance==null)
			instance=new MojangUtils();
		return instance;
	}

	/**
	 * Returns an optional player-profile with the uuid and all used names
	 *
	 * @param username the user of which the profile should be search for
	 * */
	public Optional<PlayerProfile> getPlayerProfile(String username){
		try{
			//Gets the UUID from the mojang-API
			JsonObject data = new JsonParser().parse(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/"+username).openStream())).getAsJsonObject();
			//Gets the data
			String name = data.get("name").getAsString();
			String uuid = data.get("id").getAsString();

			//Gets the profile information from the mojang-API
			JsonArray allNames = new JsonParser().parse(new InputStreamReader(new URL("https://api.mojang.com/user/profiles/"+uuid+"/names").openStream())).getAsJsonArray();
			//List with all names
			List<PlayerProfile.Name> previousNames = Streams.stream(allNames).map(i->{
				//Gets the object
				JsonObject obj = i.getAsJsonObject();
				//Converts it to the name
				return new PlayerProfile.Name(
						obj.get("name").getAsString(),
						obj.has("changedToAt")?obj.get("changedToAt").getAsLong():-1
				);
			}).collect(Collectors.toList());

			//Creates the user profile
			return Optional.of(new PlayerProfile(name,uuid,previousNames));
		}catch(Exception e){
			//Failed to get the profile
			return Optional.empty();
		}
	}

	/**
	 * Tries to get an auth token from a user using the email and password.
	 * 
	 * Can fail with the following exceptions:
	 * IOExceptions => Network failed
	 * Exception("ForbiddenOperationException") => Eighter Invalid username or password or too many failed login attempts
	 * More...
	 * 
	 * @param email the email
	 * @param password the password
	 */
	public String getAuthToken(String email,String password) throws IOException, Exception{
		// Creates the request
		HttpPost www = new HttpPost("https://authserver.mojang.com/authenticate");
		www.addHeader("Content-Type","application/json");
		
		// Creates the json object to send to the server
		JsonObject dataObject = new JsonObject();
		{
			// Creates the agent object
			JsonObject agent = new JsonObject();
			{
				agent.addProperty("name", "Minecraft");
				agent.addProperty("version", 1);
			}
			
			// Defines which game of mojang should be authenticated
			dataObject.add("agent", agent);
			
			// The credentials to authenticate
			dataObject.addProperty("username", email);
			dataObject.addProperty("password", password);
		};
		
		// Creates the body for the request
		StringEntity ent = new StringEntity(dataObject.toString());
		www.setEntity(ent);
		
		// Gets the response
		CloseableHttpResponse client = HttpClientBuilder.create().build().execute(www);
		
		// Gets the response
		JsonObject resp = new JsonParser().parse(JavaUtil.getInstance().convertInputStreamToString(client.getEntity().getContent())).getAsJsonObject();
		
		// Checks for an error
		if(resp.has("error"))
			// Parses on the error
			throw new Exception(resp.get("error").getAsString());
		else
			// Just returns the token
			return resp.get("accessToken").getAsString();
	}
	
	/**
	 * Lets the game use a registered account
	 *
	 * @param username the username or email of the account
	 * @param password the password of the account
	 */
	public boolean login(String username,String password){
		try{
			//TODO: Implement client token
			/*
			//Creates the authentication
			UserAuthentication auth = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy(),UUID.randomUUID().toString()),Agent.MINECRAFT);
			//Sets the credentials
			auth.setUsername(username);
			auth.setPassword(password);
			//Tries to login
			auth.logIn();

			//Gets the new session
			Session session = new Session(
					auth.getSelectedProfile().getName(),
					auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(),
					username.contains("@")?"mojang":"legacy"
			);

			//Gets the session field
			Field f = Minecraft.class.getField("session");
			//Sets the new username
			f.set(Minecraft.getInstance(),session);

			//Login Successful
			return true;*/
			throw new Exception();
		}catch(Exception e){
			//Failed to login
			return false;
		}
	}

	/**
	 * Lets the game use a cracked account
	 *
	 * @param username the username that the game will use
	 * */
	public void login(String username){
		try {
			//Gets the session field
			Field f = Session.class.getField("username");
			//Sets the new username
			f.set(Minecraft.getInstance().getSession(),username);
		}catch(Exception e){}
	}
}
