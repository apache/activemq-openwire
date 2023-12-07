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
package org.apache.activemq.openwire.codec;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test that Openwire marshalling for legacy versions will validate Throwable types during
 * unmarshalling commands that contain a Throwable
 */
@RunWith(Parameterized.class)
public class OpenWireLegacyValidationTest extends OpenWireUniversalValidationTest {
    private final int version;

    // Run through version 1 - 12 which are legacy
    // Newly generated will be universal and convered by the parent test
    @Parameters(name = "version={0}")
    public static Collection<Object[]> data() {
        List<Object[]> versions = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            versions.add(new Object[]{i});
        }
        return versions;
    }

    @Override
    protected boolean isUseLegacyCodecs() {
        return true;
    }

    @Override
    protected int getVersion() {
        return version;
    }

    public OpenWireLegacyValidationTest(int version) {
        this.version = version;
    }

    protected Class<?> getMarshallerFactory() throws ClassNotFoundException {
        return Class.forName("org.apache.activemq.openwire.codec.v" + version + ".MarshallerFactory");
    }

    // Create test marshallers for all legacy versions that will encode NotAThrowable
    // instead of the exception type for testing purposes
    protected DataStreamMarshaller getExceptionMarshaller() {
        switch (version) {
            case 1:
                return new org.apache.activemq.openwire.codec.v1.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 2:
                return new org.apache.activemq.openwire.codec.v2.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 3:
                return new org.apache.activemq.openwire.codec.v3.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 4:
                return new org.apache.activemq.openwire.codec.v4.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 5:
                return new org.apache.activemq.openwire.codec.v5.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 6:
                return new org.apache.activemq.openwire.codec.v6.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 7:
                return new org.apache.activemq.openwire.codec.v7.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 8:
                return new org.apache.activemq.openwire.codec.v8.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 9:
                return new org.apache.activemq.openwire.codec.v9.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 10:
                return new org.apache.activemq.openwire.codec.v10.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            case 11:
                return new org.apache.activemq.openwire.codec.v11.ExceptionResponseMarshaller() {
                    @Override
                    protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                        DataOutput dataOut) throws IOException {
                        dataOut.writeBoolean(o != null);
                        looseMarshalString(NotAThrowable.class.getName(), dataOut);
                        looseMarshalString(o.getMessage(), dataOut);
                    }
                };
            default:
                throw new IllegalArgumentException("Unknown openwire version of " + version);
        }
    }

}
