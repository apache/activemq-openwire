/*
        if (getType().equals(boolean.class)) {
            return "is" + capitalize(getPropertyName());
        } else {
            return "get" + capitalize(getPropertyName());
        }
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

import org.apache.activemq.openwire.annotations.OpenWireProperty;

/**
 * Wraps a property of an OpenWire protocol type to provide support
 * for generating code to handle that property.
 */
public class OpenWirePropertyDescriptor implements Comparable<OpenWirePropertyDescriptor> {

    private final Class<?> openWireType;
    private final Field openWireProperty;
    private final OpenWireProperty propertyAnnotation;

    private final String getterName;
    private final String setterName;

    public OpenWirePropertyDescriptor(Class<?> openWireType, Field openWireProperty) throws Exception {
        this.openWireType = openWireType;
        this.openWireProperty = openWireProperty;
        this.propertyAnnotation = openWireProperty.getAnnotation(OpenWireProperty.class);

        this.setterName = GeneratorUtils.findSetMethodForProperty(this.openWireType, this).getName();
        this.getterName = GeneratorUtils.findGetMethodForProperty(this.openWireType, this).getName();
    }

    /**
     * @return the declared name of this property.
     */
    public String getPropertyName() {
        return openWireProperty.getName();
    }

    /**
     * @return the first OpenWire version this property appeared in
     */
    public int getVersion() {
        return propertyAnnotation.version();
    }

    /**
     * @return the position in the marshaling process this type should occupy.
     */
    public int getMarshalingSequence() {
        return propertyAnnotation.sequence();
    }

    /**
     * @return the defined size attribute for this property.
     */
    public int getSize() {
        return propertyAnnotation.size();
    }

    /**
     * @return
     */
    public boolean isCached() {
        return propertyAnnotation.cached();
    }

    /**
     * @return true if the field is an array type.
     */
    public boolean isArray() {
        return openWireProperty.getType().isArray();
    }

    /**
     * @return true if this property is {@link Throwable} or a descendant of {@link Throwable}.
     */
    public boolean isThrowable() {
        return isThrowable(getType());
    }

    /**
     * @return the Class that represents this properties type.
     */
    public Class<?> getType() {
        return openWireProperty.getType();
    }

    /**
     * @return the name of this property
     */
    public String getTypeName() {
        return openWireProperty.getType().getSimpleName();
    }

    /**
     * @return the name of the set method in the OpenWireType that handles this property.
     */
    public String getSetterName() {
        return setterName;
    }

    /**
     * @return the name of the get method in the OpenWireType that handles this property.
     */
    public String getGetterName() {
        return getterName;
    }

    private static boolean isThrowable(Class<?> type) {
        if (type.getCanonicalName().equals(Throwable.class.getName())) {
            return true;
        }

        return type.getSuperclass() != null && isThrowable(type.getSuperclass());
    }

    @Override
    public int compareTo(OpenWirePropertyDescriptor other) {
        return Integer.compare(getMarshalingSequence(), other.getMarshalingSequence());
    }
}
