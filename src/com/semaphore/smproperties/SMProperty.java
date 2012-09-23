/*  Semaphore Manager
 *  
 *   Copyright (c) 2012 Stratos Karafotis (stratosk@semaphore.gr)
 *   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package com.semaphore.smproperties;

public abstract class SMProperty extends SMBaseProperty {

    private String Path;
    private boolean Dynamic;

    public boolean isDynamic() {
        return Dynamic;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public SMProperty(String name, String path, boolean dynamic) {
        super(name);
        Path = path;
        Dynamic = dynamic;
    }

    public abstract void readValue();

    public abstract void writeValue();
}
