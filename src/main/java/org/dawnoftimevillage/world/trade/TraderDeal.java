package org.dawnoftimevillage.world.trade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;

public class TraderDeal {
    private final DealType dealType;
    private final ItemStack requiredA;
    private final ItemStack requiredB;
    private final ItemStack resultA;
    private final ItemStack resultB;
    private int requiredACostModifier;
    private int requiredBCostModifier;
    private int resultACostModifier;
    private int resultBCostModifier;
    private int uses;
    private int traderXpReward;
    private int playerXpReward;

    public TraderDeal(CompoundTag tag) {
        this.dealType = DealType.valueOf(tag.getString("dealType"));
        this.requiredA = ItemStack.of(tag.getCompound("requiredA"));
        this.requiredB = ItemStack.of(tag.getCompound("requiredB"));
        this.resultA = ItemStack.of(tag.getCompound("resultA"));
        this.resultB = ItemStack.of(tag.getCompound("resultB"));
        this.uses = tag.getInt("uses");
        this.traderXpReward = tag.getInt("traderXpReward");
        this.playerXpReward = tag.getInt("playerXpReward");
    }

    private TraderDeal(Builder builder) {
        this.dealType = builder.dealType;
        this.requiredA = builder.requiredA;
        this.requiredB = builder.requiredB;
        this.resultA = builder.resultA;
        this.resultB = builder.resultB;
        this.traderXpReward = builder.traderXpReward;
        this.playerXpReward = builder.playerXpReward;
    }

    public boolean isSatisfiedBy(ItemStack offerA, ItemStack offerB) {
        return this.isRequiredItem(offerA, getRequiredA()) && offerA.getCount() >= getRequiredA().getCount()
            && this.isRequiredItem(offerB, getRequiredB()) && offerB.getCount() >= getRequiredB().getCount();
    }

    private boolean isRequiredItem(ItemStack offer, ItemStack required) {
        if (required.isEmpty() && offer.isEmpty()) {
            return true;
        } else {
            ItemStack stack = offer.copy();
            if (stack.getItem().isDamageable(stack)) {
                stack.setDamageValue(stack.getDamageValue());
            }
            return ItemStack.isSame(stack, required) && (!required.hasTag() || stack.hasTag() && NbtUtils.compareNbt(required.getTag(), stack.getTag(), false));
        }
    }

    public boolean makeDeal(ItemStack offerA, ItemStack offerB) {
        if (!this.isSatisfiedBy(offerA, offerB)) {
            return false;
        } else {
            offerA.shrink(getRequiredA().getCount());
            if (!getRequiredB().isEmpty()) {
                offerB.shrink(getRequiredB().getCount());
            }
            return true;
        }
    }

    public void increaseUses() {
        ++this.uses;
    }

    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("dealType", this.dealType.name().toLowerCase());
        tag.put("requiredA", this.requiredA.save(new CompoundTag()));
        tag.put("requiredB", this.requiredB.save(new CompoundTag()));
        tag.put("resultA", this.resultA.save(new CompoundTag()));
        tag.put("resultB", this.resultB.save(new CompoundTag()));
        tag.putInt("uses", this.uses);
        tag.putInt("traderXpReward", this.traderXpReward);
        tag.putInt("playerXpReward", this.playerXpReward);
        return tag;
    }

    public ItemStack getRequiredA() {return this.requiredA;}

    public ItemStack getRequiredB() {return this.requiredB;}

    public ItemStack getResultA() {return this.resultA;}

    public ItemStack getResultB() {return this.resultB;}

    public static class Builder {
        private DealType dealType;
        private ItemStack requiredA;
        private ItemStack requiredB;
        private ItemStack resultA;
        private ItemStack resultB;
        private int traderXpReward;
        private int playerXpReward;

        public Builder(DealType dealType, ItemStack requiredA, ItemStack resultA) {
            this.dealType = dealType;
            this.requiredA = requiredA;
            this.resultA = resultA;
        }

        public Builder secondInput(ItemStack requiredB) {
            this.requiredB = requiredB;
            return this;
        }

        public Builder secondResult(ItemStack resultB) {
            this.resultB = resultB;
            return this;
        }

        public Builder traderXpReward(int traderXpReward) {
            this.traderXpReward = traderXpReward;
            return this;
        }

        public Builder playerXpReward(int playerXpReward) {
            this.playerXpReward = playerXpReward;
            return this;
        }

        public TraderDeal build() {
            return new TraderDeal(this);
        }
    }
}