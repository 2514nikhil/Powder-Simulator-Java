package com.powdersimulator;

public class WaterBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        if (ctx.inb(x, by)) {
            CellType below = ctx.getCell(x, by);
            if (below == CellType.Lava) {
                if (Math.random() < 0.4) ctx.setCell(x, y, CellType.Steam);
                if (Math.random() < 0.05) ctx.setCell(x, by, CellType.Stone);
                return;
            }
            if (below == CellType.Fire) {
                ctx.setCell(x, by, CellType.Empty);
                if (Math.random() < 0.2) { ctx.setCell(x, y, CellType.Steam); return; }
            }
            if (below == CellType.Empty || below == CellType.Oil) {
                ctx.swapCell(x, y, x, by);
                return;
            }
        }

        boolean leftFirst = Math.random() < 0.5;
        int d1 = leftFirst ? -1 : 1;
        int d2 = leftFirst ? 1 : -1;

        if (ctx.inb(x + d1, by)) {
            CellType diag = ctx.getCell(x + d1, by);
            if (diag == CellType.Lava) {
                if (Math.random() < 0.4) ctx.setCell(x, y, CellType.Steam);
                if (Math.random() < 0.05) ctx.setCell(x + d1, by, CellType.Stone);
                return;
            }
            if (diag == CellType.Empty || diag == CellType.Oil) { ctx.swapCell(x, y, x + d1, by); return; }
        }
        if (ctx.inb(x + d2, by)) {
            CellType diag = ctx.getCell(x + d2, by);
            if (diag == CellType.Lava) {
                if (Math.random() < 0.4) ctx.setCell(x, y, CellType.Steam);
                if (Math.random() < 0.05) ctx.setCell(x + d2, by, CellType.Stone);
                return;
            }
            if (diag == CellType.Empty || diag == CellType.Oil) { ctx.swapCell(x, y, x + d2, by); return; }
        }

        int maxFlow = 5;
        for (int attempt = 0; attempt < 2; attempt++) {
            int d = (attempt == 0) ? d1 : d2;
            int flowDist = 0;
            
            for (int i = 1; i <= maxFlow; i++) {
                int nx = x + i * d;
                if (!ctx.inb(nx, y)) break;
                CellType target = ctx.getCell(nx, y);
                
                if (target == CellType.Lava) {
                    if (Math.random() < 0.4) ctx.setCell(x, y, CellType.Steam);
                    if (Math.random() < 0.05) ctx.setCell(nx, y, CellType.Stone);
                    return;
                }
                if (target == CellType.Fire) {
                    ctx.setCell(nx, y, CellType.Empty);
                }
                
                if (target != CellType.Empty && target != CellType.Oil) break;
                flowDist = i;
                
                if (ctx.inb(nx, y + 1)) {
                    CellType belowNode = ctx.getCell(nx, y + 1);
                    if (belowNode == CellType.Lava) {
                        if (Math.random() < 0.4) ctx.setCell(x, y, CellType.Steam);
                        if (Math.random() < 0.05) ctx.setCell(nx, y + 1, CellType.Stone);
                        return;
                    }
                    if (belowNode == CellType.Empty || belowNode == CellType.Oil) break;
                }
            }
            if (flowDist > 0) {
                ctx.swapCell(x, y, x + d * flowDist, y);
                return;
            }
        }
    }
}
