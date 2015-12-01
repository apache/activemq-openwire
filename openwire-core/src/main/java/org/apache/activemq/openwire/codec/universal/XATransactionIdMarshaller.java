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
 * Marshalling code for Open Wire for XATransactionId
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class XATransactionIdMarshaller extends TransactionIdMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return XATransactionId.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new XATransactionId();
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

        XATransactionId info = (XATransactionId) target;

        info.setFormatId(dataIn.readInt());
        info.setGlobalTransactionId(tightUnmarshalConstByteArray(dataIn, bs, 0));
        info.setBranchQualifier(tightUnmarshalConstByteArray(dataIn, bs, 0));
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
        XATransactionId info = (XATransactionId) source;

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalByteArray1(info.getGlobalTransactionId(), bs);
        rc += tightMarshalByteArray1(info.getBranchQualifier(), bs);

        return rc + 4;
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

        XATransactionId info = (XATransactionId) source;

        dataOut.writeInt(info.getFormatId());
        tightMarshalByteArray2(info.getGlobalTransactionId(), dataOut, bs);
        tightMarshalByteArray2(info.getBranchQualifier(), dataOut, bs);
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        XATransactionId info = (XATransactionId) source;

        super.looseMarshal(wireFormat, source, dataOut);
        dataOut.writeInt(info.getFormatId());
        looseMarshalByteArray(wireFormat, info.getGlobalTransactionId(), dataOut);
        looseMarshalByteArray(wireFormat, info.getBranchQualifier(), dataOut);
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

        XATransactionId info = (XATransactionId) target;

        info.setFormatId(dataIn.readInt());
        info.setGlobalTransactionId(looseUnmarshalByteArray(dataIn));
        info.setBranchQualifier(looseUnmarshalByteArray(dataIn));
    }
}
