package com.kennycason.kumo.nlp;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.filter.CompositeFilter;
import com.kennycason.kumo.nlp.filter.Filter;
import com.kennycason.kumo.nlp.filter.StopWordFilter;
import com.kennycason.kumo.nlp.filter.WordSizeFilter;
import com.kennycason.kumo.nlp.normalize.CharacterStrippingNormalizer;
import com.kennycason.kumo.nlp.normalize.LowerCaseNormalizer;
import com.kennycason.kumo.nlp.normalize.Normalizer;
import com.kennycason.kumo.nlp.normalize.TrimToEmptyNormalizer;
import com.kennycason.kumo.nlp.tokenizer.core.WhiteSpaceWordTokenizer;
import com.kennycason.kumo.nlp.tokenizer.api.WordTokenizer;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.reducing;

/**
 * Created by kenny on 7/1/14.
 */
public class FrequencyAnalyzer {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final int DEFAULT_WORD_MAX_LENGTH = 32;

    public static final int DEFAULT_WORD_MIN_LENGTH = 3;

    public static final int DEFAULT_WORD_FREQUENCIES_TO_RETURN = 50;

    public static final long DEFAULT_URL_LOAD_TIMEOUT = 3000L; // 3 sec

    private final Set<String> stopWords = new HashSet<>();

    private WordTokenizer wordTokenizer = new WhiteSpaceWordTokenizer();

    private final List<Filter> filters = new ArrayList<>();

    private final List<Normalizer> normalizers = new ArrayList<>();

    private int wordFrequenciesToReturn = DEFAULT_WORD_FREQUENCIES_TO_RETURN;

    private int maxWordLength = DEFAULT_WORD_MAX_LENGTH;

    private int minWordLength = DEFAULT_WORD_MIN_LENGTH;

    private String characterEncoding = DEFAULT_ENCODING;

    private long urlLoadTimeout = DEFAULT_URL_LOAD_TIMEOUT;

    public FrequencyAnalyzer() {
        this.normalizers.add(new TrimToEmptyNormalizer());
        this.normalizers.add(new CharacterStrippingNormalizer());
        this.normalizers.add(new LowerCaseNormalizer());
    }

    public List<WordFrequency> load(final InputStream inputStream) throws IOException {
        return load(IOUtils.readLines(inputStream, characterEncoding));
    }

    public List<WordFrequency> load(final InputStream inputStream,String characterEncoding) throws IOException {
        return load(IOUtils.readLines(inputStream, characterEncoding));
    }

    public List<WordFrequency> load(final File file) throws IOException {
        return this.load(new FileInputStream(file));
    }

    public List<WordFrequency> load(final String filePath) throws IOException {
        return this.load(new File(filePath));
    }

    public List<WordFrequency> load(final URL url) throws IOException {
        final Document doc = Jsoup.parse(url, (int) urlLoadTimeout);
        return load(Collections.singletonList(doc.body().text()));
    }


    public List<WordFrequency> load(final List<String> texts) {
        final List<WordFrequency> wordFrequencies = new ArrayList<>();


        final Map<String, Integer> cloud = buildWordFrequencies(texts, wordTokenizer);
        cloud.forEach((key, value) -> wordFrequencies.add(new WordFrequency(key, value)));
        return takeTopFrequencies(wordFrequencies);
    }


    /**
     *load data with auto fill function, if word exist, multiple the words. Else use nothing instead
     * the first input parameter can be  InputStream or list of string
     * @param autoFill decide whether or not to fill the cloud automatically
     * @param autoFillWord  is used to define use what word to fill the cloud when there is no enough word.
     */
    public List<WordFrequency> load(final InputStream inputStream, boolean autoFill, String autoFillWord) throws IOException {
        return load(IOUtils.readLines(inputStream, characterEncoding), autoFill, autoFillWord);
    }
    public List<WordFrequency> load(final InputStream inputStream, boolean autoFill) throws IOException {
        return load(IOUtils.readLines(inputStream, characterEncoding), autoFill, "nothing");
    }
    //if enable autoFill but not specify autoFillWord, use nothing as default
    //also allow use List<String> directly
    public List<WordFrequency> load(final List<String> texts, boolean autoFill){
        return load(texts, autoFill, "nothing");
    }
    //If autoFill is false, just call load
    //Else if there are some words to draw, repeat them until the number of characters is similar with wordFrequenciesToReturn
    //Else repeat autoFillWord to satisfy the drawing cloud need.
    //Note: the repeat time is dependence on the sum of word length. We consider character number is more important.
    //Which means long word will be repeated less
    public List<WordFrequency> load(final List<String> texts, boolean autoFill, String autoFillWord) {
        if(!autoFill){
            return load(texts);
        }

        final List<WordFrequency> wordFrequencies = new ArrayList<>();

        final Map<String, Integer> cloud = buildWordFrequencies(texts, wordTokenizer);
        int totalLength = 0;
        for (Map.Entry<String, Integer> entry : cloud.entrySet()) {
            totalLength += entry.getKey().length();
        }

        if (totalLength == 0){
            cloud.put(autoFillWord,1);
            totalLength = autoFillWord.length();
            cloud.forEach((key, value) -> wordFrequencies.add(new WordFrequency(key, value)));

        }
        final int timesToAdd = Math.max(wordFrequenciesToReturn / totalLength,1);
        for (int i = 0; i < timesToAdd; i++) {
            cloud.forEach((key, value) -> wordFrequencies.add(new WordFrequency(key, value)));
        }

        return takeTopFrequencies(wordFrequencies);
    }
    
    public List<WordFrequency> loadWordFrequencies(final List<WordFrequency> wordFrequencies) {
        return takeTopFrequencies(wordFrequencies);
    }
    
    private Map<String, Integer> buildWordFrequencies(final List<String> texts, final WordTokenizer tokenizer) {
        return texts.stream()
                    .map(tokenizer::tokenize)
                    .flatMap(List::stream)
                    .map(this::normalize)
                    .filter(buildFilter())
                    .collect(Collectors.groupingBy(e -> e, reducing(0, e -> 1, Integer::sum)));
    }

    private Filter buildFilter() {
        final List<Filter> allFilters = new ArrayList<>();
        allFilters.add(new StopWordFilter(stopWords));
        allFilters.add(new WordSizeFilter(minWordLength, maxWordLength));
        allFilters.addAll(filters);
        return new CompositeFilter(allFilters);
    }

    private String normalize(final String word) {
        String normalized = word;
        for (final Normalizer normalizer : normalizers) {
            normalized = normalizer.apply(normalized);
        }
        return normalized;
    }

    private List<WordFrequency> takeTopFrequencies(final Collection<WordFrequency> wordCloudEntities) {
        return wordCloudEntities
                .stream()
                .sorted(WordFrequency::compareTo)
                .limit(wordFrequenciesToReturn)
                .collect(Collectors.toList());
    }

    public void setStopWords(final Collection<String> stopWords) {
        this.stopWords.clear();
        this.stopWords.addAll(stopWords);
    }

    public void setWordFrequenciesToReturn(final int wordFrequenciesToReturn) {
        this.wordFrequenciesToReturn = wordFrequenciesToReturn;
    }

    public void setMinWordLength(final int minWordLength) {
        this.minWordLength = minWordLength;
    }

    public void setMaxWordLength(final int maxWordLength) {
        this.maxWordLength = maxWordLength;
    }

    public void setWordTokenizer(final WordTokenizer wordTokenizer) {
        this.wordTokenizer = wordTokenizer;
    }

    public void clearFilters() {
        this.filters.clear();
    }

    public void addFilter(final Filter filter) {
        this.filters.add(filter);
    }

    public void setFilter(final Filter filter) {
        this.filters.clear();
        this.filters.add(filter);
    }

    public void clearNormalizers() {
        this.normalizers.clear();
    }

    public void addNormalizer(final Normalizer normalizer) {
        this.normalizers.add(normalizer);
    }

    public void setNormalizer(final Normalizer normalizer) {
        this.normalizers.clear();
        this.normalizers.add(normalizer);
    }

    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public void setUrlLoadTimeout(final long urlLoadTimeout) {
        this.urlLoadTimeout = urlLoadTimeout;
    }
}
