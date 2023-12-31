package org.dotvill.registry;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dotvill.DotVill;
import org.dotvill.menu.BuyMenu;
import org.dotvill.menu.SellMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DotVill.MOD_ID);

    public static final RegistryObject<MenuType<BuyMenu>> BUY_MENU = MENUS.register("buy_menu",
            () -> IForgeMenuType.create(BuyMenu::new));

    public static final RegistryObject<MenuType<SellMenu>> SELL_MENU = MENUS.register("sell_menu",
            () -> IForgeMenuType.create(SellMenu::new));
}