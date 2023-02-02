/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.builder.help;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.tools.xjc.Options;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilder;

/**
 * Provides helper methods for working with JAXB and a Knowledgebase, it takes care of the Classpath issues when
 * mapping against internal type declarations.
 *
 *
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 */
public class KnowledgeBuilderHelper {

    private static DroolsJaxbHelperProvider provider;

    /**
     * Generates pojos for a given XSD using XJC and adds them to the specified KnowlegeBase.
     * <pre>
     * Options xjcOpts = new Options();
     * xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
     * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
     *
     * String[] classNames = KnowledgeBuilderHelper.addXsdModel( ResourceFactory.newClassPathResource( "order.xsd",
     *                                                                                                 getClass() ),
     *                                                           kbuilder,
     *                                                           xjcOpts,
     *                                                           "xsd" );
     * </pre>
     *
     * @param resource
     *     The resource to the XSD model
     * @param kbuilder
     *     the KnowledgeBuilder where the generated .class files will be placed
     * @param xjcOpts
     *     XJC Options
     * @param systemId
     *     XJC systemId
     * @return
     *     Returns an array of class names that where generated by the XSD.
     * @throws IOException
     */
    public static String[] addXsdModel(Resource resource,
                                       KnowledgeBuilder kbuilder,
                                       Options xjcOpts,
                                       String systemId) throws IOException {
        return getDroolsJaxbHelperProvider().addXsdModel( resource,
                                                          kbuilder,
                                                          xjcOpts,
                                                          systemId );
    }

    /**
     * Creates a new JAXBContext, from which the Marshaller and Unmarshaller can be created, which are used by the Transformer
     * pipeline stage.
     *
     * @param classNames
     *     An array of class names that can be resolved by this JAXBContext
     * @param kbase
     *     The KnowledgeBase
     * @return
     *     The JAXB Context
     * @throws JAXBException
     */
    public static JAXBContext newJAXBContext(String[] classNames,
                                             KieBase kbase) throws JAXBException {
        return newJAXBContext( classNames,
                               Collections.emptyMap(),
                               kbase );
    }

    public static JAXBContext newJAXBContext(String[] classNames,
                                             Map<String, ? > properties,
                                             KieBase kbase) throws JAXBException {
        return getDroolsJaxbHelperProvider().newJAXBContext( classNames,
                                                             properties,
                                                             kbase );
    }

    public static synchronized DroolsJaxbHelperProvider getDroolsJaxbHelperProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static synchronized void setDroolsJaxbHelperProvider(DroolsJaxbHelperProvider provider) {
        KnowledgeBuilderHelper.provider = provider;
    }

    private static void loadProvider() {
        try {
            Class<DroolsJaxbHelperProvider> cls = (Class<DroolsJaxbHelperProvider>) Class.forName( "org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl" );
            setDroolsJaxbHelperProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new RuntimeException( "Provider org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl could not be set.",
                                                       e2 );
        }
    }

}
