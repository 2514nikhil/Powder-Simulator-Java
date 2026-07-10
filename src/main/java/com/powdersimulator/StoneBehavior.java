package com.powdersimulator;

public class StoneBehavior implements ElementBehavior {
    @Override
    public void update(int x, int y, SimulationContext ctx) {
        int by = y + 1;
        int vel = ctx.getVelocity(x, y);

        if (ctx.inb(x, by) && ctx.getCell(x, by) == CellType.Empty) {
            ctx.setVelocity(x, by, Math.min(vel + 1, 12));
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x, by);
        } else if (ctx.inb(x, by) && (ctx.getCell(x, by) == CellType.Water || ctx.getCell(x, by) == CellType.Oil)) {
            ctx.setVelocity(x, by, Math.max(vel - 2, 0));
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x, by);
        } else if (ctx.inb(x, by) && ctx.getCell(x, by) == CellType.Lava) {
            if (Math.random() < 0.3) {
                 ctx.setVelocity(x, by, 0); ctx.setVelocity(x, y, 0);
                 ctx.swapCell(x, y, x, by);
            } else { tryStoneRoll(x, y, vel, ctx); }
        } else if (ctx.inb(x, by) && ctx.getCell(x, by) == CellType.Sand) {
            if (vel >= 5) {
                ctx.setVelocity(x, by, Math.max(vel - 3, 0));
                ctx.setVelocity(x, y, 0);
                ctx.swapCell(x, y, x, by);
            } else {
                ctx.setVelocity(x, y, 0);
                tryStoneRoll(x, y, vel, ctx);
            }
        } else {
            ctx.setVelocity(x, y, 0);
            tryStoneRoll(x, y, vel, ctx);
        }
    }

    private void tryStoneRoll(int x, int y, int vel, SimulationContext ctx) {
        int by = y + 1;

        boolean canRollLeft = ctx.inb(x - 1, by) &&
                (ctx.getCell(x - 1, by) == CellType.Empty || ctx.getCell(x - 1, by) == CellType.Water || ctx.getCell(x - 1, by) == CellType.Oil);
        boolean canRollRight = ctx.inb(x + 1, by) &&
                (ctx.getCell(x + 1, by) == CellType.Empty || ctx.getCell(x + 1, by) == CellType.Water || ctx.getCell(x + 1, by) == CellType.Oil);

        boolean leftHasFallSpace = canRollLeft && ctx.inb(x - 1, by + 1) &&
                (ctx.getCell(x - 1, by + 1) == CellType.Empty || ctx.getCell(x - 1, by + 1) == CellType.Water || ctx.getCell(x - 1, by + 1) == CellType.Oil);
        boolean rightHasFallSpace = canRollRight && ctx.inb(x + 1, by + 1) &&
                (ctx.getCell(x + 1, by + 1) == CellType.Empty || ctx.getCell(x + 1, by + 1) == CellType.Water || ctx.getCell(x + 1, by + 1) == CellType.Oil);

        double rollProbability = Math.min(0.85, 0.25 + (vel * 0.1));

        if (leftHasFallSpace && rightHasFallSpace) {
            if (Math.random() < rollProbability) {
                int dir = Math.random() < 0.5 ? -1 : 1;
                ctx.setVelocity(x + dir, by, Math.max(1, vel - 1));
                ctx.setVelocity(x, y, 0);
                ctx.swapCell(x, y, x + dir, by);
            }
        } else if (leftHasFallSpace && Math.random() < rollProbability) {
            ctx.setVelocity(x - 1, by, Math.max(1, vel - 1));
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x - 1, by);
        } else if (rightHasFallSpace && Math.random() < rollProbability) {
            ctx.setVelocity(x + 1, by, Math.max(1, vel - 1));
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x + 1, by);
        } else if (canRollLeft && Math.random() < rollProbability * 0.45) {
            ctx.setVelocity(x - 1, by, 0);
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x - 1, by);
        } else if (canRollRight && Math.random() < rollProbability * 0.45) {
            ctx.setVelocity(x + 1, by, 0);
            ctx.setVelocity(x, y, 0);
            ctx.swapCell(x, y, x + 1, by);
        }
    }
}
