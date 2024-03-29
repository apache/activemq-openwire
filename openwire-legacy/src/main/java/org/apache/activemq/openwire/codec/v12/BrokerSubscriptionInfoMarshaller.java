/**
 *
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
import org.apache.activemq.openwire.commands.BrokerSubscriptionInfo;
import org.apache.activemq.openwire.commands.ConsumerInfo;
import org.apache.activemq.openwire.commands.DataStructure;

public class BrokerSubscriptionInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure we marshal
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return BrokerSubscriptionInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new object instance
     */
    public DataStructure createObject() {
        return new BrokerSubscriptionInfo();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void tightUnmarshal(
        OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        BrokerSubscriptionInfo info = (BrokerSubscriptionInfo)o;
        info.setBrokerId((BrokerId) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setBrokerName(tightUnmarshalString(dataIn, bs));

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            ConsumerInfo value[] = new ConsumerInfo[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (ConsumerInfo) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
            }
            info.setSubscriptionInfos(value);
        }
        else {
            info.setSubscriptionInfos(null);
        }

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {

        BrokerSubscriptionInfo info = (BrokerSubscriptionInfo)o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalNestedObject1(wireFormat, info.getBrokerId(), bs);
        rc += tightMarshalString1(info.getBrokerName(), bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getSubscriptionInfos(), bs);

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o the instance to be marshaled
     * @param dataOut the output stream
     * @throws IOException thrown if an error occurs
     */
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        BrokerSubscriptionInfo info = (BrokerSubscriptionInfo)o;
        tightMarshalNestedObject2(wireFormat, info.getBrokerId(), dataOut, bs);
        tightMarshalString2(info.getBrokerName(), dataOut, bs);
        tightMarshalObjectArray2(wireFormat, info.getSubscriptionInfos(), dataOut, bs);

    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        BrokerSubscriptionInfo info = (BrokerSubscriptionInfo)o;
        info.setBrokerId((BrokerId) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setBrokerName(looseUnmarshalString(dataIn));

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            ConsumerInfo value[] = new ConsumerInfo[size];
            for( int i=0; i < size; i++ ) {
                value[i] = (ConsumerInfo) looseUnmarsalNestedObject(wireFormat,dataIn);
            }
            info.setSubscriptionInfos(value);
        }
        else {
            info.setSubscriptionInfos(null);
        }

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {

        BrokerSubscriptionInfo info = (BrokerSubscriptionInfo)o;

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalNestedObject(wireFormat, info.getBrokerId(), dataOut);
        looseMarshalString(info.getBrokerName(), dataOut);
        looseMarshalObjectArray(wireFormat, info.getSubscriptionInfos(), dataOut);

    }
}
