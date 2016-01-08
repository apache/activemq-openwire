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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.apache.activemq.openwire.buffer.Buffer;
import org.apache.activemq.openwire.commands.WireFormatInfo;
import org.apache.activemq.util.ByteSequence;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test that the WireFormatInfo marshals and un-marshals correctly.
 */
public abstract class WireFormatInfoMarshaledSizeTest {

    private static final Logger LOG = LoggerFactory.getLogger(WireFormatInfoMarshaledSizeTest.class);

    private OpenWireFormat wireFormat;

    public int getExpectedMarshaledSize() {
        return 244;
    }

    public abstract int getVersion();

    @Before
    public void setUp() throws Exception {
        OpenWireFormatFactory factory = new OpenWireFormatFactory();
        factory.setVersion(getVersion());

        wireFormat = factory.createWireFormat();
    }

    @Test
    public void testMarshalledSize1() throws Exception {
        WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();

        Buffer result = wireFormat.marshal(info);
        assertNotNull(result);
        LOG.info("Size of marshalled object: {}", result.getLength());
        assertEquals(getExpectedMarshaledSize(), result.getLength());

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(result.toByteArray());
        DataInputStream input = new DataInputStream(bytesIn);

        int size = input.readInt();
        assertEquals(getExpectedMarshaledSize() - 4, size);
    }

    @Test
    public void testMarshalledSize2() throws Exception {
        WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(bytesOut);

        wireFormat.marshal(info, dataOut);
        dataOut.close();
        ByteSequence result = new ByteSequence(bytesOut.toByteArray());

        LOG.info("Size of marshalled object: {}", result.getLength());
        assertEquals(getExpectedMarshaledSize(), result.getLength());

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(result.data);
        DataInputStream input = new DataInputStream(bytesIn);

        int size = input.readInt();
        assertEquals(getExpectedMarshaledSize() - 4, size);
    }

    @Test
    public void testMarshalThenUnmarshal1() throws Exception {
        final WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();
        LOG.info("Original: {}", info);

        Buffer marshaledForm = wireFormat.marshal(info);
        assertNotNull(marshaledForm);

        Object result = wireFormat.unmarshal(marshaledForm);
        assertNotNull(result);
        assertTrue(result instanceof WireFormatInfo);
        WireFormatInfo duplicate = (WireFormatInfo) result;
        LOG.info("Duplicated: {}", duplicate);

        assertEquals(info.getVersion(), duplicate.getVersion());
        assertEquals(info.getCacheSize(), duplicate.getCacheSize());
        assertEquals(info.getMaxFrameSize(), duplicate.getMaxFrameSize());
        assertEquals(info.getMaxInactivityDuration(), duplicate.getMaxInactivityDuration());
        assertEquals(info.getMaxInactivityDurationInitalDelay(), duplicate.getMaxInactivityDurationInitalDelay());
        assertEquals(info.isCacheEnabled(), duplicate.isCacheEnabled());
        assertEquals(info.isSizePrefixDisabled(), duplicate.isSizePrefixDisabled());
        assertEquals(info.isTcpNoDelayEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isStackTraceEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isTightEncodingEnabled(), duplicate.isTightEncodingEnabled());
    }

    @Test
    public void testMarshalThenUnmarshal2() throws Exception {
        final WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();
        LOG.info("Original: {}", info);

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(bytesOut);

        wireFormat.marshal(info, dataOut);
        dataOut.close();

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(bytesIn);

        Object result = wireFormat.unmarshal(dataIn);
        assertNotNull(result);
        assertTrue(result instanceof WireFormatInfo);
        WireFormatInfo duplicate = (WireFormatInfo) result;
        LOG.info("Duplicated: {}", duplicate);

        assertEquals(info.getVersion(), duplicate.getVersion());
        assertEquals(info.getCacheSize(), duplicate.getCacheSize());
        assertEquals(info.getMaxFrameSize(), duplicate.getMaxFrameSize());
        assertEquals(info.getMaxInactivityDuration(), duplicate.getMaxInactivityDuration());
        assertEquals(info.getMaxInactivityDurationInitalDelay(), duplicate.getMaxInactivityDurationInitalDelay());
        assertEquals(info.isCacheEnabled(), duplicate.isCacheEnabled());
        assertEquals(info.isSizePrefixDisabled(), duplicate.isSizePrefixDisabled());
        assertEquals(info.isTcpNoDelayEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isStackTraceEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isTightEncodingEnabled(), duplicate.isTightEncodingEnabled());
    }

    @Test
    public void testMarshalThenUnmarshal3() throws Exception {
        final WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();
        LOG.info("Original: {}", info);

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(bytesOut);

        wireFormat.marshal(info, dataOut);
        dataOut.close();

        Buffer marshaledForm = new Buffer(bytesOut.toByteArray());

        Object result = wireFormat.unmarshal(marshaledForm);
        assertNotNull(result);
        assertTrue(result instanceof WireFormatInfo);
        WireFormatInfo duplicate = (WireFormatInfo) result;
        LOG.info("Duplicated: {}", duplicate);

        assertEquals(info.getVersion(), duplicate.getVersion());
        assertEquals(info.getCacheSize(), duplicate.getCacheSize());
        assertEquals(info.getMaxFrameSize(), duplicate.getMaxFrameSize());
        assertEquals(info.getMaxInactivityDuration(), duplicate.getMaxInactivityDuration());
        assertEquals(info.getMaxInactivityDurationInitalDelay(), duplicate.getMaxInactivityDurationInitalDelay());
        assertEquals(info.isCacheEnabled(), duplicate.isCacheEnabled());
        assertEquals(info.isSizePrefixDisabled(), duplicate.isSizePrefixDisabled());
        assertEquals(info.isTcpNoDelayEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isStackTraceEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isTightEncodingEnabled(), duplicate.isTightEncodingEnabled());
    }

    @Test
    public void testMarshalThenUnmarshal4() throws Exception {
        final WireFormatInfo info = wireFormat.getPreferedWireFormatInfo();
        LOG.info("Original: {}", info);

        Buffer marshaledForm = wireFormat.marshal(info);
        assertNotNull(marshaledForm);

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(marshaledForm.data);
        DataInputStream dataIn = new DataInputStream(bytesIn);

        Object result = wireFormat.unmarshal(dataIn);
        assertNotNull(result);
        assertTrue(result instanceof WireFormatInfo);
        WireFormatInfo duplicate = (WireFormatInfo) result;
        LOG.info("Duplicated: {}", duplicate);

        assertEquals(info.getVersion(), duplicate.getVersion());
        assertEquals(info.getCacheSize(), duplicate.getCacheSize());
        assertEquals(info.getMaxFrameSize(), duplicate.getMaxFrameSize());
        assertEquals(info.getMaxInactivityDuration(), duplicate.getMaxInactivityDuration());
        assertEquals(info.getMaxInactivityDurationInitalDelay(), duplicate.getMaxInactivityDurationInitalDelay());
        assertEquals(info.isCacheEnabled(), duplicate.isCacheEnabled());
        assertEquals(info.isSizePrefixDisabled(), duplicate.isSizePrefixDisabled());
        assertEquals(info.isTcpNoDelayEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isStackTraceEnabled(), duplicate.isTcpNoDelayEnabled());
        assertEquals(info.isTightEncodingEnabled(), duplicate.isTightEncodingEnabled());
    }
}
