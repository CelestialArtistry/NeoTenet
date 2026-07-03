package org.teneted.neotenet.launcher.data;

import java.util.List;

public record Processor(
        String jar,
        List<String> classpath
) {

}
