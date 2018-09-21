package com.imperva.ddc.core.language;

import com.imperva.ddc.core.query.Field;

/**
 * Created by gabi.beyo on 21/06/2015.
 * Phrase is the Leaf in the Composite Design Pattern
 * A Phrase is the smallest unit in a valid search request {@link Sentence}
 */
public class Phrase extends Word {
    private Field attribute;
    private String value;
    private PhraseOperator phraseOperator;

    /**
     * @return Phrase value
     */
    public String getValue() { return value; }

    /**
     * @param value Phrase value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return A {@link Field} object which represent a specific attribute
     */
    public Field getAttribute() {
        return attribute;
    }

    /**
     * @param attribute A {@link Field} object which represent a specific attribute
     */
    public void setAttribute(Field attribute) {
        this.attribute = attribute;
    }

    /**
     * @return A {@link PhraseOperator} which glues various phrases together
     */
    public PhraseOperator getPhraseOperator() { return phraseOperator; }

    /**
     * @param phraseOperator  A {@link PhraseOperator} which glues various phrases together
     */
    public void setPhraseOperator(PhraseOperator phraseOperator) { this.phraseOperator = phraseOperator; }
}

