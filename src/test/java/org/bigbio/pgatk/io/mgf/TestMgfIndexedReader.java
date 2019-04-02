package org.bigbio.pgatk.io.mgf;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bigbio.pgatk.io.common.IndexElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.bigbio.pgatk.io.common.Spectrum;

public class TestMgfIndexedReader {

    private MgfIndexedReader mgfFile = new MgfIndexedReader();
    private File sourceFile;

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception {
        URL testFile = getClass().getClassLoader().getResource("F001257.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        sourceFile = new File(testFile.toURI());
        mgfFile = new MgfIndexedReader(sourceFile);
    }

    @Test
    public void testGetAccessions() {
        Assert.assertEquals(3, mgfFile.getAccessions().size());
        Assert.assertEquals("P12346", mgfFile.getAccessions().get(1));
        Assert.assertEquals("P12347", mgfFile.getAccessions().get(2));
    }

    @Test
    public void testSetAccessions() {
        ArrayList<String> accessions = new ArrayList<>();
        accessions.add("P12345");
        accessions.add("P12346");
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setAccessions(accessions);
        Assert.assertEquals("P12345,P12346", String.join(",", mgfFile.getAccessions()));
    }

    @Test
    public void testGetCharge() {
        Assert.assertEquals("2+,3+,4+,5+", mgfFile.getCharge());
    }

    @Test
    public void testSetCharge() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setCharge("8-,5-,4-,3-");
        Assert.assertEquals("8-,5-,4-,3-", modifiedMgfFile.getCharge());
    }

    /**
     * Tests getting a spectrum from the test MGF file.
     */
    @Test
    public void testGetSpectrum() throws Exception{
        Ms2Query specturm;
        List<String> allSpectra = mgfFile.getSpectraIds();
        specturm = (Ms2Query) mgfFile.getSpectrumById(allSpectra.get(3));
        Assert.assertNotNull(specturm);
        Assert.assertEquals("4", specturm.getId());
        Assert.assertEquals("PRIDE_Exp_mzData_Ac_9266.xml_id_4", specturm.getTitle());
        Assert.assertEquals(17, specturm.getPeakList().size());
        Assert.assertEquals(new Integer(2), specturm.getMsLevel());
        Assert.assertEquals("2+,3+", specturm.getChargeState());
        Assert.assertNull(specturm.getPrecursorCharge());
        Assert.assertEquals(413.2861, specturm.getPrecursorMZ(), 0.0);
        Assert.assertEquals(413.2861, specturm.getPeptideMass(), 0.0);
        Assert.assertEquals(null, specturm.getPrecursorIntensity());
        Assert.assertEquals(1, specturm.getAdditional().size());
        Assert.assertNull(specturm.getComposition());
        Assert.assertNull(specturm.getErrorTolerantTags());
        Assert.assertNull(specturm.getInstrument());
        Assert.assertNull(specturm.getPeptideIntensity());
        Assert.assertNull(specturm.getRetentionTime());
        Assert.assertNull( specturm.getScan());
        Assert.assertNull(specturm.getSequenceQualifiers());
        Assert.assertNull(specturm.getTags());
        Assert.assertNull(specturm.getTolerance());
        Assert.assertNull(specturm.getToleranceUnit());
        Assert.assertEquals(0, specturm.getUserTags().size());
        Assert.assertNull(specturm.getVariableModifications());
    }


    @Test
    public void testGetEnzyme() {
        Assert.assertEquals("Trypsin", mgfFile.getEnzyme());
    }

    @Test
    public void testSetEnzyme() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setEnzyme("Trypsin");
        Assert.assertEquals("Trypsin", modifiedMgfFile.getEnzyme());
    }

    @Test
    public void testGetSearchTitle() {
        Assert.assertEquals("First test experiment (values are not real)", mgfFile.getSearchTitle());
    }

    @Test
    public void testSetSearchTitle() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setSearchTitle("My first test experiment");
        Assert.assertEquals("My first test experiment", modifiedMgfFile.getSearchTitle());
    }

    @Test
    public void testGetPrecursorRemoval() {
        Assert.assertEquals("20,120", mgfFile.getPrecursorRemoval());
    }

    @Test
    public void testSetPrecursorRemoval() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPrecursorRemoval("10,120");
        Assert.assertEquals("10,120", modifiedMgfFile.getPrecursorRemoval());
    }

    @Test
    public void testGetDatabase() {
        Assert.assertEquals("SwissProt v57", mgfFile.getDatabase());
    }

    @Test
    public void testSetDatabase() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setDatabase("UniProt 1");
        Assert.assertEquals("UniProt 1", modifiedMgfFile.getDatabase());
    }

    @Test
    public void testGetPerformDecoySearch() {
        Assert.assertEquals(Boolean.FALSE, mgfFile.getPerformDecoySearch());
    }

    @Test
    public void testSetPerformDecoySearch() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPerformDecoySearch(true);
        Assert.assertEquals(true, mgfFile.getPerformDecoySearch());
    }

    @Test
    public void testGetIsErrorTolerant() {
        Assert.assertEquals(Boolean.TRUE, mgfFile.getIsErrorTolerant());
    }

    @Test
    public void testSetIsErrorTolerant() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setIsErrorTolerant(false);
        Assert.assertEquals(false, mgfFile.getIsErrorTolerant());
    }

    @Test
    public void testGetFormat() {
        Assert.assertEquals("Mascot generic", mgfFile.getFormat());
    }

    @Test
    public void testSetFormat() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setFormat("Sequest (.DTA)");
        Assert.assertEquals("Sequest (.DTA)", modifiedMgfFile.getFormat());
    }

    @Test
    public void testGetFrames() {
        Assert.assertEquals(6, mgfFile.getFrames().size());
        Assert.assertEquals(new Integer(5), mgfFile.getFrames().get(4));
        Assert.assertEquals(new Integer(3), mgfFile.getFrames().get(2));
    }

    @Test
    public void testSetFrames() {
        ArrayList<Integer> frames = new ArrayList<>();
        frames.add(2);
        frames.add(4);
        frames.add(5);
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setFrames(frames);
        Assert.assertEquals("[2, 4, 5]", modifiedMgfFile.getFrames().toString());
    }

    @Test
    public void testGetInstrument() {
        Assert.assertEquals("ESI-QUAD", mgfFile.getInstrument());
    }

    @Test
    public void testSetInstrument() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setInstrument("Default");
        Assert.assertEquals("Default", modifiedMgfFile.getInstrument());
    }

    @Test
    public void testGetVariableModifications() {
        Assert.assertEquals("Oxidation (M)", mgfFile.getVariableModifications());
    }

    @Test
    public void testSetVariableModifications() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setVariableModifications("My mod");
        Assert.assertEquals("My mod", modifiedMgfFile.getVariableModifications());
    }

    @Test
    public void testGetFragmentIonTolerance() {
        Assert.assertEquals(0.5, mgfFile.getFragmentIonTolerance(), 0.0);
    }

    @Test
    public void testSetFragmentIonTolerance() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setFragmentIonTolerance(0.3);
        Assert.assertEquals(new Double(0.3), modifiedMgfFile.getFragmentIonTolerance());
    }

    @Test
    public void testGetFragmentIonToleranceUnit() {
        Assert.assertEquals(MgfUtils.FragmentToleranceUnits.DA, mgfFile.getFragmentIonToleranceUnit());
    }

    @Test
    public void testSetFragmentIonToleranceUnit() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setFragmentIonToleranceUnit(MgfUtils.FragmentToleranceUnits.MMU);
        Assert.assertEquals(MgfUtils.FragmentToleranceUnits.MMU, modifiedMgfFile.getFragmentIonToleranceUnit());
    }

    @Test
    public void testGetMassType() {
        Assert.assertEquals(MgfUtils.MassType.MONOISOTOPIC, mgfFile.getMassType());
    }

    @Test
    public void testSetMassType() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setMassType(MgfUtils.MassType.AVERAGE);
        Assert.assertEquals(MgfUtils.MassType.AVERAGE, modifiedMgfFile.getMassType());
    }

    @Test
    public void testGetFixedMofications() {
        Assert.assertEquals("Carbamidomethylation (C)", mgfFile.getFixedMofications());
    }

    @Test
    public void testSetFixedMofications() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setFixedMofications("Another mod");
        Assert.assertEquals("Another mod", modifiedMgfFile.getFixedMofications());
    }

    @Test
    public void testGetPeptideIsotopeError() {
        Assert.assertEquals(1.3, mgfFile.getPeptideIsotopeError(), 0.0);
    }

    @Test
    public void testSetPeptideIsotopeError() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPeptideIsotopeError(1.9);
        Assert.assertEquals(new Double(1.9), modifiedMgfFile.getPeptideIsotopeError());
    }

    @Test
    public void testGetPartials() {
        Assert.assertEquals(new Integer(1), mgfFile.getPartials());
    }

    @Test
    public void testSetPartials() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPartials(2);
        Assert.assertEquals(new Integer(2), modifiedMgfFile.getPartials());
    }

    @Test
    public void testGetPrecursor() {
        Assert.assertEquals(1047.0, mgfFile.getPrecursor(),0.0);
    }

    @Test
    public void testSetPrecursor() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPrecursor(1011.0);
        Assert.assertEquals(new Double(1011.0), modifiedMgfFile.getPrecursor());
    }

    @Test
    public void testGetQuantitation() {
        Assert.assertEquals("iTRAQ 4plex", mgfFile.getQuantitation());
    }

    @Test
    public void testSetQuantitation() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setQuantitation("SILAC");
        Assert.assertEquals("SILAC", modifiedMgfFile.getQuantitation());
    }

    @Test
    public void testGetMaxHitsToReport() {
        Assert.assertEquals("1500", mgfFile.getMaxHitsToReport());
    }

    @Test
    public void testSetMaxHitsToReport() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setMaxHitsToReport("Auto");
        Assert.assertEquals("Auto", modifiedMgfFile.getMaxHitsToReport());
    }

    @Test
    public void testGetReportType() {
        Assert.assertEquals(MgfUtils.ReportType.PEPTIDE, mgfFile.getReportType());
    }

    @Test
    public void testSetReportType() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setReportType(MgfUtils.ReportType.PROTEIN);
        Assert.assertEquals(MgfUtils.ReportType.PROTEIN, modifiedMgfFile.getReportType());
    }

    @Test
    public void testGetSearchType() {
        Assert.assertEquals(MgfUtils.SearchType.MIS, mgfFile.getSearchType());
    }

    @Test
    public void testSetSearchType() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setSearchType(MgfUtils.SearchType.PMF);
        Assert.assertEquals(MgfUtils.SearchType.PMF, modifiedMgfFile.getSearchType());
    }

    @Test
    public void testGetProteinMass() {
        Assert.assertEquals("10489", mgfFile.getProteinMass());
    }

    @Test
    public void testSetProteinMass() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setProteinMass("1010");
        Assert.assertEquals("1010", modifiedMgfFile.getProteinMass());
    }

    @Test
    public void testGetTaxonomy() {
        Assert.assertEquals("Human 9606", mgfFile.getTaxonomy());
    }

    @Test
    public void testSetTaxonomy() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setTaxonomy("My taxon");
        Assert.assertEquals("My taxon", modifiedMgfFile.getTaxonomy());
    }

    @Test
    public void testGetPeptideMassTolerance() {
        Assert.assertEquals(0.2, mgfFile.getPeptideMassTolerance(), 0.0);
    }

    @Test
    public void testSetPeptideMassTolerance() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPeptideMassTolerance(0.3);
        Assert.assertEquals(new Double(0.3), modifiedMgfFile.getPeptideMassTolerance());
    }

    @Test
    public void testGetPeptideMassToleranceUnit() {
        Assert.assertEquals(MgfUtils.PeptideToleranceUnit.PPM, mgfFile.getPeptideMassToleranceUnit());
    }

    @Test
    public void testSetPeptideMassToleranceUnit() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setPeptideMassToleranceUnit(MgfUtils.PeptideToleranceUnit.PERCENT);
        Assert.assertEquals(MgfUtils.PeptideToleranceUnit.PERCENT, modifiedMgfFile.getPeptideMassToleranceUnit());
    }

    @Test
    public void testGetUserParameter() {
        Assert.assertEquals(3, mgfFile.getUserParameter().size());
        Assert.assertEquals("2nd user param", mgfFile.getUserParameter().get(1));
    }

    @Test
    public void testSetUserParameter() {
        ArrayList<String> params = new ArrayList<>();
        params.add("My param");
        params.add("Another param");
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setUserParameter(params);
        Assert.assertEquals(2, modifiedMgfFile.getUserParameter().size());
    }

    @Test
    public void testGetUserMail() {
        Assert.assertEquals("jgriss@ebi.ac.uk", mgfFile.getUserMail());
    }

    @Test
    public void testSetUserMail() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setUserMail("another@mail");
        Assert.assertEquals("another@mail", modifiedMgfFile.getUserMail());
    }

    @Test
    public void testGetUserName() {
        Assert.assertEquals("Johannes Griss", mgfFile.getUserName());
    }

    @Test
    public void testSetUserName() {
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setUserName("Another name");
        Assert.assertEquals("Another name", modifiedMgfFile.getUserName());
    }

    @Test
    public void testSetMs2Queries() throws Exception {
        Ms2Query query = new Ms2Query("BEGIN IONS\nPEPMASS=406.283\n145.119100 8\n217.142900 75\n409.221455 11\n438.314735 46\n567.400183 24\nEND IONS\n", 1, false, true);
        ArrayList<Ms2Query> queries = new ArrayList<>();
        queries.add(query);
        MgfIndexedReader modifiedMgfFile = mgfFile;
        modifiedMgfFile.setMs2Queries(queries);
        Assert.assertEquals(1, modifiedMgfFile.getMs2QueryCount());
        Assert.assertEquals(query.toString(), modifiedMgfFile.getMs2Query(0, false).toString());
    }

    @Test
    public void testGetMs2QueryCount() {
        Assert.assertEquals(10, mgfFile.getMs2QueryCount());
    }

    @Test
    public void testGetMs2Query() throws Exception{
        Assert.assertNotNull(mgfFile.getMs2Query(7, false));
    }

    @Test
    public void testGetIndex() throws Exception {
        loadTestFile();
        List<IndexElement> index = mgfFile.getMsNIndexes(2);
        Spectrum s = mgfFile.getSpectrumByIndex(3);
        Spectrum s1 = MgfIndexedReader.getIndexedSpectrum(sourceFile, index.get(2), false);
        Assert.assertEquals(s.toString(), s1.toString());
    }

    @Test
    public void testPerformanceTime() throws Exception {
        long time = System.currentTimeMillis();
        URL testFile = getClass().getClassLoader().getResource("qExactive01819.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        File file = new File(testFile.toURI());
        MgfIndexedReader mgfFile = new MgfIndexedReader(file);
        int count = 0;
        while (mgfFile.hasNext()){
            Spectrum spec = mgfFile.next();
            count++;
        }
        System.out.println("Spectra Read: " + count + " in Time " + (System.currentTimeMillis() - time));

    }




}
