package com.powdersimulator;

public class OilBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        ctx.incOilAge(x, y);

        int by = y + 1;
        int above = y - 1;
        
        if (ctx.inb(x, by) && ctx.getCell(x, by) == CellType.Lava) {
            ctx.setCell(x, y, CellType.Fire);
            ctx.addBurnTimer(x, y, 20);
            return;
        }

        if (ctx.inb(x, above) && ctx.getCell(x, above) == CellType.Water) {
            ctx.swapCell(x, y, x, above);
            return;
        }

        if (ctx.inb(x, by) && ctx.getCell(x, by) == CellType.Empty) {
            if (Math.random() < 0.6) ctx.swapCell(x, y, x, by);
            return;
        }

        if (Math.random() < 0.85) {
            int dir = Math.random() < 0.5 ? -1 : 1;
            if (ctx.inb(x + dir, y) && ctx.getCell(x + dir, y) == CellType.Empty) {
                ctx.swapCell(x, y, x + dir, y);
                return;
            } else if (ctx.inb(x - dir, y) && ctx.getCell(x - dir, y) == CellType.Empty) {
                ctx.swapCell(x, y, x - dir, y);
                return;
            }
        }
        
        int dir = Math.random() < 0.5 ? -1 : 1;
        if (ctx.inb(x + dir, by) && ctx.getCell(x + dir, by) == CellType.Empty && Math.random() < 0.25) {
            ctx.swapCell(x, y, x + dir, by);
        }
    }
}
