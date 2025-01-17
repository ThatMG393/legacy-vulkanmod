package com.thatmg393.vulkan.synchronization.base;

// Don't forget to add 'synchronized'!
public abstract class Synchronization<T> {
    public abstract void add(T obj);
    public abstract void waitAll();
}
