package io.github.bigbio.pgatk.io.mzxml.mzxml.unmarshaller;

import io.github.bigbio.pgatk.io.mzxml.mzxml.model.MzXMLObject;
import io.github.bigbio.pgatk.io.mzxml.mzxml.model.MzXmlElement;

public interface MzXMLUnmarshaller {
	<T extends MzXMLObject> T unmarshal(String xmlSnippet, MzXmlElement element) throws Exception;
}
