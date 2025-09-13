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

@Mixin(value = ForgeHooksClient.class, remap = false)
public class ArmorOptimizerMixin {
    @Unique
    private static final HashMap<Item, Model> apocalypticPatches$modelCache = new HashMap<>();

    @Inject(method = "getArmorModel", at = @At("HEAD"), cancellable = true)
    private static void getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default, CallbackInfoReturnable<Model> cir) {
        if(itemStack.getItem().builtInRegistryHolder().key().location().getNamespace().equals(ApocalypsenowMod.MODID)) {
            if(apocalypticPatches$modelCache.get(itemStack.getItem()) == null) {
                var model = IClientItemExtensions.of(itemStack).getHumanoidArmorModel(entityLiving, itemStack, slot, _default);
                ForgeHooksClient.copyModelProperties(_default, model);
                apocalypticPatches$modelCache.put(itemStack.getItem(), model);
                System.out.println("Successfully cached model = " + itemStack);
                cir.setReturnValue(model);
            } else {
                var model = apocalypticPatches$modelCache.get(itemStack.getItem());
                ForgeHooksClient.copyModelProperties(_default, (HumanoidModel<?>) model);
                cir.setReturnValue(model);
            }
        }
        cir.setReturnValue(IClientItemExtensions.of(itemStack).getGenericArmorModel(entityLiving, itemStack, slot, _default));
    }
}
