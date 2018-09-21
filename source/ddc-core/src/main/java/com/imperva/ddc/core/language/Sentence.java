package com.imperva.ddc.core.language;

import java.util.List;

/**
 * Created by gabi.beyo on 21/06/2015.
 * Sentence is the 'Composite' in the Composite Design Pattern
 * A Sentence is an abstract representation of a search criteria request
 * A Sentence is composed of two or more phrases or one or more sentences
 */
public class Sentence extends Word {

    private List<Word> sentences;
    private SentenceOperator operator;

    /**
     * @return Operators which glues various sentences together
     */
    public SentenceOperator getOperator() {
        return operator;
    }
    public void setOperator(SentenceOperator operator) {
        this.operator = operator;
    }

    /**
     * @return A list of {@link Word}. List of sentences (a Sentence can be a Sentence object or a phrase as well)
     */
    public List<Word> getSentences() {
        return sentences;
    }

    /**
     * @param sentences A list of {@link Word}. List of sentences (a Sentence can be a Sentence object or a phrase as well)
     */
    public void setSentences(List<Word> sentences) {
        this.sentences = sentences;
    }
}
