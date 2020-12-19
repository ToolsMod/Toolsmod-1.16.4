package de.whiletrue.toolsmod.gui.config.sub.info.credits;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

public class Credit {

	//Author and text
	private String author,text;
	
	//Optional link
	@Nullable
	private String link;
	
	public Credit(JsonObject obj) throws Exception{
		this.author=obj.get("author").getAsString();
		this.text=obj.get("text").getAsString();
		if(obj.has("link"))
			this.link=obj.get("link").getAsString();
	}
	
	public String getAuthor() {
		return this.author;
	}
	public String getText() {
		return this.text;
	}
	public String getLink() {
		return this.link;
	}
	
}
