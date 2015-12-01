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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.JMSException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireProperty;

/**
 * An implementation of ActiveMQ's BlobMessage for out of band BLOB transfer
 *
 * openwire:marshaller code="29"
 */
@OpenWireType(typeCode = 29, version = 3)
public class OpenWireBlobMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_BLOB_MESSAGE;

    public static final String BINARY_MIME_TYPE = "application/octet-stream";

    @OpenWireProperty(version = 3, sequence = 1)
    private String remoteBlobUrl;

    @OpenWireProperty(version = 3, sequence = 2, cached = true)
    private String mimeType;

    @OpenWireProperty(version = 3, sequence = 3)
    private boolean deletedByBroker;

    @OpenWireExtension
    private transient URL url;

    @OpenWireExtension(serialized = true)
    private String name;

    @Override
    public OpenWireBlobMessage copy() {
        OpenWireBlobMessage copy = new OpenWireBlobMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireBlobMessage copy) {
        super.copy(copy);
        copy.setRemoteBlobUrl(getRemoteBlobUrl());
        copy.setMimeType(getMimeType());
        copy.setDeletedByBroker(isDeletedByBroker());
        copy.setName(getName());
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=3 cache=false
     */
    public String getRemoteBlobUrl() {
        return remoteBlobUrl;
    }

    public void setRemoteBlobUrl(String remoteBlobUrl) {
        this.remoteBlobUrl = remoteBlobUrl;
        url = null;
    }

    /**
     * The MIME type of the BLOB which can be used to apply different content types to messages.
     *
     * @openwire:property version=3 cache=true
     */
    @Override
    public String getMimeType() {
        if (mimeType == null) {
            return BINARY_MIME_TYPE;
        }
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    /**
     * The name of the attachment which can be useful information if transmitting files over
     * ActiveMQ
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @openwire:property version=3 cache=false
     */
    public boolean isDeletedByBroker() {
        return deletedByBroker;
    }

    public void setDeletedByBroker(boolean deletedByBroker) {
        this.deletedByBroker = deletedByBroker;
    }

    public InputStream getInputStream() throws IOException, JMSException {
        return null;
    }

    public URL getURL() throws JMSException {
        if (url == null && remoteBlobUrl != null) {
            try {
                url = new URL(remoteBlobUrl);
            } catch (MalformedURLException e) {
                throw new JMSException(e.getMessage());
            }
        }
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
        remoteBlobUrl = url != null ? url.toExternalForm() : null;
    }

    @Override
    public void onSend() throws JMSException {
        super.onSend();

        // lets ensure we upload the BLOB first out of band before we send the
        // message
        // TODO - Lets support this later.
    }

    public void deleteFile() throws IOException, JMSException {
    }
}
