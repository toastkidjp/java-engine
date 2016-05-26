/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the Sun Microsystems nor the names of
 * is contributors may be used to endorse or promote products derived from this software
 * without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * MemoryClassLoader.java
 * @author A. Sundararajan
 */

package com.sun.script.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ClassLoader that loads .class bytes from memory.
 */
public final class MemoryClassLoader extends URLClassLoader {
    private final Map<String, byte[]> classBytes;

    public MemoryClassLoader(final Map<String, byte[]> classBytes,
               final String classPath, final ClassLoader parent) {
        super(toURLs(classPath), parent);
        this.classBytes = classBytes;
    }

    public MemoryClassLoader(final Map<String, byte[]> classBytes, final String classPath) {
        this(classBytes, classPath, null);
    }

    public Class load(final String className) throws ClassNotFoundException {
        return loadClass(className);
    }

    public Iterable<Class> loadAll() throws ClassNotFoundException {
        final List<Class> classes = new ArrayList<Class>(classBytes.size());
        for (final String name : classBytes.keySet()) {
            classes.add(loadClass(name));
        }
        return classes;
    }

    @Override
    protected Class findClass(final String className) throws ClassNotFoundException {
        final byte[] buf = classBytes.get(className);
        if (buf != null) {
            // clear the bytes in map -- we don't need it anymore
            classBytes.put(className, null);
            return defineClass(className, buf, 0, buf.length);
        } else {
            return super.findClass(className);
        }
    }

    private static URL[] toURLs(final String classPath) {
        if (classPath == null) {
            return new URL[0];
        }

        final List<URL> list = new ArrayList<URL>();
        final StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final File file = new File(token);
            if (file.exists()) {
                try {
                    list.add(file.toURI().toURL());
                } catch (final MalformedURLException mue) {}
            } else {
                try {
                    list.add(new URL(token));
                } catch (final MalformedURLException mue) {}
            }
        }
        final URL[] res = new URL[list.size()];
        list.toArray(res);
        return res;
    }
}