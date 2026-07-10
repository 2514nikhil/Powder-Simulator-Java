package com.powdersimulator;

public class FireBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int burnTimer = ctx.getBurnTimer(x, y);
        burnTimer = Math.max(0, burnTimer - 1);
        ctx.setBurnTimer(x, y, burnTimer);

        if (burnTimer <= 15) {
            int above = y - 1;
            if (ctx.inb(x, above) && ctx.getCell(x, above) == CellType.Empty) {
                ctx.swapCell(x, y, x, above);
                return; // skip spreading this tick
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (!ctx.inb(nx, ny)) continue;
                
                CellType target = ctx.getCell(nx, ny);
                if (target == CellType.Oil) {
                    if (Math.random() < 0.4) {
                        ctx.setCell(nx, ny, CellType.Fire);
                        ctx.addBurnTimer(nx, ny, 6 + (int) (Math.random() * 6));
                    }
                } else if (target == CellType.Wood) {
                    if (Math.random() < 0.1) { ctx.setCell(nx, ny, CellType.Fire); ctx.addBurnTimer(nx, ny, 10 + (int) (Math.random() * 8)); }
                } else if (target == CellType.Sand) {
                    if (Math.random() < 0.06) {
                        ctx.setCell(nx, ny, CellType.Metal);
                    }
                } else if (target == CellType.Water) {
                    if (Math.random() < 0.6) {
                        ctx.setCell(x, y, CellType.Empty);
                        continue;
                    }
                }
            }
        }

        if (burnTimer <= 0) {
            ctx.setCell(x, y, CellType.Empty);
        }
    }
}
