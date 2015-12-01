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

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withParametersCount;
import static org.reflections.ReflectionUtils.withPrefix;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.activemq.openwire.annotations.OpenWireProperty;
import org.apache.activemq.openwire.annotations.OpenWireType;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.google.common.base.Predicates;

/**
 * Collection of useful methods when generating OpenWire types.
 */
public class GeneratorUtils {

    public static final String OPENWIRE_TYPES_PACKAGE = "org.apache.activemq.openwire.commands";
    public static final Reflections REFLECTIONS = new Reflections(OPENWIRE_TYPES_PACKAGE);

    /**
     * Returns the set of OpenWire types annotated with the OpenWireType marker.
     *
     * @return a set of class objects representing all the annotated OpenWire types.
     *
     * @throws Exception if an error occurs reading the types.
     */
    public static Set<Class<?>> findOpenWireTypes() throws Exception {
        final Reflections reflections = new Reflections(OPENWIRE_TYPES_PACKAGE);

        final Set<Class<?>> protocolTypes =
            reflections.getTypesAnnotatedWith(OpenWireType.class);

        return protocolTypes;
    }

    /**
     * Given an OpenWire protocol object, find and return all the fields in the object
     * that are annotated with the OpenWireProperty marker.
     *
     * @param openWireType
     *      the OpenWire protocol object to query for property values.
     *
     * @return a {@code Set<Field>} containing the annotated properties from the given object.
     *
     * @throws Exception if an error occurs while scanning for properties.
     */
    public static Set<Field> finalOpenWireProperties(Class<?> openWireType) throws Exception {
        @SuppressWarnings("unchecked")
        final Set<Field> properties =
            ReflectionUtils.getAllFields(openWireType, ReflectionUtils.withAnnotation(OpenWireProperty.class));

        return properties;
    }

    /**
     * Attempt to locate the get method for the given property contained in the target OpenWire
     * type.
     *
     * @param openWireType
     *      The OpenWire type to search.
     * @param property
     *      The property whose get method must be located.
     *
     * @return the name of the get method for the given property.
     *
     * @throws Exception if an error occurs finding the get method.
     */
    @SuppressWarnings("unchecked")
    public static Method findGetMethodForProperty(Class<?> openWireType, OpenWirePropertyDescriptor property) throws Exception {

        if (property.getType().equals(boolean.class)) {
            Set<Method> getters = getAllMethods(openWireType,
                Predicates.and(
                        withModifier(Modifier.PUBLIC),
                        withPrefix("is"),
                        withParametersCount(0)));

            // Found an isX method, use that.
            if (!getters.isEmpty()) {
                for (Method method : getters) {
                    if (method.getName().equalsIgnoreCase("is" + property.getPropertyName())) {
                        return method;
                    }
                }
            }
        }

        Set<Method> getters = getAllMethods(openWireType,
            Predicates.and(
                    withModifier(Modifier.PUBLIC),
                    withPrefix("get"),
                    withParametersCount(0)));

        // Found an getX method, use that.
        if (!getters.isEmpty()) {
            for (Method method : getters) {
                if (method.getName().equalsIgnoreCase("get" + property.getPropertyName())) {
                    return method;
                }
            }
        }

        throw new IllegalArgumentException("Property class has invalid bean method names.");
    }

    /**
     * Attempt to locate the set method for the given property contained in the target OpenWire
     * type.
     *
     * @param openWireType
     *      The OpenWire type to search.
     * @param property
     *      The property whose set method must be located.
     *
     * @return the name of the set method for the given property.
     *
     * @throws Exception if an error occurs finding the set method.
     */
    @SuppressWarnings("unchecked")
    public static Method findSetMethodForProperty(Class<?> openWireType, OpenWirePropertyDescriptor property) throws Exception {

        Set<Method> setters = getAllMethods(openWireType,
            Predicates.and(
                    withModifier(Modifier.PUBLIC),
                    withPrefix("set"),
                    withParametersCount(1)));

        // Found an getX method, use that.
        if (!setters.isEmpty()) {
            for (Method method : setters) {
                if (method.getName().equalsIgnoreCase("set" + property.getPropertyName())) {
                    return method;
                }
            }
        }

        throw new IllegalArgumentException("Property class has invalid bean method names.");
    }

    /**
     * Construct a File instance that points to the targeted output folder
     *
     * @param base
     *      The base directory to start from.
     * @param targetPackage
     *      The name of the java package where the generated code will go.
     *
     * @return a new File object that points to the output folder.
     *
     * @throws Exception if an error occurs.
     */
    public static File createDestination(String base, String targetPackage) throws Exception {
        targetPackage = targetPackage.replace(".", File.separator);

        final File outputFolder = new File(base, targetPackage);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        return outputFolder;
    }

    /**
     * Returns the capitalize version of the given string.  If the string is empty, does
     * not start with a letter, or is the first letter is already upper case then the
     * original String is returned.
     *
     * @param value
     *      The String value to capitalize.
     *
     * @return the given value with the first letter capitalized.
     */
    public static String capitalize(final String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        char entry = value.charAt(0);

        if (!Character.isLetter(entry) || Character.isUpperCase(entry)) {
            return value;
        }

        return Character.toUpperCase(entry) + value.substring(1);
    }
}
