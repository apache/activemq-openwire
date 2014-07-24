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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.OpenWireQueue;
import org.apache.activemq.openwire.commands.OpenWireTempQueue;
import org.apache.activemq.openwire.commands.OpenWireTempTopic;
import org.apache.activemq.openwire.commands.OpenWireTopic;
import org.junit.Test;

/**
 * Test various usages of the OpenWireDestination and its subclasses.
 */
public class OpenWireDestinationTest {

    @Test
    public void testSorting() throws Exception {
        SortedSet<OpenWireDestination> set = new TreeSet<OpenWireDestination>();
        OpenWireDestination[] destinations = {new OpenWireQueue("A"), new OpenWireQueue("B"),
                                              new OpenWireTopic("A"), new OpenWireTopic("B")};
        List<OpenWireDestination> expected = Arrays.asList(destinations);
        set.addAll(expected);
        List<OpenWireDestination> actual = new ArrayList<OpenWireDestination>(set);
        assertEquals("Sorted order", expected, actual);
    }

    class CombyDest implements Queue, Topic, TemporaryQueue, TemporaryTopic {

        private final String qName;
        private final String topicName;

        public CombyDest(String qName, String topicName) {
            this.qName = qName;
            this.topicName = topicName;
        }

        @Override
        public void delete() throws JMSException {}

        @Override
        public String getTopicName() throws JMSException {
            return topicName;
        }

        @Override
        public String getQueueName() throws JMSException {
            return qName;
        }
    }

    @Test
    public void testTransformPollymorphic() throws Exception {
        OpenWireQueue queue = new OpenWireQueue("TEST");
        assertEquals(OpenWireDestination.transform(queue), queue);
        assertTrue("is a q", OpenWireDestination.transform(new CombyDest(null, "Topic")) instanceof OpenWireTopic);
        assertTrue("is a q", OpenWireDestination.transform(new CombyDest("Q", null)) instanceof OpenWireQueue);
        try {
            OpenWireDestination.transform(new CombyDest(null, null));
            fail("expect ex as cannot disambiguate");
        } catch (JMSException expected) {
        }
        try {
            OpenWireDestination.transform(new CombyDest("Q", "T"));
            fail("expect ex as cannot disambiguate");
        } catch (JMSException expected) {
        }
    }

    @Test
    public void testEmptyQueueName() {
        try {
            new OpenWireQueue("");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testEmptyTopicName() {
        try {
            new OpenWireTopic("");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testDestinationOptions() throws Exception {
        doTestDestinationOptions(new OpenWireQueue("TEST?k1=v1&k2=v2"));
        doTestDestinationOptions(new OpenWireTopic("TEST?k1=v1&k2=v2"));
        doTestDestinationOptions(new OpenWireTempQueue("TEST:1?k1=v1&k2=v2"));
        doTestDestinationOptions(new OpenWireTempTopic("TEST:1?k1=v1&k2=v2"));
    }

    private void doTestDestinationOptions(OpenWireDestination destination) throws IOException {
        Map<String, String> options = destination.getOptions();
        assertNotNull(options);
        assertEquals("v1", options.get("k1"));
        assertEquals("v2", options.get("k2"));
    }
}
