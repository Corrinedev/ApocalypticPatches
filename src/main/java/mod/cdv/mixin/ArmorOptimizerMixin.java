package mod.cdv.mixin;

import net.mcreator.apocalypsenow.ApocalypsenowMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

import static mod.cdv.util.CacheKeyHolder.apocalypticPatches$modelCache;

@Mixin(value = ForgeHooksClient.class, remap = false)
public class ArmorOptimizerMixin {
    /** This class & <code>util.CacheKeyHolder</code> prevents the memory leak associated with ApocalypseNow's <br>
     * instantiation of new <code>HumanoidModel<?></code> classes. <br>
     * <br>
     * Performance gain (controlled test): <br>
     * Allocation Rate: ~3125mb/s -> ~415 mb/s <br>
     * Ram Usage: 6.4gb -> 3.3gb <br>
     * FPS: (240-260) -> (512-546) <br>
     * <br>
     * @return <code>HumanoidModel<?></code> || <code>Model</code>
     */
    @Inject(method = "getArmorModel", at = @At("HEAD"), cancellable = true)
    private static void getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default, CallbackInfoReturnable<Model> cir) {
        if(apocalypticPatches$modelCache.get(itemStack.getItem()) != null) {
            var model = apocalypticPatches$modelCache.get(itemStack.getItem());
            ForgeHooksClient.copyModelProperties(_default, (HumanoidModel<?>) model);
            cir.setReturnValue(model);
        } else {
            if (itemStack.getItem().getDescriptionId().contains(ApocalypsenowMod.MODID)) {
                var model = IClientItemExtensions.of(itemStack).getHumanoidArmorModel(entityLiving, itemStack, slot, _default);
                ForgeHooksClient.copyModelProperties(_default, model);
                apocalypticPatches$modelCache.put(itemStack.getItem(), model);
                cir.setReturnValue(model);
            } else {
                cir.setReturnValue(IClientItemExtensions.of(itemStack).getGenericArmorModel(entityLiving, itemStack, slot, _default));
            }
        }
    }
}
