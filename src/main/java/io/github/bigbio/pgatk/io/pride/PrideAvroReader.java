package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;

import java.io.File;
import java.io.IOException;

public class PrideAvroReader {


  private final File source;
  private final Schema schema;
  private final DataFileReader<AnnotatedSpectrum> reader;

  public PrideAvroReader(File source) throws PgatkIOException {

    try {
      this.source = source;
      this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);
      DatumReader<AnnotatedSpectrum> datum = new ReflectDatumReader<>(schema);
      reader = new DataFileReader<>(source, datum);
    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error opening the file %s", source),e );
    }
  }

  public boolean hasNext() {
    return reader.hasNext();
  }

  public AnnotatedSpectrum next(){
    return reader.next();
  }

  public void close() throws PgatkIOException {
    try {
      reader.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error close file in path: " + source.toString(), e);
    }
  }
}
