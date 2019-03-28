package org.bigbio.pgatk.io.mgf;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StrangeEolTest {

	private MgfIndexedReader mgfFile = new MgfIndexedReader();

	@Before
	public void setUp() throws Exception {
		loadTestFile();
	}

	private void loadTestFile() throws Exception{
		URL testFile = getClass().getClassLoader().getResource("strange_eol.mgf");
		Assert.assertNotNull("Error loading mgf test file", testFile);
		File sourceFile = new File(testFile.toURI());
		mgfFile = new MgfIndexedReader(sourceFile);
	}

	@Test
	public void testGetMs2QueryCount() {
		Assert.assertEquals(5, mgfFile.getMs2QueryCount());
	}
}
