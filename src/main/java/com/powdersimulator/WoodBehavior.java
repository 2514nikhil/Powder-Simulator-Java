package com.powdersimulator;

public class WoodBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        // Wood is a static solid.
        // It catches fire when Fire/Lava interacts with it.
    }
}
