package org.dotvill.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.dotvill.entity.Dillager;
import org.dotvill.util.ModUtils;

public abstract class DillagerBaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation VILLAGER_BASE_SCREEN_TEXTURE = ModUtils.resource("textures/gui/dillager_base_screen.png");
    private static final int BASE_SCREEN_TEXTURE_WIDTH = 234;
    private static final int BASE_SCREEN_TEXTURE_HEIGHT = 73;

    private static final int UNSEL_TAB_WIDTH = 30;
    private static final int UNSEL_TAB_HEIGHT = 26;
    private static final int UNSEL_TAB_OFFSET_X = 0;
    private static final int UNSEL_TAB_OFFSET_Y = 32;
    private static final int SEL_TAB_WIDTH = 35;
    private static final int SEL_TAB_HEIGHT = 26;
    private static final int SEL_TAB_OFFSET_X = 30;
    private static final int SEL_TAB_OFFSET_Y = 32;

    private static final int TEXT_BUBBLE_WIDTH = 75;
    private static final int TEXT_BUBBLE_HEIGHT = 38;
    private static final int TEXT_BUBBLE_OFFSET_X = 0;
    private static final int TEXT_BUBBLE_OFFSET_Y = 0;
    private static final int TEXT_BUBBLE_X = 170;
    private static final int TEXT_BUBBLE_Y = 7;
    private static final int ENTITY_IN_INVENTORY_X = 130;
    private static final int ENTITY_IN_INVENTORY_Y = 100;
    private static final int ENTITY_IN_INVENTORY_SCALE = 35;
    protected Dillager villager;

    public DillagerBaseScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        //renderTabs(graphics);
        if (this.villager != null) {
            renderVillagerInScreen(graphics, mouseX, mouseY);
            renderVillagerTextBubble(graphics);
        }
    }

    private void renderVillagerInScreen(GuiGraphics graphics, int mouseX, int mouseY) {
        float lookAtX = 0;
        float lookAtY = 0;
        if (mouseX >= (leftPos - 30) && mouseX <= leftPos+imageWidth && mouseY >= topPos && mouseY <= topPos+imageHeight) {
            lookAtX = (float)(leftPos +240 ) - mouseX;
            lookAtY = (float)(topPos-5) - mouseY;
        } else {
            lookAtX = 40;
            lookAtY = 0;
        }
        lookAtX = (float)(leftPos +240 ) - mouseX;
        lookAtY = (float)(topPos+20) - mouseY;
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,leftPos + 247,topPos + 37, 40, lookAtX, lookAtY, this.villager);
    }

    private void renderVillagerTextBubble(GuiGraphics graphics) {
        graphics.blit(VILLAGER_BASE_SCREEN_TEXTURE, this.leftPos+107, topPos-28, TEXT_BUBBLE_OFFSET_X, TEXT_BUBBLE_OFFSET_Y, 117, 32, BASE_SCREEN_TEXTURE_WIDTH, BASE_SCREEN_TEXTURE_HEIGHT);

        graphics.drawString(this.font, Component.literal("Toolsmith").withStyle(ChatFormatting.GOLD), leftPos + 111, topPos - 37, 4210752, false);
        graphics.drawString(this.font, Component.literal("Yeah man buy me").withStyle(ChatFormatting.GRAY), leftPos + 113, topPos - 21, 4210752, false);
        graphics.drawString(this.font, Component.literal("everything here").withStyle(ChatFormatting.GRAY), leftPos + 114, topPos - 11, 4210752, false);
    }

    protected void renderTabs(GuiGraphics graphics) {
        //SEL
        graphics.blit(VILLAGER_BASE_SCREEN_TEXTURE, this.leftPos - SEL_TAB_WIDTH + 3 , this.topPos + 3 + 8, SEL_TAB_OFFSET_X, SEL_TAB_OFFSET_Y, SEL_TAB_WIDTH, SEL_TAB_HEIGHT, BASE_SCREEN_TEXTURE_WIDTH, BASE_SCREEN_TEXTURE_HEIGHT);

        graphics.blit(VILLAGER_BASE_SCREEN_TEXTURE, this.leftPos - SEL_TAB_WIDTH + 3 + 11, this.topPos + 3 + 6 + 8, 12, 58, 20, 15, BASE_SCREEN_TEXTURE_WIDTH, BASE_SCREEN_TEXTURE_HEIGHT);

        //UNSEL
        graphics.blit(VILLAGER_BASE_SCREEN_TEXTURE, this.leftPos - UNSEL_TAB_WIDTH, this.topPos + 3 + 8 + UNSEL_TAB_HEIGHT + 1, UNSEL_TAB_OFFSET_X, UNSEL_TAB_OFFSET_Y, UNSEL_TAB_WIDTH, UNSEL_TAB_HEIGHT, BASE_SCREEN_TEXTURE_WIDTH, BASE_SCREEN_TEXTURE_HEIGHT);

        graphics.blit(VILLAGER_BASE_SCREEN_TEXTURE, this.leftPos - UNSEL_TAB_WIDTH + 11, this.topPos + 3 + 8+  UNSEL_TAB_HEIGHT + 1 + 3 + 4, 0, 58, 12, 15, BASE_SCREEN_TEXTURE_WIDTH, BASE_SCREEN_TEXTURE_HEIGHT);
    }

    private void renderSelectedTab(GuiGraphics graphics) {

    }

    private void renderUnselectedTab(GuiGraphics graphics) {

    }

}
