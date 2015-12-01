/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.generator.builtin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.activemq.openwire.generator.AbstractGenerator;
import org.apache.activemq.openwire.generator.GeneratorUtils;
import org.apache.activemq.openwire.generator.OpenWireTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a MarshallerFactory instance that can be used to create the
 * codec configuration in the OpenWireFormat object.
 */
public class UniversalMarshallerFactoryGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalMarshallerFactoryGenerator.class);

    private final String codecBase = "org.apache.activemq.openwire.codec";
    private final String codecPackage = codecBase + ".universal";
    private String factoryFileName = "MarshallerFactory";

    @Override
    public void run(List<OpenWireTypeDescriptor> typeDescriptors) throws Exception {
        final File outputFolder = GeneratorUtils.createDestination(getBaseDir(), codecPackage);
        LOG.info("Output location for generated marshaler factory is: {}", outputFolder.getAbsolutePath());

        final File factoryFile = new File(outputFolder, getFactoryFileName() + ".java");
        LOG.debug("Generating marshaller Factory: {}", factoryFile);

        try (PrintWriter out = new PrintWriter(new FileWriter(factoryFile));) {
            writeApacheLicense(out);
            writePreamble(out);
            writeClassDefinition(out);
            writeFactoryImplementation(out, typeDescriptors);
            writeClassClosure(out);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    //----- Factory creation methods -----------------------------------------//

    private void writePreamble(PrintWriter out) {
        out.println("package " + getCodecPackage() + ";");
        out.println("");
        out.println("import " + getCodecPackageBase() + ".DataStreamMarshaller;");
        out.println("import " + getCodecPackageBase() + ".OpenWireFormat;");
        out.println("");
    }

    private void writeClassDefinition(PrintWriter out) {

        out.println("/**");
        out.println(" * Marshalling Factory for the Universal OpenWire Codec package.");
        out.println(" *");
        out.println(" * NOTE!: This file is auto generated - do not modify!");
        out.println(" *");
        out.println(" */");
        out.println("public class " + getFactoryFileName() + "{");
        out.println("");
    }

    private void writeFactoryImplementation(PrintWriter out, List<OpenWireTypeDescriptor> typeDescriptors) {

        out.println("    /**");
        out.println("     * Creates a Map of command type -> Marshallers");
        out.println("     */");
        out.println("    static final private DataStreamMarshaller marshaller[] = new DataStreamMarshaller[256];");
        out.println("    static {");
        out.println("");

        List<OpenWireTypeDescriptor> sorted = new ArrayList<OpenWireTypeDescriptor>(typeDescriptors);
        Collections.sort(sorted, new Comparator<OpenWireTypeDescriptor>() {
            @Override
            public int compare(OpenWireTypeDescriptor o1, OpenWireTypeDescriptor o2) {
                return o1.getTypeName().compareTo(o2.getTypeName());
            }
        });

        for (final OpenWireTypeDescriptor openWireType : sorted) {
            if (!openWireType.isAbstract()) {
                out.println("        add(new " + openWireType.getTypeName() + "Marshaller());");
            }
        }

        out.println("    }");
        out.println("");
        out.println("    static private void add(DataStreamMarshaller dsm) {");
        out.println("        marshaller[dsm.getDataStructureType()] = dsm;");
        out.println("    }");
        out.println("");
        out.println("    static public DataStreamMarshaller[] createMarshallerMap(OpenWireFormat wireFormat) {");
        out.println("        return marshaller;");
        out.println("    }");
    }

    private void writeClassClosure(PrintWriter out) {
        out.println("}");
    }

    //----- Public Property access methods -----------------------------------//

    /**
     * @return the base codec package name where the OpenWire marshalers support code lives.
     */
    public String getCodecPackageBase() {
        return codecBase;
    }

    /**
     * @return the package name where the OpenWire marshalers are written.
     */
    public String getCodecPackage() {
        return codecPackage;
    }

    public String getFactoryFileName() {
        return factoryFileName;
    }

    public void setFactoryFileName(String factoryFileName) {
        this.factoryFileName = factoryFileName;
    }
}
