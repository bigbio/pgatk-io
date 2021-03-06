package io.github.bigbio.pgatk.io.msp;

import io.github.bigbio.pgatk.io.utils.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibrarySpectrumBuilder {

  private String peptideSequence;
  private Integer charge;
  private List<String> proteins;
  private List<Tuple<Integer, String>> mods;
  private Double precursorMz;
  private Map<String, Double> scores;
  private Map<String, String> properties;

  public void clear() {
    peptideSequence = null;
    charge = 0;
  }

  public String getPeptideSequence() {
    return peptideSequence;
  }

  public void setPeptideSequence(String peptideSequence) {
    this.peptideSequence = peptideSequence;
  }

  public Integer getCharge() {
    return charge;
  }

  public void setCharge(Integer charge) {
    this.charge = charge;
  }

  public void addProteinAccessionNumber(String value) {
    if(this.proteins == null)
      this.proteins = new ArrayList<>();
    this.proteins.add(value);
  }

  public void addMod(int nTerm, String s) {
    if (this.mods == null)
      this.mods = new ArrayList<>();
    this.mods.add(new Tuple<>(nTerm, s));
  }

  public void setPrecursorMz(Double precursorMz) {
    this.precursorMz = precursorMz;
  }

  public Double getPrecursorMz() {
    return precursorMz;
  }

  public void addScores(String tag, String value) {
    if(this.scores == null)
      this.scores = new HashMap<>();
    this.scores.put(tag, Double.parseDouble(value));
  }

  public List<Tuple<Integer, String>> getPtms() {
    return this.mods;
  }

  public Map<String, Double> getScores() {
    return this.scores;
  }

  public List<String> getProteins() {
    return this.proteins;
  }

  public void addAttribute(String spec, String value) {
    if(this.properties == null)
      this.properties = new HashMap<>();
    this.properties.put(spec, value);
  }

  public List<Tuple<Integer, String>> getMods() {
    return mods;
  }

  public Map<String, String> getProperties() {
    return properties;
  }
}
