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
 * Marshalling code for Open Wire for ConnectionControl
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class ConnectionControlMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return ConnectionControl.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new ConnectionControl();
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

        ConnectionControl info = (ConnectionControl) target;
        int version = wireFormat.getVersion();

        info.setClose(bs.readBoolean());
        info.setExit(bs.readBoolean());
        info.setFaultTolerant(bs.readBoolean());
        info.setResume(bs.readBoolean());
        info.setSuspend(bs.readBoolean());
        if (version >= 6) {
            info.setConnectedBrokers(tightUnmarshalString(dataIn, bs));
        }
        if (version >= 6) {
            info.setReconnectTo(tightUnmarshalString(dataIn, bs));
        }
        if (version >= 6) {
            info.setRebalanceConnection(bs.readBoolean());
        }
        if (version >= 6) {
            info.setToken(tightUnmarshalConstByteArray(dataIn, bs, 0));
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
        ConnectionControl info = (ConnectionControl) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        bs.writeBoolean(info.isClose());
        bs.writeBoolean(info.isExit());
        bs.writeBoolean(info.isFaultTolerant());
        bs.writeBoolean(info.isResume());
        bs.writeBoolean(info.isSuspend());
        if (version >= 6) {
            rc += tightMarshalString1(info.getConnectedBrokers(), bs);
        }
        if (version >= 6) {
            rc += tightMarshalString1(info.getReconnectTo(), bs);
        }
        if (version >= 6) {
            bs.writeBoolean(info.isRebalanceConnection());
        }
        if (version >= 6) {
            rc += tightMarshalByteArray1(info.getToken(), bs);
        }

        return rc + 0;
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

        ConnectionControl info = (ConnectionControl) source;
        int version = wireFormat.getVersion();

        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        if (version >= 6) {
            tightMarshalString2(info.getConnectedBrokers(), dataOut, bs);
        }
        if (version >= 6) {
            tightMarshalString2(info.getReconnectTo(), dataOut, bs);
        }
        if (version >= 6) {
            bs.readBoolean();
        }
        if (version >= 6) {
            tightMarshalByteArray2(info.getToken(), dataOut, bs);
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        ConnectionControl info = (ConnectionControl) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        dataOut.writeBoolean(info.isClose());
        dataOut.writeBoolean(info.isExit());
        dataOut.writeBoolean(info.isFaultTolerant());
        dataOut.writeBoolean(info.isResume());
        dataOut.writeBoolean(info.isSuspend());
        if (version >= 6) {
            looseMarshalString(info.getConnectedBrokers(), dataOut);
        }
        if (version >= 6) {
            looseMarshalString(info.getReconnectTo(), dataOut);
        }
        if (version >= 6) {
            dataOut.writeBoolean(info.isRebalanceConnection());
        }
        if (version >= 6) {
            looseMarshalByteArray(wireFormat, info.getToken(), dataOut);
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

        ConnectionControl info = (ConnectionControl) target;
        int version = wireFormat.getVersion();

        info.setClose(dataIn.readBoolean());
        info.setExit(dataIn.readBoolean());
        info.setFaultTolerant(dataIn.readBoolean());
        info.setResume(dataIn.readBoolean());
        info.setSuspend(dataIn.readBoolean());
        if (version >= 6) {
            info.setConnectedBrokers(looseUnmarshalString(dataIn));
        }
        if (version >= 6) {
            info.setReconnectTo(looseUnmarshalString(dataIn));
        }
        if (version >= 6) {
            info.setRebalanceConnection(dataIn.readBoolean());
        }
        if (version >= 6) {
            info.setToken(looseUnmarshalByteArray(dataIn));
        }
    }
}
