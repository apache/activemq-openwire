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
package org.apache.activemq.openwire.commands;

import java.io.IOException;
import java.util.Arrays;

import javax.transaction.xa.Xid;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireProperty;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;

/**
 * @openwire:marshaller code="112"
 */
@OpenWireType(typeCode = 112)
public class XATransactionId extends TransactionId implements Xid, Comparable<XATransactionId> {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_XA_TRANSACTION_ID;

    @OpenWireProperty(version = 1, sequence = 1)
    private int formatId;

    @OpenWireProperty(version = 1, sequence = 2)
    private byte[] globalTransactionId;

    @OpenWireProperty(version = 1, sequence = 3)
    private byte[] branchQualifier;

    @OpenWireExtension
    private transient DataByteArrayOutputStream outputStream;

    @OpenWireExtension
    private transient byte[] encodedXidBytes;

    @OpenWireExtension
    private transient int hash;

    @OpenWireExtension
    private transient String transactionKey;

    public XATransactionId() {
    }

    public XATransactionId(Xid xid) {
        this.formatId = xid.getFormatId();
        this.globalTransactionId = xid.getGlobalTransactionId();
        this.branchQualifier = xid.getBranchQualifier();
    }

    public XATransactionId(byte[] encodedBytes) throws IOException {
        encodedXidBytes = encodedBytes;
        initFromEncodedBytes();
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    final int XID_PREFIX_SIZE = 16;
    //+|-,(long)lastAck,(byte)priority,(int)formatid,(short)globalLength....
    private void initFromEncodedBytes() throws IOException {
        DataByteArrayInputStream inputStream = new DataByteArrayInputStream(encodedXidBytes);
        inputStream.skipBytes(10);
        formatId = inputStream.readInt();
        int globalLength = inputStream.readShort();
        globalTransactionId = new byte[globalLength];
        try {
            inputStream.read(globalTransactionId);
            branchQualifier = new byte[inputStream.available()];
            inputStream.read(branchQualifier);
        } catch (IOException fatal) {
            throw new RuntimeException(this + ", failed to decode:", fatal);
        } finally {
            inputStream.close();
        }
    }

    public synchronized byte[] getEncodedXidBytes() {
        if (encodedXidBytes == null) {
            outputStream = new DataByteArrayOutputStream(XID_PREFIX_SIZE + globalTransactionId.length + branchQualifier.length);
            try {
                outputStream.position(10);
                outputStream.writeInt(formatId);
                outputStream.writeShort(globalTransactionId.length);
            } catch (IOException fatal) {
                throw new RuntimeException(this + ", failed to encode:", fatal);
            }
            try {
                outputStream.write(globalTransactionId);
                outputStream.write(branchQualifier);
            } catch (IOException fatal) {
                throw new RuntimeException(this + ", failed to encode:", fatal);
            }
            encodedXidBytes = outputStream.getData();
        }
        return encodedXidBytes;
    }

    public DataByteArrayOutputStream internalOutputStream() {
        return outputStream;
    }

    @Override
    public synchronized String getTransactionKey() {
        if (transactionKey == null) {
            StringBuffer s = new StringBuffer();
            s.append("XID:[" + formatId + ",globalId=");
            s.append(stringForm(formatId, globalTransactionId));
            s.append(",branchId=");
            s.append(stringForm(formatId, branchQualifier));
            s.append("]");
            transactionKey = s.toString();
        }
        return transactionKey;
    }

    private String stringForm(int format, byte[] uid) {
        StringBuffer s = new StringBuffer();
        switch (format) {
            case 131077:  // arjuna
                stringFormArj(s, uid);
                break;
            default: // aries
                stringFormDefault(s, uid);
        }
        return s.toString();
    }

    private void stringFormDefault(StringBuffer s, byte[] uid) {
        for (int i = 0; i < uid.length; i++) {
            s.append(Integer.toHexString(uid[i]));
        }
    }

    private void stringFormArj(StringBuffer s, byte[] uid) {
        DataByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new DataByteArrayInputStream(uid);
            s.append(Long.toString(byteArrayInputStream.readLong(), 16));
            s.append(':');
            s.append(Long.toString(byteArrayInputStream.readLong(), 16));
            s.append(':');

            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
            s.append(':');
            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
            s.append(':');
            s.append(Integer.toString(byteArrayInputStream.readInt(), 16));
        } catch (Exception ignored) {
            stringFormDefault(s, uid);
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public String toString() {
        return getTransactionKey();
    }

    @Override
    public boolean isXATransaction() {
        return true;
    }

    @Override
    public boolean isLocalTransaction() {
        return false;
    }

    /**
     * @openwire:property version=1
     */
    @Override
    public int getFormatId() {
        return formatId;
    }

    /**
     * @openwire:property version=1
     */
    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    /**
     * @openwire:property version=1
     */
    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    public void setBranchQualifier(byte[] branchQualifier) {
        this.branchQualifier = branchQualifier;
        this.hash = 0;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
        this.hash = 0;
    }

    public void setGlobalTransactionId(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        this.hash = 0;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = formatId;
            hash = hash(globalTransactionId, hash);
            hash = hash(branchQualifier, hash);
            if (hash == 0) {
                hash = 0xaceace;
            }
        }
        return hash;
    }

    private static int hash(byte[] bytes, int hash) {
        int size = bytes.length;
        for (int i = 0; i < size; i++) {
            hash ^= bytes[i] << ((i % 4) * 8);
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != XATransactionId.class) {
            return false;
        }

        XATransactionId xid = (XATransactionId)o;
        return xid.formatId == formatId &&
               Arrays.equals(xid.globalTransactionId, globalTransactionId) &&
               Arrays.equals(xid.branchQualifier, branchQualifier);
    }

    @Override
    public int compareTo(XATransactionId xid) {
        if (xid == null) {
            return -1;
        }

        return getTransactionKey().compareTo(xid.getTransactionKey());
    }
}
