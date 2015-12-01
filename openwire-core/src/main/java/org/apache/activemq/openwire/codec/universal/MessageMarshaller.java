/*
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
package org.apache.activemq.openwire.codec.universal;

import org.fusesource.hawtbuf.Buffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.activemq.openwire.codec.*;
import org.apache.activemq.openwire.commands.*;

/**
 * Marshalling code for Open Wire for Message
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public abstract class MessageMarshaller extends BaseCommandMarshaller {

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param target the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @param bs the boolean stream where the type's booleans were marshaled
     *
     * @throws IOException if an error occurs while reading the data
     */
    public void tightUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, target, dataIn, bs);

        Message info = (Message) target;
        int version = wireFormat.getVersion();

        info.beforeUnmarshall(wireFormat);
        info.setProducerId((ProducerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((OpenWireDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setTransactionId((TransactionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setOriginalDestination((OpenWireDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setMessageId((MessageId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setOriginalTransactionId((TransactionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setGroupID(tightUnmarshalString(dataIn, bs));
        info.setGroupSequence(dataIn.readInt());
        info.setCorrelationId(tightUnmarshalString(dataIn, bs));
        info.setPersistent(bs.readBoolean());
        info.setExpiration(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setPriority(dataIn.readByte());
        info.setReplyTo((OpenWireDestination) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setTimestamp(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setType(tightUnmarshalString(dataIn, bs));
        info.setContent(tightUnmarshalByteSequence(dataIn, bs));
        info.setMarshalledProperties(tightUnmarshalByteSequence(dataIn, bs));
        info.setDataStructure((DataStructure) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setTargetConsumerId((ConsumerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setCompressed(bs.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());
        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
        }
        info.setArrival(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setUserId(tightUnmarshalString(dataIn, bs));
        info.setRecievedByDFBridge(bs.readBoolean());
        if (version >= 2) {
            info.setDroppable(bs.readBoolean());
        }
        if (version >= 3) {
            if (bs.readBoolean()) {
                short size = dataIn.readShort();
                BrokerId value[] = new BrokerId[size];
                for (int i = 0; i < size; i++) {
                    value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
                }
                info.setCluster(value);
            } else {
                info.setCluster(null);
            }
        }
        if (version >= 3) {
            info.setBrokerInTime(tightUnmarshalLong(wireFormat, dataIn, bs));
        }
        if (version >= 3) {
            info.setBrokerOutTime(tightUnmarshalLong(wireFormat, dataIn, bs));
        }
        if (version >= 10) {
            info.setJMSXGroupFirstForConsumer(bs.readBoolean());
        }

        info.afterUnmarshall(wireFormat);
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param source the object to marshal
     * @param bs the boolean stream where the type's booleans are written
     *
     * @throws IOException if an error occurs while writing the data
     */
    public int tightMarshal1(OpenWireFormat wireFormat, Object source, BooleanStream bs) throws IOException {
        Message info = (Message) source;
        int version = wireFormat.getVersion();

        info.beforeMarshall(wireFormat);

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getProducerId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getTransactionId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getOriginalDestination(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getMessageId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getOriginalTransactionId(), bs);
        rc += tightMarshalString1(info.getGroupId(), bs);
        rc += tightMarshalString1(info.getCorrelationId(), bs);
        bs.writeBoolean(info.isPersistent());
        rc += tightMarshalLong1(wireFormat, info.getExpiration(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getReplyTo(), bs);
        rc += tightMarshalLong1(wireFormat, info.getTimestamp(), bs);
        rc += tightMarshalString1(info.getType(), bs);
        rc += tightMarshalByteSequence1(info.getContent(), bs);
        rc += tightMarshalByteSequence1(info.getMarshalledProperties(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getDataStructure(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getTargetConsumerId(), bs);
        bs.writeBoolean(info.isCompressed());
        rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        rc += tightMarshalLong1(wireFormat, info.getArrival(), bs);
        rc += tightMarshalString1(info.getUserId(), bs);
        bs.writeBoolean(info.isRecievedByDFBridge());
        if (version >= 2) {
            bs.writeBoolean(info.isDroppable());
        }
        if (version >= 3) {
            rc += tightMarshalObjectArray1(wireFormat, info.getCluster(), bs);
        }
        if (version >= 3) {
            rc += tightMarshalLong1(wireFormat, info.getBrokerInTime(), bs);
        }
        if (version >= 3) {
            rc += tightMarshalLong1(wireFormat, info.getBrokerOutTime(), bs);
        }
        if (version >= 10) {
            bs.writeBoolean(info.isJMSXGroupFirstForConsumer());
        }

        return rc + 9;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param source the object to marshal
     * @param dataOut the DataOut where the properties are written
     * @param bs the boolean stream where the type's booleans are written
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void tightMarshal2(OpenWireFormat wireFormat, Object source, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, source, dataOut, bs);

        Message info = (Message) source;
        int version = wireFormat.getVersion();

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getProducerId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getTransactionId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getOriginalDestination(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getMessageId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getOriginalTransactionId(), dataOut, bs);
        tightMarshalString2(info.getGroupId(), dataOut, bs);
        dataOut.writeInt(info.getGroupSequence());
        tightMarshalString2(info.getCorrelationId(), dataOut, bs);
        bs.readBoolean();
        tightMarshalLong2(wireFormat, info.getExpiration(), dataOut, bs);
        dataOut.writeByte(info.getPriority());
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getReplyTo(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getTimestamp(), dataOut, bs);
        tightMarshalString2(info.getType(), dataOut, bs);
        tightMarshalByteSequence2(info.getContent(), dataOut, bs);
        tightMarshalByteSequence2(info.getMarshalledProperties(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getDataStructure(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getTargetConsumerId(), dataOut, bs);
        bs.readBoolean();
        dataOut.writeInt(info.getRedeliveryCounter());
        tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getArrival(), dataOut, bs);
        tightMarshalString2(info.getUserId(), dataOut, bs);
        bs.readBoolean();
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 3) {
            tightMarshalObjectArray2(wireFormat, info.getCluster(), dataOut, bs);
        }
        if (version >= 3) {
            tightMarshalLong2(wireFormat, info.getBrokerInTime(), dataOut, bs);
        }
        if (version >= 3) {
            tightMarshalLong2(wireFormat, info.getBrokerOutTime(), dataOut, bs);
        }
        if (version >= 10) {
            bs.readBoolean();
        }

        info.afterMarshall(wireFormat);
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        Message info = (Message) source;
        int version = wireFormat.getVersion();

        info.beforeMarshall(wireFormat);
        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getProducerId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getTransactionId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getOriginalDestination(), dataOut);
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getMessageId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getOriginalTransactionId(), dataOut);
        looseMarshalString(info.getGroupId(), dataOut);
        dataOut.writeInt(info.getGroupSequence());
        looseMarshalString(info.getCorrelationId(), dataOut);
        dataOut.writeBoolean(info.isPersistent());
        looseMarshalLong(wireFormat, info.getExpiration(), dataOut);
        dataOut.writeByte(info.getPriority());
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getReplyTo(), dataOut);
        looseMarshalLong(wireFormat, info.getTimestamp(), dataOut);
        looseMarshalString(info.getType(), dataOut);
        looseMarshalByteSequence(wireFormat, info.getContent(), dataOut);
        looseMarshalByteSequence(wireFormat, info.getMarshalledProperties(), dataOut);
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getDataStructure(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getTargetConsumerId(), dataOut);
        dataOut.writeBoolean(info.isCompressed());
        dataOut.writeInt(info.getRedeliveryCounter());
        looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        looseMarshalLong(wireFormat, info.getArrival(), dataOut);
        looseMarshalString(info.getUserId(), dataOut);
        dataOut.writeBoolean(info.isRecievedByDFBridge());
        if (version >= 2) {
            dataOut.writeBoolean(info.isDroppable());
        }
        if (version >= 3) {
            looseMarshalObjectArray(wireFormat, info.getCluster(), dataOut);
        }
        if (version >= 3) {
            looseMarshalLong(wireFormat, info.getBrokerInTime(), dataOut);
        }
        if (version >= 3) {
            looseMarshalLong(wireFormat, info.getBrokerOutTime(), dataOut);
        }
        if (version >= 10) {
            dataOut.writeBoolean(info.isJMSXGroupFirstForConsumer());
        }

        info.afterMarshall(wireFormat);
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param target the object to un-marshal
     * @param dataIn the data input stream to build the object from
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, target, dataIn);

        Message info = (Message) target;
        int version = wireFormat.getVersion();

        info.beforeUnmarshall(wireFormat);
        info.setProducerId((ProducerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDestination((OpenWireDestination) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setTransactionId((TransactionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setOriginalDestination((OpenWireDestination) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setMessageId((MessageId) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setOriginalTransactionId((TransactionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setGroupID(looseUnmarshalString(dataIn));
        info.setGroupSequence(dataIn.readInt());
        info.setCorrelationId(looseUnmarshalString(dataIn));
        info.setPersistent(dataIn.readBoolean());
        info.setExpiration(looseUnmarshalLong(wireFormat, dataIn));
        info.setPriority(dataIn.readByte());
        info.setReplyTo((OpenWireDestination) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setTimestamp(looseUnmarshalLong(wireFormat, dataIn));
        info.setType(looseUnmarshalString(dataIn));
        info.setContent(looseUnmarshalByteSequence(dataIn));
        info.setMarshalledProperties(looseUnmarshalByteSequence(dataIn));
        info.setDataStructure((DataStructure) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setTargetConsumerId((ConsumerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setCompressed(dataIn.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());
        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat,dataIn);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
        }
        info.setArrival(looseUnmarshalLong(wireFormat, dataIn));
        info.setUserId(looseUnmarshalString(dataIn));
        info.setRecievedByDFBridge(dataIn.readBoolean());
        if (version >= 2) {
            info.setDroppable(dataIn.readBoolean());
        }
        if (version >= 3) {
            if (dataIn.readBoolean()) {
                short size = dataIn.readShort();
                BrokerId value[] = new BrokerId[size];
                for (int i = 0; i < size; i++) {
                    value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat,dataIn);
                }
                info.setCluster(value);
            } else {
                info.setCluster(null);
            }
        }
        if (version >= 3) {
            info.setBrokerInTime(looseUnmarshalLong(wireFormat, dataIn));
        }
        if (version >= 3) {
            info.setBrokerOutTime(looseUnmarshalLong(wireFormat, dataIn));
        }
        if (version >= 10) {
            info.setJMSXGroupFirstForConsumer(dataIn.readBoolean());
        }

        info.afterUnmarshall(wireFormat);
    }
}
