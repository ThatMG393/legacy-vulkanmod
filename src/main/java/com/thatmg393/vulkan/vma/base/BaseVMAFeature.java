package com.thatmg393.vulkan.vma.base;

public abstract class BaseVMAFeature<T extends BaseFeatureBuilder> {
    public abstract void create(T params);
    public abstract void destroy(long id);
}
