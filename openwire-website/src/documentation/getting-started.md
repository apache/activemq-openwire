<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  Architecture
-->
# Getting Started Guide

{:toc:2-5}

This guide will help you get started using the OpenWire protocol library in your own applications.

## Maven dependencies

To use the openwire library in your maven projects you need to add a dependency to your maven POM file for the core openwire library as follows:

    <dependency>
      <groupId>org.apache.activemq</groupId>
      <artifactId>openwire-core</artifactId>
      <version>${project_version}</version>
    </dependency>

If you need to communicate with client's or brokers using older versions of the protocol then you must also include the legacy codec module:

    <dependency>
      <groupId>org.apache.activemq</groupId>
      <artifactId>openwire-legacy</artifactId>
      <version>${project_version}</version>
    </dependency>


