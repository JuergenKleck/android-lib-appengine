package info.simplyapps.appengine.storage;

import info.simplyapps.appengine.storage.dto.BasicTable;

/**
 * Migration container
 *
 * @author juergen
 */
public class Migrate {

    public int from;
    public int to;
    public BasicTable data;

    public Migrate() {

    }

    public Migrate(int from, int to, BasicTable data) {
        super();
        this.from = from;
        this.to = to;
        this.data = data;
    }

}
