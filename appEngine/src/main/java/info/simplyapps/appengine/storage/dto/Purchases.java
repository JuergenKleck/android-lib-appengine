package info.simplyapps.appengine.storage.dto;

import java.io.Serializable;

public final class Purchases extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -213755492728977914L;

    public Purchases() {

    }

    public Purchases(String name) {
        this.name = name;
    }

    public String name;

}
