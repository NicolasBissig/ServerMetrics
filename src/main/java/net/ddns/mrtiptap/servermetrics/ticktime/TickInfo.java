package net.ddns.mrtiptap.servermetrics.ticktime;

import lombok.Value;

@Value
public class TickInfo {
    double minimum;
    double average;
    double maximum;
    double durationSum;
}
