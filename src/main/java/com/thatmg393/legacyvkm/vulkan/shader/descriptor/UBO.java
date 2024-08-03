package com.thatmg393.legacyvkm.vulkan.shader.descriptor;

import com.thatmg393.legacyvkm.vulkan.shader.descriptor.base.BaseDescriptor;
import com.thatmg393.legacyvkm.vulkan.shader.layout.AlignedStruct;

public class UBO extends AlignedStruct implements BaseDescriptor {
    @Override
    public int getBinding() {
        return 0;
    }

    @Override
    public int getStages() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }
}

