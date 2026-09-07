package fuzs.vibrantparrots.common.advancements.criterion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VariantUtils;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ParrotPredicate(Optional<Holder<ParrotVariant>> variant) implements EntitySubPredicate {
    public static final MapCodec<ParrotPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ParrotVariant.CODEC.optionalFieldOf(VariantUtils.TAG_VARIANT).forGetter(ParrotPredicate::variant))
            .apply(instance, ParrotPredicate::new));

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (!(entity instanceof VibrantParrot parrot)) {
            return false;
        } else if (this.variant.isPresent() && parrot.getParrotVariant().value() != this.variant.get().value()) {
            return false;
        } else {
            return true;
        }
    }

    public static ParrotPredicate hasVariant(Holder<ParrotVariant> variant) {
        return new ParrotPredicate(Optional.of(variant));
    }
}
