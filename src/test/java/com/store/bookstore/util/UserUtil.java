package com.store.bookstore.util;

import com.store.bookstore.models.Role;
import com.store.bookstore.models.User;
import java.util.Set;

public class UserUtil {

    public static Role getUserRole() {
        Role user = new Role();
        user.setId(1L);
        user.setRoleName(Role.RoleName.ROLE_USER);
        return user;
    }

    public static User getUserFromDb() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alison@gmail.com");
        user.setPassword("noSilA12*-3");
        user.setFirstName("Alison");
        user.setLastName("Bloom");
        user.setShippingAddress("Block st. 1/23");
        user.setRoles(Set.of(getUserRole()));
        return user;
    }
}
