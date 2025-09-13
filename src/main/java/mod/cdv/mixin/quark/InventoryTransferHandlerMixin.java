package mod.cdv.mixin.quark;

import net.mcreator.apocalypsenow.init.ApocalypsenowModMenus;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

import java.util.HashSet;

@Mixin(value = InventoryTransferHandler.class, remap = false)
public abstract class InventoryTransferHandlerMixin {
    @Inject(
            method = "accepts",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void accepts(AbstractContainerMenu container, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!(container instanceof CraftingMenu) && (!player.level().isClientSide() || !(container instanceof CreativeModeInventoryScreen.ItemPickerMenu))) {
            if(isApocalypseContainer(container.getType()))
                cir.setReturnValue(true);
        }
        if(container instanceof CreativeModeInventoryScreen.ItemPickerMenu)
            cir.setReturnValue(false);
    }

    @Unique
    private static HashSet<MenuType<?>> APOCALYPSE_NOW_TYPES = null;

    @Unique
    private static boolean isApocalypseContainer(MenuType<?> type) {
        if(APOCALYPSE_NOW_TYPES == null) {
            //Cache types to prevent creating streams, minor performance improvement, can be removed and directly streamed if ever problematic
            APOCALYPSE_NOW_TYPES = new HashSet<>(ApocalypsenowModMenus.REGISTRY.getEntries().stream().map(RegistryObject::get).toList());
            System.out.println("cache = " + APOCALYPSE_NOW_TYPES);
        }

        return APOCALYPSE_NOW_TYPES.contains(type);
    }
}