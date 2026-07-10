
with open('src/main/java/com/powdersimulator/CellType.java', 'w', encoding='utf-8') as f:
    f.write('package com.powdersimulator;\n\npublic enum CellType {\n    Empty, Sand, Water, Stone, Metal, Fire, Oil, Lava, Wood, Steam, Acid, TNT\n}')

# Patch WaterBehavior
with open('src/main/java/com/powdersimulator/WaterBehavior.java', 'r', encoding='utf-8') as f:
    text = f.read()
text = text.replace('ctx.setCell(x, y, CellType.Fire)', 'ctx.setCell(x, y, CellType.Steam)')
text = text.replace('ctx.setCell(nx, y, CellType.Fire)', 'ctx.setCell(nx, y, CellType.Steam)')
text = text.replace('ctx.setCell(x + d1, by, CellType.Fire)', 'ctx.setCell(x + d1, by, CellType.Steam)')
text = text.replace('ctx.setCell(x + d2, by, CellType.Fire)', 'ctx.setCell(x + d2, by, CellType.Steam)')
with open('src/main/java/com/powdersimulator/WaterBehavior.java', 'w', encoding='utf-8') as f:
    f.write(text)

# Patch LavaBehavior
with open('src/main/java/com/powdersimulator/LavaBehavior.java', 'r', encoding='utf-8') as f:
    text = f.read()
text = text.replace('ctx.setCell(x, by, CellType.Fire); // Boil water', 'ctx.setCell(x, by, CellType.Steam); // Boil water')
text = text.replace('ctx.setCell(x, above, CellType.Fire);\n                if (Math.random() < 0.05)', 'ctx.setCell(x, above, CellType.Steam);\n                if (Math.random() < 0.05)')
text = text.replace('ctx.setCell(nx, y, CellType.Fire);\n                    if (Math.random() < 0.05)', 'ctx.setCell(nx, y, CellType.Steam);\n                    if (Math.random() < 0.05)')
text = text.replace('ctx.setCell(nx, by, CellType.Fire);\n                    if (Math.random() < 0.05)', 'ctx.setCell(nx, by, CellType.Steam);\n                    if (Math.random() < 0.05)')
with open('src/main/java/com/powdersimulator/LavaBehavior.java', 'w', encoding='utf-8') as f:
    f.write(text)

# Patch PowderEngine
with open('src/main/java/com/powdersimulator/PowderEngine.java', 'r', encoding='utf-8') as f:
    text = f.read()
text = text.replace('behaviors.put(CellType.Wood, new WoodBehavior());', 'behaviors.put(CellType.Wood, new WoodBehavior());\n        behaviors.put(CellType.Steam, new SteamBehavior());\n        behaviors.put(CellType.Acid, new AcidBehavior());\n        behaviors.put(CellType.TNT, new TNTBehavior());')
with open('src/main/java/com/powdersimulator/PowderEngine.java', 'w', encoding='utf-8') as f:
    f.write(text)

# Patch PowderSimulator
with open('src/main/java/com/powdersimulator/PowderSimulator.java', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('"Fire", "Oil", "Lava", "Wood", "Erase"', '"Fire", "Oil", "Lava", "Wood", "Steam", "Acid", "TNT", "Erase"')

text = text.replace('case Wood: \n                if (x % 3 == 0) return Color.rgb(101, 67, 33); // Bark lines\n                return Color.rgb(139, 69, 19);', 'case Wood: \n                if (x % 3 == 0) return Color.rgb(101, 67, 33); \n                return Color.rgb(139, 69, 19);\n            case Steam: \n                return Color.rgb(200, 200, 220); \n            case Acid: \n                double acidPulse = Math.sin(frameCount * 0.1 + x * 0.2 + y * 0.2) * 0.5 + 0.5;\n                return Color.rgb(100 + (int)(acidPulse * 50), 255, 50); \n            case TNT: \n                if (((x / 4) + (y / 4)) % 2 == 0) return Color.rgb(220, 40, 40);\n                return Color.rgb(240, 240, 240);')

text = text.replace('case "Wood": selected = CellType.Wood; break;', 'case "Wood": selected = CellType.Wood; break;\n            case "Steam": selected = CellType.Steam; break;\n            case "Acid": selected = CellType.Acid; break;\n            case "TNT": selected = CellType.TNT; break;')

text = text.replace('(label.equals("Wood") && selected == CellType.Wood) ||', '(label.equals("Wood") && selected == CellType.Wood) ||\n                    (label.equals("Steam") && selected == CellType.Steam) ||\n                    (label.equals("Acid") && selected == CellType.Acid) ||\n                    (label.equals("TNT") && selected == CellType.TNT) ||')

text = text.replace('case DIGIT8: selected = CellType.Wood; break;', 'case DIGIT8: selected = CellType.Wood; break;\n                case DIGIT9: selected = CellType.Acid; break;\n                case DIGIT0: selected = CellType.TNT; break;\n                case MINUS: selected = CellType.Steam; break;')

with open('src/main/java/com/powdersimulator/PowderSimulator.java', 'w', encoding='utf-8') as f:
    f.write(text)

print('Patch applied successfully.')
