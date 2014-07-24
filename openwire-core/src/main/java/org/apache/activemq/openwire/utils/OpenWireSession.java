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
package org.apache.activemq.openwire.utils;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.activemq.openwire.commands.ConnectionId;
import org.apache.activemq.openwire.commands.ConsumerId;
import org.apache.activemq.openwire.commands.ProducerId;
import org.apache.activemq.openwire.commands.RemoveInfo;
import org.apache.activemq.openwire.commands.SessionId;
import org.apache.activemq.openwire.commands.SessionInfo;

/**
 * Encapsulates an ActiveMQ compatible OpenWire Session ID and provides methods
 * for creating consumer and producer ID objects that are children of this session.
 */
public class OpenWireSession extends SessionInfo {

    private final AtomicLong consumerIdGenerator = new AtomicLong(1);
    private final AtomicLong producerIdGenerator = new AtomicLong(1);
    private final AtomicLong deliveryIdGenerator = new AtomicLong(1);

    /**
     * Creates a new OpenWireSessionId instance with the given ID.
     *
     * @param sessionId
     *        the SessionId assigned to this instance.
     */
    public OpenWireSession(SessionId sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Creates a new OpenWireSessionId instance based on the given ConnectionId
     * and a session sequence number.
     *
     * @param connectionId
     *        the ConnectionId to use for this Session ID.
     * @param sequence
     *        the sequence number that identifies this Session instance.
     */
    public OpenWireSession(ConnectionId connectionId, long sequence) {
        this(new SessionId(connectionId, sequence));
    }

    /**
     * @return the fixed SessionId of this OpenWireSessionId instance.
     */
    @Override
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * @return the next ConsumerId instance for the managed SessionId.
     */
    public ConsumerId getNextConsumerId() {
        return new ConsumerId(sessionId, consumerIdGenerator.getAndIncrement());
    }

    /**
     * @return the next ProducerId instance for the managed SessionId.
     */
    public ProducerId getNextProducerId() {
        return new ProducerId(sessionId, producerIdGenerator.getAndIncrement());
    }

    /**
     * @return the next Id to assign incoming message deliveries from the managed session Id.
     */
    public long getNextDeliveryId() {
        return this.deliveryIdGenerator.getAndIncrement();
    }

    @Override
    public String toString() {
        return sessionId.toString();
    }

    /**
     * Factory method used to create OpenWireConsumerId instances from this Session.
     *
     * @returns an OpenWireConsumerId rooted at this SessionId.
     */
    public OpenWireConsumer createOpenWireConsumer() {
        return new OpenWireConsumer(this, getNextConsumerId());
    }

    /**
     * Factory method used to create OpenWireProducerId instances from this Session.
     *
     * @returns an OpenWireProducerId rooted at this SessionId.
     */
    public OpenWireProducer createOpenWireProducer() {
        return new OpenWireProducer(this, getNextProducerId());
    }

    /**
     * Factory method for creating a SessionInfo to wrap the managed SessionId
     *
     * @returns a SessionInfo object that wraps the internal SessionId.
     */
    public SessionInfo createSessionInfo() {
        return new SessionInfo(getSessionId());
    }

    /**
     * Factory method for creating a suitable RemoveInfo for this session instance.
     *
     * @return a new RemoveInfo instance that can be used to remove this session.
     */
    public RemoveInfo createRemoveInfo() {
        return new RemoveInfo(getSessionId());
    }
}
