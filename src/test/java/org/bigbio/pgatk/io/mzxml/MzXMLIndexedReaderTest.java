package org.bigbio.pgatk.io.mzxml;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bigbio.pgatk.io.common.IndexElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.bigbio.pgatk.io.common.Spectrum;
import org.bigbio.pgatk.io.mzxml.mzxml.model.DataProcessing;
import org.bigbio.pgatk.io.mzxml.mzxml.model.MsInstrument;
import org.bigbio.pgatk.io.mzxml.mzxml.model.ParentFile;
import org.bigbio.pgatk.io.mzxml.mzxml.model.Scan;

public class MzXMLIndexedReaderTest {


	private static MzXMLIndexedReader mzxmlIndexedReader;
	private File sourcefile;

	@Before
	public void setUp() throws Exception {

		 // create the mzxml dao
        try {
            URL testFile = getClass().getClassLoader().getResource("testfile.mzXML");
            Assert.assertNotNull("Error loading mzXML test file", testFile);
            sourcefile = new File(testFile.toURI());
            
            if (mzxmlIndexedReader == null)
            	mzxmlIndexedReader = new MzXMLIndexedReader(sourcefile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
	}

	@Test
	public void testGetParentFile() {
		try {
			List<ParentFile> parentFiles = mzxmlIndexedReader.getParentFile();
			Assert.assertEquals(1, parentFiles.size());
			ParentFile file = parentFiles.get(0);
			Assert.assertNotNull(file);
			Assert.assertEquals("R1_RG59_B4_1.RAW", file.getFileName());
			Assert.assertEquals("RAWData", file.getFileType());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get parent file.");
		}
	}

	@Test
	public void testGetMsInstrument() {
		try {
			List<MsInstrument> instruments = mzxmlIndexedReader.getMsInstrument();
			
			Assert.assertNotNull(instruments);
			Assert.assertEquals(1, instruments.size());
			
			MsInstrument instrument = instruments.get(0);
			Assert.assertNotNull(instrument);
			Assert.assertNotNull(instrument.getMsDetector());
			Assert.assertEquals("unknown", instrument.getMsDetector().getTheValue());
			Assert.assertEquals("FTMS", instrument.getMsMassAnalyzer().getTheValue());
			
			Assert.assertNull(instrument.getMsInstrumentID());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get parent file.");
		}
	}

	@Test
	public void testGetDataProcessing() {
		try {
			List<DataProcessing> processings = mzxmlIndexedReader.getDataProcessing();
			
			Assert.assertEquals(1, processings.size());
			
			DataProcessing p = processings.get(0);
			Assert.assertNotNull(p);
			Assert.assertEquals("ReAdW", p.getSoftware().getName());
			Assert.assertNull(p.getIntensityCutoff());
			Assert.assertEquals(0, p.getProcessingOperationAndComment().size());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get parent file.");
		}
	}

	@Test
	public void testGetSpearation() {
		try {
			Assert.assertNull(mzxmlIndexedReader.getSpearation());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get parent file.");
		}
	}

	@Test
	public void testGetSpotting() {
		try {
			Assert.assertNull(mzxmlIndexedReader.getSpotting());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get parent file.");
		}
	}

	@Test
	public void testGetScanCount() {
		Assert.assertEquals(6449, mzxmlIndexedReader.getMS1ScanCount() + mzxmlIndexedReader.getMS2ScanCount());
	}

	@Test
	public void testGetScanIterator() {
		Iterable<Scan> scans = mzxmlIndexedReader.geMS1ScanIterator();
		
		int scanCount = 0;
		
		for (Scan s : scans) {
			scanCount++;
			Assert.assertNotNull(s);
			Assert.assertEquals(new Long(1), s.getMsLevel());
		}
		
		scans = mzxmlIndexedReader.getMS2ScanIterator();
		
		for (Scan s : scans) {
			scanCount++;
			Assert.assertNotNull(s);
			Assert.assertEquals(new Long(2), s.getMsLevel());
		}
		Assert.assertEquals(6449, scanCount);
	}

	@Test
	public void testGetSpectrumIterator() {
		Iterator<Spectrum> it = mzxmlIndexedReader.getSpectrumIterator();
		
		int count = 0;
		
		while (it.hasNext()) {
			Spectrum s = it.next();
			Assert.assertNotNull(s);
			count++;
		}
		Assert.assertEquals(6449, count);
	}

	@Test
	public void testConvertPeaksToMap() {
		Iterator<Scan> scans = mzxmlIndexedReader.getScanIterator();
		
		// just test the first scan
		Scan scan = scans.next();
		
		try {
			Assert.assertEquals(922.5859985351562, MzXMLIndexedReader.convertPeaksToMap(scan.getPeaks().get(0)).get(1186.860595703125), 0.0);
		} catch (MzXMLParsingException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public void testGetScanNumbers() {
		List<Long> scanNumbers = mzxmlIndexedReader.getScanNumbers();
		
		Assert.assertNotNull(scanNumbers);
		Assert.assertEquals(6449, scanNumbers.size());
	}
	
	public void testGetScanByNum() {
		try {
			Scan scan = mzxmlIndexedReader.getScanByNum((long) 2011);
			
			Assert.assertNotNull(scan);
			Assert.assertEquals(new Long(2), scan.getMsLevel());
			Assert.assertEquals(new Long(210), scan.getPeaksCount());
			Assert.assertEquals("PT2160.4S", scan.getRetentionTime().toString());
			Assert.assertEquals(6151.1f, scan.getTotIonCurrent(), 0.0);
			
			Assert.assertEquals(1, scan.getPeaks().size());
			Map<Double, Double> peakList = MzXMLIndexedReader.convertPeaksToMap(scan.getPeaks().get(0));
			
			Assert.assertEquals(210, peakList.size());
		} catch (MzXMLParsingException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testGetIndexedSpectrum() {
		List<IndexElement> index = mzxmlIndexedReader.getMsNIndexes(2);
		try {
			//Spectrum s1 = mzxmlIndexedReader.getSpectrumByIndex(14);
			Spectrum s2 = MzXMLIndexedReader.getIndexedSpectrum(sourcefile, index.get(9));
			
			Assert.assertNotNull(s2);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
