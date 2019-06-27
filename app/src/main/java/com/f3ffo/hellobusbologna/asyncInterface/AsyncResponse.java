package com.f3ffo.hellobusbologna.asyncInterface;

import com.f3ffo.hellobusbologna.output.OutputItem;

import java.util.List;

public interface AsyncResponse {
    void processStart();
    void processFinish(List<OutputItem> output);
}
