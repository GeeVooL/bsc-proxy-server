package com.mdevv.components;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WordFilter {

  public WordFilter(String[] filteredWords) {
    this.filteredWords = Arrays.asList(filteredWords);
  }

  public void write(byte[] buffer) {
    stringBuilder.append(new String(buffer));
  }

  public void filter() {
    filteredString = stringBuilder.toString();
    filteredWords.forEach(word -> {
      filteredString = filteredString.replaceAll(Pattern.quote(word), getMarkedWord(word));
    });
  }

  public byte[] read() {
    return filteredString.getBytes();
  }

  static String getMarkedWord(String word) {
    return "<font color=\"red\"><b>" + word + "</b></font>";
  }

  private List<String> filteredWords;
  private StringBuilder stringBuilder = new StringBuilder();
  private String filteredString;
}
