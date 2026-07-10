package com.powdersimulator;

public class LavaBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        if (ctx.inb(x, by)) {
            CellType below = ctx.getCell(x, by);
            if (below == CellType.Empty) {
                if (Math.random() < 0.8) ctx.swapCell(x, y, x, by);
                return;
            }
            if (below == CellType.Water) {
                if (Math.random() < 0.4) ctx.setCell(x, by, CellType.Steam); // Much steam when touching water
                if (Math.random() < 0.05) ctx.setCell(x, y, CellType.Stone);
            }
            if (below == CellType.Fire) {
                if (Math.random() < 0.05) ctx.setCell(x, by, CellType.Steam); // So less steam when touching fire
            }
            if (below == CellType.Wood || below == CellType.Oil) {
                ctx.setCell(x, by, CellType.Fire);
            }
            if (below == CellType.Sand && Math.random() < 0.1) {
                ctx.setCell(x, by, CellType.Lava); 
            }
        }

        int above = y - 1;
        if (ctx.inb(x, above)) {
            CellType top = ctx.getCell(x, above);
            if (top == CellType.Wood || top == CellType.Oil) {
                ctx.setCell(x, above, CellType.Fire);
                ctx.addBurnTimer(x, above, 15);
            } else if (top == CellType.Water) {
                if (Math.random() < 0.4) ctx.setCell(x, above, CellType.Steam); // Much steam
                if (Math.random() < 0.05) {
                    ctx.setCell(x, y, CellType.Stone);
                    return;
                }
            } else if (top == CellType.Fire) {
                if (Math.random() < 0.05) ctx.setCell(x, above, CellType.Steam); // Tiny bit of steam
            } else if (top == CellType.Sand && Math.random() < 0.05) {
                ctx.setCell(x, above, CellType.Lava);
            }
        }
        
        boolean moved = false;
        int[] dirs = Math.random() < 0.5 ? new int[] { -1, 1 } : new int[] { 1, -1 };
        if (Math.random() < 0.4) {
            for (int dir : dirs) {
                int nx = x + dir;
                if (!ctx.inb(nx, y)) continue;
                CellType target = ctx.getCell(nx, y);
                if (target == CellType.Empty) {
                    ctx.swapCell(x, y, nx, y);
                    moved = true;
                    break;
                } else if (target == CellType.Water) {
                    if (Math.random() < 0.4) ctx.setCell(nx, y, CellType.Steam);
                    if (Math.random() < 0.05) ctx.setCell(x, y, CellType.Stone);
                    moved = true;
                    break;
                } else if (target == CellType.Fire) {
                    if (Math.random() < 0.05) ctx.setCell(nx, y, CellType.Steam);
                } else if (target == CellType.Wood || target == CellType.Oil) {
                    ctx.setCell(nx, y, CellType.Fire);
                    ctx.addBurnTimer(nx, y, 15);
                } else if (target == CellType.Sand) {
                    if (Math.random() < 0.05) ctx.setCell(nx, y, CellType.Lava);
                } else if (target == CellType.Stone) {
                    if (Math.random() < 0.01) ctx.setCell(nx, y, CellType.Lava);
                }
            }
        }
        if (moved) return;

        if (Math.random() < 0.6) {
            for (int dir : dirs) {
                int nx = x + dir;
                if (!ctx.inb(nx, by)) continue;
                CellType diagonal = ctx.getCell(nx, by);
                if (diagonal == CellType.Empty) {
                    ctx.swapCell(x, y, nx, by);
                    break;
                } else if (diagonal == CellType.Water) {
                    if (Math.random() < 0.4) ctx.setCell(nx, by, CellType.Steam);
                    if (Math.random() < 0.05) ctx.setCell(x, y, CellType.Stone);
                    break;
                } else if (diagonal == CellType.Fire) {
                    if (Math.random() < 0.05) ctx.setCell(nx, by, CellType.Steam);
                }
            }
        }
    }
}
