package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import io.github.bigbio.pgatk.io.braf.BufferedRandomAccessFile;
import io.github.bigbio.pgatk.io.common.*;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PrideJsonIndexedReader implements MzReader {

  File source;
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ParanamerModule());
  }

  /** Index Spectra in the Json file */
  private List<IndexElement> index = new ArrayList<>();

  /**  Index in the file to {@link ArchiveSpectrum} */
  private HashMap<Integer, Spectrum> indexSpectra = new HashMap<>();

  /** Usi to {@link ArchiveSpectrum} */
  private HashMap<String, Spectrum> peakLists = new HashMap<>();

  /** Indicates whether the cache should be used */
  private boolean useCache = false;

  private Map<String, IndexElement> keys = new HashMap<>();

  /**
   * Constructor of {@link PrideJsonIndexedReader}
   * @param source File with the spectra
   * @param inMemory Load in memory all the spectra
   * @throws PgatkIOException
   */
  public PrideJsonIndexedReader(File source, boolean inMemory) throws PgatkIOException {
    this.source = source;
    this.useCache = inMemory;

    // open the file
    try {

      BufferedRandomAccessFile braf = new BufferedRandomAccessFile(this.source.getAbsolutePath(),
        "r", 1024 * 100);

      // process the file line by line
      String line;
      long beginIonsIndex = 0; // the index where the last "BEGIN IONS" was encountered
      while ((line = braf.getNextLine()) != null) {
        int size = (int) (braf.getFilePointer() - beginIonsIndex);
        index.add(new IndexElementImpl(beginIonsIndex, size));
        ArchiveSpectrum spectrum = objectMapper.readValue(line, ArchiveSpectrum.class);
        keys.put(spectrum.usi, new IndexElementImpl(beginIonsIndex, size));
        if(this.useCache){
          indexSpectra.put(index.size()-1, spectrum);
          peakLists.put(spectrum.usi, spectrum);
        }
        //always update file pointer before continue
        beginIonsIndex = braf.getFilePointer();
      }
      braf.close();
    } catch (IOException e) {
      throw new PgatkIOException("Failed to read from PrideJson file.", e);
    }
  }


  @Override
  public int getSpectraCount() {
    return index.size();
  }

  @Override
  public boolean acceptsFile() {
    return true;
  }

  @Override
  public boolean acceptsDirectory() {
    return false;
  }

  @Override
  public List<String> getSpectraIds() {
    return new ArrayList<>(keys.keySet());
  }

  @Override
  public Spectrum getSpectrumById(String id) {
    if(useCache){
      return peakLists.get(id);
    }
    return null;
  }

  @Override
  public Spectrum getSpectrumByIndex(int index) {
    return null;
  }

  @Override
  public List<IndexElement> getMsNIndexes(int msLevel) {
    return null;
  }

  @Override
  public List<Integer> getMsLevels() {
    return null;
  }

  @Override
  public Map<String, IndexElement> getIndexElementForIds() {
    return null;
  }
}
