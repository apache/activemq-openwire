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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.utils.MarshallingSupport;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import org.junit.Test;

/**
 *
 */
public class OpenWireTextMessageTest {

    @Test
    public void testGetDataStructureType() {
        OpenWireTextMessage msg = new OpenWireTextMessage();
        assertEquals(msg.getDataStructureType(), CommandTypes.OPENWIRE_TEXT_MESSAGE);
    }

    @Test
    public void testShallowCopy() throws JMSException {
        OpenWireTextMessage msg = new OpenWireTextMessage();
        String string = "str";
        msg.setText(string);
        Message copy = msg.copy();
        assertTrue(msg.getText() == ((OpenWireTextMessage) copy).getText());
    }

    @Test
    public void testSetText() {
        OpenWireTextMessage msg = new OpenWireTextMessage();
        String str = "testText";
        try {
            msg.setText(str);
            assertEquals(msg.getText(), str);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBytes() throws JMSException, IOException {
        OpenWireTextMessage msg = new OpenWireTextMessage();
        String str = "testText";
        msg.setText(str);
        msg.beforeMarshall(null);

        Buffer bytes = msg.getContent();
        msg = new OpenWireTextMessage();
        msg.setContent(bytes);

        assertEquals(msg.getText(), str);
    }

    @Test
    public void testClearBody() throws JMSException, IOException {
        OpenWireTextMessage textMessage = new OpenWireTextMessage();
        textMessage.setText("string");
        textMessage.clearBody();
        assertNull(textMessage.getText());
        try {
            textMessage.setText("String");
            textMessage.getText();
        } catch (MessageNotWriteableException mnwe) {
            fail("should be writeable");
        } catch (MessageNotReadableException mnre) {
            fail("should be readable");
        }
    }

    @Test
    public void testShortText() throws Exception {
        String shortText = "Content";
        OpenWireTextMessage shortMessage = new OpenWireTextMessage();
        setContent(shortMessage, shortText);
        assertTrue(shortMessage.toString().contains("text = " + shortText));
        assertTrue(shortMessage.getText().equals(shortText));

        String longText = "Very very very very veeeeeeery loooooooooooooooooooooooooooooooooong text";
        String longExpectedText = "Very very very very veeeeeeery looooooooooooo...ooooong text";
        OpenWireTextMessage longMessage = new OpenWireTextMessage();
        setContent(longMessage, longText);
        assertTrue(longMessage.toString().contains("text = " + longExpectedText));
        assertTrue(longMessage.getText().equals(longText));
    }

    @Test
    public void testNullText() throws Exception {
        OpenWireTextMessage nullMessage = new OpenWireTextMessage();
        setContent(nullMessage, null);
        assertTrue(nullMessage.toString().contains("text = null"));
    }

    void setContent(Message message, String text) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(baos);
        MarshallingSupport.writeUTF8(dataOut, text);
        dataOut.close();
        message.setContent(baos.toBuffer());
    }
}
