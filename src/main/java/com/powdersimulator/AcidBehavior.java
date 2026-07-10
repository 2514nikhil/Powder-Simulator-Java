package com.powdersimulator;

public class AcidBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        
        int[][] dirs = {{0,1}, {0,-1}, {-1,0}, {1,0}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (ctx.inb(nx, ny)) {
                CellType target = ctx.getCell(nx, ny);
                if (target == CellType.Wood || target == CellType.Stone || target == CellType.Metal || target == CellType.Sand) {
                    // Acid corrodes materials and turns into Steam
                    if (Math.random() < 0.05) { 
                        ctx.setCell(nx, ny, CellType.Steam); 
                        ctx.setCell(x, y, CellType.Empty);
                        return; 
                    }
                }
                if (target == CellType.Water) {
                    if (Math.random() < 0.05) {
                        ctx.setCell(x, y, CellType.Water);
                        return;
                    }
                }
            }
        }

        if (ctx.inb(x, by)) {
            CellType below = ctx.getCell(x, by);
            if (below == CellType.Empty || below == CellType.Water) {
                ctx.swapCell(x, y, x, by);
                return;
            }
        }
        
        int dir = Math.random() < 0.5 ? -1 : 1;
        if (ctx.inb(x + dir, by) && ctx.getCell(x + dir, by) == CellType.Empty) {
            ctx.swapCell(x, y, x + dir, by); return;
        } else if (ctx.inb(x - dir, by) && ctx.getCell(x - dir, by) == CellType.Empty) {
            ctx.swapCell(x, y, x - dir, by); return;
        }
        
        if (ctx.inb(x + dir, y) && ctx.getCell(x + dir, y) == CellType.Empty) {
            ctx.swapCell(x, y, x + dir, y); return;
        } else if (ctx.inb(x - dir, y) && ctx.getCell(x - dir, y) == CellType.Empty) {
            ctx.swapCell(x, y, x - dir, y); return;
        }
    }
}
