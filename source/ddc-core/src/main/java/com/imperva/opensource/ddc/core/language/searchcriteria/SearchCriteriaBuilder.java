package com.imperva.opensource.ddc.core.language.searchcriteria;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.exceptions.ParsingException;
import com.imperva.opensource.ddc.core.query.*;
import com.imperva.opensource.ddc.core.language.Phrase;
import com.imperva.opensource.ddc.core.language.Sentence;
import com.imperva.opensource.ddc.core.language.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gabi.beyo on 18/06/2015.
 */
public abstract class SearchCriteriaBuilder extends RequestBuilder{

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCriteriaBuilder.class.getName());
    private QueryRequest queryRequest;


    protected String translateFilter(Word word) {
        if(word == null)
            throw new ParsingException("Phrases and Sentences can't be empty");
        String result = "";
        if (word instanceof Phrase) {
            Phrase phrase = (Phrase) word;
            String escapedValue = escapeSpecialChars(phrase.getValue(), phrase.getAttribute());
            phrase.setValue(escapedValue);
            result += translatePhrase(phrase);
        } else if (word instanceof Sentence) {
            Sentence sentence = (Sentence) word;
            String translatedFilter = translateFilter(sentence.getSentences());
            result += translateSentence(translatedFilter, sentence);
        }
        return result.trim();
    }

    protected String translateFilter(List<Word> words) {
        String result = "";
        for (Iterator<Word> wordsIterator = words.iterator(); wordsIterator.hasNext(); ) {
            Word word = wordsIterator.next();
            result += translateFilter(word);
        }
        return result.trim();
    }

    protected static String translateSentence(String translatedPhrase, Sentence sentence) {
        String result = "";
        String translatedSentence = "(%s%s)";
        switch (sentence.getOperator()) {
            case AND:
                translatedSentence = String.format(translatedSentence, "&", translatedPhrase);
                break;
            case OR:
                translatedSentence = String.format(translatedSentence, "|", translatedPhrase);
                break;
            case EMPTY:
                translatedSentence = translatedPhrase;
                break;
            default:
                translatedSentence = "";
                break;
        }
        result += translatedSentence;
        return result.trim();
    }

    protected String translatePhrase(Phrase phrase) {
        String result = "";
        String translatedPhraseTemplate = "(%s%s%s)";
        String translatedPhraseTemplate2 = "(%s%s%s%s%s)";
        String translatedPhraseTemplate3 = "(%s%s%s%s%s)";
        String attr = translateField(phrase.getAttribute());
        String value = phrase.getValue();
        switch (phrase.getPhraseOperator()) {
            case EQUAL:
                result += String.format(translatedPhraseTemplate, attr, "=", value);
                break;
            case GREATERTHAN:
                result += String.format(translatedPhraseTemplate, attr, ">", value);
                break;
            case GREATERTHANOREQUAL:
                result += String.format(translatedPhraseTemplate, attr, ">=", value);
                break;
            case NOTEQUAL:
                result += String.format(translatedPhraseTemplate2, "!(", attr, "=", value,")");
                break;
            case SMALLERTHAN:
                result += String.format(translatedPhraseTemplate, attr, "<", value);
                break;
            case SMALLERTHANOREQUAL:
                result += String.format(translatedPhraseTemplate, attr, "<=", value);
                break;
            case CONTAINS:
                result += String.format(translatedPhraseTemplate3, attr, "=","*", value, "*");
                break;
            default:
                result += "";
                break;

        }
        return result.trim();
    }



    protected QueryRequest getQueryRequest() {
        return queryRequest;
    }

    public void setQueryRequest(QueryRequest queryRequest) {
        this.queryRequest = queryRequest;
    }


    public abstract void translateFields();
    public abstract SearchCriteria get();
}
