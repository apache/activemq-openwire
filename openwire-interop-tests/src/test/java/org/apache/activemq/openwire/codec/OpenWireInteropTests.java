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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.openwire.commands.ConnectionInfo;
import org.apache.activemq.openwire.commands.ConsumerInfo;
import org.apache.activemq.openwire.commands.Message;
import org.apache.activemq.openwire.commands.OpenWireQueue;
import org.apache.activemq.openwire.commands.OpenWireTextMessage;
import org.apache.activemq.openwire.commands.OpenWireTopic;
import org.apache.activemq.openwire.commands.ProducerInfo;
import org.apache.activemq.openwire.util.Wait;
import org.apache.activemq.openwire.utils.OpenWireConnection;
import org.apache.activemq.openwire.utils.OpenWireConsumer;
import org.apache.activemq.openwire.utils.OpenWireProducer;
import org.apache.activemq.openwire.utils.OpenWireSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public abstract class OpenWireInteropTests extends OpenWireInteropTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireInteropTests.class);

    protected OpenWireConnection connectionId;
    protected boolean tightEncodingEnabled;

    public OpenWireInteropTests(boolean tightEncodingEnabled) {
        this.tightEncodingEnabled = tightEncodingEnabled;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { Boolean.FALSE }, { Boolean.TRUE } });
    }

    @Override
    protected boolean isTightEncodingEnabled() {
        return tightEncodingEnabled;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        connectionId = new OpenWireConnection();
    }

    @Test(timeout = 60000)
    public void testCanConnect() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertEquals(getOpenWireVersion(), getRemoteWireFormatInfo().getVersion());

        if (isTightEncodingEnabled()) {
            LOG.info("Should be using tight encoding: are we? {}", wireFormat.isTightEncodingEnabled());
            assertTrue(wireFormat.isTightEncodingEnabled());
        } else {
            LOG.info("Should not be using tight encoding: are we? {}", wireFormat.isTightEncodingEnabled());
            assertFalse(wireFormat.isTightEncodingEnabled());
        }
    }

    @Test(timeout = 60000)
    public void testCreateConnection() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());
    }

    @Test(timeout = 60000)
    public void testCreateSession() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());
        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
    }

    @Test(timeout = 60000)
    public void testCreateProducer() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        ProducerInfo info = producerId.createProducerInfo(new OpenWireTopic(name.getMethodName() + "-Topic"));
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getTopicProducers().length);

        assertTrue(request(producerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getTopicProducers().length);
    }

    @Test(timeout = 60000)
    public void testCreateConsumer() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireConsumer consumerId = sessionId.createOpenWireConsumer();

        ConsumerInfo info = consumerId.createConsumerInfo(new OpenWireTopic(name.getMethodName() + "-Topic"));
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getTopicSubscribers().length);

        assertTrue(request(consumerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getTopicSubscribers().length);
    }

    @Test(timeout = 60000)
    public void testSendMessageToQueue() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        OpenWireQueue queue = new OpenWireQueue(name.getMethodName() + "-Queue");

        ProducerInfo info = producerId.createProducerInfo(queue);
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getQueueProducers().length);

        OpenWireTextMessage message = new OpenWireTextMessage();
        message.setText("test");
        message.setTimestamp(System.currentTimeMillis());
        message.setMessageId(producerId.getNextMessageId());
        message.setProducerId(producerId.getProducerId());
        message.setDestination(queue);
        message.onSend();

        assertTrue(request(message, 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(queue.getPhysicalName()).getQueueSize());

        assertTrue(request(producerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getQueueProducers().length);
    }

    @Test(timeout = 60000)
    public void testConsumeMessageFromQueue() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        OpenWireQueue queue = new OpenWireQueue(name.getMethodName() + "-Queue");

        ProducerInfo producerInfo = producerId.createProducerInfo(queue);
        producerInfo.setDispatchAsync(false);
        assertTrue(request(producerInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getQueueProducers().length);

        OpenWireTextMessage message = new OpenWireTextMessage();
        message.setText("test");
        message.setTimestamp(System.currentTimeMillis());
        message.setMessageId(producerId.getNextMessageId());
        message.setProducerId(producerId.getProducerId());
        message.setDestination(queue);
        message.onSend();

        assertTrue(request(message, 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(queue.getPhysicalName()).getQueueSize());

        OpenWireConsumer consumerId = sessionId.createOpenWireConsumer();
        ConsumerInfo consumerInfo = consumerId.createConsumerInfo(queue);
        consumerInfo.setDispatchAsync(false);
        consumerInfo.setPrefetchSize(1);
        assertTrue(request(consumerInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);

        assertTrue("Should have received a message", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return messages.size() == 1;
            }
        }));

        Message incoming = messages.poll();
        assertTrue(incoming instanceof OpenWireTextMessage);
        OpenWireTextMessage received = (OpenWireTextMessage) incoming;
        assertEquals("test", received.getText());

        assertTrue(request(consumerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
    }

    protected ConnectionInfo createConnectionInfo() {
        ConnectionInfo info = new ConnectionInfo(connectionId.getConnectionId());
        info.setManageable(false);
        info.setFaultTolerant(false);
        info.setClientId(name.getMethodName());
        return info;
    }
}
