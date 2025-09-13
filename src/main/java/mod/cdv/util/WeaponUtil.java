package mod.cdv.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

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
}
