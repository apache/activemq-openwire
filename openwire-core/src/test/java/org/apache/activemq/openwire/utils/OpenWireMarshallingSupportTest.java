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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;

import org.apache.activemq.openwire.buffer.DataByteArrayInputStream;
import org.apache.activemq.openwire.buffer.DataByteArrayOutputStream;
import org.junit.Test;

public class OpenWireMarshallingSupportTest {

    @Test
    public void testMarshalBoolean() throws Exception {
        DataByteArrayOutputStream dataOut = new DataByteArrayOutputStream();
        OpenWireMarshallingSupport.marshalBoolean(dataOut, false);
        OpenWireMarshallingSupport.marshalBoolean(dataOut, true);
        DataByteArrayInputStream input = new DataByteArrayInputStream(dataOut.toBuffer());
        DataInputStream dataIn = new DataInputStream(input);
        Boolean result = (Boolean) OpenWireMarshallingSupport.unmarshalPrimitive(dataIn);
        assertFalse(result);
        result = (Boolean) OpenWireMarshallingSupport.unmarshalPrimitive(dataIn);
        assertTrue(result);
    }
}
