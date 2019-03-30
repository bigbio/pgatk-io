package org.bigbio.pgatk.io.mzxml.mzxml.unmarshaller;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import org.bigbio.pgatk.io.mzxml.mzxml.model.ModelConstants;
import org.bigbio.pgatk.io.mzxml.mzxml.model.MzXMLObject;
import org.bigbio.pgatk.io.mzxml.mzxml.model.MzXmlElement;

@Slf4j
public class MzXMLUnmarshallerFactory {

    private static MzXMLUnmarshallerFactory instance = new MzXMLUnmarshallerFactory();
    private static JAXBContext jc = null;

    private MzXMLUnmarshallerFactory() {
    }

    public static MzXMLUnmarshallerFactory getInstance() {
        return instance;
    }

    public MzXMLUnmarshaller initializeUnmarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            MzXMLUnmarshaller pum = new MzXMLUnmarshallerImpl();
            log.debug("Unmarshaller Initialized");

            return pum;

        } catch (JAXBException e) {
            log.error("UnmarshallerFactory.initializeUnmarshaller", e);
            throw new IllegalStateException("Could not initialize unmarshaller", e);
        }
    }

    private class MzXMLUnmarshallerImpl implements MzXMLUnmarshaller {

        private Unmarshaller unmarshaller = null;

        private MzXMLUnmarshallerImpl() throws JAXBException {
            unmarshaller = jc.createUnmarshaller();
        }

        /**
         * Add synchronization feature, unmarshaller is not thread safe by default.
         *
         * @param xmlSnippet raw xml string
         * @param element    The mzXML element to unmarshall
         * @param <T>        an instance of class type.
         * @return T    return an instance of class type.
         */
		@Override
		public synchronized <T extends MzXMLObject> T unmarshal(String xmlSnippet, MzXmlElement element)
				throws Exception {
			
			T retval;
            try {

                if (xmlSnippet == null || element == null) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                JAXBElement<T> holder = unmarshaller.unmarshal(new SAXSource(new InputSource(new StringReader(xmlSnippet))), element.getClassType());
                retval = holder.getValue();

            } catch (JAXBException e) {
                throw new Exception("Error unmarshalling object: " + e.getMessage(), e);
            }

            return retval;
		}

    }
}
