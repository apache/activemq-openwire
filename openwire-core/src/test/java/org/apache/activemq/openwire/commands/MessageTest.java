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

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.activemq.openwire.commands.MessageId;
import org.apache.activemq.openwire.commands.OpenWireMessage;
import org.apache.activemq.openwire.commands.OpenWireQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class MessageTest extends DataStructureTestSupport {

    public MessageTest(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    @Parameters
    public static Collection<Object[]> data() {
      Object[][] data = new Object[][] { { Boolean.TRUE }, { Boolean.FALSE } };
      return Arrays.asList(data);
    }

    @Test
    public void testOpenWireMessageMarshaling() throws IOException {
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", 1));
        assertBeanMarshalls(message);
    }

    @Test
    public void testOpenWireMessageMarshalingBigMessageId() throws IOException {
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Short.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @Test
    public void testOpenWireMessageMarshalingBiggerMessageId() throws IOException {
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Integer.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @Test
    public void testOpenWireMessageMarshalingBiggestMessageId() throws IOException {
        OpenWireMessage message = new OpenWireMessage();
        message.setCommandId((short)1);
        message.setOriginalDestination(new OpenWireQueue("queue"));
        message.setGroupID("group");
        message.setGroupSequence(4);
        message.setCorrelationId("correlation");
        message.setMessageId(new MessageId("c1:1:1", Long.MAX_VALUE));
        assertBeanMarshalls(message);
    }

    @Test
    public void testMessageIdMarshaling() throws IOException {
        assertBeanMarshalls(new MessageId("c1:1:1", 1));
    }

    @Test
    public void testPropRemove() throws Exception {
        OpenWireMessage message = new OpenWireMessage();
        message.setProperty("RM","RM");

        OpenWireMessage unMarshalled = (OpenWireMessage) marshalAndUnmarshall(message, wireFormat);

        unMarshalled.getProperty("NA");
        unMarshalled.removeProperty("RM");

        OpenWireMessage unMarshalledAgain = (OpenWireMessage) marshalAndUnmarshall(unMarshalled, wireFormat);
        assertNull("Prop is gone", unMarshalledAgain.getProperty("RM"));
    }
}
