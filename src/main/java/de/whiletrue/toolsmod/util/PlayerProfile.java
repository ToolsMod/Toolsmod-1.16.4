package de.whiletrue.toolsmod.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerProfile {

    //Current username
    private String name
    //Users UUID
    ,uuid;
    //Previous used names
    private List<Name> previousNames;

    public PlayerProfile(String name, String uuid, List<Name> previousNames) {
        this.name=name;
        this.uuid=uuid;
        this.previousNames=previousNames;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public List<Name> getPreviousNames() {
        return previousNames;
    }

    public static class Name{
        //Username
        private String name;
        //Change-date in ms
        private long changeDate;

        public Name(String name,long changeDate) {
            this.name=name;
            this.changeDate=changeDate;
        }


        public String getName() {
            return name;
        }

        public long getChangeDate() {
            return changeDate;
        }

        /**
         * Returns the optional change-date
         * if optional is empty it means it was the first name
         * */
        public Optional<String> formattedDate() {
            //Checks if the date is empty
            if(this.changeDate==-1)
                return Optional.empty();
            //Creates the formatted date
            return Optional.of(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(this.changeDate)));
        }
    }
}
