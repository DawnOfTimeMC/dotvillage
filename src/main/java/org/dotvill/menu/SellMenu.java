package org.dotvill.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.dotvill.registry.ModMenus;
import org.dotvill.trade.*;

public class SellMenu extends AbstractContainerMenu {
    protected static final int INPUT_A_SLOT = 0;
    protected static final int INPUT_B_SLOT = 1;
    protected static final int INPUT_C_SLOT = 2;
    protected static final int RESULT_SLOT = 3;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int HOTBAR_SLOT_START = 30;
    private static final int HOTBAR_SLOT_END = 39;
    private static final int INPUT_A_X = 128;
    private static final int INPUT_B_X = 154;
    private static final int INPUT_C_X = 180;
    private static final int RESULT_X = 238;
    private static final int ROW_Y = 45;
    private final TradingModule tradingModule;
    private final TraderContainer tradeContainer;

    public SellMenu(int containerID, Inventory playerInventory, FriendlyByteBuf friendlyByteBuf) {
        this(containerID, playerInventory, new ClientSideTradingModule(playerInventory.player, TraderDeals.createFromStream(friendlyByteBuf), (LivingEntity) playerInventory.player.level().getEntity(friendlyByteBuf.readInt())));
    }

    public SellMenu(int containerID, Inventory playerInventory, TradingModule tradingModule) {
        super(ModMenus.SELL_MENU.get(), containerID);
        this.tradingModule = tradingModule;
        this.tradeContainer = new TraderContainer(tradingModule);
        this.addSlot(new Slot(this.tradeContainer, INPUT_A_SLOT, INPUT_A_X, ROW_Y));
        this.addSlot(new Slot(this.tradeContainer, INPUT_B_SLOT, INPUT_B_X, ROW_Y));
        this.addSlot(new Slot(this.tradeContainer, INPUT_C_SLOT, INPUT_C_X, ROW_Y));
        this.addSlot(new TraderResultSlot(playerInventory.player, tradingModule, this.tradeContainer, RESULT_SLOT, RESULT_X, ROW_Y));

        for(int i = 0; i < 3; ++i) { // Player inv
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 113 + j * 18, 92 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) { // Player hotbar
            this.addSlot(new Slot(playerInventory, k, 113 + k * 18, 150));
        }

    }

    public void slotsChanged(Container inventory) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(inventory);
    }

    public void setSelectionHint(int currentRecipeIndex) {
        this.tradeContainer.setSelectionHint(currentRecipeIndex);
    }

    public boolean stillValid(Player player) {
        return this.tradingModule.getTradingPlayer() == player;
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stackCopy = stackInSlot.copy();
            if (slotIndex == RESULT_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stackInSlot, stackCopy);
                this.playTradeSound();
            } else if (slotIndex != INPUT_A_SLOT && slotIndex != INPUT_B_SLOT && slotIndex != INPUT_C_SLOT) { // IF SLOT IS IN PLAYER INV
                if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) {
                    if (!this.moveItemStackTo(stackInSlot, INV_SLOT_END, HOTBAR_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= INV_SLOT_END && slotIndex < HOTBAR_SLOT_END && !this.moveItemStackTo(stackInSlot, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return stackCopy;
    }

    private void playTradeSound() {
        if (!this.tradingModule.isClientSide()) {
            Entity entity = this.tradingModule.getOwner();
            entity.playSound(SoundEvents.VILLAGER_YES, 1.0F,1.0F);
            //entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.tradingModule.getDealSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        }

    }

    public void removed(Player player) {
        super.removed(player);
        this.tradingModule.setTradingPlayer(null);
        if (!this.tradingModule.isClientSide()) {
            if (!player.isAlive() || player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()) {
                ItemStack itemstack = this.tradeContainer.removeItemNoUpdate(INPUT_A_SLOT);
                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                }
                itemstack = this.tradeContainer.removeItemNoUpdate(INPUT_B_SLOT);
                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                }
                itemstack = this.tradeContainer.removeItemNoUpdate(INPUT_C_SLOT);
                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                }
            } else if (player instanceof ServerPlayer) {
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(INPUT_A_SLOT));
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(INPUT_B_SLOT));
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(INPUT_C_SLOT));
            }

        }
    }

    public void tryMoveItems(int selectedDealIndex) {
        if (selectedDealIndex >= 0 && this.getDeals().size() > selectedDealIndex) {
            ItemStack stackInSlotA = this.tradeContainer.getItem(INPUT_A_SLOT);
            if (!stackInSlotA.isEmpty()) {
                if (!this.moveItemStackTo(stackInSlotA, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(0, stackInSlotA);
            }

            ItemStack stackInSlotB = this.tradeContainer.getItem(INPUT_B_SLOT);
            if (!stackInSlotB.isEmpty()) {
                if (!this.moveItemStackTo(stackInSlotB, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, stackInSlotB);
            }

            ItemStack stackInSlotC = this.tradeContainer.getItem(INPUT_C_SLOT);
            if (!stackInSlotC.isEmpty()) {
                if (!this.moveItemStackTo(stackInSlotC, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, stackInSlotC);
            }

            if (this.tradeContainer.getItem(INPUT_A_SLOT).isEmpty() && this.tradeContainer.getItem(INPUT_B_SLOT).isEmpty() && this.tradeContainer.getItem(INPUT_C_SLOT).isEmpty()) {
                ItemStack requiredA = this.getDeals().get(selectedDealIndex).getInputA();
                this.moveFromInventoryToPaymentSlot(INPUT_A_SLOT, requiredA);
                ItemStack requiredB = this.getDeals().get(selectedDealIndex).getInputB();
                this.moveFromInventoryToPaymentSlot(INPUT_B_SLOT, requiredB);
                ItemStack requiredC = this.getDeals().get(selectedDealIndex).getInputC();
                this.moveFromInventoryToPaymentSlot(INPUT_C_SLOT, requiredC);
            }

        }
    }

    private void moveFromInventoryToPaymentSlot(int pPaymentSlotIndex, ItemStack pPaymentSlot) {
        if (!pPaymentSlot.isEmpty()) {
            for(int i = 3; i < 39; ++i) {
                ItemStack itemstack = this.slots.get(i).getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pPaymentSlot, itemstack)) {
                    ItemStack itemstack1 = this.tradeContainer.getItem(pPaymentSlotIndex);
                    int j = itemstack1.isEmpty() ? 0 : itemstack1.getCount();
                    int k = Math.min(pPaymentSlot.getMaxStackSize() - j, itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copy();
                    int l = j + k;
                    itemstack.shrink(k);
                    itemstack2.setCount(l);
                    this.tradeContainer.setItem(pPaymentSlotIndex, itemstack2);
                    if (l >= pPaymentSlot.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }

    }

    /**
     * {@link net.minecraft.client.multiplayer.ClientPacketListener} uses this to set offers for the client side
     * MerchantContainer.
     */
    public void setDeals(TraderDeals deals) {
        //this.tradingModule.setDeals(deals);
    }

    public TraderDeals getDeals() {
        return this.tradingModule.getDeals();
    }

    public LivingEntity getOwner() {
        return this.tradingModule.getOwner();
    }

    public static class TraderResultSlot extends Slot {
        private final TraderContainer slots;
        private final Player player;
        private int removeCount;
        private final TradingModule trader;

        public TraderResultSlot(Player player, TradingModule tradingModule, TraderContainer traderContainer, int slot, int posX, int posY) {
            super(traderContainer, slot, posX, posY);
            this.player = player;
            this.trader = tradingModule;
            this.slots = traderContainer;
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        /**
         * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
         */
        public ItemStack remove(int pAmount) {
            if (this.hasItem()) {
                this.removeCount += Math.min(pAmount, this.getItem().getCount());
            }

            return super.remove(pAmount);
        }

        /**
         * Typically increases an internal count, then calls {@code onCrafting(item)}.
         * @param pStack the output - ie, iron ingots, and pickaxes, not ore and wood.
         */
        protected void onQuickCraft(ItemStack pStack, int pAmount) {
            this.removeCount += pAmount;
            this.checkTakeAchievements(pStack);
        }

        /**
         *
         * @param pStack the output - ie, iron ingots, and pickaxes, not ore and wood.
         */
        protected void checkTakeAchievements(ItemStack pStack) {
            pStack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            this.removeCount = 0;
        }

        public void onTake(Player pPlayer, ItemStack pStack) {
            this.checkTakeAchievements(pStack);
            TraderDeal deal = this.slots.getActiveDeal();
            if (deal != null) {
                ItemStack stackA = this.slots.getItem(0);
                ItemStack stackB = this.slots.getItem(1);
                ItemStack stackC = this.slots.getItem(2);
                if (deal.makeDeal(stackA, stackB, stackC)
                        || deal.makeDeal(stackA, stackC, stackB)
                        || deal.makeDeal(stackB, stackA, stackC)
                        || deal.makeDeal(stackB, stackC, stackA)
                        || deal.makeDeal(stackC, stackA, stackB)
                        || deal.makeDeal(stackC, stackB, stackA)) {
                    this.trader.notifyDeal(deal);
                    pPlayer.awardStat(Stats.TRADED_WITH_VILLAGER);
                    this.slots.setItem(0, stackA);
                    this.slots.setItem(1, stackB);
                    this.slots.setItem(2, stackC);
                }
            }
        }
    }
}
