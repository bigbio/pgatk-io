package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PrideJsonWriter {

  private final File source;
  private BufferedWriter archiveSpectrumBufferedWriter;

  private static final ObjectMapper objectMapper;
  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ParanamerModule());
  }

  /**
   * Create a {@link BufferedWriter} from a source File
   * @param source File where the spectra will be written
   * @throws PgatkIOException
   */
  public PrideJsonWriter(File source) throws PgatkIOException {
    this.source = source;
    try {
      archiveSpectrumBufferedWriter = new BufferedWriter(new FileWriter(this.source, false));
    } catch (IOException e) {
      throw new PgatkIOException("File not found -- " + this.source.getAbsolutePath());
    }
  }

  /**
   * Write a list of {@link ArchiveSpectrum} to a file output
   * @param spectra List of {@link ArchiveSpectrum}
   * @throws PgatkIOException
   */
  public void writeList(List<Spectrum> spectra) throws PgatkIOException {
    for(Spectrum spectrum: spectra){
      write(spectrum);
    }
  }

  /**
   * Write an {@link ArchiveSpectrum} to a file output
   * @param spectrum {@link ArchiveSpectrum}
   * @throws PgatkIOException
   */
  public void write(Spectrum spectrum) throws PgatkIOException {
    try {
        String s = objectMapper.writeValueAsString(spectrum);
        archiveSpectrumBufferedWriter.write(s);
        archiveSpectrumBufferedWriter.newLine();
      } catch (IOException e) {
        throw new PgatkIOException("Error writing spectrum object -- " + spectrum.toString());
      }
  }

  public void close() throws PgatkIOException {
    try {
      archiveSpectrumBufferedWriter.flush();
      archiveSpectrumBufferedWriter.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error closing the file -- " + this.source.getAbsolutePath());
    }
  }

  public void flush() throws PgatkIOException {
    try {
      archiveSpectrumBufferedWriter.flush();
    } catch (IOException e) {
      throw new PgatkIOException("Error closing the file -- " + this.source.getAbsolutePath());
    }
  }
}
