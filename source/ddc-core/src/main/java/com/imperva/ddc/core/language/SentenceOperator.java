package com.imperva.ddc.core.language;

/**
 * Created by gabi.beyo on 21/06/2015.
 * Operators which glues various sentences together
 */
public enum SentenceOperator {
    /**
     * Glue Phrases {@link Phrase} and Sentences {@link Sentence} with no logical operator
     */
    EMPTY(0),
    /**
     * Glue Phrases {@link Phrase} and Sentences {@link Sentence}  with the logical AND operator
     */
    AND(1),
    /**
     * Glue Phrases {@link Phrase} and Sentences {@link Sentence}  with the logical OR operator
     */
    OR(2);

    Integer scope;
    private SentenceOperator(int scope) {
        this.scope = scope;
    }
}
