package com.imperva.ddc.core;

import com.imperva.ddc.core.query.Endpoint;
import com.imperva.ddc.core.query.LdapConnectionResult;

abstract class DriverBase {
    abstract LdapConnectionResult connect(Endpoint endpoint);
}
