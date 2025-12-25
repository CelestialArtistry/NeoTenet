package org.teneted.neotenet.launcher;

import java.lang.instrument.Instrumentation;

public class NeoTenetAgent {


    public static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent start");
        NeoTenetAgent.instrumentation = instrumentation;
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        premain(args,instrumentation);
    }
}