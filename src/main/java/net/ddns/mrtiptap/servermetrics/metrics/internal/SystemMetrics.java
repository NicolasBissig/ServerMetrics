package net.ddns.mrtiptap.servermetrics.metrics.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SystemMetrics implements MeterBinder {

    private final ConfigurationSection configuration;

    private final Map<String, Supplier<MeterBinder>> keyToMetric = new HashMap<>();

    public SystemMetrics(ConfigurationSection configuration) {
        this.configuration = configuration;

        keyToMetric.put("ClassLoader", ClassLoaderMetrics::new);
        keyToMetric.put("DiskSpace", () -> new DiskSpaceMetrics(new File("/")));
        keyToMetric.put("FileDescriptor", FileDescriptorMetrics::new);
        keyToMetric.put("JvmGc", JvmGcMetrics::new);
        keyToMetric.put("JvmHeapPresure", JvmHeapPressureMetrics::new);
        keyToMetric.put("JvmInfo", JvmInfoMetrics::new);
        keyToMetric.put("JvmMemory", JvmMemoryMetrics::new);
        keyToMetric.put("JvmThread", JvmThreadMetrics::new);
        keyToMetric.put("Processor", ProcessorMetrics::new);
        keyToMetric.put("Uptime", UptimeMetrics::new);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        configuration.getKeys(false).stream()
            .map(metric -> keyToMetric.get(metric).get())
            .forEach(binder -> binder.bindTo(registry));
    }
}
