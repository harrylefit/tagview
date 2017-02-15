package vn.eazy.tagview.event;

import vn.eazy.tagview.model.BaseData;

/**
 * Created by Harry on 2/15/17.
 */

public class SuggestionItemEvent {
    private BaseData data;

    public SuggestionItemEvent(BaseData data) {
        this.data = data;
    }

    public BaseData getData() {
        return data;
    }
}
