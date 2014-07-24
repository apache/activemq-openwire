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
package org.apache.activemq.openwire.jms;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataOutputStream;

import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.commands.OpenWireMessage;
import org.apache.activemq.openwire.jms.OpenWireJMSTextMessage;
import org.apache.activemq.openwire.utils.OpenWireMarshallingSupport;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import org.junit.Test;

/**
 *
 */
public class OpenWireJMSTextMessageTest {

    @Test
    public void testReadOnlyBody() throws JMSException {
        OpenWireJMSTextMessage textMessage = new OpenWireJMSTextMessage();
        textMessage.setText("test");
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        }
        try {
            textMessage.setText("test");
            fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testWriteOnlyBody() throws JMSException { // should always be readable
        OpenWireJMSTextMessage textMessage = new OpenWireJMSTextMessage();
        textMessage.setReadOnlyBody(false);
        try {
            textMessage.setText("test");
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        }
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
            textMessage.setText("test");
            fail("should throw exception");
        } catch (MessageNotReadableException e) {
            fail("should be readable");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testShortText() throws Exception {
        String shortText = "Content";
        OpenWireJMSTextMessage shortMessage = new OpenWireJMSTextMessage();
        setContent(shortMessage.getOpenWireMessage(), shortText);
        assertTrue(shortMessage.getText().equals(shortText));

        String longText = "Very very very very veeeeeeery loooooooooooooooooooooooooooooooooong text";
        OpenWireJMSTextMessage longMessage = new OpenWireJMSTextMessage();
        setContent(longMessage.getOpenWireMessage(), longText);
        assertTrue(longMessage.getText().equals(longText));
    }

    protected void setContent(OpenWireMessage message, String text) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(baos);
        OpenWireMarshallingSupport.writeUTF8(dataOut, text);
        dataOut.close();
        message.setContent(baos.toBuffer());
    }
}
