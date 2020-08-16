package me.brandonzx3.ServerSelector;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.arcadelia.coop.event.ArcPlayerJoinEvent;
import net.md_5.bungee.api.ChatColor;

class NavItem<Slot, Item, Service> {
    public final Slot slot;
    public final Item item;
    public final Service service;
    public NavItem(Slot slot, Item item, Service service) {
        this.slot = slot;
        this.item = item;
        this.service = service;
    }
}

class Navigator {
    int size;
    ArrayList<NavItem<Integer, Material, String>> navItems = new ArrayList();
}


public class Selector extends JavaPlugin implements Listener {

    private void ConfigFailed() {
        throw new RuntimeException("Failed to load config file");
    }



   @Override
    public void onEnable() {
        FileConfiguration configRoot = this.getConfig();
        ConfigurationSection selectorSection = configRoot.getConfigurationSection("Selector");

        if(selectorSection == null) ConfigFailed();
        Navigator nav = new Navigator();
        nav.size = selectorSection.getInt("size");
        for(java.util.Map<?,?> navItem : selectorSection.getMapList("items")) {
            nav.navItems.add(new NavItem(navItem.get("slot"), Material.matchMaterial((String) navItem.get("item")), navItem.get("service")));
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    void OnPlayerJoin(final ArcPlayerJoinEvent e) {
        Player player = e.getPlayer().getPlayer();
        Inventory inv = player.getInventory();
        ItemStack navigator = new ItemStack(Material.NETHER_STAR);
        ItemMeta navMeta = navigator.getItemMeta();
        navMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "Navigator");
        navigator.setItemMeta(navMeta);
        if(inv.getItem(4).getType() != Material.NETHER_STAR) {
            inv.setItem(4, navigator);
        }
    }
}