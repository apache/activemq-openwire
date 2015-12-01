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

import static org.apache.activemq.openwire.codec.OpenWireConstants.ADIVSORY_MESSAGE_TYPE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.jms.JMSException;

import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireProperty;
import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.apache.activemq.openwire.utils.OpenWireMarshallingSupport;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * Represents an ActiveMQ message
 *
 * @openwire:marshaller
 */
@OpenWireType(typeCode = 0, marshalAware = true)
public abstract class Message extends BaseCommand implements MarshallAware {

    public static final String ORIGINAL_EXPIRATION = "originalExpiration";

    /**
     * The default minimum amount of memory a message is assumed to use
     */
    public static final int DEFAULT_MINIMUM_MESSAGE_SIZE = 1024;

    @OpenWireProperty(version = 1, sequence = 1, cached = true)
    protected ProducerId producerId;

    @OpenWireProperty(version = 1, sequence = 2, cached = true)
    protected OpenWireDestination destination;

    @OpenWireProperty(version = 1, sequence = 3, cached = true)
    protected TransactionId transactionId;

    @OpenWireProperty(version = 1, sequence = 4, cached = true)
    protected OpenWireDestination originalDestination;

    @OpenWireProperty(version = 1, sequence = 5)
    protected MessageId messageId;

    @OpenWireProperty(version = 1, sequence = 6, cached = true)
    protected TransactionId originalTransactionId;

    @OpenWireProperty(version = 1, sequence = 7)
    protected String groupId;

    @OpenWireProperty(version = 1, sequence = 8)
    protected int groupSequence;

    @OpenWireProperty(version = 1, sequence = 9)
    protected String correlationId;

    @OpenWireProperty(version = 1, sequence = 10)
    protected boolean persistent;

    @OpenWireProperty(version = 1, sequence = 11)
    protected long expiration;

    @OpenWireProperty(version = 1, sequence = 12)
    protected byte priority;

    @OpenWireProperty(version = 1, sequence = 13)
    protected OpenWireDestination replyTo;

    @OpenWireProperty(version = 1, sequence = 14)
    protected long timestamp;

    @OpenWireProperty(version = 1, sequence = 15)
    protected String type;

    @OpenWireProperty(version = 1, sequence = 16)
    protected Buffer content;

    @OpenWireProperty(version = 1, sequence = 17)
    protected Buffer marshalledProperties;

    @OpenWireProperty(version = 1, sequence = 18)
    protected DataStructure dataStructure;

    @OpenWireProperty(version = 1, sequence = 19, cached = true)
    protected ConsumerId targetConsumerId;

    @OpenWireProperty(version = 1, sequence = 20)
    protected boolean compressed;

    @OpenWireProperty(version = 1, sequence = 21)
    protected int redeliveryCounter;

    @OpenWireProperty(version = 1, sequence = 22, cached = true)
    private BrokerId[] brokerPath;

    @OpenWireProperty(version = 1, sequence = 23)
    protected long arrival;

    @OpenWireProperty(version = 1, sequence = 24)
    protected String userId;

    @OpenWireProperty(version = 1, sequence = 25, serialized = false)
    protected transient boolean recievedByDFBridge;

    @OpenWireProperty(version = 2, sequence = 26, cached = true)
    protected boolean droppable;

    @OpenWireProperty(version = 3, sequence = 27, cached = true)
    private BrokerId[] cluster;

    @OpenWireProperty(version = 3, sequence = 28)
    protected long brokerInTime;

    @OpenWireProperty(version = 3, sequence = 29)
    protected long brokerOutTime;

    @OpenWireProperty(version = 10, sequence = 30)
    protected boolean jmsXGroupFirstForConsumer;

    @OpenWireExtension(serialized = true)
    protected int size;

    @OpenWireExtension(serialized = true)
    protected Map<String, Object> properties;

    public abstract Message copy();
    public abstract void clearBody() throws JMSException;
    public abstract void storeContent();
    public abstract void storeContentAndClear();

    // useful to reduce the memory footprint of a persisted message
    public void clearMarshalledState() throws JMSException {
        properties = null;
    }

    protected void copy(Message copy) {
        super.copy(copy);
        copy.producerId = producerId;
        copy.transactionId = transactionId;
        copy.destination = destination;
        copy.messageId = messageId != null ? messageId.copy() : null;
        copy.originalDestination = originalDestination;
        copy.originalTransactionId = originalTransactionId;
        copy.expiration = expiration;
        copy.timestamp = timestamp;
        copy.correlationId = correlationId;
        copy.replyTo = replyTo;
        copy.persistent = persistent;
        copy.redeliveryCounter = redeliveryCounter;
        copy.type = type;
        copy.priority = priority;
        copy.size = size;
        copy.groupId = groupId;
        copy.userId = userId;
        copy.groupSequence = groupSequence;

        if (properties != null) {
            copy.properties = new HashMap<String, Object>(properties);

            // The new message hasn't expired, so remove this feild.
            copy.properties.remove(ORIGINAL_EXPIRATION);
        } else {
            copy.properties = properties;
        }

        copy.content = content;
        copy.marshalledProperties = marshalledProperties;
        copy.dataStructure = dataStructure;
        copy.compressed = compressed;
        copy.recievedByDFBridge = recievedByDFBridge;

        copy.arrival = arrival;
        copy.brokerInTime = brokerInTime;
        copy.brokerOutTime = brokerOutTime;
        copy.brokerPath = brokerPath;
        copy.jmsXGroupFirstForConsumer = jmsXGroupFirstForConsumer;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProperties() throws IOException {
        if (properties == null) {
            if (marshalledProperties == null) {
                return Collections.EMPTY_MAP;
            }
            properties = unmarsallProperties(marshalledProperties);
        }
        return Collections.unmodifiableMap(properties);
    }

    public void clearProperties() throws JMSException {
        marshalledProperties = null;
        properties = null;
    }

    public Object getProperty(String name) throws JMSException {
        if (properties == null) {
            if (marshalledProperties == null) {
                return null;
            }
            try {
                properties = unmarsallProperties(marshalledProperties);
            } catch (IOException e) {
                throw ExceptionSupport.create("Error during properties unmarshal, reason: " + e.getMessage(), e);
            }
        }
        Object result = properties.get(name);
        if (result instanceof UTF8Buffer) {
            result = result.toString();
        }

        return result;
    }

    public void setProperty(String name, Object value) throws JMSException {
        lazyCreateProperties();
        properties.put(name, value);
    }

    public void removeProperty(String name) throws JMSException {
        lazyCreateProperties();
        properties.remove(name);
    }

    protected void lazyCreateProperties() throws JMSException {
        if (properties == null) {
            if (marshalledProperties == null) {
                properties = new HashMap<String, Object>();
            } else {
                try {
                    properties = unmarsallProperties(marshalledProperties);
                } catch (IOException e) {
                    throw ExceptionSupport.create(
                        "Error during properties unmarshal, reason: " + e.getMessage(), e);
                }
                marshalledProperties = null;
            }
        } else {
            marshalledProperties = null;
        }
    }

    private Map<String, Object> unmarsallProperties(Buffer marshalledProperties) throws IOException {
        return OpenWireMarshallingSupport.unmarshalPrimitiveMap(new DataInputStream(new ByteArrayInputStream(marshalledProperties)));
    }

    @Override
    public void beforeMarshall(OpenWireFormat wireFormat) throws IOException {
        // Need to marshal the properties.
        if (marshalledProperties == null && properties != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            OpenWireMarshallingSupport.marshalPrimitiveMap(properties, os);
            os.close();
            marshalledProperties = baos.toBuffer();
        }
    }

    @Override
    public void afterMarshall(OpenWireFormat wireFormat) throws IOException {
    }

    @Override
    public void beforeUnmarshall(OpenWireFormat wireFormat) throws IOException {
    }

    @Override
    public void afterUnmarshall(OpenWireFormat wireFormat) throws IOException {
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ProducerId getProducerId() {
        return producerId;
    }

    public void setProducerId(ProducerId producerId) {
        this.producerId = producerId;
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
     * @openwire:property version=1 cache=true
     */
    public TransactionId getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isInTransaction() {
        return transactionId != null;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public OpenWireDestination getOriginalDestination() {
        return originalDestination;
    }

    public void setOriginalDestination(OpenWireDestination destination) {
        this.originalDestination = destination;
    }

    /**
     * @openwire:property version=1
     */
    public MessageId getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public TransactionId getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(TransactionId transactionId) {
        this.originalTransactionId = transactionId;
    }

    /**
     * @openwire:property version=1
     */
    public String getGroupId() {
        return groupId;
    }

    public void setGroupID(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @openwire:property version=1
     */
    public int getGroupSequence() {
        return groupSequence;
    }

    public void setGroupSequence(int groupSequence) {
        this.groupSequence = groupSequence;
    }

    /**
     * @openwire:property version=1
     */
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * @openwire:property version=1
     */
    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean deliveryMode) {
        this.persistent = deliveryMode;
    }

    /**
     * @openwire:property version=1
     */
    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * @openwire:property version=1
     */
    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        if (priority < 0) {
            this.priority = 0;
        } else if (priority > 9) {
            this.priority = 9;
        } else {
            this.priority = priority;
        }
    }

    /**
     * @openwire:property version=1
     */
    public OpenWireDestination getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(OpenWireDestination replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * @openwire:property version=1
     */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @openwire:property version=1
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @openwire:property version=1
     */
    public Buffer getContent() {
        return content;
    }

    public void setContent(Buffer content) {
        this.content = content;
        if (content == null) {
            compressed = false;
        }
    }

    /**
     * @openwire:property version=1
     */
    public Buffer getMarshalledProperties() {
        return marshalledProperties;
    }

    public void setMarshalledProperties(Buffer marshalledProperties) {
        this.marshalledProperties = marshalledProperties;
    }

    /**
     * @openwire:property version=1
     */
    public DataStructure getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(DataStructure data) {
        this.dataStructure = data;
    }

    /**
     * Can be used to route the message to a specific consumer. Should be null
     * to allow the broker use normal JMS routing semantics. If the target
     * consumer id is an active consumer on the broker, the message is dropped.
     * Used by the AdvisoryBroker to replay advisory messages to a specific
     * consumer.
     *
     * @openwire:property version=1 cache=true
     */
    public ConsumerId getTargetConsumerId() {
        return targetConsumerId;
    }

    public void setTargetConsumerId(ConsumerId targetConsumerId) {
        this.targetConsumerId = targetConsumerId;
    }

    public boolean isExpired() {
        long expireTime = getExpiration();
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public boolean isAdvisory() {
        return type != null && type.equals(ADIVSORY_MESSAGE_TYPE);
    }

    /**
     * @openwire:property version=1
     */
    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public boolean isRedelivered() {
        return redeliveryCounter > 0;
    }

    public void setRedelivered(boolean redelivered) {
        if (redelivered) {
            if (!isRedelivered()) {
                setRedeliveryCounter(1);
            }
        } else {
            if (isRedelivered()) {
                setRedeliveryCounter(0);
            }
        }
    }

    /**
     * @openwire:property version=1
     */
    public int getRedeliveryCounter() {
        return redeliveryCounter;
    }

    public void setRedeliveryCounter(int deliveryCounter) {
        this.redeliveryCounter = deliveryCounter;
    }

    /**
     * The route of brokers the command has moved through.
     *
     * @openwire:property version=1 cache=true
     */
    public BrokerId[] getBrokerPath() {
        return brokerPath;
    }

    public void setBrokerPath(BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }

    /**
     * Used to schedule the arrival time of a message to a broker. The broker
     * will not dispatch a message to a consumer until it's arrival time has
     * elapsed.
     *
     * @openwire:property version=1
     */
    public long getArrival() {
        return arrival;
    }

    public void setArrival(long arrival) {
        this.arrival = arrival;
    }

    /**
     * Only set by the broker and defines the userID of the producer connection
     * who sent this message. This is an optional field, it needs to be enabled
     * on the broker to have this field populated.
     *
     * @openwire:property version=1
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String jmsxUserId) {
        this.userId = jmsxUserId;
    }

    @Override
    public boolean isMarshallAware() {
        return true;
    }

    public int getSize() {
        int minimumMessageSize = DEFAULT_MINIMUM_MESSAGE_SIZE;
        if (size < minimumMessageSize || size == 0) {
            size = minimumMessageSize;
            if (marshalledProperties != null) {
                size += marshalledProperties.getLength();
            }
            if (content != null) {
                size += content.getLength();
            }
        }
        return size;
    }

    /**
     * @openwire:property version=1
     * @return Returns the recievedByDFBridge.
     */
    public boolean isRecievedByDFBridge() {
        return recievedByDFBridge;
    }

    /**
     * @param recievedByDFBridge The recievedByDFBridge to set.
     */
    public void setRecievedByDFBridge(boolean recievedByDFBridge) {
        this.recievedByDFBridge = recievedByDFBridge;
    }

    /**
     * @openwire:property version=2 cache=true
     */
    public boolean isDroppable() {
        return droppable;
    }

    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
    }

    /**
     * If a message is stored in multiple nodes on a cluster, all the cluster
     * members will be listed here. Otherwise, it will be null.
     *
     * @openwire:property version=3 cache=true
     */
    public BrokerId[] getCluster() {
        return cluster;
    }

    public void setCluster(BrokerId[] cluster) {
        this.cluster = cluster;
    }

    @Override
    public boolean isMessage() {
        return true;
    }

    /**
     * @openwire:property version=3
     */
    public long getBrokerInTime() {
        return this.brokerInTime;
    }

    public void setBrokerInTime(long brokerInTime) {
        this.brokerInTime = brokerInTime;
    }

    /**
     * @openwire:property version=3
     */
    public long getBrokerOutTime() {
        return this.brokerOutTime;
    }

    public void setBrokerOutTime(long brokerOutTime) {
        this.brokerOutTime = brokerOutTime;
    }

    /**
     * @openwire:property version=10
     */
    public boolean isJMSXGroupFirstForConsumer() {
        return jmsXGroupFirstForConsumer;
    }

    public void setJMSXGroupFirstForConsumer(boolean val) {
        jmsXGroupFirstForConsumer = val;
    }

    /**
     * For a Message that is not currently using compression in its message body this
     * method will initiate a store of current content and then compress the data in
     * the message body.
     *
     * @throws IOException if an error occurs during the compression process.
     */
    public void compress() throws IOException {
        if (!isCompressed()) {
            storeContent();
            if (!isCompressed() && getContent() != null) {
                doCompress();
            }
        }
    }

    /**
     * For a message whose body is compressed this method will perform a full decompression
     * of the contents and return the resulting uncompressed buffer, if the contents are not
     * compressed then they are returned unchanged.
     *
     * @return a Buffer instance that contains the message contents, uncompressed if needed.
     *
     * @throws IOException if an error occurs during decompression of the message contents.
     */
    public Buffer decompress() throws IOException {
        if (isCompressed()) {
            return doDecompress();
        } else {
            return content;
        }
    }

    protected Buffer doDecompress() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(this.content.getData(), this.content.getOffset(), this.content.getLength());
        InflaterInputStream inflater = new InflaterInputStream(input);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8*1024];
            int read = 0;
            while ((read = inflater.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } finally {
            inflater.close();
            output.close();
        }

        return output.toBuffer();
    }

    protected void doCompress() throws IOException {
        compressed = true;
        Buffer bytes = getContent();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        OutputStream os = new DeflaterOutputStream(bytesOut);
        os.write(bytes.data, bytes.offset, bytes.length);
        os.close();
        setContent(bytesOut.toBuffer());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + messageId + " }";
    }
}
