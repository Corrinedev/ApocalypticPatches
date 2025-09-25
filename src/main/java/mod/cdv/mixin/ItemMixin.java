package mod.cdv.mixin;

import mod.cdv.AttributeRegistry;
import mod.cdv.util.WeaponUtil;
import net.mcreator.apocalypsenow.init.ApocalypsenowModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    public void applyHeadshot(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker, CallbackInfoReturnable<Boolean> cir) {
        WeaponUtil.applyHeadshot(pStack, pTarget, pAttacker);
    }
}
