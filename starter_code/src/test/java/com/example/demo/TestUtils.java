package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {

    public static <K, T>void InjectObjects(K target, String fieldName, T toInject){
        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createUser(){
        User u = new User();
        u.setId(1);
        u.setUsername("test");
        u.setPassword("thisIsHashed");

        Cart c = new Cart();
        c.setId(1L);
        c.setUser(u);
        c.setTotal(BigDecimal.ZERO);
        u.setCart(c);
        return u;
    }

    public static Item createItem(){
        Item i = new Item();
        i.setId(1L);
        i.setDescription("Desc");
        i.setName("Item");
        i.setPrice(BigDecimal.TEN);
        return i;
    }
}
