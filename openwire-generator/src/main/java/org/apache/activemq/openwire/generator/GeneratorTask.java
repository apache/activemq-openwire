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
package org.apache.activemq.openwire.generator;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.codehaus.jam.JamService;
import org.codehaus.jam.JamServiceFactory;
import org.codehaus.jam.JamServiceParams;

/**
 * The main task that controls the OpenWire code generation routines.
 */
public class GeneratorTask extends Task {

    protected int fromVersion = 1;
    protected int toVersion = 1;
    protected boolean rangedGenerate = true;
    protected File sourceDir = new File("./src/main/java");
    protected File targetDir = new File("./src/main/java");
    protected boolean generateMarshalers = true;
    protected boolean generateTests = false;
    protected String commandsPackage;
    protected String codecPackageRoot;

    public static void main(String[] args) {

        Project project = new Project();
        project.init();
        GeneratorTask generator = new GeneratorTask();
        generator.setProject(project);

        if (args.length > 0) {
            generator.fromVersion = Integer.parseInt(args[0]);
        }

        if (args.length > 0) {
            generator.toVersion = Integer.parseInt(args[0]);
        }

        if (args.length > 1) {
            generator.sourceDir = new File(args[1]);
        }

        if (args.length > 2) {
            generator.targetDir = new File(args[1]);
        }

        if (args.length > 3) {
            generator.commandsPackage = args[2];
        }

        if (args.length > 4) {
            generator.codecPackageRoot = args[3];
        }

        generator.execute();
    }

    @Override
    public void execute() throws BuildException {
        try {
            System.out.println("======================================================");
            System.out.println("OpenWire Generator: Command source files in: ");
            System.out.println("" + sourceDir);
            System.out.println("======================================================");

            JamServiceFactory jamServiceFactory = JamServiceFactory.getInstance();
            JamServiceParams params = jamServiceFactory.createServiceParams();
            File[] dirs = new File[] { sourceDir };
            params.includeSourcePattern(dirs, "**/*.java");
            JamService jam = jamServiceFactory.createService(params);

            if (generateMarshalers) {
                if (!isRangedGenerate()) {
                    runMarshalerGenerateScript(jam, fromVersion);
                    if (toVersion != fromVersion) {
                        runMarshalerGenerateScript(jam, toVersion);
                    }
                } else {
                    for (int i = fromVersion; i <= toVersion; ++i) {
                        runMarshalerGenerateScript(jam, i);
                    }
                }
            }

            if (generateTests) {
                if (!isRangedGenerate()) {
                    runTestGenerateScript(jam, fromVersion);
                    if (toVersion != fromVersion) {
                        runTestGenerateScript(jam, toVersion);
                    }
                } else {
                    for (int i = fromVersion; i <= toVersion; ++i) {
                        runTestGenerateScript(jam, i);
                    }
                }
            }

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    protected void runMarshalerGenerateScript(JamService jam, int version) throws Exception {
        System.out.println("======================================================");
        System.out.println(" Generating Marshallers for OpenWire version: " + version);
        System.out.println("======================================================");
        MarshallingGenerator script = new MarshallingGenerator();
        runScript(script, jam, version);
    }

    protected void runTestGenerateScript(JamService jam, int version) throws Exception {
        System.out.println("======================================================");
        System.out.println(" Generating Test Cases for OpenWire version: " + version);
        System.out.println("======================================================");
        TestsGenerator script = new TestsGenerator();
        runScript(script, jam, version);
    }

    protected void runScript(MultiSourceGenerator script, JamService jam, int version) throws Exception {
        script.setJam(jam);
        script.setTargetDir(targetDir.getCanonicalPath());
        script.setOpenwireVersion(version);
        if (commandsPackage != null) {
            script.setCommandsPackage(commandsPackage);
        }
        if (codecPackageRoot != null) {
            script.setCodecPackageRoot(codecPackageRoot);
        }
        script.run();
    }

    public int getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(int version) {
        this.fromVersion = version;
    }

    public int getToVersion() {
        return toVersion;
    }

    public void setToVersion(int version) {
        this.toVersion = version;
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

    public boolean isGenerateMarshalers() {
        return generateMarshalers;
    }

    public void setGenerateMarshalers(boolean generateMarshalers) {
        this.generateMarshalers = generateMarshalers;
    }

    public boolean isGenerateTests() {
        return generateTests;
    }

    public void setGenerateTests(boolean generateTests) {
        this.generateTests = generateTests;
    }

    public boolean isRangedGenerate() {
        return this.rangedGenerate;
    }

    public void setRangedGenerate(boolean rangedGenerate) {
        this.rangedGenerate = rangedGenerate;
    }
}
