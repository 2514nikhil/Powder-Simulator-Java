package com.powdersimulator;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class PowderEngine implements SimulationContext {
    private final int width;
    private final int height;
    private final CellType[] grid;
    private final int[] stoneVelocity;
    private final int[] burnTimer;
    private final int[] oilAge;
    
    private final Map<CellType, ElementBehavior> behaviors = new EnumMap<>(CellType.class);

    public PowderEngine(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        grid = new CellType[size];
        stoneVelocity = new int[size];
        burnTimer = new int[size];
        oilAge = new int[size];
        
        Arrays.fill(grid, CellType.Empty);
        
        behaviors.put(CellType.Sand, new SandBehavior());
        behaviors.put(CellType.Water, new WaterBehavior());
        behaviors.put(CellType.Stone, new StoneBehavior());
        behaviors.put(CellType.Oil, new OilBehavior());
        behaviors.put(CellType.Fire, new FireBehavior());
        behaviors.put(CellType.Lava, new LavaBehavior());
        behaviors.put(CellType.Wood, new WoodBehavior());
        behaviors.put(CellType.Steam, new SteamBehavior());
        behaviors.put(CellType.Acid, new AcidBehavior());
        behaviors.put(CellType.TNT, new TNTBehavior());
    }
    
    public void clear() {
        Arrays.fill(grid, CellType.Empty);
        Arrays.fill(stoneVelocity, 0);
        Arrays.fill(burnTimer, 0);
        Arrays.fill(oilAge, 0);
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    private int idx(int x, int y) { return y * width + x; }
    
    @Override
    public boolean inb(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public CellType getCell(int x, int y) {
        return inb(x, y) ? grid[idx(x, y)] : CellType.Empty;
    }

    @Override
    public void setCell(int x, int y, CellType ct) {
        if (!inb(x, y)) return;
        int i = idx(x, y);
        grid[i] = ct;
        stoneVelocity[i] = 0;
        burnTimer[i] = 0;
        oilAge[i] = 0;
        if (ct == CellType.Fire) {
            burnTimer[i] = 8 + (int) (Math.random() * 12);
        } else if (ct == CellType.Oil) {
            oilAge[i] = 0;
        }
    }

    @Override
    public void swapCell(int x1, int y1, int x2, int y2) {
        if (!inb(x1, y1) || !inb(x2, y2)) return;
        int i1 = idx(x1, y1), i2 = idx(x2, y2);
        
        CellType tmp = grid[i1]; grid[i1] = grid[i2]; grid[i2] = tmp;
        int tmpVel = stoneVelocity[i1]; stoneVelocity[i1] = stoneVelocity[i2]; stoneVelocity[i2] = tmpVel;
        int tmpBurn = burnTimer[i1]; burnTimer[i1] = burnTimer[i2]; burnTimer[i2] = tmpBurn;
        int tmpOil = oilAge[i1]; oilAge[i1] = oilAge[i2]; oilAge[i2] = tmpOil;
    }

    @Override
    public int getVelocity(int x, int y) { return inb(x, y) ? stoneVelocity[idx(x, y)] : 0; }
    @Override
    public void setVelocity(int x, int y, int vel) { if (inb(x, y)) stoneVelocity[idx(x, y)] = vel; }

    @Override
    public int getBurnTimer(int x, int y) { return inb(x, y) ? burnTimer[idx(x, y)] : 0; }
    @Override
    public void setBurnTimer(int x, int y, int time) { if (inb(x, y)) burnTimer[idx(x, y)] = time; }
    @Override
    public void addBurnTimer(int x, int y, int amount) { if (inb(x, y)) burnTimer[idx(x, y)] += amount; }

    @Override
    public int getOilAge(int x, int y) { return inb(x, y) ? oilAge[idx(x, y)] : 0; }
    @Override
    public void incOilAge(int x, int y) { if (inb(x, y)) oilAge[idx(x, y)]++; }

    private boolean leftToRight = true;

    public void step() {
        leftToRight = !leftToRight;
        
        for (int y = height - 2; y >= 0; y--) {
            if (leftToRight) {
                for (int x = 0; x < width; x++) {
                    processCell(x, y);
                }
            } else {
                for (int x = width - 1; x >= 0; x--) {
                    processCell(x, y);
                }
            }
        }
    }

    private void processCell(int x, int y) {
        CellType me = grid[idx(x, y)];
        ElementBehavior behavior = behaviors.get(me);
        if (behavior != null) {
            behavior.update(x, y, this);
        }
    }

    public int count(CellType type) {
        int count = 0;
        for (CellType c : grid) {
            if (c == type) count++;
        }
        return count;
    }
}
