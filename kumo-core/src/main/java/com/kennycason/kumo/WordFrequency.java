package com.kennycason.kumo;

import com.kennycason.kumo.font.KumoFont;

import java.io.Serializable;

/**
 * Created by kenny on 6/29/14.
 */
public class WordFrequency implements Comparable<WordFrequency> , Serializable {

    private String word;

    private int frequency;

    private KumoFont font;

    public WordFrequency(final String word, final int frequency) {
        this.word = word;
        this.frequency = frequency;
        this.font = null;
    }

    public WordFrequency() {
    }

    public WordFrequency(final String word, final int frequency, final KumoFont font) {
        this.word = word;
        this.frequency = frequency;
        this.font = font;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean hasFont() {
        return font != null;
    }

    public KumoFont getFont() {
        return font;
    }

    @Override
    public int compareTo(final WordFrequency wordFrequency) {
        return  wordFrequency.frequency - frequency;
    }

    @Override
    public String toString() {
        return "WordFrequency [word=" + word + ", frequency=" + frequency + ", font=" + (font == null ? "default" : font.getFont().getFontName()) + "]";
    }
}
