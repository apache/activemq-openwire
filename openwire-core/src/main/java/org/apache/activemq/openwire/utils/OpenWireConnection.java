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
import org.apache.activemq.openwire.commands.ConnectionInfo;
import org.apache.activemq.openwire.commands.ConsumerId;
import org.apache.activemq.openwire.commands.LocalTransactionId;
import org.apache.activemq.openwire.commands.RemoveInfo;
import org.apache.activemq.openwire.commands.SessionId;
import org.apache.activemq.openwire.commands.TransactionId;

/**
 * Encapsulates an ActiveMQ compatible OpenWire connection Id used to create instance
 * of ConnectionId objects and provides methods for creating OpenWireSession instances
 * that are children of this Connection.
 */
public class OpenWireConnection extends ConnectionInfo {

    private static final OpenWireIdGenerator idGenerator = new OpenWireIdGenerator();

    private SessionId connectionSessionId;

    private final AtomicLong sessionIdGenerator = new AtomicLong(1);
    private final AtomicLong consumerIdGenerator = new AtomicLong(1);
    private final AtomicLong tempDestinationIdGenerator = new AtomicLong(1);
    private final AtomicLong localTransactionIdGenerator = new AtomicLong(1);

    /**
     * Creates a fixed OpenWire Connection Id instance.
     */
    public OpenWireConnection() {
        this(idGenerator.generateId());
    }

    /**
     * Creates a fixed OpenWire Connection Id instance.
     *
     * @param connectionId
     *        the set ConnectionId value that this class will use to seed new Session IDs.
     */
    public OpenWireConnection(String connectionId) {
        this.connectionId = new ConnectionId(connectionId);
    }

    /**
     * Creates a fixed OpenWire Connection Id instance.
     *
     * @param connectionId
     *        the set ConnectionId value that this class will use to seed new Session IDs.
     */
    public OpenWireConnection(ConnectionId connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public ConnectionId getConnectionId() {
        return connectionId;
    }

    /**
     * @return the SessionId used for the internal Connection Session instance.
     */
    public SessionId getConnectionSessionId() {
        if (this.connectionSessionId == null) {
            synchronized (this) {
                if (this.connectionSessionId == null) {
                    this.connectionSessionId = new SessionId(connectionId, -1);
                }
            }
        }

        return this.connectionSessionId;
    }

    /**
     * Creates a new SessionId for a Session instance that is rooted by this Connection
     *
     * @return the next logical SessionId for this ConnectionId instance.
     */
    public SessionId getNextSessionId() {
        return new SessionId(connectionId, sessionIdGenerator.getAndIncrement());
    }

    /**
     * Creates a new Transaction ID used for local transactions created from this Connection.
     *
     * @return a new TransactionId instance.
     */
    public TransactionId getNextLocalTransactionId() {
        return new LocalTransactionId(connectionId, localTransactionIdGenerator.getAndIncrement());
    }

    /**
     * Create a new Consumer Id for ConnectionConsumer instances.
     *
     * @returns a new ConsumerId valid for use in ConnectionConsumer instances.
     */
    public ConsumerId getNextConnectionConsumerId() {
        return new ConsumerId(getConnectionSessionId(), consumerIdGenerator.getAndIncrement());
    }

    /**
     * Creates a new Temporary Destination name based on the Connection ID.
     *
     * @returns a new String destination name used to create temporary destinations.
     */
    public String getNextTemporaryDestinationName() {
        return connectionId.getValue() + ":" + tempDestinationIdGenerator.getAndIncrement();
    }

    /**
     * Factory method for creating a ConnectionInfo command that contains the connection
     * ID from this OpenWireConnection instance.
     *
     * @return a new ConnectionInfo that contains the proper connection Id.
     */
    public ConnectionInfo createConnectionInfo() {
        return this.copy();
    }

    /**
     * Factory method for creating a suitable RemoveInfo command that can be used to remove
     * this connection from a Broker.
     *
     * @return a new RemoveInfo that properly references this connection's Id.
     */
    public RemoveInfo createRemoveInfo() {
        return new RemoveInfo(getConnectionId());
    }

    /**
     * Factory method for OpenWireSession instances
     *
     * @return a new OpenWireSession with the next logical session ID for this connection.
     */
    public OpenWireSession createOpenWireSession() {
        return new OpenWireSession(connectionId, sessionIdGenerator.getAndIncrement());
    }
}
