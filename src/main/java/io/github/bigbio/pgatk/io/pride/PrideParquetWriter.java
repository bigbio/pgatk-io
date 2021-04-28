package io.github.bigbio.pgatk.io.pride;

import java.io.File;
import java.io.IOException;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

public class PrideParquetWriter {

  Path path;
  Configuration conf;
  Schema schema;
  ParquetWriter<AnnotatedSpectrum> writer;

  /**
   * Default constructor
   * @param source Parquet file
   * @throws PgatkIOException
   */
  public PrideParquetWriter(File source) throws PgatkIOException {
    this.path = new Path(source.getPath());
    this.conf = new Configuration(false);
    this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);

    try {

      // Delete if path exists.
      FileSystem fs = FileSystem.get(conf);
      fs.delete(this.path,true);

      this.writer = AvroParquetWriter.<AnnotatedSpectrum>builder(this.path)
        .withSchema(schema)
        .withCompressionCodec(CompressionCodecName.SNAPPY)
        .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
        .withDataModel(ReflectData.get())
        .withDictionaryEncoding(true)
        .build();
    } catch (IOException e) {
      throw new PgatkIOException("Error creating the output file in path: " + source.getAbsolutePath(), e);
    }
  }

  /**
   * Write an {@link AnnotatedSpectrum} into a parquet file
   * @param spectrum {@link AnnotatedSpectrum} spectrum
   * @throws IOException
   */
  public void write(AnnotatedSpectrum spectrum) throws IOException {
    writer.write(spectrum);
  }

  public void close() throws PgatkIOException {
    try {
      writer.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error closing file: " + this.path.toString(), e);
    }
  }


}
