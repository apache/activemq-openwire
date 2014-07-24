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

import org.apache.activemq.openwire.commands.MessageId;
import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.ProducerId;
import org.apache.activemq.openwire.commands.ProducerInfo;
import org.apache.activemq.openwire.commands.RemoveInfo;
import org.apache.activemq.openwire.commands.SessionId;

/**
 * Encapsulates an ActiveMQ compatible OpenWire Producer ID and provides
 * functionality used to generate message IDs for the producer.
 */
public class OpenWireProducer extends ProducerInfo {

    private final OpenWireSession parent;

    private final AtomicLong messageSequence = new AtomicLong(1);

    /**
     * Creates a new instance with the given parent Session Id and assigned Producer Id
     *
     * @param parent
     *        the OpenWireSessionId that is the parent of the new Producer.
     * @param producerId
     *        the ProducerId assigned to this instance.
     */
    public OpenWireProducer(OpenWireSession parent, ProducerId producerId) {
        super(producerId);
        this.parent = parent;
    }

    /**
     * Creates a new instance with the given parent Session Id and copy the given ProducerInfo
     *
     * @param parent
     *        the OpenWireSessionId that is the parent of the new Producer.
     * @param producerInfo
     *        the ProducerInfo used to populate this instance.
     */
    public OpenWireProducer(OpenWireSession parent, ProducerInfo producerInfo) {
        this.parent = parent;
        producerInfo.copy(this);
    }

    /**
     * @return the SessionId of this ProducerId instance.
     */
    public SessionId getSessionId() {
        return this.parent.getSessionId();
    }

    /**
     * @return the parent OpenWireSessionId
     */
    public OpenWireSession getParent() {
        return parent;
    }

    /**
     * Factory method used to simplify creation of MessageIds from this Producer
     *
     * @return the next logical MessageId for the producer this instance represents.
     */
    public MessageId getNextMessageId() {
        return new MessageId(producerId, messageSequence.getAndIncrement());
    }

    @Override
    public String toString() {
        return producerId.toString();
    }

    /**
     * Factory method for creating a ProducerInfo to wrap this instance's ProducerId.
     *
     * @return a new ProducerInfo instance that can be used to register a remote producer.
     */
    public ProducerInfo createProducerInfo() {
        return this.copy();
    }

    /**
     * Factory method for creating a ProducerInfo to wrap this instance's ProducerId.
     *
     * @param destination
     *        the target destination for this ProducerInfo instance.
     *
     * @return a new ProducerInfo instance that can be used to register a remote producer.
     */
    public ProducerInfo createProducerInfo(OpenWireDestination destination) {
        this.setDestination(destination);
        return this.copy();
    }

    /**
     * Factory method for creating a RemoveInfo command that can be used to remove this
     * producer instance from the Broker.
     *
     * @return a new RemoveInfo instance that can remove this producer.
     */
    public RemoveInfo createRemoveInfo() {
        return new RemoveInfo(getProducerId());
    }
}
