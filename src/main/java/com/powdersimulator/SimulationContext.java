package com.powdersimulator;

public interface SimulationContext {
    CellType getCell(int x, int y);
    void setCell(int x, int y, CellType ct);
    void swapCell(int x1, int y1, int x2, int y2);
    boolean inb(int x, int y);
    
    int getVelocity(int x, int y);
    void setVelocity(int x, int y, int vel);
    
    int getBurnTimer(int x, int y);
    void setBurnTimer(int x, int y, int time);
    void addBurnTimer(int x, int y, int amount);
    
    int getOilAge(int x, int y);
    void incOilAge(int x, int y);
}
