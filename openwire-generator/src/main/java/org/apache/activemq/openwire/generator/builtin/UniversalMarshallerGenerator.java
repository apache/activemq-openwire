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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.activemq.openwire.generator.AbstractGenerator;
import org.apache.activemq.openwire.generator.GeneratorUtils;
import org.apache.activemq.openwire.generator.OpenWirePropertyDescriptor;
import org.apache.activemq.openwire.generator.OpenWireTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generator that create a set of OpenWire command marshalers that can
 * handle all OpenWire versions.
 */
public class UniversalMarshallerGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalMarshallerGenerator.class);

    private final String codecBase = "org.apache.activemq.openwire.codec";
    private final String codecPackage = codecBase + ".universal";

    @Override
    public void run(List<OpenWireTypeDescriptor> typeDescriptors) throws Exception {
        final File outputFolder = GeneratorUtils.createDestination(getBaseDir(), codecPackage);
        LOG.info("Output location for generated marshalers is: {}", outputFolder.getAbsolutePath());

        for (final OpenWireTypeDescriptor openWireType : typeDescriptors) {
            LOG.debug("Generating marshaller for type: {}", openWireType.getTypeName());
            processClass(openWireType, outputFolder);
        }
    }

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

    //----- Implementation ---------------------------------------------------//

    protected void processClass(OpenWireTypeDescriptor openWireType, File outputFolder) throws Exception {
        final File marshalerFile = new File(outputFolder, getClassName(openWireType) + ".java");

        try (PrintWriter out = new PrintWriter(new FileWriter(marshalerFile));) {
            LOG.debug("Output file: {}", marshalerFile.getAbsolutePath());
            writeApacheLicense(out);
            writePreamble(out, openWireType);
            writeClassDefinition(out, openWireType);
            writeTypeSupportMethods(out, openWireType);

            writeTightUnmarshal(out, openWireType);
            writeTightMarshal1(out, openWireType);
            writeTightMarshal2(out, openWireType);

            writeLooseMarshal(out, openWireType);
            writeLooseUnmarshal(out, openWireType);

            writeClassClosure(out, openWireType);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writePreamble(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("package " + getCodecPackage() + ";");
        out.println("");

        Set<String> languageTypes = new HashSet<String>();

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final Class<?> type = property.getType();
            if (type.getCanonicalName().startsWith("java.util")) {
                languageTypes.add(type.getCanonicalName());
            } else if (type.getCanonicalName().startsWith("org.fusesource.")) {
                languageTypes.add(type.getCanonicalName());
            }
        }

        for (String languageType : languageTypes) {
            out.println("import " + languageType + ";");
        }
        out.println("import java.io.DataInput;");
        out.println("import java.io.DataOutput;");
        out.println("import java.io.IOException;");
        out.println("");
        out.println("import " + getCodecPackageBase() + ".*;");
        out.println("import " + openWireType.getPackageName() + ".*;");
        out.println("");
    }

    private void writeClassDefinition(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        final String abstractModifier = openWireType.isAbstract() ? "abstract " : "";
        final String className = getClassName(openWireType);
        final String baseClassName = getBaseClassName(openWireType);

        out.println("/**");
        out.println(" * Marshalling code for Open Wire for " + openWireType.getTypeName() + "");
        out.println(" *");
        out.println(" * NOTE!: This file is auto generated - do not modify!");
        out.println(" *");
        out.println(" */");
        out.println("public " + abstractModifier + "class " + className + " extends " + baseClassName + " {");
        out.println("");
    }

    private void writeTypeSupportMethods(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        if (!openWireType.isAbstract()) {
            out.println("    /**");
            out.println("     * Return the type of Data Structure handled by this Marshaler");
            out.println("     *");
            out.println("     * @return short representation of the type data structure");
            out.println("     */");
            out.println("    public byte getDataStructureType() {");
            out.println("        return " + openWireType.getTypeName() + ".DATA_STRUCTURE_TYPE;");
            out.println("    }");
            out.println("    ");
            out.println("    /**");
            out.println("     * @return a new instance of the managed type.");
            out.println("     */");
            out.println("    public DataStructure createObject() {");
            out.println("        return new " + openWireType.getTypeName() + "();");
            out.println("    }");
            out.println("");
        }
    }

    private void writeTightUnmarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Un-marshal an object instance from the data input stream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param target the object to un-marshal");
        out.println("     * @param dataIn the data input stream to build the object from");
        out.println("     * @param bs the boolean stream where the type's booleans were marshaled");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while reading the data");
        out.println("     */");
        out.println("    public void tightUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn, BooleanStream bs) throws IOException {");
        out.println("        super.tightUnmarshal(wireFormat, target, dataIn, bs);");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") target;");
            if (isOpenWireVersionNeeded(openWireType)) {
                out.println("        int version = wireFormat.getVersion();");
            }
            out.println("");
        }

        if (openWireType.isMarshalAware()) {
            out.println("        info.beforeUnmarshall(wireFormat);");
        }

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String setter = property.getSetterName();

            String indent = "        ";
            if (property.getVersion() > 1) {
                indent = indent + "    ";
                out.println("        if (version >= " + property.getVersion() + ") {");
            }

            if (property.isArray() && !typeName.equals("byte[]")) {
                final String arrayType = property.getType().getComponentType().getSimpleName();

                if (size > 0) {
                    out.println(indent + "{");
                    out.println(indent + "    " + arrayType + " value[] = new " + arrayType + "[" + size + "];");
                    out.println(indent + "    " + "for (int i = 0; i < " + size + "; i++) {");
                    out.println(indent + "        value[i] = (" + arrayType + ") tightUnmarsalNestedObject(wireFormat,dataIn, bs);");
                    out.println(indent + "    }");
                    out.println(indent + "    info." + setter + "(value);");
                    out.println(indent + "}");
                } else {
                    out.println(indent + "if (bs.readBoolean()) {");
                    out.println(indent + "    short size = dataIn.readShort();");
                    out.println(indent + "    " + arrayType + " value[] = new " + arrayType + "[size];");
                    out.println(indent + "    for (int i = 0; i < size; i++) {");
                    out.println(indent + "        value[i] = (" + arrayType + ") tightUnmarsalNestedObject(wireFormat,dataIn, bs);");
                    out.println(indent + "    }");
                    out.println(indent + "    info." + setter + "(value);");
                    out.println(indent + "} else {");
                    out.println(indent + "    info." + setter + "(null);");
                    out.println(indent + "}");
                }
            } else {
                if (typeName.equals("boolean")) {
                    out.println(indent + "info." + setter + "(bs.readBoolean());");
                } else if (typeName.equals("byte")) {
                    out.println(indent + "info." + setter + "(dataIn.readByte());");
                } else if (typeName.equals("char")) {
                    out.println(indent + "info." + setter + "(dataIn.readChar());");
                } else if (typeName.equals("short")) {
                    out.println(indent + "info." + setter + "(dataIn.readShort());");
                } else if (typeName.equals("int")) {
                    out.println(indent + "info." + setter + "(dataIn.readInt());");
                } else if (typeName.equals("long")) {
                    out.println(indent + "info." + setter + "(tightUnmarshalLong(wireFormat, dataIn, bs));");
                } else if (typeName.equals("String")) {
                    out.println(indent + "info." + setter + "(tightUnmarshalString(dataIn, bs));");
                } else if (typeName.equals("byte[]")) {
                    if (size >= 0) {
                        out.println(indent + "info." + setter + "(tightUnmarshalConstByteArray(dataIn, bs, " + size + "));");
                    } else {
                        out.println(indent + "info." + setter + "(tightUnmarshalByteArray(dataIn, bs));");
                    }
                } else if (typeName.equals("Buffer")) {
                    out.println(indent + "info." + setter + "(tightUnmarshalByteSequence(dataIn, bs));");
                } else if (property.isThrowable()) {
                    out.println(indent + "info." + setter + "((" + property.getTypeName() + ") tightUnmarsalThrowable(wireFormat, dataIn, bs));");
                } else if (property.isCached()) {
                    out.println(indent + "info." + setter + "((" + property.getTypeName() + ") tightUnmarsalCachedObject(wireFormat, dataIn, bs));");
                } else {
                    out.println(indent + "info." + setter + "((" + property.getTypeName() + ") tightUnmarsalNestedObject(wireFormat, dataIn, bs));");
                }
            }

            if (property.getVersion() > 1) {
                out.println("        }");
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterUnmarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeTightMarshal1(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write the booleans that this object uses to a BooleanStream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param source the object to marshal");
        out.println("     * @param bs the boolean stream where the type's booleans are written");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public int tightMarshal1(OpenWireFormat wireFormat, Object source, BooleanStream bs) throws IOException {");

        if (openWireType.hasProperties()) {
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
            if (isOpenWireVersionNeeded(openWireType)) {
                out.println("        int version = wireFormat.getVersion();");
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeMarshall(wireFormat);");
        }

        out.println("");
        out.println("        int rc = super.tightMarshal1(wireFormat, source, bs);");

        int baseSize = 0;
        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            String indent = "        ";
            if (property.getVersion() > 1) {
                indent = indent + "    ";
                out.println("        if (version >= " + property.getVersion() + ") {");
            }

            if (typeName.equals("boolean")) {
                out.println(indent + "bs.writeBoolean(" + getter + ");");
            } else if (typeName.equals("byte")) {
                baseSize += 1;
            } else if (typeName.equals("char")) {
                baseSize += 2;
            } else if (typeName.equals("short")) {
                baseSize += 2;
            } else if (typeName.equals("int")) {
                baseSize += 4;
            } else if (typeName.equals("long")) {
                out.println(indent + "rc += tightMarshalLong1(wireFormat, " + getter + ", bs);");
            } else if (typeName.equals("String")) {
                out.println(indent + "rc += tightMarshalString1(" + getter + ", bs);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println(indent + "rc += tightMarshalConstByteArray1(" + getter + ", bs, " + size + ");");
                } else {
                    out.println(indent + "rc += tightMarshalByteArray1(" + getter + ", bs);");
                }
            } else if (typeName.equals("Buffer")) {
                out.println(indent + "rc += tightMarshalByteSequence1(" + getter + ", bs);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println(indent + "rc += tightMarshalObjectArrayConstSize1(wireFormat, " + getter + ", bs, " + size + ");");
                } else {
                    out.println(indent + "rc += tightMarshalObjectArray1(wireFormat, " + getter + ", bs);");
                }
            } else if (property.isThrowable()) {
                out.println(indent + "rc += tightMarshalThrowable1(wireFormat, " + getter + ", bs);");
            } else {
                if (property.isCached()) {
                    out.println(indent + "rc += tightMarshalCachedObject1(wireFormat, (DataStructure)" + getter + ", bs);");
                } else {
                    out.println(indent + "rc += tightMarshalNestedObject1(wireFormat, (DataStructure)" + getter + ", bs);");
                }
            }

            if (property.getVersion() > 1) {
                out.println("        }");
            }
        }

        out.println("");
        out.println("        return rc + " + baseSize + ";");
        out.println("    }");
        out.println("");
    }

    private void writeTightMarshal2(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write a object instance to data output stream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param source the object to marshal");
        out.println("     * @param dataOut the DataOut where the properties are written");
        out.println("     * @param bs the boolean stream where the type's booleans are written");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void tightMarshal2(OpenWireFormat wireFormat, Object source, DataOutput dataOut, BooleanStream bs) throws IOException {");
        out.println("        super.tightMarshal2(wireFormat, source, dataOut, bs);");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
            if (isOpenWireVersionNeeded(openWireType)) {
                out.println("        int version = wireFormat.getVersion();");
            }
            out.println("");
        }

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            String indent = "        ";
            if (property.getVersion() > 1) {
                indent = indent + "    ";
                out.println("        if (version >= " + property.getVersion() + ") {");
            }

            if (typeName.equals("boolean")) {
                out.println(indent + "bs.readBoolean();");
            } else if (typeName.equals("byte")) {
                out.println(indent + "dataOut.writeByte(" + getter + ");");
            } else if (typeName.equals("char")) {
                out.println(indent + "dataOut.writeChar(" + getter + ");");
            } else if (typeName.equals("short")) {
                out.println(indent + "dataOut.writeShort(" + getter + ");");
            } else if (typeName.equals("int")) {
                out.println(indent + "dataOut.writeInt(" + getter + ");");
            } else if (typeName.equals("long")) {
                out.println(indent + "tightMarshalLong2(wireFormat, " + getter + ", dataOut, bs);");
            } else if (typeName.equals("String")) {
                out.println(indent + "tightMarshalString2(" + getter + ", dataOut, bs);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println(indent + "tightMarshalConstByteArray2(" + getter + ", dataOut, bs, " + size + ");");
                } else {
                    out.println(indent + "tightMarshalByteArray2(" + getter + ", dataOut, bs);");
                }
            } else if (typeName.equals("Buffer")) {
                out.println(indent + "tightMarshalByteSequence2(" + getter + ", dataOut, bs);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println(indent + "tightMarshalObjectArrayConstSize2(wireFormat, " + getter + ", dataOut, bs, " + size + ");");
                } else {
                    out.println(indent + "tightMarshalObjectArray2(wireFormat, " + getter + ", dataOut, bs);");
                }
            } else if (property.isThrowable()) {
                out.println(indent + "tightMarshalThrowable2(wireFormat, " + getter + ", dataOut, bs);");
            } else {
                if (property.isCached()) {
                    out.println(indent + "tightMarshalCachedObject2(wireFormat, (DataStructure)" + getter + ", dataOut, bs);");
                } else {
                    out.println(indent + "tightMarshalNestedObject2(wireFormat, (DataStructure)" + getter + ", dataOut, bs);");
                }
            }

            if (property.getVersion() > 1) {
                out.println("        }");
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterMarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeLooseUnmarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Un-marshal an object instance from the data input stream");
        out.println("     *");
        out.println("     * @param target the object to un-marshal");
        out.println("     * @param dataIn the data input stream to build the object from");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void looseUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn) throws IOException {");
        out.println("        super.looseUnmarshal(wireFormat, target, dataIn);");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") target;");
            if (isOpenWireVersionNeeded(openWireType)) {
                out.println("        int version = wireFormat.getVersion();");
            }
            out.println("");
        }

        if (openWireType.isMarshalAware()) {
            out.println("        info.beforeUnmarshall(wireFormat);");
        }

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String setter = property.getSetterName();

            String indent = "        ";
            if (property.getVersion() > 1) {
                indent = indent + "    ";
                out.println("        if (version >= " + property.getVersion() + ") {");
            }

            if (property.isArray() && !typeName.equals("byte[]")) {
                final String arrayType = property.getType().getComponentType().getSimpleName();

                if (size > 0) {
                    out.println(indent + "{");
                    out.println(indent + "    " + arrayType + " value[] = new " + arrayType + "[" + size + "];");
                    out.println(indent + "    " + "for (int i = 0; i < " + size + "; i++) {");
                    out.println(indent + "        value[i] = (" + arrayType + ") looseUnmarsalNestedObject(wireFormat,dataIn);");
                    out.println(indent + "    }");
                    out.println(indent + "    info." + setter + "(value);");
                    out.println(indent + "}");
                } else {
                    out.println(indent + "if (dataIn.readBoolean()) {");
                    out.println(indent + "    short size = dataIn.readShort();");
                    out.println(indent + "    " + arrayType + " value[] = new " + arrayType + "[size];");
                    out.println(indent + "    for (int i = 0; i < size; i++) {");
                    out.println(indent + "        value[i] = (" + arrayType + ") looseUnmarsalNestedObject(wireFormat,dataIn);");
                    out.println(indent + "    }");
                    out.println(indent + "    info." + setter + "(value);");
                    out.println(indent + "} else {");
                    out.println(indent + "    info." + setter + "(null);");
                    out.println(indent + "}");
                }
            } else {
                if (typeName.equals("boolean")) {
                    out.println(indent + "info." + setter + "(dataIn.readBoolean());");
                } else if (typeName.equals("byte")) {
                    out.println(indent + "info." + setter + "(dataIn.readByte());");
                } else if (typeName.equals("char")) {
                    out.println(indent + "info." + setter + "(dataIn.readChar());");
                } else if (typeName.equals("short")) {
                    out.println(indent + "info." + setter + "(dataIn.readShort());");
                } else if (typeName.equals("int")) {
                    out.println(indent + "info." + setter + "(dataIn.readInt());");
                } else if (typeName.equals("long")) {
                    out.println(indent + "info." + setter + "(looseUnmarshalLong(wireFormat, dataIn));");
                } else if (typeName.equals("String")) {
                    out.println(indent + "info." + setter + "(looseUnmarshalString(dataIn));");
                } else if (typeName.equals("byte[]")) {
                    if (size > 0) {
                        out.println(indent + "info." + setter + "(looseUnmarshalConstByteArray(dataIn, " + size + "));");
                    } else {
                        out.println(indent + "info." + setter + "(looseUnmarshalByteArray(dataIn));");
                    }
                } else if (typeName.equals("Buffer")) {
                    out.println(indent + "info." + setter + "(looseUnmarshalByteSequence(dataIn));");
                } else if (property.isThrowable()) {
                    out.println(indent + "info." + setter + "((" + typeName + ") looseUnmarsalThrowable(wireFormat, dataIn));");
                } else if (property.isCached()) {
                    out.println(indent + "info." + setter + "((" + typeName + ") looseUnmarsalCachedObject(wireFormat, dataIn));");
                } else {
                    out.println(indent + "info." + setter + "((" + typeName + ") looseUnmarsalNestedObject(wireFormat, dataIn));");
                }
            }

            if (property.getVersion() > 1) {
                out.println("        }");
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterUnmarshall(wireFormat);");
        }

        out.println("    }");
    }

    private void writeLooseMarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write the object to the output using loose marshaling.");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {");

        if (openWireType.hasProperties()) {
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
            if (isOpenWireVersionNeeded(openWireType)) {
                out.println("        int version = wireFormat.getVersion();");
            }
            out.println("");
        }

        if (openWireType.isMarshalAware()) {
            out.println("        info.beforeMarshall(wireFormat);");
        }

        out.println("        super.looseMarshal(wireFormat, source, dataOut);");

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            String indent = "        ";
            if (property.getVersion() > 1) {
                indent = indent + "    ";
                out.println("        if (version >= " + property.getVersion() + ") {");
            }

            if (typeName.equals("boolean")) {
                out.println(indent + "dataOut.writeBoolean(" + getter + ");");
            } else if (typeName.equals("byte")) {
                out.println(indent + "dataOut.writeByte(" + getter + ");");
            } else if (typeName.equals("char")) {
                out.println(indent + "dataOut.writeChar(" + getter + ");");
            } else if (typeName.equals("short")) {
                out.println(indent + "dataOut.writeShort(" + getter + ");");
            } else if (typeName.equals("int")) {
                out.println(indent + "dataOut.writeInt(" + getter + ");");
            } else if (typeName.equals("long")) {
                out.println(indent + "looseMarshalLong(wireFormat, " + getter + ", dataOut);");
            } else if (typeName.equals("String")) {
                out.println(indent + "looseMarshalString(" + getter + ", dataOut);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println(indent + "looseMarshalConstByteArray(wireFormat, " + getter + ", dataOut, " + size + ");");
                } else {
                    out.println(indent + "looseMarshalByteArray(wireFormat, " + getter + ", dataOut);");
                }
            } else if (typeName.equals("Buffer")) {
                out.println(indent + "looseMarshalByteSequence(wireFormat, " + getter + ", dataOut);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println(indent + "looseMarshalObjectArrayConstSize(wireFormat, " + getter + ", dataOut, " + size + ");");
                } else {
                    out.println(indent + "looseMarshalObjectArray(wireFormat, " + getter + ", dataOut);");
                }
            } else if (property.isThrowable()) {
                out.println(indent + "looseMarshalThrowable(wireFormat, " + getter + ", dataOut);");
            } else {
                if (property.isCached()) {
                    out.println(indent + "looseMarshalCachedObject(wireFormat, (DataStructure)" + getter + ", dataOut);");
                } else {
                    out.println(indent + "looseMarshalNestedObject(wireFormat, (DataStructure)" + getter + ", dataOut);");
                }
            }

            if (property.getVersion() > 1) {
                out.println("        }");
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterMarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeClassClosure(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("}");
    }

    //----- Helper Methods for Code Generation -------------------------------//

    private boolean isOpenWireVersionNeeded(OpenWireTypeDescriptor openWireType) {
        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            if (property.getVersion() > 1) {
                return true;
            }
        }

        return false;
    }

    private String getClassName(OpenWireTypeDescriptor openWireType) {
        return openWireType.getTypeName() + "Marshaller";
    }

    private String getBaseClassName(OpenWireTypeDescriptor openWireType) {
        String answer = "BaseDataStreamMarshaller";

        final String superName = openWireType.getSuperClass();
        if (!superName.equals("Object") &&
            !superName.equals("JNDIBaseStorable") &&
            !superName.equals("DataStructureSupport")) {

            answer = superName + "Marshaller";
        }

        return answer;
    }
}
