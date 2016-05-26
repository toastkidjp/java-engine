

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
 * JavaScriptEngineFactory.java
 * @author A. Sundararajan
 */

package com.sun.script.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * This is script engine factory for "Java" script engine.
 */
public class JavaScriptEngineFactory implements ScriptEngineFactory {
    @Override
    public String getEngineName() {
        return "java";
    }

    @Override
    public String getEngineVersion() {
        return "1.6";
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public String getLanguageName() {
        return "java";
    }

    @Override
    public String getLanguageVersion() {
        return "1.6";
    }

    @Override
    public String getMethodCallSyntax(final String obj, final String m, final String... args) {
        final StringBuilder buf = new StringBuilder();
        buf.append(obj);
        buf.append(".");
        buf.append(m);
        buf.append("(");
        if (args.length != 0) {
            int i = 0;
            for (; i < args.length - 1; i++) {
                buf.append(args[i] + ", ");
            }
            buf.append(args[i]);
        }
        buf.append(")");
        return buf.toString();
    }

    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getOutputStatement(final String toDisplay) {
        final StringBuilder buf = new StringBuilder();
        buf.append("System.out.print(\"");
        final int len = toDisplay.length();
        for (int i = 0; i < len; i++) {
            final char ch = toDisplay.charAt(i);
            switch (ch) {
            case '"':
                buf.append("\\\"");
                break;
            case '\\':
                buf.append("\\\\");
                break;
            default:
                buf.append(ch);
                break;
            }
        }
        buf.append("\");");
        return buf.toString();
    }

    @Override
    public String getParameter(final String key) {
        if (key.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(ScriptEngine.NAME)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return "MULTITHREADED";
        } else {
            return null;
        }
    }

    @Override
    public String getProgram(final String... statements) {
        // we generate a Main class with main method
        // that contains all the given statements

        final StringBuilder buf = new StringBuilder();
        buf.append("class ");
        buf.append(getClassName());
        buf.append(" {\n");
        buf.append("    public static void main(String[] args) {\n");
        if (statements.length != 0) {
            for (int i = 0; i < statements.length; i++) {
                buf.append("        ");
                buf.append(statements[i]);
                buf.append(";\n");
            }
        }
        buf.append("    }\n");
        buf.append("}\n");
        return buf.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        final JavaScriptEngine engine = new JavaScriptEngine();
        engine.setFactory(this);
        return engine;
    }


    // used to generate a unique class name in getProgram
    private String getClassName() {
        return "com_sun_script_java_Main$" + getNextClassNumber();
    }

    private static synchronized long getNextClassNumber() {
        return nextClassNum++;
    }

    private static long nextClassNum = 0L;
    private static List<String> names;
    private static List<String> extensions;
    private static List<String> mimeTypes;
    static {
        names = new ArrayList<String>(1);
        names.add("java");
        names = Collections.unmodifiableList(names);
        extensions = names;
        mimeTypes = new ArrayList<String>(0);
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
}
