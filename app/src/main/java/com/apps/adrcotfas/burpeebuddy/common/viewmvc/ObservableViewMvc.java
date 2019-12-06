package com.apps.adrcotfas.burpeebuddy.common.viewmvc;

public interface ObservableViewMvc<ListenerType> extends ViewMvc {

    void registerListener(ListenerType listener);

    void unregisterListener(ListenerType listener);
}
