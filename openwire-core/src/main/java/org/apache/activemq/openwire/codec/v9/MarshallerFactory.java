/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.codec.v9;

import org.apache.activemq.openwire.codec.DataStreamMarshaller;
import org.apache.activemq.openwire.codec.OpenWireFormat;

public class MarshallerFactory {

    /**
     * Creates a Map of command type -> Marshallers
     */
    static final private DataStreamMarshaller marshaller[] = new DataStreamMarshaller[256];
    static {

        add(new OpenWireBlobMessageMarshaller());
        add(new OpenWireBytesMessageMarshaller());
        add(new OpenWireMapMessageMarshaller());
        add(new OpenWireMessageMarshaller());
        add(new OpenWireObjectMessageMarshaller());
        add(new OpenWireQueueMarshaller());
        add(new OpenWireStreamMessageMarshaller());
        add(new OpenWireTempQueueMarshaller());
        add(new OpenWireTempTopicMarshaller());
        add(new OpenWireTextMessageMarshaller());
        add(new OpenWireTopicMarshaller());
        add(new BrokerIdMarshaller());
        add(new BrokerInfoMarshaller());
        add(new ConnectionControlMarshaller());
        add(new ConnectionErrorMarshaller());
        add(new ConnectionIdMarshaller());
        add(new ConnectionInfoMarshaller());
        add(new ConsumerControlMarshaller());
        add(new ConsumerIdMarshaller());
        add(new ConsumerInfoMarshaller());
        add(new ControlCommandMarshaller());
        add(new DataArrayResponseMarshaller());
        add(new DataResponseMarshaller());
        add(new DestinationInfoMarshaller());
        add(new DiscoveryEventMarshaller());
        add(new ExceptionResponseMarshaller());
        add(new FlushCommandMarshaller());
        add(new IntegerResponseMarshaller());
        add(new JournalQueueAckMarshaller());
        add(new JournalTopicAckMarshaller());
        add(new JournalTraceMarshaller());
        add(new JournalTransactionMarshaller());
        add(new KeepAliveInfoMarshaller());
        add(new LastPartialCommandMarshaller());
        add(new LocalTransactionIdMarshaller());
        add(new MessageAckMarshaller());
        add(new MessageDispatchMarshaller());
        add(new MessageDispatchNotificationMarshaller());
        add(new MessageIdMarshaller());
        add(new MessagePullMarshaller());
        add(new NetworkBridgeFilterMarshaller());
        add(new PartialCommandMarshaller());
        add(new ProducerAckMarshaller());
        add(new ProducerIdMarshaller());
        add(new ProducerInfoMarshaller());
        add(new RemoveInfoMarshaller());
        add(new RemoveSubscriptionInfoMarshaller());
        add(new ReplayCommandMarshaller());
        add(new ResponseMarshaller());
        add(new SessionIdMarshaller());
        add(new SessionInfoMarshaller());
        add(new ShutdownInfoMarshaller());
        add(new SubscriptionInfoMarshaller());
        add(new TransactionInfoMarshaller());
        add(new WireFormatInfoMarshaller());
        add(new XATransactionIdMarshaller());
    }

    static private void add(DataStreamMarshaller dsm) {
        marshaller[dsm.getDataStructureType()] = dsm;
    }

    static public DataStreamMarshaller[] createMarshallerMap(OpenWireFormat wireFormat) {
        return marshaller;
    }
}
