package com.thatmg393.vkapi.shader.descriptor;

import com.thatmg393.vkapi.shader.descriptor.base.BaseDescriptor;
import com.thatmg393.vkapi.shader.layout.AlignedStruct;

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
