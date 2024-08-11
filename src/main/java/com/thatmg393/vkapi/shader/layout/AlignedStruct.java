package com.thatmg393.vkapi.shader.layout;

import java.util.ArrayList;

public abstract class AlignedStruct {
    private ArrayList<Uniform> uniforms = new ArrayList<>();
    private int size;

    public void update(long ptr) {
        uniforms.forEach(e -> e.update(ptr));
    }
}
