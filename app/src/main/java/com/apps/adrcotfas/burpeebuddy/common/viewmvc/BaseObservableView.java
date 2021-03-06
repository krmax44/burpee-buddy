package com.apps.adrcotfas.burpeebuddy.common.viewmvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseObservableView<ListenerType> extends BaseViewMvc
        implements ObservableView<ListenerType> {

    private final Set<ListenerType> mListeners = new HashSet<>();

    @Override
    public void registerListener(ListenerType listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        mListeners.remove(listener);
    }

    protected Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(mListeners);
    }
}
