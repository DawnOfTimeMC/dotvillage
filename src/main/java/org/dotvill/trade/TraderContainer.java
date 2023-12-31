package org.dotvill.trade;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;

public class TraderContainer implements Container {
    private final TradingModule tradingModule;
    private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(4, ItemStack.EMPTY);
    @Nullable
    private TraderDeal activeDeal;
    private int selectionHint;
    private static int INPUT_A = 0;
    private static int INPUT_B = 1;
    private static int INPUT_C = 2;
    private static int RESULT = 3;

    public TraderContainer(TradingModule tradingModule) {
        this.tradingModule = tradingModule;
    }

    public int getContainerSize() {
        return this.itemStacks.size();
    }

    public boolean isEmpty() {
        for(ItemStack stack : this.itemStacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int index) {
        return this.itemStacks.get(index);
    }

    public ItemStack removeItem(int index, int count) {
        ItemStack stack = this.itemStacks.get(index);
        if (index == RESULT && !stack.isEmpty()) {
            return ContainerHelper.removeItem(this.itemStacks, index, stack.getCount());
        } else {
            ItemStack itemstack1 = ContainerHelper.removeItem(this.itemStacks, index, count);
            if (!itemstack1.isEmpty() && this.isPaymentSlot(index)) {
                this.updateSellItem();
            }

            return itemstack1;
        }
    }

    private boolean isPaymentSlot(int slot) {
        return slot == INPUT_A || slot == INPUT_B || slot == INPUT_C;
    }

    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.itemStacks, index);
    }

    public void setItem(int index, ItemStack stack) {
        this.itemStacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (this.isPaymentSlot(index)) {
            this.updateSellItem();
        }

    }

    public boolean stillValid(Player pPlayer) {
        return this.tradingModule.getTradingPlayer() == pPlayer;
    }

    public void setChanged() {
        this.updateSellItem();
    }

    public void updateSellItem() {
        this.activeDeal = null;
        ItemStack stackInSlotA = this.itemStacks.get(INPUT_A);
        ItemStack stackInSlotB = this.itemStacks.get(INPUT_B);
        ItemStack stackInSlotC = this.itemStacks.get(INPUT_C);

        if (stackInSlotA.isEmpty() && stackInSlotB.isEmpty() && stackInSlotC.isEmpty()) {
            this.setItem(RESULT, ItemStack.EMPTY);
        } else {
            TraderDeals deals = this.tradingModule.getDeals();
            if (!deals.isEmpty()) {
                TraderDeal deal1 = deals.getRecipeFor(stackInSlotA, stackInSlotB, stackInSlotC, this.selectionHint);
                TraderDeal deal2 = deals.getRecipeFor(stackInSlotA, stackInSlotC, stackInSlotB, this.selectionHint);
                TraderDeal deal3 = deals.getRecipeFor(stackInSlotB, stackInSlotA, stackInSlotC, this.selectionHint);
                TraderDeal deal4 = deals.getRecipeFor(stackInSlotB, stackInSlotC, stackInSlotA, this.selectionHint);
                TraderDeal deal5 = deals.getRecipeFor(stackInSlotC, stackInSlotA, stackInSlotB, this.selectionHint);
                TraderDeal deal6 = deals.getRecipeFor(stackInSlotC, stackInSlotB, stackInSlotA, this.selectionHint);

                TraderDeal deal = ObjectUtils.firstNonNull(deal1, deal2, deal3, deal4, deal5, deal6);

                if (deal != null) {
                    this.activeDeal = deal;
                    this.setItem(RESULT, deal.assemble());
                } else {
                    this.setItem(RESULT, ItemStack.EMPTY);
                }
            }
            //this.traderComponent.notifyTradeUpdated(this.getItem(2));
        }
    }

    @Nullable
    public TraderDeal getActiveDeal() {
        return this.activeDeal;
    }

    public void setSelectionHint(int pCurrentRecipeIndex) {
        this.selectionHint = pCurrentRecipeIndex;
        this.updateSellItem();
    }

    public void clearContent() {
        this.itemStacks.clear();
    }

}
