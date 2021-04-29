package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.xerial.snappy.SnappyOutputStream;

import static java.nio.file.StandardOpenOption.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrideAvroWriter {

  private final Schema schema;
  private final OutputStream os;
  DatumWriter<AnnotatedSpectrum> writer;
  Path source;
  BinaryEncoder binEncoder;

  public PrideAvroWriter(File source) throws PgatkIOException {

    try {
      this.source = Paths.get(source.getAbsolutePath());
      this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);
      writer = new ReflectDatumWriter<AnnotatedSpectrum>(schema);

      this.os = Files.newOutputStream(this.source, CREATE, WRITE, TRUNCATE_EXISTING);
      SnappyOutputStream out = new SnappyOutputStream(os);
      binEncoder = EncoderFactory.get().binaryEncoder(out, null);

    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error writing the file %s", source),e );
    }
  }

  public void write(AnnotatedSpectrum spec) throws PgatkIOException {
    try {
      writer.write(spec, binEncoder);
    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error writing spectrum file %s", source),e );
    }
  }

  public void close() throws PgatkIOException {
    try {
      binEncoder.flush();
      os.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error when flush the data into a file",e);
    }
  }
}
