/*
 * The MIT License
 *
 * Copyright (c) 2013-2014, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.docker.commons.client;

import org.jenkinsci.plugins.docker.commons.fingerprint.ContainerRecord;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class RunCommand extends DockerCommand {

    private final String image;
    private String command;
    private String[] commandArgs;

    public RunCommand(@Nonnull String image) {
        args.add("run");
        this.image = image;
    }

    public ContainerRecord getContainer() {
        String containerId = getOut();
        if (containerId == null) {
            return null;
        }

        // TODO need to docker inpsect the container and get some info from it.
        
        return new ContainerRecord("", containerId, "", 0L, null);
    }

    @Override
    public void preLaunch() {
        // The image name and the 
        args.add(image);
        // Add the command (to be executed in the container), if supplied
        if (command != null) {
            args.add(command);
            if (commandArgs != null && commandArgs.length > 0) {
                args.add(commandArgs);
            }
        }
    }

    public RunCommand withContainerCommand(@Nonnull String command, String... args) {
        this.command = command;
        this.commandArgs = args;
        return this;
    }

    public RunCommand detached() {
        args.add(DockerCommandOptions.DETACHED.name());
        return this;
    }

    public RunCommand withEnvVar(@Nonnull String name, @Nonnull String value) {
        return withEnvVar(name, value, false);
    }

    public RunCommand withEnvVar(@Nonnull String name, @Nonnull String value, boolean masked) {
        String var = String.format("%s=%s", name, value);
        args.add(DockerCommandOptions.ENV_VAR.name());
        if (masked) {
            args.addMasked(var);
        } else {
            args.add(var);
        }
        return this;
    }
}
