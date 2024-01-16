/**
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
package org.apache.activemq.openwire.utils;

public class OpenWireValidationSupport {

    private static final String jmsPackageToReplace = "javax.jms";
    private static final String jmsPackageToUse = "jakarta.jms";

    /**
     * Verify that the provided class extends {@link Throwable} and throw an
     * {@link IllegalArgumentException} if it does not.
     *
     * @param clazz
     */
    public static void validateIsThrowable(Class<?> clazz) {
        if (!Throwable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + clazz + " is not assignable to Throwable");
        }
    }

    /**
     * This method can be used to convert from javax -> jakarta or
     * vice versa depending on the version used by the client
     *
     * @param className
     * @return
     */
    public static String convertJmsPackage(String className) {
        if (className != null && className.startsWith(jmsPackageToReplace)) {
            return className.replace(jmsPackageToReplace, jmsPackageToUse);
        }
        return className;
    }
}
