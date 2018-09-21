package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.exceptions.ParsingException;
import com.imperva.ddc.core.language.*;
import com.imperva.ddc.core.query.Field;
import com.imperva.ddc.core.query.FieldType;
import com.imperva.ddc.core.query.QueryRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.*;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class ActiveDirectorySearchCriteriaBuilderImplTest {


    private ActiveDirectorySearchCriteriaBuilderImpl activeDirectorySearchCriteriaBuilder;
    private QueryRequest queryRequest;

    @Before
    public void setup() {
        activeDirectorySearchCriteriaBuilder = spy(new ActiveDirectorySearchCriteriaBuilderImpl());
        queryRequest = mock(QueryRequest.class);
        activeDirectorySearchCriteriaBuilder.setQueryRequest(queryRequest);
    }

    @Test
    public void testTranslateFieldsMethodsInvoke() {
        Field field = new Field();
        List<Field> fields = new ArrayList<>();
        fields.add(field);

        when(queryRequest.getRequestedFields()).thenReturn(fields);
        activeDirectorySearchCriteriaBuilder.translateFields();
        verify(queryRequest, times(1)).getRequestedFields();
        verify(activeDirectorySearchCriteriaBuilder, times(1)).translateField(queryRequest.getRequestedFields().get(0));
    }

    @Test
    public void testTranslateFilterInjectedByUser() {
        when(queryRequest.getSearchText()).thenReturn("STAM");
        activeDirectorySearchCriteriaBuilder.translateFilter();
        verify(activeDirectorySearchCriteriaBuilder, times(0)).translateFilter(any(Word.class));
    }

//    @Test
//    public void testTranslateFilterNotInjectedByUser() {
//        when(queryRequest.getSearchText()).thenReturn(null);
//        activeDirectorySearchCriteriaBuilder.translateFilter();
//        verify(activeDirectorySearchCriteriaBuilder, times(1)).translateFilter(any(Word.class));
//    }

    @Test
    public void testTranslateFilter() {

        when(queryRequest.getSearchText()).thenReturn(null);
        QueryAssembler assembler = new QueryAssembler();
        Sentence gabiSentence = assembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "gabi")
                .addPhrase(FieldType.CITY, PhraseOperator.EQUAL, "TLV")
                .closeSentence(SentenceOperator.AND);

        Sentence motySentence = assembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "moty")
                .addPhrase(FieldType.CITY, PhraseOperator.EQUAL, "ITA")
                .closeSentence(SentenceOperator.AND);

        Sentence combinedSentence = assembler.addSentence(motySentence)
                .addSentence(gabiSentence)
                .closeSentence(SentenceOperator.OR);

        when(queryRequest.getSearchSentence()).thenReturn(combinedSentence);

        activeDirectorySearchCriteriaBuilder.translateFilter();

        SearchCriteria searchCriteria = activeDirectorySearchCriteriaBuilder.get();
        assertTrue(searchCriteria.getSearchFilter().equals("(|(&(givenName=moty)(l=ITA))(&(givenName=gabi)(l=TLV)))"));
    }

    @Test
    public void testTranslateFields() {
        Field field = new Field();
        field.setType(FieldType.FIRST_NAME);
        Field field2 = new Field();
        field2.setType(FieldType.CITY);
        List<Field> fields = new ArrayList<>();
        fields.add(field);
        fields.add(field2);

        when(queryRequest.getRequestedFields()).thenReturn(fields);
        activeDirectorySearchCriteriaBuilder.translateFields();
        SearchCriteria searchCriteria = activeDirectorySearchCriteriaBuilder.get();

        assertTrue(searchCriteria.getRequestedFields().get(0).getName().equals("givenName"));
        assertTrue(searchCriteria.getRequestedFields().get(1).getName().equals("l"));
    }

    @Test(expected = ParsingException.class)
    public void testTranslateEmptyFilterPhraseValue() {
        Phrase phrase = mock(Phrase.class);
        doReturn("").when(activeDirectorySearchCriteriaBuilder).translatePhrase(phrase);
        activeDirectorySearchCriteriaBuilder.translateFilter(phrase);
        verify(activeDirectorySearchCriteriaBuilder, times(1)).translatePhrase(phrase);
    }

    @Test(expected = ParsingException.class)
    public void testTranslateEmptyFilterPhrase() {
        Phrase phrase = null;
        doReturn("").when(activeDirectorySearchCriteriaBuilder).translatePhrase(phrase);
        activeDirectorySearchCriteriaBuilder.translateFilter(phrase);
        verify(activeDirectorySearchCriteriaBuilder, times(1)).translatePhrase(phrase);
    }

    @Test
    public void testTranslateSentenceAND() {
        Sentence sentence = mock(Sentence.class);
        String translatedFilter = "(l=LTV)";

        when(sentence.getOperator()).thenReturn(SentenceOperator.AND);
        String result = activeDirectorySearchCriteriaBuilder.translateSentence(translatedFilter, sentence);

        assertEquals(result, "(&(l=LTV))");
    }

    @Test
    public void testTranslateSentenceOR() {
        Sentence sentence = mock(Sentence.class);
        String translatedFilter = "(l=LTV)";

        when((sentence).getOperator()).thenReturn(SentenceOperator.OR);
        String result = activeDirectorySearchCriteriaBuilder.translateSentence(translatedFilter, sentence);

        assertEquals(result, "(|(l=LTV))");
    }

    @Test
    public void testTranslateSentenceEMPTY() {
        Sentence sentence = mock(Sentence.class);
        String translatedFilter = "(l=LTV)";

        when((sentence).getOperator()).thenReturn(SentenceOperator.EMPTY);
        String result = activeDirectorySearchCriteriaBuilder.translateSentence(translatedFilter, sentence);

        assertEquals(result, translatedFilter);
    }

//    @Test
//    public void testTranslateFilterSentence() {
//        Sentence sentence = mock(Sentence.class);
//        List<Word> words = new ArrayList<>();
//        String translatedFilter = "(l=LTV)";
//
//        when(sentence.getSentences()).thenReturn(words);
//        doReturn(translatedFilter).when(activeDirectorySearchCriteriaBuilder).translateFilter(words);
//        doReturn(translatedFilter).when(activeDirectorySearchCriteriaBuilder).translateSentence(translatedFilter, sentence);
//
//        activeDirectorySearchCriteriaBuilder.translateFilter(sentence);
//
//        verify(activeDirectorySearchCriteriaBuilder, times(1)).translateSentence(translatedFilter,sentence);
//    }
    
}
