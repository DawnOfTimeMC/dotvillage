package org.dotvill.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.dotvill.client.screen.tooltip.TradeItemTooltip;
import org.dotvill.entity.Dillager;
import org.dotvill.menu.SellMenu;
import org.dotvill.network.ModNetwork;
import org.dotvill.network.ServerboundSelectTraderDealPacket;
import org.dotvill.trade.TraderDeal;
import org.dotvill.trade.TraderDeals;
import org.dotvill.util.ModUtils;

import java.util.List;
import java.util.Optional;

import static org.dotvill.client.screen.BuyScreen.DealButton.DEAL_BUTTON_HEIGHT;
import static org.dotvill.client.screen.BuyScreen.DealButton.DEAL_BUTTON_WIDTH;

public class SellScreen extends DillagerBaseScreen<SellMenu> {
    private static final ResourceLocation TEXTURE = ModUtils.resource("textures/gui/sell_screen.png");
    private static final int BUY_SCREEN_TEXTURE_WIDTH = 299;
    private static final int BUY_SCREEN_TEXTURE_HEIGHT = 174;
    // Texture size and offset in file
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_OFFSET_X = 287;
    private static final int SCROLLER_OFFSET_Y = 60;
    private static final int SCROLLER_AVAIL_OFFSET_X = 281;
    private static final int SCROLLER_AVAIL_OFFSET_Y = 60;
    // Positions of elements in gui
    private static final int GRID_X = 8;
    private static final int GRID_Y = 26;
    private static final int SCROLLER_X = 101;
    private static final int SCROLL_BAR_TOP_Y = 26;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_BOTTOM_Y = 99;
    private Component villagerInfo = Component.literal("Armorer");
    private Component villagerSentence;
    private static final int GRID_COLUMNS = 5;
    private static final int GRID_ROWS = 7;
    public static final int NUMBER_OF_DEAL_BUTTONS = GRID_ROWS * GRID_COLUMNS;
    private final DealButton[] dealButtons = new DealButton[NUMBER_OF_DEAL_BUTTONS];
    private int selectedDealIndex = -1;
    private int scrollOff;
    private boolean isDragging;

    public SellScreen(SellMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 281;
        this.imageHeight = 174;
        this.inventoryLabelX = 113;
        this.inventoryLabelY = 80;
        this.titleLabelX = 44;
        this.titleLabelY = 5;
        if (menu.getOwner() instanceof Dillager vill) {
            this.villager = vill;
        }
    }

    protected void init() {
        super.init();
        createButtons();
    }

    private void createButtons() {
        int x, startX = this.leftPos + GRID_X;
        int y = this.topPos + GRID_Y;
        int buttonIndex = 0;
        for (int i = 0; i < GRID_ROWS; ++i) {
            x = startX;
            for(int j = 0; j < GRID_COLUMNS; ++j) {
                this.dealButtons[buttonIndex] = this.addRenderableWidget(new DealButton(x, y, buttonIndex, (pressedButton) -> {
                    if (pressedButton instanceof DealButton button) {
                        this.selectedDealIndex = button.getIndex() + (this.scrollOff * GRID_COLUMNS);
                        this.postButtonClick();
                    }
                }));
                ++buttonIndex;
                x += DEAL_BUTTON_WIDTH;
            }
            y += DEAL_BUTTON_HEIGHT;
        }
    }

    private void postButtonClick() {
        this.menu.setSelectionHint(this.selectedDealIndex);
        this.menu.tryMoveItems(this.selectedDealIndex);
        ModNetwork.sendToServer(new ServerboundSelectTraderDealPacket(this.selectedDealIndex));
    }

    private int nbOfDeals() {
        return this.menu.getDeals().size();
    }

    private boolean canScroll() {
        return nbOfDeals() > NUMBER_OF_DEAL_BUTTONS;
    }

    private int nbOfTimesCanScrollDown() {
        return Mth.ceil((((double)this.menu.getDeals().size() - (double)NUMBER_OF_DEAL_BUTTONS) / 6));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.canScroll()) {
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - delta), 0, nbOfTimesCanScrollDown());
        }
        return true;
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll() && pMouseX > (double)(i + 119) && pMouseX < (double)(i + 119 + 6) && pMouseY > (double)(j + 24) && pMouseY <= (double)(j + 24 + 139 + 1)) {
            this.isDragging = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging) {
            int j = this.topPos + 24;
            int k = j + SCROLL_BAR_HEIGHT;
            int l = nbOfTimesCanScrollDown();
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderBackground(graphics);
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, BUY_SCREEN_TEXTURE_WIDTH, BUY_SCREEN_TEXTURE_HEIGHT);
        renderTabs(graphics);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        graphics.drawString(this.font, Component.literal("Goods"), this.inventoryLabelX, 28, 4210752, false);
        graphics.drawString(this.font, Component.literal("Value"), 220, 38, 4210752, false);
        graphics.drawString(this.font, title, titleLabelX, titleLabelY, 4210752, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderScroller(graphics);

        for (DealButton button : dealButtons) {
            button.visible = (button.index + this.scrollOff * GRID_COLUMNS) < nbOfDeals();
        }

        TraderDeals deals = this.menu.getDeals();
        if (!deals.isEmpty()) {
            int startX  = leftPos + GRID_X + 1;
            int x = startX;
            int y = topPos + GRID_Y + 1 + 1;

            int index = 0;
            for(TraderDeal deal : deals) {
                if (!this.canScroll() || index >= (this.scrollOff * GRID_COLUMNS) && index < NUMBER_OF_DEAL_BUTTONS + (this.scrollOff * GRID_COLUMNS)) {
                    ItemStack result = deal.getResult();
                    graphics.pose().pushPose();
                    graphics.pose().translate(0.0F, 0.0F, 100.0F);
                    graphics.renderFakeItem(result, x, y);
                    renderSoldItemDecorations(graphics, this.font, result, x, y);
                    graphics.pose().popPose();

                    x += DEAL_BUTTON_WIDTH;
                    if (x == startX + (DEAL_BUTTON_WIDTH * GRID_COLUMNS)) {
                        x = startX;
                        y += DEAL_BUTTON_HEIGHT;
                    }
                }
                ++index;
            }
            RenderSystem.enableDepthTest();
        }
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderScroller(GuiGraphics graphics) {
        int i = 1 + nbOfTimesCanScrollDown();
        if (i > 1) {
            int j = SCROLL_BAR_HEIGHT - (SCROLLER_HEIGHT + (i - 1) * SCROLL_BAR_HEIGHT / i);
            int k = 1 + j / i + SCROLL_BAR_HEIGHT / i;
            int scrollerOffset = Math.min(SCROLL_BAR_BOTTOM_Y, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                scrollerOffset = SCROLL_BAR_BOTTOM_Y;
            }

            graphics.blit(TEXTURE, leftPos + SCROLLER_X, topPos + SCROLL_BAR_TOP_Y + scrollerOffset, 0, SCROLLER_AVAIL_OFFSET_X, SCROLLER_AVAIL_OFFSET_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT, BUY_SCREEN_TEXTURE_WIDTH, BUY_SCREEN_TEXTURE_HEIGHT);
        } else {
            graphics.blit(TEXTURE, leftPos + SCROLLER_X, topPos + SCROLL_BAR_TOP_Y, 0, SCROLLER_OFFSET_X, SCROLLER_OFFSET_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT, BUY_SCREEN_TEXTURE_WIDTH, BUY_SCREEN_TEXTURE_HEIGHT);
        }

    }

    private void renderSoldItemDecorations(GuiGraphics graphics, Font font, ItemStack stack, int x, int y) {
        if (!stack.isEmpty() && stack.getCount() > 1) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            graphics.drawString(font, String.valueOf(stack.getCount()), x + 20 - 3 - font.width(String.valueOf(stack.getCount())), y + 6 + 3, 16777215, true);
            graphics.pose().popPose();
        }
    }

    class DealButton extends Button {
        public static final int DEAL_BUTTON_WIDTH = 18;
        public static final int DEAL_BUTTON_HEIGHT = 20;
        public static final int DEAL_BUTTON_OFFSET_X = 281;
        public static final int DEAL_BUTTON_OFFSET_Y = 0;
        public static final int DEAL_BUTTON_SEL_OFFSET_X = 281;
        public static final int DEAL_BUTTON_SEL_OFFSET_Y = 20;
        public static final int DEAL_BUTTON_HOVER_OFFSET_X = 281;
        public static final int DEAL_BUTTON_HOVER_OFFSET_Y = 40;
        final int index;

        public DealButton(int x, int y, int index, OnPress onPress) {
            super(x, y, DEAL_BUTTON_WIDTH, DEAL_BUTTON_HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

                int offsetX;
                int offsetY;
                if (this.isHovered && (this.index + SellScreen.this.scrollOff * GRID_COLUMNS) != SellScreen.this.selectedDealIndex) {
                    offsetX = DEAL_BUTTON_HOVER_OFFSET_X;
                    offsetY = DEAL_BUTTON_HOVER_OFFSET_Y;
                } else if ((this.index + SellScreen.this.scrollOff * GRID_COLUMNS) == SellScreen.this.selectedDealIndex) {
                    offsetX = DEAL_BUTTON_SEL_OFFSET_X;
                    offsetY = DEAL_BUTTON_SEL_OFFSET_Y;

                } else {
                    offsetX = DEAL_BUTTON_OFFSET_X;
                    offsetY = DEAL_BUTTON_OFFSET_Y;
                }

                graphics.blit(TEXTURE, getX(), getY(), offsetX, offsetY, this.width, this.height, SellScreen.BUY_SCREEN_TEXTURE_WIDTH, SellScreen.BUY_SCREEN_TEXTURE_HEIGHT);
                if (isHoveredOrFocused()) {
                    renderToolTip(graphics,mouseX,mouseY);
                }
            }

        }

        public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY) {
            if (this.isHovered && SellScreen.this.menu.getDeals().size() > this.index + (SellScreen.this.scrollOff * GRID_COLUMNS)) {
                ItemStack result = SellScreen.this.menu.getDeals().get(this.index + (SellScreen.this.scrollOff * GRID_COLUMNS)).getResult();
                ItemStack stackA = SellScreen.this.menu.getDeals().get(this.index + (SellScreen.this.scrollOff * GRID_COLUMNS)).getInputA();
                ItemStack stackB = SellScreen.this.menu.getDeals().get(this.index + (SellScreen.this.scrollOff * GRID_COLUMNS)).getInputB();
                ItemStack stackC = SellScreen.this.menu.getDeals().get(this.index + (SellScreen.this.scrollOff * GRID_COLUMNS)).getInputC();
                List<Component> text = Screen.getTooltipFromItem(SellScreen.this.minecraft, result);
                graphics.renderTooltip(SellScreen.this.font, text, Optional.of(new TradeItemTooltip(stackA, stackB, stackC)), result, mouseX, mouseY);
            }
        }
    }
}
