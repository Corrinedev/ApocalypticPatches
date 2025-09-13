package mod.cdv.mixin;

import mod.cdv.AttributeRegistry;
import mod.cdv.util.WeaponUtil;
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
        if(WeaponUtil.isLookingAtHead(pStack, pTarget, pAttacker) && pAttacker.getAttributes().hasAttribute(AttributeRegistry.HEADSHOT_DATA.get())) {
            if (pTarget.level() instanceof ServerLevel svl) {
                Vec3 a = pTarget.getEyePosition();
                svl.sendParticles(ParticleTypes.CRIMSON_SPORE, a.x, a.y, a.z, (int) pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE), 0.1, 0.1, 0.1, 0.02);
            }
            float damage = (float) ((pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * pAttacker.getAttributeValue(AttributeRegistry.HEADSHOT_DATA.get())) - pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
            pTarget.hurt(pTarget.damageSources().mobAttack(pAttacker), damage);
            pTarget.invulnerableTime = 0;
        }
    }
}
