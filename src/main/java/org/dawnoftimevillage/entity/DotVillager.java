package org.dawnoftimevillage.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.registry.DotvEntities;
import org.dawnoftimevillage.trade.TraderData;
import org.dawnoftimevillage.village.Village;
import org.slf4j.Logger;

public class DotVillager extends AgeableMob {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> DATA_CROSSING_ARMS = SynchedEntityData.defineId(DotVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_READING = SynchedEntityData.defineId(DotVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_CULTURE = SynchedEntityData.defineId(DotVillager.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_PROFESSION = SynchedEntityData.defineId(DotVillager.class, EntityDataSerializers.BYTE);
    private TraderData traderData;
    private Village village;
    private Building house;
    private Player lastInteractingPlayer;
    private String currentActionDescription;
    private AdminOrder adminOrder;

    public DotVillager(EntityType<DotVillager> entityType, Level level) {
        super(entityType, level);
        preConstructionInit();
    }

    private void preConstructionInit() {
    }

    public void onFinalizeSpawnEvent() {
        setCulture(DotVillagerCulture.byBiome(level().getBiome(blockPosition())));
        setProfession(DotVillagerProfession.STONEMASON);
        setPersistenceRequired();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CROSSING_ARMS, false);
        this.entityData.define(DATA_READING, false);
        this.entityData.define(DATA_CULTURE, DotVillagerCulture.PLAINS.getId());
        this.entityData.define(DATA_PROFESSION, DotVillagerProfession.UNEMPLOYED.getId());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.FOLLOW_RANGE, 200.0D);
    }

    protected void registerGoals() {
        int priority = -1;
        this.goalSelector.addGoal(++priority, new FloatGoal(this));
        //this.goalSelector.addGoal(++priority, new BuilderWorkAI(this));
        this.goalSelector.addGoal(++priority, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(++priority, new LookAtPlayerGoal(this, LivingEntity.class, 10.0F));
        this.goalSelector.addGoal(++priority, new RandomLookAroundGoal(this));
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_CULTURE, tag.getByte("Culture"));
        this.entityData.set(DATA_PROFESSION, tag.getByte("Profession"));
        if (!level().isClientSide() && tag.contains("AdminOrder")) {
            CompoundTag orderTag = tag.getCompound("AdminOrder");
            switch (orderTag.getString("OrderType")) {
                //case "Build" -> this.adminOrder = new AdminOrder.BuildOrder(ConstructionProjectManager.get((ServerLevel)getLevel()).getProjectByName(orderTag.getString("BuildingSite")));
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Culture", this.entityData.get(DATA_CULTURE));
        tag.putByte("Profession", this.entityData.get(DATA_PROFESSION));
        if (!level().isClientSide() && this.hasAdminOrder()) {
            this.adminOrder.save(tag);
        }
    }

    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide() && (hand == InteractionHand.MAIN_HAND)) {
            this.lastInteractingPlayer = player;
            sayInPlayerActionBar(this.lastInteractingPlayer, Component.translatable("sentence.dawnoftimevillage.innkeeperdescription"));
            sayInPlayerChat(this.lastInteractingPlayer, Component.translatable("sentence.dawnoftimevillage.hello"));
            playSound(SoundEvents.VILLAGER_TRADE);
        }
        return super.mobInteract(player, hand);
    }

    public void sayInPlayerActionBar(Player player, Component component) {
        if (component != null) {
            player.displayClientMessage(component, true);
        }
    }

    public void sayInPlayerChat(Player player, Component component) {
        if (component != null) {
            player.sendSystemMessage(getNameForChat().append(component));
        }
    }

    public void sayToAllPlayersInChat(Component component) {
        if (component != null) {
            getServer().getPlayerList().broadcastSystemMessage(getNameForChat().append(component), false);
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        setCrossingArms(false);
        return super.hurt(source, amount);
    }

    public void die(DamageSource cause) {
        super.die(cause);
        //tradingHandler.stopTrading();
        LOGGER.info("Villager {} died, message: '{}'", this, cause.getLocalizedDeathMessage(this).getString());
    }

    private MutableComponent getNameForChat() {
        String firstLetter = getProfession().toString().substring(0, 1).toUpperCase();
        return Component.Serializer.fromJson("[\"\",{\"text\":\"" + "[" +firstLetter + getProfession().toString().substring(1) + "] " + "\",\"color\":\"yellow\"},{\"text\":\"\"}]");
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    public Entity changeDimension(ServerLevel server, net.minecraftforge.common.util.ITeleporter teleporter) {
        //tradingHandler.stopTrading();
        return super.changeDimension(server, teleporter);
    }

    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return DotvEntities.DOT_VILLAGER.get().create(level);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return this.isBaby() ? 0.81F : 1.62F;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
        //return tradingHandler.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    public boolean canBeLeashed(Player player) {
        return false;
    }

    public void thunderHit(ServerLevel level, LightningBolt lightningBolt) {
        if (level.getDifficulty() != Difficulty.PEACEFUL && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.WITCH, (timer) -> {})) {
            LOGGER.info("Villager {} was struck by lightning {}.", this, lightningBolt);
            Witch witch = EntityType.WITCH.create(level);
            if (witch != null) {
                witch.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                witch.finalizeSpawn(level, level.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
                witch.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    witch.setCustomName(this.getCustomName());
                    witch.setCustomNameVisible(this.isCustomNameVisible());
                }

                witch.setPersistenceRequired();
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, witch);
                level.addFreshEntityWithPassengers(witch);
                this.discard();
            } else {
                super.thunderHit(level, lightningBolt);
            }
        } else {
            super.thunderHit(level, lightningBolt);
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {return SoundEvents.VILLAGER_HURT;}

    protected SoundEvent getDeathSound() {return SoundEvents.VILLAGER_DEATH;}

    public int getAmbientSoundInterval() {return 200;}

    public DotVillagerCulture getCulture() {return DotVillagerCulture.byId(this.entityData.get(DATA_CULTURE));}

    public void setCulture(DotVillagerCulture culture) {this.entityData.set(DATA_CULTURE, culture.getId());}

    public DotVillagerProfession getProfession() {return DotVillagerProfession.byId(this.entityData.get(DATA_PROFESSION));}

    public void setProfession(DotVillagerProfession profession) {this.entityData.set(DATA_PROFESSION, profession.getId());}

    public boolean isCrossingArms() {return this.entityData.get(DATA_CROSSING_ARMS);}

    public void setCrossingArms(boolean crossing) {this.entityData.set(DATA_CROSSING_ARMS, crossing);}

    public boolean isReading() {return this.entityData.get(DATA_READING);}

    public void setReading(boolean reading) {this.entityData.set(DATA_READING, reading);}

    public Player getLastInteractingPlayer() {return this.lastInteractingPlayer;}

    public String getCurrentActionDescription() {return this.currentActionDescription;}

    public AdminOrder getAdminOrder() {return this.adminOrder;}

    public boolean hasAdminOrder() {return this.adminOrder != null;}

    public void setAdminOrder(AdminOrder adminOrder) {this.adminOrder = adminOrder;}
}
