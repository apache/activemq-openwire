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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OpenWireInteropTests extends OpenWireInteropTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireInteropTests.class);

    protected OpenWireConnection connectionId;
    protected boolean tightEncodingEnabled;

    @Override
    protected boolean isTightEncodingEnabled() {
        return tightEncodingEnabled;
    }

    @Override
    @BeforeEach
    public void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        connectionId = new OpenWireConnection();
    }

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testCanConnect(boolean tightEncodingEnabled) throws Exception {
        this.tightEncodingEnabled = tightEncodingEnabled;
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

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testCreateConnection(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());
    }

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testCreateSession(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());
        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
    }

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testCreateProducer(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        ProducerInfo info = producerId.createProducerInfo(new OpenWireTopic(testMethodName + "-Topic"));
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getTopicProducers().length);

        assertTrue(request(producerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getTopicProducers().length);
    }

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testCreateConsumer(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireConsumer consumerId = sessionId.createOpenWireConsumer();

        ConsumerInfo info = consumerId.createConsumerInfo(new OpenWireTopic(testMethodName + "-Topic"));
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getTopicSubscribers().length);

        assertTrue(request(consumerId.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getTopicSubscribers().length);
    }

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testSendMessageToQueue(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        OpenWireQueue queue = new OpenWireQueue(testMethodName + "-Queue");

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

    @ParameterizedTest
    @Timeout(value = 60000, unit = TimeUnit.MILLISECONDS)
    @ValueSource(booleans = {true, false})
    public void testConsumeMessageFromQueue(boolean tightEncodingEnabled) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connectionId.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        OpenWireQueue queue = new OpenWireQueue(testMethodName + "-Queue");

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

        assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return messages.size() == 1;
            }
        }), "Should have received a message");

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
        info.setClientId(testMethodName);
        return info;
    }
}
