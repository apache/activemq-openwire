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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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

    class CombyDest {

        private final String qName;
        private final String topicName;

        public CombyDest(String qName, String topicName) {
            this.qName = qName;
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }

        public String getQueueName() {
            return qName;
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
