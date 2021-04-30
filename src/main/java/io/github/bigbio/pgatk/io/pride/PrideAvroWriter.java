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

/**
 * Write a list of {@link AnnotatedSpectrum} into Avro format.
 *
 * https://stackoverflow.com/questions/36022358/kafka-avro-consumer-with-decoder-issues
 *
 * @author ypriverol
 */
public class PrideAvroWriter {

  private final Schema schema;
  DataFileWriter<AnnotatedSpectrum> writer;
  File source;

  public PrideAvroWriter(File source) throws PgatkIOException {

    try {
      this.source = source;
      this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);
      DatumWriter<AnnotatedSpectrum> datum = new ReflectDatumWriter<>(schema);
      writer = new DataFileWriter<>(datum);
      writer.setCodec(CodecFactory.snappyCodec());
      writer.create(this.schema, this.source);
      writer.setFlushOnEveryBlock(true);

    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error writing the file %s", source),e );
    }
  }

  public void write(AnnotatedSpectrum spec) throws PgatkIOException {
    try {
      writer.append(spec);
    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error writing spectrum file %s", source),e );
    }
  }

  public void close() throws PgatkIOException {
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error when flush the data into a file",e);
    }
  }
}
