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

import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class MessageTest extends DataStructureTestSupport {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testOpenWireMessageMarshaling(boolean cacheEnabled) throws IOException {
        createWireFormat(cacheEnabled);
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", 1));
        assertBeanMarshalls(message);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testOpenWireMessageMarshalingBigMessageId(boolean cacheEnabled) throws IOException {
        createWireFormat(cacheEnabled);
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Short.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testOpenWireMessageMarshalingBiggerMessageId(boolean cacheEnabled) throws IOException {
        createWireFormat(cacheEnabled);
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Integer.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testOpenWireMessageMarshalingBiggestMessageId(boolean cacheEnabled) throws IOException {
        createWireFormat(cacheEnabled);
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Long.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testMessageIdMarshaling(boolean cacheEnabled) throws IOException {
        createWireFormat(cacheEnabled);
        assertBeanMarshalls(new MessageId("c1:1:1", 1));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testPropRemove(boolean cacheEnabled) throws Exception {
        createWireFormat(cacheEnabled);
        OpenWireMessage message = new OpenWireMessage();
        message.setProperty("RM","RM");

        OpenWireMessage unMarshalled = (OpenWireMessage) marshalAndUnmarshall(message, wireFormat);

        unMarshalled.getProperty("NA");
        unMarshalled.removeProperty("RM");

        OpenWireMessage unMarshalledAgain = (OpenWireMessage) marshalAndUnmarshall(unMarshalled, wireFormat);
        assertNull(unMarshalledAgain.getProperty("RM"), "Prop is gone");
    }
}
