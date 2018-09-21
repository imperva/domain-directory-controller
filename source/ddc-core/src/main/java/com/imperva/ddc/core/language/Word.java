package com.imperva.ddc.core.language;

import java.util.UUID;

/**
 * Created by gabi.beyo on 22/06/2015.
 *
 * Used only to mark Phrase and Sentence objects as Word in order to implement Composite Design Pattern.
 * Word is the 'Component' in the Design Pattern structure
 */
public abstract class Word {
    private String id;

    /**
     * Generates for each each Word (Phrases {@link Phrase} and Sentences {@link Sentence}) a unique ID
     */
    protected Word(){
        id = UUID.randomUUID().toString();
    }

    /**
     * Get the unique ID
     * @return String the Word's unique ID
     */
    public String getId() {
        return id;
    }
}
