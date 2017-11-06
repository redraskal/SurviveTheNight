package me.redraskal.survivethenight.utils;

import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class NBTUtils {

    public static void setInt(Entity entity, String nbtTag, int value) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> c_craftEntity = Class.forName(NMSUtils.fetchBukkitClass("entity.CraftEntity"));
        Class<?> c_entity = Class.forName(NMSUtils.fetchNMSClass("Entity"));
        Class<?> c_nbtTagCompound = Class.forName(NMSUtils.fetchNMSClass("NBTTagCompound"));

        Method m_getHandle = c_craftEntity.getDeclaredMethod("getHandle");
        Method m_getNBTTag = c_entity.getDeclaredMethod("getNBTTag");
        Method m_c = c_entity.getDeclaredMethod("c", c_nbtTagCompound);
        Method m_f = c_entity.getDeclaredMethod("f", c_nbtTagCompound);

        Method m_set = c_nbtTagCompound.getDeclaredMethod("setInt", String.class, int.class);

        Object nmsEntity = c_entity.cast(m_getHandle.invoke(c_craftEntity.cast(entity)));
        Object nbtTagCompound = m_getNBTTag.invoke(nmsEntity);
        if(nbtTagCompound == null) nbtTagCompound = c_nbtTagCompound.newInstance();
        m_c.invoke(nmsEntity, nbtTagCompound);

        m_set.invoke(nbtTagCompound, nbtTag, value);

        m_f.invoke(nmsEntity, nbtTagCompound);
    }
}