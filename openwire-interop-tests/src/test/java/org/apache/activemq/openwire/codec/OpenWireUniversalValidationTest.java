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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.openwire.buffer.Buffer;
import org.apache.activemq.openwire.codec.universal.MarshallerFactory;
import org.apache.activemq.openwire.commands.CommandTypes;
import org.apache.activemq.openwire.commands.ExceptionResponse;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that Openwire marshalling will validate Throwable types during
 * unmarshalling commands that contain a Throwable
 */
public class OpenWireUniversalValidationTest {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @Before
    public void before() {
        initialized.set(false);
    }

    protected boolean isUseLegacyCodecs() {
        return false;
    }

    protected int getVersion() {
        return CommandTypes.PROTOCOL_VERSION;
    }

    @Test
    public void testOpenwireThrowableValidation() throws Exception {
        // Create a format which will use loose encoding by default
        // The code for handling exception creation is shared between both
        // tight/loose encoding so only need to test 1
        OpenWireFormat format = new OpenWireFormat();
        format.setUseLegacyCodecs(isUseLegacyCodecs());

        // Override the marshaller map with a custom impl to purposely marshal a class type that is
        // not a Throwable for testing the unmarshaller
        Class<?> marshallerFactory = getMarshallerFactory();
        Method createMarshallerMap = marshallerFactory.getMethod("createMarshallerMap", OpenWireFormat.class);
        DataStreamMarshaller[] map = (DataStreamMarshaller[]) createMarshallerMap.invoke(marshallerFactory, format);
        map[ExceptionResponse.DATA_STRUCTURE_TYPE] = getExceptionMarshaller();
        // This will trigger updating the marshaller from the marshaller map with the right version
        // which is necessary for legacy codecs
        if (isUseLegacyCodecs()) {
            assertTrue(marshallerFactory.getName().contains("codec.v" + getVersion()));
            format.setVersion(getVersion());
        }

        // Make sure we are using universal marshaller factory if legacy is false
        assertEquals(!isUseLegacyCodecs(), MarshallerFactory.class.equals(marshallerFactory));

        // Build the response and try to unmarshal which should give an IllegalArgumentException on unmarshall
        // as the test marshaller should have encoded a class type that is not a Throwable
        ExceptionResponse r = new ExceptionResponse();
        r.setException(new Exception());
        Buffer bss = format.marshal(r);
        ExceptionResponse response = (ExceptionResponse) format.unmarshal(bss);

        assertTrue(response.getException() instanceof IllegalArgumentException);
        assertTrue(response.getException().getMessage().contains("is not assignable to Throwable"));

        // assert the class was never initialized
        assertFalse(initialized.get());
    }

    static class NotAThrowable {
        private String message;

        static {
            // Class should not be initialized so set flag here to verify
            initialized.set(true);
        }

        public NotAThrowable(String message) {
            this.message = message;
        }

        public NotAThrowable() {
        }
    }

    protected Class<?> getMarshallerFactory() throws ClassNotFoundException {
        return MarshallerFactory.class;
    }

    // Create test marshallers for all non-legacy versions that will encode NotAThrowable
    // instead of the exception type for testing purposes
    protected DataStreamMarshaller getExceptionMarshaller() {
        return new org.apache.activemq.openwire.codec.universal.ExceptionResponseMarshaller() {
            @Override
            protected void looseMarshalThrowable(OpenWireFormat wireFormat, Throwable o,
                DataOutput dataOut) throws IOException {
                dataOut.writeBoolean(o != null);
                looseMarshalString(NotAThrowable.class.getName(), dataOut);
                looseMarshalString(o.getMessage(), dataOut);
            }
        };
    }

}
