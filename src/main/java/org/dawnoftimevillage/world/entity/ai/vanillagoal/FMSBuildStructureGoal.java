package org.dawnoftimevillage.world.entity.ai.vanillagoal;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.dawnoftimevillage.command.BuildingSiteCommand;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.buildingsite.BuildingSitesManager;
import org.dawnoftimevillage.world.entity.DoTVillager;
import org.dawnoftimevillage.world.entity.ai.systems.statemachine.State;
import org.dawnoftimevillage.world.entity.ai.systems.statemachine.StateMachine;
import org.dawnoftimevillage.world.entity.workorder.BuildStructureWorkOrder;

import java.util.EnumSet;

/** Build structure goal made with a FMS (finite sate machine) **/
public class FMSBuildStructureGoal extends Goal {
    public static final float BUILDER_SPEED = 0.50F; // Builder's ground speed modifier
    public static final double MAX_REACH_DIST = 16D; // Maximum distance to be able to place a block at a given position
    public static final int INTERVAL_BETWEEN_PLACES = reducedTickDelay(6);
    private final DoTVillager builder;
    private BuildingSite constructionSite;
    private StateMachine stateMachine;
    /** Ticks between each attempt to place a block.
     *  During this time, the builder try to go to the next block position
     *  Note that the builder may not be able to place the block at the end
     *  of the cooldown, for example when the targeted block is too far away.
     *  In this case, the cooldown does not reset until the builder succeed to place the block.
     **/
    private int placeAttemptCooldown;
    /** Ticks before the builder will stop building and simulate looking at the construction plan **/
    private int nextPlanCheckCooldown;
    /** Ticks before the builder will stop looking at the plan and continue building */
    private int stopCheckingPlanCooldown;
    /** Ticks during which the builder isn't moving for whatever reason **/
    private int motionLessTicks;
    /** If the last try to place a block was successful **/
    private boolean successPlacingLastBlock;

    public FMSBuildStructureGoal(DoTVillager builder) {
        this.builder = builder;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    public boolean canUse() {
        return this.builder.hasWorkOrder() && (this.builder.getWorkOrder() instanceof BuildStructureWorkOrder);
    }

    public boolean canContinueToUse() {
        return !this.constructionSite.isCompleted();
    }

    public void start() {
        setConstructionSite();
        buildStateMachine();
        /** Very first plan lookup duration **/
        this.stopCheckingPlanCooldown =  this.builder.getRandom().nextInt(adjustedTickDelay(20 * 2), adjustedTickDelay(20 * 6));
        this.builder.getNavigation().setMaxVisitedNodesMultiplier(10.0F);
    }

    private void setConstructionSite() {
        this.constructionSite = ((BuildStructureWorkOrder)this.builder.getWorkOrder()).getRequestedSite();
    }

    public void reset() {
        this.placeAttemptCooldown =  this.nextPlanCheckCooldown =  this.stopCheckingPlanCooldown = 0;
        this.motionLessTicks = 0;
        this.successPlacingLastBlock = false;
        this.stateMachine.reset();
    }

    public void stop() {
        BuildingSitesManager.get((ServerLevel)this.builder.getLevel()).onConstructionSiteCompleted(this.constructionSite);
        this.constructionSite = null;
        ((BuildStructureWorkOrder)this.builder.getWorkOrder()).notifyFinished();
        this.builder.setWorkOrder(null);
        this.builder.getNavigation().resetMaxVisitedNodesMultiplier();
        this.builder.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.builder.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        this.builder.playSound(SoundEvents.EVOKER_PREPARE_WOLOLO,2.0F,0.9F);
    }

    private void buildStateMachine() {
        State goToSite = State.create("go_to_site", this::goToSite);
        State placeBlock = State.create("place_block", this::placeBlock);
        State prepareToPlace = State.create("prepare_to_place", this::prepareToPlace);
        State checkBuildingPlan = State.create("check_building_plan", this::checkBuildingPlan)
                .withEntryCode(this::startCheckingBuildingPlan)
                .withExitCode(this::stopCheckingBuildingPlan);

        this.stateMachine = StateMachine.create("build_structure", 3, goToSite)
                .addState(goToSite)
                .addState(placeBlock)
                .addState(checkBuildingPlan)
                .addTransition(goToSite, checkBuildingPlan, () -> this.builder.distanceToSqr(this.constructionSite.getLocation().getX(), this.constructionSite.getLocation().getY(), this.constructionSite.getLocation().getZ()) <= 10)
                .addTransition(placeBlock, prepareToPlace, () -> this.successPlacingLastBlock)
                .addTransition(checkBuildingPlan, prepareToPlace, () -> this.stopCheckingPlanCooldown <= 0)
                .addTransition(prepareToPlace, placeBlock, () -> (this.placeAttemptCooldown <= 0) && (closeEnoughToBuild() || (this.motionLessTicks >= 5)))
                .addTransition(prepareToPlace, checkBuildingPlan, () -> (this.placeAttemptCooldown > 0) && (this.nextPlanCheckCooldown <= 0));
    }

    public void tick() {
        if (!this.constructionSite.isCompleted()) {
            if (BuildingSiteCommand.showDebugInfo) {
                showDebugInfo();
            }
            this.stateMachine.tick();
        }
    }

    private void goToSite() {
        this.builder.getNavigation().moveTo(this.constructionSite.getLocation().getX(), this.constructionSite.getLocation().getY(), this.constructionSite.getLocation().getZ(), this.builder.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    private void prepareToPlace() {
        if (this.placeAttemptCooldown <= (INTERVAL_BETWEEN_PLACES / 2)) {
            this.builder.setItemInHand(InteractionHand.MAIN_HAND, this.constructionSite.nextBlock().asItem().getDefaultInstance());
        }
        lookAtNextBuildPos();
        moveToNextBuildPos();
        if ((this.builder.getDeltaMovement().x == 0.0D) && (this.builder.getDeltaMovement().z == 0.0D)) {
            ++this.motionLessTicks;
        } else if (this.motionLessTicks > 0) {
            this.motionLessTicks = Math.max(0, this.motionLessTicks - 1);
        }
        --this.placeAttemptCooldown;
        --this.nextPlanCheckCooldown;
    }

    private void placeBlock() {
        lookAtNextBuildPos();
        boolean blockingHimself = this.builder.blockPosition().equals(getNextBuildPos());
        if (!blockingHimself || (blockingHimself && !this.builder.getLevel().getBlockState(this.builder.blockPosition().above().above()).isAir())) {
            if (this.constructionSite.buildNextBlock()) {
                this.builder.swing(InteractionHand.MAIN_HAND);
                this.builder.getLevel().playSound(null, this.constructionSite.lastBlockPos(), this.constructionSite.lastBlockBuilt().getSoundType(this.constructionSite.lastBlockState(), this.builder.getLevel(), this.constructionSite.lastBlockPos(), this.builder).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!this.constructionSite.isCompleted() && (this.constructionSite.nextBlock() != this.constructionSite.lastBlockBuilt())) {
                    this.builder.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
                --this.nextPlanCheckCooldown;
                this.placeAttemptCooldown = BuildingSiteCommand.superBuilder ? 1 : this.builder.getRandom().nextInt(INTERVAL_BETWEEN_PLACES - 1, INTERVAL_BETWEEN_PLACES + 1);
                this.successPlacingLastBlock = true;
            } else {
                this.successPlacingLastBlock = false;
            }
        } else {
            this.builder.getJumpControl().jump();
            this.successPlacingLastBlock = false;
        }
    }

    private void checkBuildingPlan() {
        this.builder.getNavigation().stop();
        --this.stopCheckingPlanCooldown;
    }

    private BlockPos getNextBuildPos() {
        return this.constructionSite.nextBlockPos();
    }

    private void lookAtNextBuildPos() {
        Vec3 toLookAt = getNextBuildPos().getCenter();
        this.builder.getLookControl().setLookAt(toLookAt.x, toLookAt.y, toLookAt.z);
    }

    private void moveToNextBuildPos() {
        int x = getNextBuildPos().getX();
        int y = getNextBuildPos().getY();
        int z = getNextBuildPos().getZ();
        if (this.builder.distanceToSqr(x, y, z) > (MAX_REACH_DIST / 2 )) {
            this.builder.getNavigation().moveTo(x, y, z, (BuildingSiteCommand.superBuilder ? 0.65F : BUILDER_SPEED ));
        } else {
            this.builder.getNavigation().stop();
        }
    }

    private boolean closeEnoughToBuild() {
        int x = this.constructionSite.nextBlockPos().getX();
        int y = this.constructionSite.nextBlockPos().getY();
        int z = this.constructionSite.nextBlockPos().getZ();
        return BuildingSiteCommand.superBuilder || this.builder.distanceToSqr(x, y, z) <= MAX_REACH_DIST;
    }

    private void startCheckingBuildingPlan() {
        this.stopCheckingPlanCooldown = this.builder.getRandom().nextInt(adjustedTickDelay(20 * 2), adjustedTickDelay(20 * 6));
        this.builder.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        this.builder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FILLED_MAP));
        this.builder.playSound(SoundEvents.BOOK_PAGE_TURN);
        this.builder.setReading(true);
    }

    private void stopCheckingBuildingPlan() {
        this.nextPlanCheckCooldown = BuildingSiteCommand.superBuilder ? Integer.MAX_VALUE : this.builder.getRandom().nextInt(adjustedTickDelay((20 * 30)), adjustedTickDelay(20 * 60));
        this.builder.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.builder.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.IRON_SHOVEL));
        this.builder.playSound(SoundEvents.BOOK_PAGE_TURN);
        this.builder.setReading(false);
    }

    public boolean requiresUpdateEveryTick() {
        return BuildingSiteCommand.superBuilder;
    }

    private void showDebugInfo() {
        Player player = builder.getLevel().getNearestPlayer(builder, 30D);
        if (player != null) {
            player.sendSystemMessage(
            Component.literal("Done : " + constructionSite.isCompleted()).withStyle(ChatFormatting.GOLD).append(
            Component.literal(" | Block : " + constructionSite.getProgression() + "/" + constructionSite.getBlocksQuantity()).withStyle(ChatFormatting.BLUE).append(
            Component.literal(" | Next : " + BuiltInRegistries.BLOCK.getKey(constructionSite.nextBlock()) + ", " +
            constructionSite.nextBlockPos().getX() + " " +
            constructionSite.nextBlockPos().getY() + " " +
            constructionSite.nextBlockPos().getZ()).withStyle(ChatFormatting.AQUA))));

            player.displayClientMessage(
            Component.literal(stateMachine.getCurrentState().getName() + " | ").withStyle(ChatFormatting.DARK_GREEN).append(
            Component.literal("Waiting : " + placeAttemptCooldown).withStyle(ChatFormatting.GOLD).append(
            Component.literal(" | Put Away : " + stopCheckingPlanCooldown).withStyle(ChatFormatting.BLUE).append(
            Component.literal(" | Next Check : " + nextPlanCheckCooldown).withStyle(ChatFormatting.AQUA)))),true);
        }
    }
}