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
import org.apache.activemq.openwire.codec.BaseDataStreamMarshaller;
import org.apache.activemq.openwire.codec.BooleanStream;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.DataStructure;
import org.apache.activemq.openwire.commands.MessageId;
import org.apache.activemq.openwire.commands.ProducerId;

public class MessageIdMarshaller extends BaseDataStreamMarshaller {

    /**
     * Return the type of Data Structure we marshal
     *
     * @return short representation of the type data structure
     */
    @Override
    public byte getDataStructureType() {
        return MessageId.DATA_STRUCTURE_TYPE;
    }

    /**
     * @return a new object instance
     */
    @Override
    public DataStructure createObject() {
        return new MessageId();
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
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        MessageId info = (MessageId) o;
        info.setTextView(tightUnmarshalString(dataIn, bs));
        info.setProducerId((ProducerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setProducerSequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerSequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {
        MessageId info = (MessageId) o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalString1(info.getTextView(), bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getProducerId(), bs);
        rc += tightMarshalLong1(wireFormat, info.getProducerSequenceId(), bs);
        rc += tightMarshalLong1(wireFormat, info.getBrokerSequenceId(), bs);

        return rc + 0;
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

        MessageId info = (MessageId) o;
        tightMarshalString2(info.getTextView(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, info.getProducerId(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getProducerSequenceId(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getBrokerSequenceId(), dataOut, bs);
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

        MessageId info = (MessageId) o;
        info.setTextView(looseUnmarshalString(dataIn));
        info.setProducerId((ProducerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setProducerSequenceId(looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerSequenceId(looseUnmarshalLong(wireFormat, dataIn));
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {
        MessageId info = (MessageId) o;

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalString(info.getTextView(), dataOut);
        looseMarshalCachedObject(wireFormat, info.getProducerId(), dataOut);
        looseMarshalLong(wireFormat, info.getProducerSequenceId(), dataOut);
        looseMarshalLong(wireFormat, info.getBrokerSequenceId(), dataOut);
    }
}
