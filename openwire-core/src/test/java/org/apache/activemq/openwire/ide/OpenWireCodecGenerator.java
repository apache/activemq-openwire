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
package org.apache.activemq.openwire.ide;

import java.io.File;
import java.io.IOException;

import org.apache.activemq.openwire.generator.GeneratorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for running the OpenWire source generator from within an
 * IDE or as part of a test.
 */
public class OpenWireCodecGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireCodecGenerator.class);

    private final GeneratorTask generator = new GeneratorTask();

    private int baseVersion = 10;
    private int maxVersion = 10;

    private File sourceDir;
    private File targetDir;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        OpenWireCodecGenerator runner = new OpenWireCodecGenerator();

        runner.setBaseVersion(10);
        runner.setMaxVersion(10);

        File sourceDir = new File("./src/main/java/io/neutronjms/openwire/commands").getCanonicalFile();
        File targetDir = new File("./target/generated-sources/openwire").getCanonicalFile();

        if (!sourceDir.exists()) {
            throw new IOException("Source dir does not exist. " + sourceDir);
        }

        if (!targetDir.exists()) {
            targetDir.createNewFile();
            if (!targetDir.exists()) {
                throw new IOException("Source dir does not exist. " + targetDir);
            }
        }

        runner.setSourceDir(sourceDir);
        runner.setTargetDir(targetDir);

        runner.generate();
    }

    /**
     * Runs the OpenWire generator using the configured generation values.
     */
    public void generate() {
        generator.setFromVersion(getBaseVersion());
        generator.setToVersion(getMaxVersion());
        generator.setSourceDir(getSourceDir());
        generator.setTargetDir(getTargetDir());

        try {
            generator.execute();
        } catch (RuntimeException e) {
            LOG.warn("Caught exception while executing generator: ", e);
            throw e;
        }
    }

    public int getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(int version) {
        this.baseVersion = version;
    }

    public int getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(int version) {
        this.maxVersion = version;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }
}
