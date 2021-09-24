package net.ddns.mrtiptap.servermetrics.ticktime;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class TickEndListener implements Listener {

    private final TickTimeRecorder recorder;

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        recorder.recordTickTime(event.getTickDuration());
    }

}
