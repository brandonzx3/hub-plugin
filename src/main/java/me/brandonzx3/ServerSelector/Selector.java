package me.brandonzx3.ServerSelector;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

public class Selector extends JavaPlugin implements Listener {

    int guiSize;
    ArrayList<NavItem<Integer, Material, String>> navItems = new ArrayList();

    private void ConfigFailed() {
        throw new RuntimeException("Failed to load config file");
    }



   @Override
    public void onEnable() {
        FileConfiguration configRoot = this.getConfig();
        ConfigurationSection selectorSection = configRoot.getConfigurationSection("Selector");

        if(selectorSection == null) ConfigFailed();
        this.guiSize = selectorSection.getInt("size");
        for(java.util.Map<?,?> navItem : selectorSection.getMapList("items")) {
            this.navItems.add(new NavItem(navItem.get("slot"), Material.matchMaterial((String) navItem.get("item")), navItem.get("service")));
        }

        getServer().getPluginManager().registerEvents(this, this);
;
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

class NavGui implements Listener {

    public final Inventory inv;
    Selector selector;
    
    public NavGui(Selector selector) {
        this.selector = selector;
        Bukkit.getServer().getPluginManager().registerEvents(this, selector);
        inv = Bukkit.createInventory(null, selector.guiSize, "Server Selector");
        addItems();
    }

    void addItems() {
        for(NavItem navItem : selector.navItems) {
            ItemStack item = new ItemStack((Material) navItem.item);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName((String) navItem.service);
            item.setItemMeta(itemMeta);
            inv.setItem((Integer) navItem.slot, item);
        }
    }

    void OpenInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    void OnPlayerInteract(final PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Material holdingItem = player.getItemInHand().getType();
        if(holdingItem != null) {
            if(holdingItem == Material.NETHER_STAR) {
                OpenInventory(player);
            }
        }
    }

    @EventHandler
    void OnInventoryClick(final InventoryClickEvent e) {
        if(e.getInventory() != inv) return;
        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;
        Player player = (Player)e.getWhoClicked();
    }
}