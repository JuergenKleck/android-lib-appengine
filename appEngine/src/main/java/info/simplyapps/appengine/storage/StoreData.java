package info.simplyapps.appengine.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.appengine.storage.dto.Extensions;

public abstract class StoreData implements Serializable {

    private static final long serialVersionUID = 5696810296031292822L;

    public int migration;
    public List<Configuration> configuration;
    public List<Extensions> extensions;

    public StoreData() {
        configuration = new ArrayList<>();
        extensions = new ArrayList<>();
    }

    private static StoreData self;

    public static void createInstance(StoreData obj) {
        self = obj;
    }

    public static StoreData getInstance() {
        return self;
    }

    /**
     * Update to the latest release
     */
    public abstract boolean update();

}
