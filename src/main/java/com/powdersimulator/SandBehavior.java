package com.powdersimulator;

public class SandBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        if (ctx.inb(x, by)) {
            CellType below = ctx.getCell(x, by);
            if (below == CellType.Lava) {
                if (Math.random() < 0.1) ctx.setCell(x, y, CellType.Lava);
                return;
            }
            if (below == CellType.Empty || below == CellType.Water || below == CellType.Oil) {
                ctx.swapCell(x, y, x, by);
            } else {
                boolean leftFirst = Math.random() < 0.5;
                if (leftFirst) {
                    if (canSandFall(x - 1, by, ctx)) ctx.swapCell(x, y, x - 1, by);
                    else if (canSandFall(x + 1, by, ctx)) ctx.swapCell(x, y, x + 1, by);
                } else {
                    if (canSandFall(x + 1, by, ctx)) ctx.swapCell(x, y, x + 1, by);
                    else if (canSandFall(x - 1, by, ctx)) ctx.swapCell(x, y, x - 1, by);
                }
            }
        }
    }
    
    private boolean canSandFall(int x, int y, SimulationContext ctx) {
        return ctx.inb(x, y) && (ctx.getCell(x, y) == CellType.Empty || ctx.getCell(x, y) == CellType.Water || ctx.getCell(x, y) == CellType.Oil);
    }
}
