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
package org.apache.activemq.openwire.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.activemq.openwire.buffer.Buffer;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.junit.jupiter.api.AfterEach;
import org.opentest4j.AssertionFailedError;

public abstract class DataStructureTestSupport {

    protected OpenWireFormat wireFormat;

    public void assertBeanMarshalls(Object original) throws IOException {
        Object o = marshalAndUnmarshall(original, wireFormat);
        assertNotNull(o);
        assertEqualsOpenWire(original, o);
    }

    public static void assertEqualsOpenWire(Object expect, Object was) {
        if (expect == null ^ was == null) {
            throw new AssertionFailedError("Not equals, expected: " + expect + ", was: " + was);
        }

        if (expect == null) {
            return;
        }

        if (expect.getClass() != was.getClass()) {
            throw new AssertionFailedError("Not equals, classes don't match. expected: " + expect.getClass() + ", was: " + was.getClass());
        }

        if (expect.getClass().isArray()) {
            Class<?> componentType = expect.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                boolean ok = false;
                if (componentType == byte.class) {
                    ok = Arrays.equals((byte[]) expect, (byte[]) was);
                }
                if (componentType == char.class) {
                    ok = Arrays.equals((char[]) expect, (char[]) was);
                }
                if (componentType == short.class) {
                    ok = Arrays.equals((short[]) expect, (short[]) was);
                }
                if (componentType == int.class) {
                    ok = Arrays.equals((int[]) expect, (int[]) was);
                }
                if (componentType == long.class) {
                    ok = Arrays.equals((long[]) expect, (long[]) was);
                }
                if (componentType == double.class) {
                    ok = Arrays.equals((double[]) expect, (double[]) was);
                }
                if (componentType == float.class) {
                    ok = Arrays.equals((float[]) expect, (float[]) was);
                }
                if (!ok) {
                    throw new AssertionFailedError("Arrays not equal");
                }
            } else {
                Object expectArray[] = (Object[]) expect;
                Object wasArray[] = (Object[]) was;
                if (expectArray.length != wasArray.length) {
                    throw new AssertionFailedError("Not equals, array lengths don't match. expected: " + expectArray.length + ", was: " + wasArray.length);
                }
                for (int i = 0; i < wasArray.length; i++) {
                    assertEqualsOpenWire(expectArray[i], wasArray[i]);
                }

            }
        } else if (expect instanceof Command) {
            assertEqualsOpenWire(expect.getClass(), was.getClass());
            Method[] methods = expect.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterTypes().length == 0
                    && method.getReturnType() != null) {

                    // Check to see if there is a setter for the method.
                    try {
                        if (method.getName().startsWith("get")) {
                            expect.getClass().getMethod(method.getName().replaceFirst("get", "set"), new Class[] { method.getReturnType() });
                        } else {
                            expect.getClass().getMethod(method.getName().replaceFirst("is", "set"), new Class[] { method.getReturnType() });
                        }
                    } catch (Throwable ignore) {
                        continue;
                    }

                    try {
                        assertEqualsOpenWire(method.invoke(expect, (Object) null), method.invoke(was, (Object) null));
                    } catch (IllegalArgumentException e) {
                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {
                    }
                }
            }
        } else {
            assertEquals(expect, was);
        }
    }

    @AfterEach
    public void afterEach() {
        wireFormat = null;
    }

    protected void createWireFormat(boolean cacheEnabled) {
        OpenWireFormat answer = new OpenWireFormat(10);
        answer.setCacheEnabled(cacheEnabled);
        wireFormat = answer;
    }

    protected Object marshalAndUnmarshall(Object original, OpenWireFormat wireFormat) throws IOException {
        Buffer packet = wireFormat.marshal(original);
        return wireFormat.unmarshal(packet);
    }
}
