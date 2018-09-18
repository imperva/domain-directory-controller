package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.query.Endpoint;
import com.imperva.opensource.ddc.core.query.LdapConnectionResult;

abstract class DriverBase {
    abstract LdapConnectionResult connect(Endpoint endpoint);
}
