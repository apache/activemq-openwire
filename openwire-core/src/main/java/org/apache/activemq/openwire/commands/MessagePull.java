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

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireProperty;

/**
 * Used to pull messages on demand, the command can have a time value that indicates
 * how long the Broker keeps the pull request open before returning a MessageDispatch
 * with a null payload.
 *
 * @openwire:marshaller code="20"
 */
@OpenWireType(typeCode = 20)
public class MessagePull extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.MESSAGE_PULL;

    @OpenWireProperty(version = 1, sequence = 1, cached = true)
    protected ConsumerId consumerId;

    @OpenWireProperty(version = 1, sequence = 2, cached = true)
    protected OpenWireDestination destination;

    @OpenWireProperty(version = 1, sequence = 3)
    protected long timeout;

    @OpenWireProperty(version = 3, sequence = 4)
    private String correlationId;

    @OpenWireProperty(version = 4, sequence = 5)
    private MessageId messageId;

    @OpenWireExtension
    private transient boolean tracked = false;

    @OpenWireExtension
    private transient int quantity = 1;

    @OpenWireExtension
    private transient boolean alwaysSignalDone;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processMessagePull(this);
    }

    /**
     * Configures a message pull from the consumer information
     */
    public void configure(ConsumerInfo info) {
        setConsumerId(info.getConsumerId());
        setDestination(info.getDestination());
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ConsumerId getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(ConsumerId consumerId) {
        this.consumerId = consumerId;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public OpenWireDestination getDestination() {
        return destination;
    }

    public void setDestination(OpenWireDestination destination) {
        this.destination = destination;
    }

    /**
     * @openwire:property version=1
     */
    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * An optional correlation ID which could be used by a broker to decide which messages are pulled
     * on demand from a queue for a consumer
     *
     * @openwire:property version=3
     */
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * An optional message ID which could be used by a broker to decide which messages are pulled
     * on demand from a queue for a consumer
     *
     * @openwire:property version=3
     */
    public MessageId getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    public boolean isTracked() {
        return this.tracked;
    }
}
