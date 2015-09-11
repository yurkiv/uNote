package com.yurkiv.unote.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by yurkiv on 10.09.2015.
 */
public class BusProvider {
    private static final Bus BUS=new Bus(ThreadEnforcer.MAIN);

    public BusProvider() {
    }

    public static Bus getInstance() {
        return BUS;
    }
}
