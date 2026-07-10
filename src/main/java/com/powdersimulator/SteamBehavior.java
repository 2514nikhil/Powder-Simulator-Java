package com.powdersimulator;

public class SteamBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int above = y - 1;
        
        // Steam slowly dissipates into the air over time
        if (Math.random() < 0.03) {
            ctx.setCell(x, y, CellType.Empty);
            return;
        }

        // If at the very top of the screen or hitting a permanent roof, dissipate faster
        if (!ctx.inb(x, above)) {
            ctx.setCell(x, y, CellType.Empty);
            return;
        }

        if (ctx.inb(x, above)) {
            CellType top = ctx.getCell(x, above);
            if (top == CellType.Empty) {
                ctx.swapCell(x, y, x, above);
                return;
            } else if (top != CellType.Steam) {
                // Dissipate rapidly if trapped by solid blocks overhead
                if (Math.random() < 0.1) {
                    ctx.setCell(x, y, CellType.Empty);
                    return;
                }
            }
        }

        boolean leftFirst = Math.random() < 0.5;
        int dir1 = leftFirst ? -1 : 1;
        int dir2 = leftFirst ? 1 : -1;
        
        if (ctx.inb(x + dir1, above) && ctx.getCell(x + dir1, above) == CellType.Empty) {
            ctx.swapCell(x, y, x + dir1, above);
            return;
        }
        if (ctx.inb(x + dir2, above) && ctx.getCell(x + dir2, above) == CellType.Empty) {
            ctx.swapCell(x, y, x + dir2, above);
            return;
        }

        if (Math.random() < 0.5) {
            if (ctx.inb(x + dir1, y) && ctx.getCell(x + dir1, y) == CellType.Empty) {
                ctx.swapCell(x, y, x + dir1, y);
            } else if (ctx.inb(x + dir2, y) && ctx.getCell(x + dir2, y) == CellType.Empty) {
                ctx.swapCell(x, y, x + dir2, y);
            }
        }
    }
}
