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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.jms.BytesMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.openwire.commands.ConnectionInfo;
import org.apache.activemq.openwire.commands.ConsumerInfo;
import org.apache.activemq.openwire.commands.Message;
import org.apache.activemq.openwire.commands.MessageAck;
import org.apache.activemq.openwire.commands.OpenWireBytesMessage;
import org.apache.activemq.openwire.commands.OpenWireQueue;
import org.apache.activemq.openwire.commands.OpenWireTextMessage;
import org.apache.activemq.openwire.commands.ProducerInfo;
import org.apache.activemq.openwire.util.Wait;
import org.apache.activemq.openwire.utils.OpenWireConnection;
import org.apache.activemq.openwire.utils.OpenWireConsumer;
import org.apache.activemq.openwire.utils.OpenWireProducer;
import org.apache.activemq.openwire.utils.OpenWireSession;
import org.junit.Test;

public class MessageCompressionTest extends OpenWireInteropTestSupport {

    // The following text should compress well
    private static final String TEXT = "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. ";

    public OpenWireQueue getOpenWireQueue() {
        return new OpenWireQueue(name.getMethodName());
    }

    public Queue getActiveMQQueue() {
        return new ActiveMQQueue(name.getMethodName());
    }

    @Test
    public void testTextMessageCompressionActiveMQ() throws Exception {
        sendAMQTextMessage(TEXT);
        ActiveMQTextMessage message = receiveAMQTextMessage();
        int compressedSize = message.getContent().getLength();

        sendAMQTextMessage(TEXT, false);
        message = receiveAMQTextMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testOpenWireTextMessageCompression() throws Exception {
        sendOpenWireTextMessage(TEXT);
        OpenWireTextMessage message = receiveOpenWireTextMessage();
        int compressedSize = message.getContent().getLength();

        sendOpenWireTextMessage(TEXT, false);
        message = receiveOpenWireTextMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testTextMessageCompressionActiveMQtoOpenWire() throws Exception {
        sendAMQTextMessage(TEXT);
        OpenWireTextMessage message = receiveOpenWireTextMessage();
        int compressedSize = message.getContent().getLength();

        sendAMQTextMessage(TEXT, false);
        message = receiveOpenWireTextMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testTextMessageCompressionOpenWireToActiveMQ() throws Exception {
        sendOpenWireTextMessage(TEXT);
        ActiveMQTextMessage message = receiveAMQTextMessage();
        int compressedSize = message.getContent().getLength();

        sendOpenWireTextMessage(TEXT, false);
        message = receiveAMQTextMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testBytesMessageCompressionActiveMQ() throws Exception {
        sendAMQBytesMessage(TEXT);
        ActiveMQBytesMessage message = receiveAMQBytesMessage();
        int compressedSize = message.getContent().getLength();
        byte[] bytes = new byte[TEXT.getBytes("UTF8").length];
        message.readBytes(bytes);
        assertTrue(message.readBytes(new byte[255]) == -1);
        String rcvString = new String(bytes, "UTF8");
        assertEquals(TEXT, rcvString);
        assertTrue(message.isCompressed());

        sendAMQBytesMessage(TEXT, false);
        message = receiveAMQBytesMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testBytesMessageCompressionOpenWire() throws Exception {
        sendOpenWireBytesMessage(TEXT);
        OpenWireBytesMessage message = receiveOpenWireBytesMessage();
        int compressedSize = message.getContent().getLength();
        byte[] bytes = message.getBodyBytes();
        String rcvString = new String(bytes, "UTF8");
        assertEquals(TEXT, rcvString);
        assertTrue(message.isCompressed());

        sendOpenWireBytesMessage(TEXT, false);
        message = receiveOpenWireBytesMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testBytesMessageCompressionActiveMQtoOpenWire() throws Exception {
        sendAMQBytesMessage(TEXT);
        OpenWireBytesMessage message = receiveOpenWireBytesMessage();
        int compressedSize = message.getContent().getLength();
        byte[] bytes = message.getBodyBytes();
        String rcvString = new String(bytes, "UTF8");
        assertEquals(TEXT, rcvString);
        assertTrue(message.isCompressed());

        sendAMQBytesMessage(TEXT, false);
        message = receiveOpenWireBytesMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    @Test
    public void testBytesMessageCompressionOpenWiretoActiveMQ() throws Exception {
        sendAMQBytesMessage(TEXT);
        OpenWireBytesMessage message = receiveOpenWireBytesMessage();
        int compressedSize = message.getContent().getLength();
        byte[] bytes = message.getBodyBytes();
        String rcvString = new String(bytes, "UTF8");
        assertEquals(TEXT, rcvString);
        assertTrue(message.isCompressed());

        sendAMQBytesMessage(TEXT, false);
        message = receiveOpenWireBytesMessage();
        int unCompressedSize = message.getContent().getLength();

        assertTrue("expected: compressed Size '" + compressedSize + "' < unCompressedSize '" + unCompressedSize + "'",
                   compressedSize < unCompressedSize);
    }

    //---------- Sends and Receives Message Via ActiveMQ Objects -------------//

    private void sendAMQTextMessage(String message) throws Exception {
        sendAMQTextMessage(TEXT, true);
    }

    private void sendAMQTextMessage(String message, boolean useCompression) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        factory.setUseCompression(useCompression);
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(getActiveMQQueue());
        producer.send(session.createTextMessage(message));
        connection.close();
    }

    private ActiveMQTextMessage receiveAMQTextMessage() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(getActiveMQQueue());
        ActiveMQTextMessage rc = (ActiveMQTextMessage) consumer.receive();
        connection.close();
        return rc;
    }

    private void sendAMQBytesMessage(String message) throws Exception {
        sendAMQBytesMessage(TEXT, true);
    }

    private void sendAMQBytesMessage(String message, boolean useCompression) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        factory.setUseCompression(useCompression);
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(getActiveMQQueue());
        BytesMessage bytesMessage = session.createBytesMessage();
        bytesMessage.writeBytes(message.getBytes("UTF8"));
        producer.send(bytesMessage);
        connection.close();
    }

    private ActiveMQBytesMessage receiveAMQBytesMessage() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(getActiveMQQueue());
        ActiveMQBytesMessage rc = (ActiveMQBytesMessage) consumer.receive();
        connection.close();
        return rc;
    }

    //---------- Send and Receive OpenWire Messages --------------------------//

    private void sendOpenWireTextMessage(String payload) throws Exception {
        sendOpenWireTextMessage(payload, true);
    }

    private void sendOpenWireTextMessage(String payload, boolean useCompression) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        OpenWireConnection connection = new OpenWireConnection();
        ConnectionInfo connectionInfo = connection.createConnectionInfo();
        connectionInfo.setClientId(name.getMethodName());
        assertTrue(request(connectionInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connection.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        ProducerInfo info = producerId.createProducerInfo(getOpenWireQueue());
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getQueueProducers().length);

        OpenWireTextMessage message = new OpenWireTextMessage();
        message.setUseCompression(useCompression);
        message.setText(payload);
        message.setTimestamp(System.currentTimeMillis());
        message.setMessageId(producerId.getNextMessageId());
        message.setProducerId(producerId.getProducerId());
        message.setDestination(getOpenWireQueue());
        message.onSend();

        assertTrue(request(message, 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        assertTrue(request(connection.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getCurrentConnectionsCount());
        disconnect();
    }

    private void sendOpenWireBytesMessage(String payload) throws Exception {
        sendOpenWireBytesMessage(payload, true);
    }

    private void sendOpenWireBytesMessage(String payload, boolean useCompression) throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        OpenWireConnection connection = new OpenWireConnection();
        ConnectionInfo connectionInfo = connection.createConnectionInfo();
        connectionInfo.setClientId(name.getMethodName());
        assertTrue(request(connectionInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connection.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        OpenWireProducer producerId = sessionId.createOpenWireProducer();

        ProducerInfo info = producerId.createProducerInfo(getOpenWireQueue());
        info.setDispatchAsync(false);
        assertTrue(request(info, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getQueueProducers().length);

        OpenWireBytesMessage message = new OpenWireBytesMessage();
        message.setUseCompression(useCompression);
        message.setBodyBytes(payload.getBytes("UTF8"));
        message.setTimestamp(System.currentTimeMillis());
        message.setMessageId(producerId.getNextMessageId());
        message.setProducerId(producerId.getProducerId());
        message.setDestination(getOpenWireQueue());
        message.onSend();

        assertTrue(request(message, 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        assertTrue(request(connection.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getCurrentConnectionsCount());
        disconnect();
    }

    public OpenWireTextMessage receiveOpenWireTextMessage() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        OpenWireConnection connection = new OpenWireConnection();
        ConnectionInfo connectionInfo = connection.createConnectionInfo();
        connectionInfo.setClientId(name.getMethodName());
        assertTrue(request(connectionInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connection.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        OpenWireConsumer consumerId = sessionId.createOpenWireConsumer();
        ConsumerInfo consumerInfo = consumerId.createConsumerInfo(getOpenWireQueue());
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

        MessageAck ack = new MessageAck();
        ack.setAckType(MessageAck.STANDARD_ACK_TYPE);
        ack.setConsumerId(consumerId.getConsumerId());
        ack.setDestination(getOpenWireQueue());
        ack.setLastMessageId(received.getMessageId());
        ack.setMessageCount(1);
        assertTrue(request(ack, 60, TimeUnit.SECONDS));
        assertEquals(0, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        assertTrue(request(connection.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getCurrentConnectionsCount());
        disconnect();

        return received;
    }

    public OpenWireBytesMessage receiveOpenWireBytesMessage() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        OpenWireConnection connection = new OpenWireConnection();
        ConnectionInfo connectionInfo = connection.createConnectionInfo();
        connectionInfo.setClientId(name.getMethodName());
        assertTrue(request(connectionInfo, 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        OpenWireSession sessionId = connection.createOpenWireSession();
        assertTrue(request(sessionId.createSessionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        OpenWireConsumer consumerId = sessionId.createOpenWireConsumer();
        ConsumerInfo consumerInfo = consumerId.createConsumerInfo(getOpenWireQueue());
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
        assertTrue(incoming instanceof OpenWireBytesMessage);
        OpenWireBytesMessage received = (OpenWireBytesMessage) incoming;

        MessageAck ack = new MessageAck();
        ack.setAckType(MessageAck.STANDARD_ACK_TYPE);
        ack.setConsumerId(consumerId.getConsumerId());
        ack.setDestination(getOpenWireQueue());
        ack.setLastMessageId(received.getMessageId());
        ack.setMessageCount(1);
        assertTrue(request(ack, 60, TimeUnit.SECONDS));
        assertEquals(0, getProxyToQueue(getOpenWireQueue().getPhysicalName()).getQueueSize());

        assertTrue(request(connection.createRemoveInfo(), 10, TimeUnit.SECONDS));
        assertEquals(0, brokerService.getAdminView().getCurrentConnectionsCount());
        disconnect();

        return received;
    }

    @Override
    protected int getOpenWireVersion() {
        return 10;
    }

    @Override
    protected boolean isTightEncodingEnabled() {
        return false;
    }
}
