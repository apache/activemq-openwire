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
package org.apache.activemq.openwire.codec.v1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.activemq.openwire.codec.BooleanStream;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.DataStructure;
import org.apache.activemq.openwire.commands.FlushCommand;

public class FlushCommandMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure we marshal
     *
     * @return short representation of the type data structure
     */
    @Override
    public byte getDataStructureType() {
        return FlushCommand.DATA_STRUCTURE_TYPE;
    }

    /**
     * @return a new object instance
     */
    @Override
    public DataStructure createObject() {
        return new FlushCommand();
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
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {
        int rc = super.tightMarshal1(wireFormat, o, bs);
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
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {
        super.looseMarshal(wireFormat, o, dataOut);
    }
}
