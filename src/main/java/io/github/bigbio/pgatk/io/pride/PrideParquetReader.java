package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.filter2.compat.FilterCompat;
import org.apache.parquet.filter2.predicate.FilterPredicate;
import org.apache.parquet.hadoop.ParquetReader;
import java.io.File;
import java.io.IOException;

public class PrideParquetReader {

  Path path;
  Configuration conf;
  Schema schema;
  ParquetReader<AnnotatedSpectrum> reader;

  /**
   * Default constructor
   * @param source Parquet file
   * @throws PgatkIOException
   */
  public PrideParquetReader(File source) throws PgatkIOException {

    this.path = new Path(source.getPath());
    this.conf = new Configuration(false);
    this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);

    try {
      this.reader = AvroParquetReader.<AnnotatedSpectrum>builder(this.path)
        .withDataModel(new ReflectData(AnnotatedSpectrum.class.getClassLoader()))
        .disableCompatibility()
        .withConf(conf)
        .build();
    } catch (IOException e) {
      throw new PgatkIOException("Error creating the output file in path: " + source.getAbsolutePath(), e);
    }
  }

  /**
   * Constructor of {@link PrideParquetReader} using a {@link FilterPredicate} which allows to filter data by some specific
   * fileds. For example, the user can retrieve only {@link AnnotatedSpectrum} that agreed with the Filter conditions.
   * @param source File to be read
   * @param filter A {@link FilterPredicate}
   * @throws PgatkIOException
   */
  public PrideParquetReader(File source, FilterPredicate filter) throws PgatkIOException {

    this.path = new Path(source.getPath());
    this.conf = new Configuration(false);
    this.schema = ReflectData.get().getSchema(AnnotatedSpectrum.class);

    try {
      this.reader = AvroParquetReader.<AnnotatedSpectrum>builder(this.path)
        .withDataModel(new ReflectData(AnnotatedSpectrum.class.getClassLoader()))
        .disableCompatibility()
        .withConf(conf)
        .withFilter(FilterCompat.get(filter))
        .build();
    } catch (IOException e) {
      throw new PgatkIOException("Error creating the output file in path: " +
        source.getAbsolutePath(), e);
    }
  }

  /**
   * Method to read an {@link AnnotatedSpectrum} from parquet file
   * @return An {@link AnnotatedSpectrum}
   * @throws PgatkIOException
   */
  public AnnotatedSpectrum read() throws PgatkIOException {
    AnnotatedSpectrum spec = null;
    try {
      spec = reader.read();
    } catch (IOException e) {
      throw new PgatkIOException("Error reading spectrum from file", e);
    }
    return spec;
  }

//

  public void close() throws PgatkIOException {
    try {
      reader.close();
    } catch (IOException e) {
      throw new PgatkIOException("Error close file in path: " + path.toString(), e);
    }
  }


}
