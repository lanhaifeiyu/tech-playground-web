package com.lhfeiyu.tech.DTO;

import java.security.Principal;

public class FastPrincipal implements Principal {

    private final String name;

    @Override
    public String getName() {
        return this.name;
    }

    public FastPrincipal(String name) {
        this.name = name;
    }
}
