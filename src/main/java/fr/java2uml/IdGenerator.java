package fr.java2uml;

import java.util.UUID;

public class IdGenerator {
    private static IdGenerator instance;

    public String createId(){
        return UUID.randomUUID().toString();
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }
}
