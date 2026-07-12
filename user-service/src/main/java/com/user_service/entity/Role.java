package com.user_service.entity;

import org.springframework.security.core.parameters.P;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permissions.USER_DELETE,Permissions.USER_WRITE,Permissions.USER_READ)),
    SELLER(Set.of(Permissions.USER_WRITE,Permissions.USER_READ)),
    CUSTOMER(Set.of(Permissions.USER_READ)),
    ;

    private final Set<Permissions> permissions;


    Role(Set<Permissions> permissions){this.permissions=permissions;}

    public Set<Permissions> getPermissions(){return permissions;}

}
