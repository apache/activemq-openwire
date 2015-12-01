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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.activemq.openwire.codec.*;
import org.apache.activemq.openwire.commands.*;

/**
 * Marshalling code for Open Wire for MessageAck
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class MessageAckMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return MessageAck.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new MessageAck();
    }

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

        MessageAck info = (MessageAck) target;
        int version = wireFormat.getVersion();

        info.setDestination((OpenWireDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setTransactionId((TransactionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setConsumerId((ConsumerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setAckType(dataIn.readByte());
        info.setFirstMessageId((MessageId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setLastMessageId((MessageId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setMessageCount(dataIn.readInt());
        if (version >= 7) {
            info.setPoisonCause((Throwable) tightUnmarsalThrowable(wireFormat, dataIn, bs));
        }
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
        MessageAck info = (MessageAck) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getTransactionId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getConsumerId(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getFirstMessageId(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getLastMessageId(), bs);
        if (version >= 7) {
            rc += tightMarshalThrowable1(wireFormat, info.getPoisonCause(), bs);
        }

        return rc + 5;
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

        MessageAck info = (MessageAck) source;
        int version = wireFormat.getVersion();

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getTransactionId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getConsumerId(), dataOut, bs);
        dataOut.writeByte(info.getAckType());
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getFirstMessageId(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getLastMessageId(), dataOut, bs);
        dataOut.writeInt(info.getMessageCount());
        if (version >= 7) {
            tightMarshalThrowable2(wireFormat, info.getPoisonCause(), dataOut, bs);
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        MessageAck info = (MessageAck) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getTransactionId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getConsumerId(), dataOut);
        dataOut.writeByte(info.getAckType());
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getFirstMessageId(), dataOut);
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getLastMessageId(), dataOut);
        dataOut.writeInt(info.getMessageCount());
        if (version >= 7) {
            looseMarshalThrowable(wireFormat, info.getPoisonCause(), dataOut);
        }
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

        MessageAck info = (MessageAck) target;
        int version = wireFormat.getVersion();

        info.setDestination((OpenWireDestination) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setTransactionId((TransactionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setConsumerId((ConsumerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setAckType(dataIn.readByte());
        info.setFirstMessageId((MessageId) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setLastMessageId((MessageId) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setMessageCount(dataIn.readInt());
        if (version >= 7) {
            info.setPoisonCause((Throwable) looseUnmarsalThrowable(wireFormat, dataIn));
        }
    }
}
