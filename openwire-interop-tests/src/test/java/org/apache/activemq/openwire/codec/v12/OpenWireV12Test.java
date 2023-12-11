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
package org.apache.activemq.openwire.codec.v12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.openwire.codec.OpenWireInteropTests;
import org.apache.activemq.openwire.commands.BrokerId;
import org.apache.activemq.openwire.commands.BrokerSubscriptionInfo;
import org.apache.activemq.openwire.commands.ConsumerInfo;
import org.apache.activemq.openwire.utils.OpenWireSession;
import org.apache.activemq.transport.TransportListener;
import org.junit.Test;

public class OpenWireV12Test extends OpenWireInteropTests {

    /**
     * @param tightEncodingEnabled
     */
    public OpenWireV12Test(boolean tightEncodingEnabled) {
        super(tightEncodingEnabled);
    }

    @Test(timeout = 60000)
    public void testBrokerSubscriptionInfo() throws Exception {
        connect();
        assertTrue(awaitConnected(10, TimeUnit.SECONDS));
        assertTrue(request(createConnectionInfo(), 10, TimeUnit.SECONDS));
        assertEquals(1, brokerService.getAdminView().getCurrentConnectionsCount());

        // Should be 1 transport connector and 1 connection
        assertEquals(1, brokerService.getTransportConnectors().size());
        TransportConnector connector = brokerService.getTransportConnectors().stream().findFirst().orElseThrow();
        assertEquals(1, connector.getConnections().size());
        TransportConnection connection = connector.getConnections().stream().findFirst().orElseThrow();

        // Set a custom transport listener on the broker side to capture the legacy version
        // of BrokerSubscriptionInfo that we publish with the new marshaller
        final CountDownLatch received = new CountDownLatch(1);
        BrokerSubscriptionInfoReceiver receiver = new BrokerSubscriptionInfoReceiver(received);
        connection.getTransport().setTransportListener(receiver);
        assertNull(receiver.brokerSubInfo.get());

        // Generate a couple consumer infos
        OpenWireSession sessionId = connectionId.createOpenWireSession();
        ConsumerInfo ci1 = new ConsumerInfo(sessionId.getNextConsumerId());
        ci1.setDispatchAsync(true);
        ci1.setPrefetchSize(10);
        ConsumerInfo ci2 = new ConsumerInfo(sessionId.getNextConsumerId());
        ci2.setDispatchAsync(false);
        ci2.setPrefetchSize(20);

        // Create BrokerSubscriptionInfo and send using new marshallers
        BrokerSubscriptionInfo info = new BrokerSubscriptionInfo();
        info.setBrokerName("testBrokerName");
        info.setBrokerId(new BrokerId("testBrokerId"));
        info.setSubscriptionInfos(new ConsumerInfo[]{ ci1, ci2 });
        requestNoResponse(info);
        assertTrue(received.await(10, TimeUnit.SECONDS));

        // Verify legacy object converted with legacy marshallers match
        org.apache.activemq.command.BrokerSubscriptionInfo brokerSubInfo = receiver.brokerSubInfo.get();
        assertNotNull(brokerSubInfo);
        assertEquals("testBrokerName", brokerSubInfo.getBrokerName());
        assertEquals("testBrokerId", brokerSubInfo.getBrokerId().getValue());
        assertEquals(2, brokerSubInfo.getSubscriptionInfos().length);
        org.apache.activemq.command.ConsumerInfo legacy1 = brokerSubInfo.getSubscriptionInfos()[0];
        org.apache.activemq.command.ConsumerInfo legacy2 = brokerSubInfo.getSubscriptionInfos()[1];

        // Verify first consumer info received
        assertEquals(ci1.getConsumerId().getSessionId(), legacy1.getConsumerId().getSessionId());
        assertEquals(ci1.getConsumerId().getValue(), legacy1.getConsumerId().getValue());
        assertEquals(ci1.isDispatchAsync(), legacy1.isDispatchAsync());
        assertEquals(ci1.getPrefetchSize(), legacy1.getPrefetchSize());

        // Verify second consumer info received
        assertEquals(ci2.getConsumerId().getSessionId(), legacy2.getConsumerId().getSessionId());
        assertEquals(ci2.getConsumerId().getValue(), legacy2.getConsumerId().getValue());
        assertEquals(ci2.isDispatchAsync(), legacy2.isDispatchAsync());
        assertEquals(ci2.getPrefetchSize(), legacy2.getPrefetchSize());
    }

    @Override
    protected int getOpenWireVersion() {
        return 12;
    }

    private static class BrokerSubscriptionInfoReceiver implements TransportListener {
        private final CountDownLatch received;
        private final AtomicReference<org.apache.activemq.command.BrokerSubscriptionInfo> brokerSubInfo
            = new AtomicReference<>();

        private BrokerSubscriptionInfoReceiver(CountDownLatch received) {
            this.received = received;
        }

        @Override
        public void onCommand(Object o) {
            if (o instanceof org.apache.activemq.command.BrokerSubscriptionInfo) {
                brokerSubInfo.compareAndSet(null,
                    (org.apache.activemq.command.BrokerSubscriptionInfo) o);
                received.countDown();
            }
        }

        @Override
        public void onException(IOException e) { }

        @Override
        public void transportInterupted() { }

        @Override
        public void transportResumed() { }
    }
}
