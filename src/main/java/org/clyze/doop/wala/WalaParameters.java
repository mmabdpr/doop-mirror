package org.clyze.doop.wala;

import org.clyze.doop.common.Parameters;

import java.util.ArrayList;
import java.util.List;

class WalaParameters extends Parameters {
    List<String> _platformLibraries = new ArrayList<>();
    String _javaPath = null;
    boolean _generateIR = false;

    public void setPlatformLibraries(List<String> libraries) {
        this._platformLibraries = libraries;
    }

    public List<String> getPlatformLibraries() {
        return this._platformLibraries;
    }

}
