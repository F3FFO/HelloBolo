package com.f3ffo.busbolo.asyncInterface;

import com.f3ffo.busbolo.output.OutputItem;

import java.util.List;

public interface AsyncResponseUrl {
    void processStart();

    void processFinish(List<OutputItem> output);
}
