package de.whiletrue.toolsmod.module.defined.tools.headwriter;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.stream.Collectors;

public class Alphabet {

    //Return if no character got found
    private static String ERROR_RETURN = "2e3f50ba62cbda3ecf5479b62fedebd61d76589771cc19286bf2745cd71e47c6";

    //The loaded alphabet
    private Map<String,String> loadedAlphabet;
    //The name of the alphabet
    private String name;

    public Alphabet(String name,JsonObject alphabet) {
        //Maps the alphabet to the map
        this.loadedAlphabet=alphabet.entrySet().stream().collect(Collectors.toMap(i->i.getKey().toLowerCase(), i->i.getValue().getAsString()));
        this.name=name;
    }

    public String getName() {
        return name;
    }

    /**
     * @param c the searched character
     * @return the id by the given character
     */
    public String getIDFromChar(char c){
        //Trie's to get the id by its character
        return this.loadedAlphabet.getOrDefault(
                String.valueOf(c).toLowerCase(),
                //Default tries to get the empty field
                this.loadedAlphabet.getOrDefault(" ",
                        //If the empty field could't be found, return the default error
                        ERROR_RETURN
                )
        );
    }
}
