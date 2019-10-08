package com.imperva.ddc.core.language;

import com.imperva.ddc.core.query.Field;
import com.imperva.ddc.core.query.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 22/06/2015.
 * Tool which helps build search requests represented by abstract objects.
 * On 'execute' the "Sentences" will be automatically translated to the correct concrete syntax.
 */
public class QueryAssembler {

    private List<Word> searchPhrases = new ArrayList<Word>();

    /**
     * Adds a Phrase to the LDAP query
     * @param fieldType {@link FieldType} One of the available query attributes
     * @param phraseOperator {@link PhraseOperator}
     * @param value The query value
     * @return {@link QueryAssembler} Fluent API
     */
    public QueryAssembler addPhrase(FieldType fieldType, PhraseOperator phraseOperator, String value) {
        Phrase phrase = new Phrase();
        phrase.setValue(value);
        phrase.setPhraseOperator(phraseOperator);
        this.addPhrase(fieldType, phrase);
        return this;
    }

    public QueryAssembler addPhrase(String fieldName, PhraseOperator phraseOperator, String value) {
        Phrase phrase = new Phrase();
        phrase.setValue(value);
        phrase.setPhraseOperator(phraseOperator);
        this.addPhrase(fieldName, phrase);
        return this;
    }

    /**
     * Adds a Phrase to the LDAP query
     * @param fieldType {@link FieldType} One of the available query attributes
     * @param phrase {@link Phrase} The Phrase to add to the query
     * @return {@link QueryAssembler} Fluent API
     */
    public QueryAssembler addPhrase(FieldType fieldType, Phrase phrase) {
        Field field = new Field();
        field.setType(fieldType);
        phrase.setAttribute(field);
        searchPhrases.add(phrase);
        return this;
    }


    public QueryAssembler addPhrase(String fieldName, Phrase phrase) {
        Field field = new Field();
        field.setName(fieldName);
        phrase.setAttribute(field);
        searchPhrases.add(phrase);
        return this;
    }

    /**
     * Adds a Sentence to the LDAP query
     * @param sentence {@link Sentence} The Sentence to add
     * @return {@link QueryAssembler} Fluent API
     */
    public QueryAssembler addSentence(Sentence sentence){
        searchPhrases.add(sentence);
        return this;
    }

    /**
     * Returns the assembled Sentence by gluing the various Phrases with a SentenceOperator
     * This method also clears the QueryAssembler state for further Queries
     * @param sentenceOperator {@link SentenceOperator}
     * @return {@link Sentence} The entire Sentence list
     */
    public Sentence closeSentence(SentenceOperator sentenceOperator) {
        Sentence sentence = makeSentence(searchPhrases, sentenceOperator);
        searchPhrases = new ArrayList<Word>();
        return sentence;
    }

    /**
     * Returns the assembled Sentence by gluing the various Phrases with a default SentenceOperator.EMPTY operator
     * This method also clears the QueryAssembler state for further Queries
     * @return {@link Sentence} The entire Sentence list
     */
    public Sentence closeSentence() {
        Sentence sentence = makeSentence(searchPhrases, SentenceOperator.EMPTY);
        searchPhrases = new ArrayList<Word>();
        return sentence;
    }

    /**
     * A utility helper to create a Sentence
     * @param phrases List ow words {@link Word} which assembles the entire query
     * @param sentenceOperator {@link SentenceOperator}
     * @return {@link Sentence} The Sentence
     */
    private Sentence makeSentence(List<Word> phrases, SentenceOperator sentenceOperator) {
        Sentence sentence = new Sentence();
        sentence.setOperator(sentenceOperator);
        sentence.setSentences(phrases);
        return sentence;
    }

    /**
     * A utility helper to easely add additional Sentences to the current Sentences' list
     * @param newSentence The new Sentence to append {@link Sentence}
     * @param currentSentence The existing Sentence to append to {@link Sentence}
     * @return {@link Sentence} The new assembled Sentence
     */
    public Sentence appendSentence(Sentence newSentence, Sentence currentSentence ){
        Sentence newAssembledSentence = null;
        if (newSentence != null && currentSentence != null) {
            newAssembledSentence = this.addSentence(newSentence)
                    .addSentence(currentSentence)
                    .closeSentence(SentenceOperator.AND);
        } else if (newSentence != null && currentSentence == null) {
            newAssembledSentence = newSentence;
        } else if (newSentence == null && currentSentence != null) {
            newAssembledSentence = currentSentence;
        }
        return newAssembledSentence;
    }
}
