package com.f3ffo.hellobusbologna.asyncInterface;

import com.f3ffo.hellobusbologna.items.OutputCardViewItem;

import java.util.List;

public interface AsyncResponse {
    void processStart();
    void processFinish(List<OutputCardViewItem> output);
}
