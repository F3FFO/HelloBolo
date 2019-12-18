package com.f3ffo.hellobolo.asyncInterface;

import com.f3ffo.hellobolo.output.OutputItem;

import java.util.List;

public interface AsyncResponseUrl {
    void processStart();

    void processFinish(List<OutputItem> output);
}
