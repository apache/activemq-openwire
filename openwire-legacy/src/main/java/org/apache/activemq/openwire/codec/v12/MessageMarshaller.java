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
package org.apache.activemq.openwire.codec.v12;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.codec.BooleanStream;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.BrokerId;
import org.apache.activemq.openwire.commands.ConsumerId;
import org.apache.activemq.openwire.commands.Message;
import org.apache.activemq.openwire.commands.MessageId;
import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.ProducerId;
import org.apache.activemq.openwire.commands.TransactionId;

public abstract class MessageMarshaller extends BaseCommandMarshaller {

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        Message info = (Message) o;

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
        info.setDataStructure(tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setTargetConsumerId((ConsumerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setCompressed(bs.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
        }
        info.setArrival(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setUserId(tightUnmarshalString(dataIn, bs));
        info.setRecievedByDFBridge(bs.readBoolean());
        info.setDroppable(bs.readBoolean());

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setCluster(value);
        } else {
            info.setCluster(null);
        }
        info.setBrokerInTime(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerOutTime(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setJMSXGroupFirstForConsumer(bs.readBoolean());

        info.afterUnmarshall(wireFormat);
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {

        Message info = (Message) o;

        info.beforeMarshall(wireFormat);

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getProducerId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getDestination(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getTransactionId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getOriginalDestination(), bs);
        rc += tightMarshalNestedObject1(wireFormat, info.getMessageId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getOriginalTransactionId(), bs);
        rc += tightMarshalString1(info.getGroupId(), bs);
        rc += tightMarshalString1(info.getCorrelationId(), bs);
        bs.writeBoolean(info.isPersistent());
        rc += tightMarshalLong1(wireFormat, info.getExpiration(), bs);
        rc += tightMarshalNestedObject1(wireFormat, info.getReplyTo(), bs);
        rc += tightMarshalLong1(wireFormat, info.getTimestamp(), bs);
        rc += tightMarshalString1(info.getType(), bs);
        rc += tightMarshalByteSequence1(info.getContent(), bs);
        rc += tightMarshalByteSequence1(info.getMarshalledProperties(), bs);
        rc += tightMarshalNestedObject1(wireFormat, info.getDataStructure(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getTargetConsumerId(), bs);
        bs.writeBoolean(info.isCompressed());
        rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        rc += tightMarshalLong1(wireFormat, info.getArrival(), bs);
        rc += tightMarshalString1(info.getUserId(), bs);
        bs.writeBoolean(info.isRecievedByDFBridge());
        bs.writeBoolean(info.isDroppable());
        rc += tightMarshalObjectArray1(wireFormat, info.getCluster(), bs);
        rc += tightMarshalLong1(wireFormat, info.getBrokerInTime(), bs);
        rc += tightMarshalLong1(wireFormat, info.getBrokerOutTime(), bs);
        bs.writeBoolean(info.isJMSXGroupFirstForConsumer());

        return rc + 9;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o
     *        the instance to be marshaled
     * @param dataOut
     *        the output stream
     * @throws IOException
     *         thrown if an error occurs
     */
    @Override
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        Message info = (Message) o;
        tightMarshalCachedObject2(wireFormat, info.getProducerId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getDestination(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getOriginalDestination(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, info.getMessageId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getOriginalTransactionId(), dataOut, bs);
        tightMarshalString2(info.getGroupId(), dataOut, bs);
        dataOut.writeInt(info.getGroupSequence());
        tightMarshalString2(info.getCorrelationId(), dataOut, bs);
        bs.readBoolean();
        tightMarshalLong2(wireFormat, info.getExpiration(), dataOut, bs);
        dataOut.writeByte(info.getPriority());
        tightMarshalNestedObject2(wireFormat, info.getReplyTo(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getTimestamp(), dataOut, bs);
        tightMarshalString2(info.getType(), dataOut, bs);
        tightMarshalByteSequence2(info.getContent(), dataOut, bs);
        tightMarshalByteSequence2(info.getMarshalledProperties(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, info.getDataStructure(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getTargetConsumerId(), dataOut, bs);
        bs.readBoolean();
        dataOut.writeInt(info.getRedeliveryCounter());
        tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getArrival(), dataOut, bs);
        tightMarshalString2(info.getUserId(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        tightMarshalObjectArray2(wireFormat, info.getCluster(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getBrokerInTime(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getBrokerOutTime(), dataOut, bs);
        bs.readBoolean();

        info.afterMarshall(wireFormat);
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        Message info = (Message) o;

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
        info.setDataStructure(looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setTargetConsumerId((ConsumerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setCompressed(dataIn.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
        }
        info.setArrival(looseUnmarshalLong(wireFormat, dataIn));
        info.setUserId(looseUnmarshalString(dataIn));
        info.setRecievedByDFBridge(dataIn.readBoolean());
        info.setDroppable(dataIn.readBoolean());

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setCluster(value);
        } else {
            info.setCluster(null);
        }
        info.setBrokerInTime(looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerOutTime(looseUnmarshalLong(wireFormat, dataIn));
        info.setJMSXGroupFirstForConsumer(dataIn.readBoolean());

        info.afterUnmarshall(wireFormat);
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {
        Message info = (Message) o;

        info.beforeMarshall(wireFormat);

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalCachedObject(wireFormat, info.getProducerId(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getDestination(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getTransactionId(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getOriginalDestination(), dataOut);
        looseMarshalNestedObject(wireFormat, info.getMessageId(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getOriginalTransactionId(), dataOut);
        looseMarshalString(info.getGroupId(), dataOut);
        dataOut.writeInt(info.getGroupSequence());
        looseMarshalString(info.getCorrelationId(), dataOut);
        dataOut.writeBoolean(info.isPersistent());
        looseMarshalLong(wireFormat, info.getExpiration(), dataOut);
        dataOut.writeByte(info.getPriority());
        looseMarshalNestedObject(wireFormat, info.getReplyTo(), dataOut);
        looseMarshalLong(wireFormat, info.getTimestamp(), dataOut);
        looseMarshalString(info.getType(), dataOut);
        looseMarshalByteSequence(wireFormat, info.getContent(), dataOut);
        looseMarshalByteSequence(wireFormat, info.getMarshalledProperties(), dataOut);
        looseMarshalNestedObject(wireFormat, info.getDataStructure(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getTargetConsumerId(), dataOut);
        dataOut.writeBoolean(info.isCompressed());
        dataOut.writeInt(info.getRedeliveryCounter());
        looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        looseMarshalLong(wireFormat, info.getArrival(), dataOut);
        looseMarshalString(info.getUserId(), dataOut);
        dataOut.writeBoolean(info.isRecievedByDFBridge());
        dataOut.writeBoolean(info.isDroppable());
        looseMarshalObjectArray(wireFormat, info.getCluster(), dataOut);
        looseMarshalLong(wireFormat, info.getBrokerInTime(), dataOut);
        looseMarshalLong(wireFormat, info.getBrokerOutTime(), dataOut);
        dataOut.writeBoolean(info.isJMSXGroupFirstForConsumer());
    }
}
