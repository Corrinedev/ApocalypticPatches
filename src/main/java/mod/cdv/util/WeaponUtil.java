package mod.cdv.util;

import mod.cdv.AttributeRegistry;
import mod.cdv.Boot;
import net.mcreator.apocalypsenow.init.ApocalypsenowModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class WeaponUtil {
    public static boolean isLookingAtHead(ItemStack item, LivingEntity looker, LivingEntity target) {
        //modified from net.minecraft.world.entity.monster.EnderMan::isLookingAtMe
        Vec3 viewVector = target.getViewVector(1.0F).normalize();
        Vec3 targetToLookerEye = new Vec3(
                looker.getX() - target.getX(),
                looker.getEyeY() - target.getEyeY(),  // Using eye positions for both entities
                looker.getZ() - target.getZ()
        );
        double distance = targetToLookerEye.length();
        if (distance < 1.0E-5) return false;

        Vec3 targetToLookerNormalized = targetToLookerEye.normalize();
        double dotProduct = viewVector.dot(targetToLookerNormalized);
        double threshold = 1.0D - 0.025D / Math.max(1.0, distance);

        return dotProduct > threshold && target.hasLineOfSight(looker);
    }

    public static void applyHeadshot(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if(pTarget.level().isClientSide) return;
        if(isLookingAtHead(pStack, pTarget, pAttacker) && pAttacker.getAttributes().hasAttribute(AttributeRegistry.HEADSHOT_DATA.get()) && Boot.Config.allowHeadshots.get()) {
            if (pTarget.level() instanceof ServerLevel svl) {
                Vec3 a = pTarget.getEyePosition();
                svl.sendParticles(ParticleTypes.CRIT, a.x, a.y, a.z, (int) pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE), 0.1, 0.1, 0.1, 0.02);
            }
            float damage = (float) ((pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * pAttacker.getAttributeValue(AttributeRegistry.HEADSHOT_DATA.get())) - pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
            pTarget.hurt(pTarget.damageSources().mobAttack(pAttacker), damage);
            pTarget.invulnerableTime = 0;
        }
    }
}
