package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.xerial.snappy.SnappyInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;

public class PrideAvroReader {

  private final Schema schema;
  private final ReflectDatumReader<AnnotatedSpectrum> reader;
  private final Path source;
  private final InputStream in;
  private final BinaryDecoder decoder;

  public PrideAvroReader(File source) throws PgatkIOException {

    schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);
    reader = new ReflectDatumReader<>(schema);
    this.source = Paths.get(source.getAbsolutePath());


    try {
      in = Files.newInputStream(this.source, READ);
      SnappyInputStream is = new SnappyInputStream(in);
      decoder = DecoderFactory.get().binaryDecoder(is, null);
    } catch (IOException e) {
      throw new PgatkIOException(String.format("Error opening the file %s", source),e );
    }
  }

  public AnnotatedSpectrum read() throws PgatkIOException {
    AnnotatedSpectrum spec = null;
    try {
        if (!decoder.isEnd())
          spec = reader.read(null, decoder);
    } catch (IOException e) {
      e.getMessage();
    }
    return spec;
  }

  public void close() throws PgatkIOException {
    try {
      in.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error close file in path: " + source.toString(), e);
    }
  }
}
