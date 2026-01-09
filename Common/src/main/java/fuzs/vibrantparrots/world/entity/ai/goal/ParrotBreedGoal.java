package fuzs.vibrantparrots.world.entity.ai.goal;

import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.animal.parrot.ModParrot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ParrotBreedGoal extends BreedGoal {

    public ParrotBreedGoal(Parrot parrot, double speedModifier) {
        super(parrot, speedModifier);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.getOrDefault(this.animal, Optional.empty())
                .isEmpty();
    }

    /**
     * @see BreedGoal#getFreePartner()
     */
    @Override
    protected @Nullable Animal getFreePartner() {
        List<? extends Animal> potentialPartners = this.level.getNearbyEntities(this.partnerClass,
                PARTNER_TARGETING,
                this.animal,
                this.animal.getBoundingBox().inflate(8.0));
        double minDistance = Double.MAX_VALUE;
        Animal animal = null;
        for (Animal otherAnimal : potentialPartners) {
            if (ModParrot.canMate((Parrot) this.animal, otherAnimal) && !otherAnimal.isPanicking()
                    && this.animal.distanceToSqr(otherAnimal) < minDistance) {
                animal = otherAnimal;
                minDistance = this.animal.distanceToSqr(otherAnimal);
            }
        }

        return animal;
    }

    /**
     * @see ModParrot#spawnChildFromBreeding(ServerLevel, Animal)
     */
    @Override
    protected void breed() {
        this.animal.finalizeSpawnChildFromBreeding(this.level, this.partner, null);
        ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.set(this.animal,
                Optional.of(ModParrot.EGG_LAY_TIME.sample(this.animal.getRandom())));
    }
}
