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
package org.apache.activemq.openwire.codec.v1;

import org.apache.activemq.openwire.codec.DataStreamMarshaller;
import org.apache.activemq.openwire.codec.OpenWireFormat;

public final class MarshallerFactory {

    /**
     * Creates a Map of command type -> Marshalers
     */
    private static final DataStreamMarshaller MARSHALLER[] = new DataStreamMarshaller[256];
    static {

        add(new LocalTransactionIdMarshaller());
        add(new PartialCommandMarshaller());
        add(new IntegerResponseMarshaller());
        add(new OpenWireQueueMarshaller());
        add(new OpenWireObjectMessageMarshaller());
        add(new ConnectionIdMarshaller());
        add(new ConnectionInfoMarshaller());
        add(new ProducerInfoMarshaller());
        add(new MessageDispatchNotificationMarshaller());
        add(new SessionInfoMarshaller());
        add(new TransactionInfoMarshaller());
        add(new OpenWireStreamMessageMarshaller());
        add(new MessageAckMarshaller());
        add(new ProducerIdMarshaller());
        add(new MessageIdMarshaller());
        add(new OpenWireTempQueueMarshaller());
        add(new RemoveSubscriptionInfoMarshaller());
        add(new SessionIdMarshaller());
        add(new DataArrayResponseMarshaller());
        add(new JournalQueueAckMarshaller());
        add(new ResponseMarshaller());
        add(new ConnectionErrorMarshaller());
        add(new ConsumerInfoMarshaller());
        add(new XATransactionIdMarshaller());
        add(new JournalTraceMarshaller());
        add(new ConsumerIdMarshaller());
        add(new OpenWireTextMessageMarshaller());
        add(new SubscriptionInfoMarshaller());
        add(new JournalTransactionMarshaller());
        add(new ControlCommandMarshaller());
        add(new LastPartialCommandMarshaller());
        add(new NetworkBridgeFilterMarshaller());
        add(new OpenWireBytesMessageMarshaller());
        add(new WireFormatInfoMarshaller());
        add(new OpenWireTempTopicMarshaller());
        add(new DiscoveryEventMarshaller());
        add(new ReplayCommandMarshaller());
        add(new OpenWireTopicMarshaller());
        add(new BrokerInfoMarshaller());
        add(new DestinationInfoMarshaller());
        add(new ShutdownInfoMarshaller());
        add(new DataResponseMarshaller());
        add(new ConnectionControlMarshaller());
        add(new KeepAliveInfoMarshaller());
        add(new FlushCommandMarshaller());
        add(new ConsumerControlMarshaller());
        add(new JournalTopicAckMarshaller());
        add(new BrokerIdMarshaller());
        add(new MessageDispatchMarshaller());
        add(new OpenWireMapMessageMarshaller());
        add(new OpenWireMessageMarshaller());
        add(new RemoveInfoMarshaller());
        add(new ExceptionResponseMarshaller());
    }

    private MarshallerFactory() {
    }

    private static void add(DataStreamMarshaller dsm) {
        MARSHALLER[dsm.getDataStructureType()] = dsm;
    }

    public static DataStreamMarshaller[] createMarshallerMap(OpenWireFormat wireFormat) {
        return MARSHALLER;
    }
}
