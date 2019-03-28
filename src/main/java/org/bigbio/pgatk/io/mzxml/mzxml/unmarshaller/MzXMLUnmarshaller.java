package org.bigbio.pgatk.io.mzxml.mzxml.unmarshaller;

import org.bigbio.pgatk.io.mzxml.mzxml.model.MzXMLObject;
import org.bigbio.pgatk.io.mzxml.mzxml.model.MzXmlElement;

public interface MzXMLUnmarshaller {
	<T extends MzXMLObject> T unmarshal(String xmlSnippet, MzXmlElement element) throws Exception;
}
