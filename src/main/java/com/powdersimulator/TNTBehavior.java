package com.powdersimulator;

public class TNTBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        if (ctx.inb(x, by) && (ctx.getCell(x, by) == CellType.Empty || ctx.getCell(x, by) == CellType.Water || ctx.getCell(x, by) == CellType.Oil)) {
             ctx.swapCell(x, y, x, by);
        }

        boolean ignite = false;
        int[][] dirs = {{0,1}, {0,-1}, {-1,0}, {1,0}, {-1,-1}, {1,1}, {-1,1}, {1,-1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (ctx.inb(nx, ny)) {
                CellType target = ctx.getCell(nx, ny);
                if (target == CellType.Fire || target == CellType.Lava) {
                    ignite = true;
                    break;
                }
            }
        }

        if (ignite) {
            int radius = 10;
            ctx.setCell(x, y, CellType.Empty);
            
            for (int oy = -radius; oy <= radius; oy++) {
                for (int ox = -radius; ox <= radius; ox++) {
                    int nx = x + ox, ny = y + oy;
                    if (ctx.inb(nx, ny) && (ox * ox + oy * oy <= radius * radius)) {
                        CellType current = ctx.getCell(nx, ny);
                        
                        // Metal survives partially, TNT triggers chain reaction directly 
                        if (current != CellType.TNT && current != CellType.Metal) {
                            if (Math.random() < 0.5) {
                                ctx.setCell(nx, ny, CellType.Fire);
                                ctx.addBurnTimer(nx, ny, 10 + (int)(Math.random() * 15));
                            } else {
                                ctx.setCell(nx, ny, CellType.Empty);
                            }
                        } else if (current == CellType.Metal) {
                            if (Math.random() < 0.1) {
                                ctx.setCell(nx, ny, CellType.Fire); // Slightly damages metal
                            }
                        }
                    }
                }
            }
        }
    }
}
