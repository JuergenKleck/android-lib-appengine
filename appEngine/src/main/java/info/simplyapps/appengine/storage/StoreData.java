package info.simplyapps.appengine.storage;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.appengine.storage.dto.Extensions;
import info.simplyapps.appengine.storage.dto.Purchases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class StoreData implements Serializable {

    private static final long serialVersionUID = 5696810296031292822L;

    public int migration;
    public List<Purchases> purchases;
    public List<Configuration> configuration;
    public List<Extensions> extensions;

    public StoreData() {
        purchases = new ArrayList<Purchases>();
        configuration = new ArrayList<Configuration>();
        extensions = new ArrayList<Extensions>();
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
