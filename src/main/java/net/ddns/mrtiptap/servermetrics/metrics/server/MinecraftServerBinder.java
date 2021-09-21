package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@RequiredArgsConstructor
@Getter
public abstract class MinecraftServerBinder implements MeterBinder {
    private final Server minecraftServer;
}
