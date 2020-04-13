package info.simplyapps.appengine;

import info.simplyapps.appengine.storage.StoreData;
import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.appengine.storage.dto.Extensions;

/**
 * @author simplyapps.info
 */
public abstract class SystemHelper {

    public static boolean notEmpty(String s) {
        return s != null && s.length() > 0;
    }

    public synchronized static final boolean hasConfiguration(String name) {
        if (StoreData.getInstance().configuration != null) {
            for (Configuration c : StoreData.getInstance().configuration) {
                if (c.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized static final Configuration getConfiguration(String name, String defaultValue) {
        if (StoreData.getInstance().configuration != null) {
            for (Configuration c : StoreData.getInstance().configuration) {
                if (c.name.equals(name)) {
                    return c;
                }
            }
        }
        return new Configuration(name, defaultValue);
    }

    public synchronized static final void setConfiguration(Configuration cNew) {
        if (StoreData.getInstance().configuration != null) {
            Configuration cOld = null;
            for (Configuration c : StoreData.getInstance().configuration) {
                if (c.name.equals(cNew.name)) {
                    cOld = c;
                    break;
                }
            }
            if (cOld != null) {
                StoreData.getInstance().configuration.remove(cOld);
                // keep persistence state
                if (cOld.id > -1) {
                    cNew.id = cOld.id;
                }
            }
            StoreData.getInstance().configuration.add(cNew);
        }
    }

    public synchronized static final Extensions getExtensions(String name) {
        if (StoreData.getInstance().extensions != null) {
            for (Extensions c : StoreData.getInstance().extensions) {
                if (c.name.equals(name)) {
                    return c;
                }
            }
        }
        return new Extensions(name, -1);
    }

    public synchronized static final void setExtensions(Extensions cNew) {
        if (StoreData.getInstance().extensions != null) {
            Extensions cOld = null;
            for (Extensions c : StoreData.getInstance().extensions) {
                if (c.name.equals(cNew.name)) {
                    cOld = c;
                    break;
                }
            }
            if (cOld != null) {
                StoreData.getInstance().extensions.remove(cOld);
                // keep persistence state
                if (cOld.id > -1) {
                    cNew.id = cOld.id;
                }
            }
            StoreData.getInstance().extensions.add(cNew);
        }
    }

    public static final boolean hasExtensionByName(String name) {
        Extensions ext = getExtensions(name);
        return ext != null && ext.amount > -1;
    }

    public static final boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

}
