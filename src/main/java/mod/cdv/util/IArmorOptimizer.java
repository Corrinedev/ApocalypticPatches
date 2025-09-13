package mod.cdv.util;

import net.mcreator.apocalypsenow.ApocalypsenowMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static mod.cdv.util.CacheKeyHolder.cacheKey;

public interface IArmorOptimizer extends IClientItemExtensions {
    HashMap<Item, HumanoidModel<?>> modelCache = new HashMap<>();
    @Override
    default @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if(!cacheKey && itemStack.getItem().builtInRegistryHolder().key().location().getNamespace().equals(ApocalypsenowMod.MODID)) {
            if(modelCache.get(itemStack.getItem()) == null) {
                cacheKey = true;
                modelCache.put(itemStack.getItem(), IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original));
                cacheKey = false;
                System.out.println("Successfully cached model = " + modelCache.get(itemStack.getItem()));
            } else if(modelCache.get(itemStack.getItem()) != null) {
                return modelCache.get(itemStack.getItem());
            }
        }
        return IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
    }
}
