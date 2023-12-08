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
package org.apache.activenq.openwire.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.activemq.openwire.annotations.OpenWireProperty;
import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.generator.OpenWireTypeDescriptor;
import org.junit.jupiter.api.Test;

public class OpenWireTypeDescriptorTest {

    @Test
    public void testSuccess() {
        new OpenWireTypeDescriptor(ValidOpenWireType.class);
    }

    @Test
    public void testNotAnOpenWireType() {
        assertIllegalArgumentException(String.class, "String is missing required OpenWireType annotation");
    }

    @Test
    public void testDuplicateSequence() {
        assertIllegalArgumentException(DuplicateSequence.class, "Sequence numbers must not be duplicated");
    }

    @Test
    public void testZeroSequence() {
        assertIllegalArgumentException(ZeroSequence.class, "Sequence number must be positive");
    }

    @Test
    public void testNotContiguous() {
        assertIllegalArgumentException(NotContiguousSequence.class, "Sequence numbers must be contiguous");
    }

    private void assertIllegalArgumentException(Class<?> openWireType, String error) {
        try {
            new OpenWireTypeDescriptor(openWireType);
            fail("IllegalArgumentException was expected with error " + error);
        } catch (IllegalArgumentException e) {
            assertTrue(e.toString().contains(error));
        }
    }

    @OpenWireType(typeCode = 1, version = 1)
    private static class ValidOpenWireType {

        @OpenWireProperty(version = 1, sequence = 1)
        protected int property1;

        @OpenWireProperty(version = 1, sequence = 2)
        protected int property2;

        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }
    }

    @OpenWireType(typeCode = 1, version = 1)
    private static class DuplicateSequence {

        @OpenWireProperty(version = 1, sequence = 1)
        protected int property1;

        @OpenWireProperty(version = 1, sequence = 1)
        protected int property2;

        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }
    }

    @OpenWireType(typeCode = 1, version = 1)
    private static class ZeroSequence {

        @OpenWireProperty(version = 1, sequence = 0)
        protected int property1;

        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }
    }

    @OpenWireType(typeCode = 1, version = 1)
    private static class NotContiguousSequence {

        @OpenWireProperty(version = 1, sequence = 1)
        protected int property1;

        @OpenWireProperty(version = 1, sequence = 3)
        protected int property2;

        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }
    }
}
