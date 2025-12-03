package mod.cdv.util;

import net.mcreator.apocalypsenow.init.ApocalypsenowModMenus;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.model.Model;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.HashSet;

public final class CacheKeyHolder {
    public static final HashMap<Item, Model> apocalypticPatches$modelCache = new HashMap<>();

    public static HashSet<MenuType<?>> APOCALYPSE_NOW_TYPES = null;

    public static boolean  isApocalypseContainer(MenuType<?> type) {
        if(APOCALYPSE_NOW_TYPES == null) {
            //Cache types to prevent creating streams, minor performance improvement, can be removed and directly streamed if ever problematic
            APOCALYPSE_NOW_TYPES = new HashSet<>(ApocalypsenowModMenus.REGISTRY.getEntries().stream().map(RegistryObject::get).toList());
        }

        return APOCALYPSE_NOW_TYPES.contains(type);
    }

    public static boolean isApocalypseContainer(AbstractContainerMenu menu) {
        if(menu instanceof CreativeModeInventoryScreen.ItemPickerMenu) return false;

        if(APOCALYPSE_NOW_TYPES == null) {
            //Cache types to prevent creating streams, minor performance improvement, can be removed and directly streamed if ever problematic
            APOCALYPSE_NOW_TYPES = new HashSet<>(ApocalypsenowModMenus.REGISTRY.getEntries().stream().map(RegistryObject::get).toList());
        }
        return APOCALYPSE_NOW_TYPES.contains(menu.getType());
    }
}
