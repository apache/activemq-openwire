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
 * Marshalling code for Open Wire for ConsumerControl
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class ConsumerControlMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return ConsumerControl.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new ConsumerControl();
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

        ConsumerControl info = (ConsumerControl) target;
        int version = wireFormat.getVersion();

        if (version >= 6) {
            info.setDestination((OpenWireDestination) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        }
        info.setClose(bs.readBoolean());
        info.setConsumerId((ConsumerId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setPrefetch(dataIn.readInt());
        if (version >= 2) {
            info.setFlush(bs.readBoolean());
        }
        if (version >= 2) {
            info.setStart(bs.readBoolean());
        }
        if (version >= 2) {
            info.setStop(bs.readBoolean());
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
        ConsumerControl info = (ConsumerControl) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        if (version >= 6) {
            rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        }
        bs.writeBoolean(info.isClose());
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getConsumerId(), bs);
        if (version >= 2) {
            bs.writeBoolean(info.isFlush());
        }
        if (version >= 2) {
            bs.writeBoolean(info.isStart());
        }
        if (version >= 2) {
            bs.writeBoolean(info.isStop());
        }

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

        ConsumerControl info = (ConsumerControl) source;
        int version = wireFormat.getVersion();

        if (version >= 6) {
            tightMarshalNestedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        }
        bs.readBoolean();
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getConsumerId(), dataOut, bs);
        dataOut.writeInt(info.getPrefetch());
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 2) {
            bs.readBoolean();
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        ConsumerControl info = (ConsumerControl) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        if (version >= 6) {
            looseMarshalNestedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        }
        dataOut.writeBoolean(info.isClose());
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getConsumerId(), dataOut);
        dataOut.writeInt(info.getPrefetch());
        if (version >= 2) {
            dataOut.writeBoolean(info.isFlush());
        }
        if (version >= 2) {
            dataOut.writeBoolean(info.isStart());
        }
        if (version >= 2) {
            dataOut.writeBoolean(info.isStop());
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

        ConsumerControl info = (ConsumerControl) target;
        int version = wireFormat.getVersion();

        if (version >= 6) {
            info.setDestination((OpenWireDestination) looseUnmarsalNestedObject(wireFormat, dataIn));
        }
        info.setClose(dataIn.readBoolean());
        info.setConsumerId((ConsumerId) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setPrefetch(dataIn.readInt());
        if (version >= 2) {
            info.setFlush(dataIn.readBoolean());
        }
        if (version >= 2) {
            info.setStart(dataIn.readBoolean());
        }
        if (version >= 2) {
            info.setStop(dataIn.readBoolean());
        }
    }
}
