package com.f3ffo.hellobusbologna;

import java.util.List;

public interface AsyncResponse {
    void processStart();
    void processFinish(List<CardViewItem> output);
}
