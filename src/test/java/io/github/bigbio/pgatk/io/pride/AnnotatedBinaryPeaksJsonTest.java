package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AnnotatedBinaryPeaksJsonTest {

  AnnotatedSpectrum spec;
  File outpFile;

  @Before
  public void setup() throws PgatkIOException, IOException {

    List<Double> masses = Arrays.asList(2.3,3.4,4.5);
    List<Double> intensities = Arrays.asList(100.7,100.8,100.9);

    List<ParquetTuple> samples = new ArrayList<>();
    samples.add(ParquetTuple
      .builder().key("organism")
      .value("human")
      .build()
    );
    List<ParquetTuple> biologicalAnnotations = new ArrayList<>();
    biologicalAnnotations.add(ParquetTuple
      .builder().key("unique peptide")
      .value("true")
      .build()
    );
    List<ParquetTuple> qualityScores = new ArrayList<>();
    qualityScores.add(ParquetTuple
      .builder().key("PEP Score")
      .value("3.4567")
      .build()
    );
    List<ParquetTuple> msAnnotations = new ArrayList<>();
    msAnnotations.add(ParquetTuple
      .builder().key("instrument")
      .value("Orbitrap")
      .build()
    );
    List<ParquetModification> modifications = new ArrayList<>();
    List<ParquetTuple> scores = new ArrayList<>();
    scores.add(new ParquetTuple("FLP","234.56778"));
    modifications.add(ParquetModification
      .builder()
      .modification("Oxidation")
      .position(1)
      .properties(scores)
      .build()
    );

    spec =  new AnnotatedSpectrum("USI1", "KKKKR", "[Acetyl]-KKKKR", (List<String>)
      Collections.singletonList("Protein1"), (List<String>) Collections.singletonList("Gene1"), Collections.EMPTY_LIST,
      Collections.EMPTY_LIST, "sample1", "human", samples, biologicalAnnotations, 1234.123456789, 2,
      modifications, masses, intensities, 123.46543, 2, 0, qualityScores,
      msAnnotations, "PXD1", false, 123456783452.1234455677);

    outpFile = new File("binarySpectrum.avro");
    PrideAvroWriter avroWriter = new PrideAvroWriter(outpFile);
    avroWriter.write(spec);
    avroWriter.close();
  }

  @Test
  public void binaryFilterTest() throws PgatkIOException, IOException {


    outpFile = new File("binarySpectrum.avro");
    PrideAvroReader reader = new PrideAvroReader(outpFile);
    while((spec = reader.read()) != null){
      System.out.println(spec.toString());
      System.out.println(spec.getPeakList().toString());
    }

    reader.close();
    outpFile.deleteOnExit();
  }



}
