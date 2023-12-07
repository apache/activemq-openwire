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
package org.apache.activemq.openwire.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.openwire.annotations.OpenWireType;

/**
 * Wrapper used to describe all the elements of an OpenWire type.
 */
public class OpenWireTypeDescriptor {

    private final Class<?> openWireType;
    private final OpenWireType typeAnnotation;
    private final List<OpenWirePropertyDescriptor> properties;

    public OpenWireTypeDescriptor(Class<?> openWireType) {
        this.openWireType = openWireType;
        this.typeAnnotation = Optional.ofNullable(openWireType.getAnnotation(OpenWireType.class))
            .orElseThrow(() -> new IllegalArgumentException(openWireType + " is missing required OpenWireType annotation."));
        this.properties = GeneratorUtils.finalOpenWireProperties(openWireType)
            // Only track fields from the given type and not its super types.
            .stream().filter(field -> field.getDeclaringClass().equals(openWireType))
            .map(field -> new OpenWirePropertyDescriptor(openWireType, field))
            .sorted().toList();

        // Make sure all sequence numbers seen are correct
        validateSequenceNumbers();
    }

    /**
     * @return the name of the OpenWire protocol type being wrapped.
     */
    public String getTypeName() {
        return openWireType.getSimpleName();
    }

    /**
     * @return the name of the package this type is contained in.
     */
    public String getPackageName() {
        return openWireType.getPackage().getName();
    }

    /**
     * @return the name of the super class of this object.
     */
    public String getSuperClass() {
        Class<?> superClass = openWireType.getSuperclass();
        if (superClass == null) {
            superClass = Object.class;
        }

        return superClass.getSimpleName();
    }

    /**
     * @return the first version this type was introduced in.
     */
    public int getVersion() {
        return typeAnnotation.version();
    }

    /**
     * @return true if the type requires awareness of the marshaling process.
     */
    public boolean isMarshalAware() {
        return typeAnnotation.marshalAware();
    }

    /**
     * @return the unique OpenWire type code of this instance.
     */
    public int getTypeCode() {
        return typeAnnotation.typeCode();
    }

    /**
     * @return true if the OpenWire type is an abstract base of other types.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(openWireType.getModifiers());
    }

    /**
     * @return true if this type has properties to marshal and unmarshal.
     */
    public boolean hasProperties() {
        return !properties.isEmpty();
    }

    /**
     * @return the properties of this described OpenWire type.
     */
    public List<OpenWirePropertyDescriptor> getProperties() {
        return properties;
    }

    private void validateSequenceNumbers() {
        int expected = 1;
        for (OpenWirePropertyDescriptor desc : properties) {
            if (desc.getMarshalingSequence() < 1) {
                sequenceError(desc, expected, "Sequence number must be positive");
            } else if (desc.getMarshalingSequence() < expected) {
                sequenceError(desc, expected, "Sequence numbers must not be duplicated");
            } else if (expected != desc.getMarshalingSequence()) {
                sequenceError(desc, expected, "Sequence numbers must be contiguous");
            }
            expected++;
        }
    }

    private String sequenceError(OpenWirePropertyDescriptor descriptor, int expected, String error) {
        throw new IllegalArgumentException("Property: '" + descriptor.getPropertyName()
            + "' on OpenWireType: '" + openWireType + "' has invalid sequence number '"
            + descriptor.getMarshalingSequence() + "'. Error: " + error
            + ". Expected sequence number is: '" + expected + "'.");
    }

}
