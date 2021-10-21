package net.ddns.mrtiptap.servermetrics.metrics.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.DiskSpaceMetrics;
import java.io.File;

public class RootDiskSpaceMetrics implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        for (File root : File.listRoots()) {
            new DiskSpaceMetrics(root).bindTo(registry);
        }
    }
}
