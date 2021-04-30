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

    List<AvroTuple> samples = new ArrayList<>();
    samples.add(AvroTuple
      .builder().key("organism")
      .value("human")
      .build()
    );
    List<AvroTuple> biologicalAnnotations = new ArrayList<>();
    biologicalAnnotations.add(AvroTuple
      .builder().key("unique peptide")
      .value("true")
      .build()
    );
    List<AvroTerm> qualityScores = new ArrayList<>();
    qualityScores.add(AvroTerm.builder().name("PEP Score")
      .accession("MS:234").value("3.4567")
      .build()
    );
    List<AvroTuple> msAnnotations = new ArrayList<>();
    msAnnotations.add(AvroTuple
      .builder().key("instrument")
      .value("Orbitrap")
      .build()
    );
    List<AvroModification> modifications = new ArrayList<>();
    List<AvroTerm> scores = new ArrayList<>();
    scores.add(AvroTerm.builder().name("FLP")
      .accession("MS:111")
      .value("234.56778")
      .build());
    modifications.add(AvroModification
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
    while(reader.hasNext()){
      spec = reader.next();
      System.out.println(spec.toString());
      System.out.println(spec.getPeakList().toString());
    }

    reader.close();
    outpFile.deleteOnExit();
  }



}
