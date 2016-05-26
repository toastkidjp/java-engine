Java Engine
===

This is JSR-223 script engine for "Java". This engine uses Java Compiler
API (JSR 199) to compile and generate bytecode from Java source.

A Java source is evaluated by compiling it to bytecode and then calling
the public static void main(String[]) method. Memory backed JavaFileManager
implementation and byte buffer class loader is used to compile and load
Java classes.

There are 5 pre-defined variables that can be set in the ScriptContext
before calling ScriptEngine.eval() or ScriptEngine.compile() method.

When using this engine with your Java program, you need to put tools.jar
under $JAVA_HOME/lib in your CLASSPATH.

1) sourcepath

-- path for additional .java source files that may be required for 
compilation. This value is passed for the -sourcepath option of javac. 
If not specified, the value of system property "com.sun.script.java.sourcepath"
is used. If that is not set, then no -sourcepath option will be set.

2) classpath

-- path for .java class files that may be required for compilation. 
This value is passed for the -classpath option of javac. If not specified,
the value of system property "com.sun.script.java.classpath" is used.
If that is not set, then no -classpath option will be set.

3) arguments 

-- argument array to be passed to the main method during eval. If not specified,
then an empty String array is passed to main method.

4) parentLoader

-- ClassLoader that is used as parent loader for the loader that loads 
compiled (i.e., generated by ScriptEngine.eval()) classes. If this is not
set, then the value of system property "com.sun.script.java.parentLoader" 
is used. If this is not set, null parent loader will be used.

5) mainClass 

-- class whose main method is called. If not specified then the value of 
system property "com.sun.script.java.mainClass" is used. If that is not
specified, first class with main method is selected. Note that order depends 
on javac's .class emitting order. It is better to have atmost one class with 
main method or configure mainClass explicitly.

ScriptEngine.eval() method returns the Class object of the main class. 
ScriptException is thrown when no main class is configued and none is found 
automatically.

Unlike other script engines, this is a script engine for a statically and
strongly typed language. Also, Java does not have global variables. So, 
ScriptContext is exposed differently. The script engine looks for optional

    public static void setScriptContext(ScriptContext ctx)

method in the main class and calls the same (if found) before calling main.