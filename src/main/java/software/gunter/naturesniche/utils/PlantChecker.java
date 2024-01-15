package software.gunter.naturesniche.utils;

import net.minecraft.block.*;

import java.util.HashSet;
import java.util.Set;

public class PlantChecker {
    private static final Set<Class<?>> validPlantClasses = new HashSet<>();

    static {
        // Fügen Sie die Standard-Pflanzenklassen hinzu
        validPlantClasses.add(CropBlock.class);
        validPlantClasses.add(StemBlock.class);
        validPlantClasses.add(CocoaBlock.class);
        validPlantClasses.add(SaplingBlock.class);
    }

    private static void loadAdditionalPlantClassFromString(String plantClass) throws ClassNotFoundException {
        // Implementieren Sie das Laden aus der Konfigurationsdatei
        validPlantClasses.add(Class.forName(plantClass));
    }

    public static boolean isPlant(Block block) {
        for (Class<?> plantClass : validPlantClasses) {
            if (plantClass.isAssignableFrom(block.getClass())) {
                return true;
            }
        }
        return false;
    }
}
