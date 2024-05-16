package bet.ter.sieves;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SieveEvents implements Listener {

    private Main plugin;
    public SieveEvents(Main plugin) { this.plugin = plugin; }

    public static RandomCollection<String> stoneSieve = new RandomCollection<>();
    public static RandomCollection<String> sandSieve = new RandomCollection<>();
    private String prefix = "§8[§7§lBetter§4§lSieves§8]";

    BukkitTask taskStone;
    BukkitTask taskNether;

    @EventHandler
    public void onItemSent(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        Player p = (Player)e.getWhoClicked();
        if (item != null) {
            Inventory dropper = e.getInventory();
            if (e.getView().getTitle().contains("Sieve")){
                Location l = dropper.getLocation();
                Location l2 = new Location(l.getWorld(), l.getX(), l.getY() + 1.0D, l.getZ());
                Block above = l2.getBlock();
                ItemStack stone = new ItemStack(Material.STONE, 1);             // item di riferimento per la pietra
                ItemStack redsand = new ItemStack(Material.SAND, 1, (short) 1); //item di riferimento per la sabbia rossa
                if (above.getType().equals(Material.CHEST) || above.getType().equals(Material.HOPPER)) {  //controlla se c'è la chest/hopper sopra il setacciatore, se non c'è.. NON VA AVANTI
                    if (e.getView().getTitle().contains("Stone")) {  // se è il Setacciatore della Stone
                        Inventory finalInv = getContainerInventory(above);
                        if (finalInv != null){
                            taskStone = new BukkitRunnable() {  //si avvia il task
                                public void run() {
                                for (int i = 0; i < dropper.getSize(); i++) {  //per ogni slot del dropper (9)
                                    ItemStack is = dropper.getItem(i);  // prende l'item nello slot
                                    if (is != null) {  // se non è null o air
                                        if (is.getData().equals(stone.getData())) {  //se l'item è Stone pura.. Granite ecc hanno un DATA diverso da quello della Stone normale (0) e controlla appunto questo
                                            for (int k = 0; k < 4; k++) { 
                                                if (is.getAmount() >= 1) {
                                                    String randMaterial = stoneSieve.next();//prendo l'output come Stringa e lo processo, per vedere se è granite ecc, altrimenti, nell'else puo essere solo GRAVEL e non serve modificare e la aggiungo direttamente//                                                                if (randMaterial.contains("_")) {
                                                    String[] split = randMaterial.split("_"); // vai a vedere Sieve.java per capire
                                                    ItemStack randItem;
                                                    if (split[0].equalsIgnoreCase("STONE")) {   // caso in cui STONE_1, STONE_3, STONE_5 e quindi DIORITE, GRANITE, ANDESITE
                                                        randItem = new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]));
                                                    } else if (split[0].equalsIgnoreCase("SAND")) { // caso in cui sia RED_SAND (SAND_1)
                                                        if(split.length == 2)
                                                            randItem = new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1])); //REDSAND
                                                        else
                                                            randItem = new ItemStack(Material.getMaterial(split[0])); //SAND
                                                    } else{ //il caso in cui sia gravel
                                                        randItem = new ItemStack(Material.getMaterial(randMaterial), 1); //GRAVEL
                                                    }

                                                    if(containerSlotEmpty(finalInv, randItem)) {
                                                        finalInv.addItem(randItem);
                                                        is.setAmount(is.getAmount() - 1); //viene aggiornato il valore dello stack
                                                    } else {
                                                        p.sendMessage(prefix + " §cChest/Hopper pieno, setaccio bloccato!");
                                                        taskStone.cancel();
                                                        break;
                                                    }

                                                } else
                                                    taskStone.cancel();
                                            }
                                            dropper.setItem(i, is); //il tuo setacciatore prendeva tutto lo stack o GLI STACK, li setacciava tutti e rip, qua invece NO, quattro pezzi alla volta
                                            if (is.getAmount() < 1)
                                                taskStone.cancel();
                                            break; //esco dal ciclo for(i) perche altrimenti per ogni slot me ne processerebbe 4 e non è molto stonks, cioe si ma no//se ci sono meno di quattro pezzi nel dropper viene cancellato il task, pero non calcola tutti gli slot, sarebbe da aggiungere, esempio: nello slot 1 ci sono 2 di stone e nello slot 2 ci sono 3 di stone, insieme fanno 5 ma comunque non vengono processati perche in un singolo slot ce ne devono essere piu di 4
                                        } else
                                            p.sendMessage(prefix + " §cBlocco non valido, setaccia solo pietra!"); // caso in cui si mette altro nel setacciatore avverte il player con molta gentilezza, spammandoglielo ogni volta che lui clicca :^)
                                    }
                                }
                                }

                            }.runTaskLater(plugin, 20L); //esegue il task dopo un secondo, per debug ho messo un secondo, poi si aggiusta
                        }
                    } else if (e.getView().getTitle().contains("Nether")) {
                        Inventory finalInv = getContainerInventory(above);
                        if (finalInv != null) {
                            taskNether = new BukkitRunnable() {
                                public void run() {
                                for (int i = 0; i < dropper.getSize(); i++) {
                                    ItemStack is = dropper.getItem(i);
                                    if (is != null) {
                                        if (is.getData().equals(redsand.getData())) {
                                            for (int k = 0; k < 4; k++) {
                                                if (is.getAmount() >= 1) {
                                                    String randMaterial = sandSieve.next();
                                                    ItemStack randItem = new ItemStack(Material.getMaterial(randMaterial));
                                                    if(containerSlotEmpty(finalInv, randItem)) {
                                                        finalInv.addItem(randItem);
                                                        is.setAmount(is.getAmount() - 1);
                                                    } else {
                                                        p.sendMessage(prefix + " §cChest/Hopper pieno, setaccio bloccato!");
                                                        taskStone.cancel();
                                                        break;
                                                    }
                                                } else
                                                    taskNether.cancel();
                                            }
                                            dropper.setItem(i, is);
                                            if (is.getAmount() < 1)
                                                taskNether.cancel();
                                            break;
                                        } else
                                            p.sendMessage(prefix + " §cBlocco non valido, setaccia solo sabbia rossa!");
                                    }
                                }
                                }
                            }.runTaskLater(plugin, 20L);
                        }
                    }
                } else p.sendMessage(prefix + " §cPlace a chest/hopper on top of the sieve!");

            }
        }
    }

    private Inventory getContainerInventory(Block above) {
        Inventory inv = null;
        Chest chest;
        Hopper hopper;
        if (above.getType().equals(Material.CHEST)) {
            chest = (Chest) above.getState();
            inv = chest.getInventory();
        } else if (above.getType().equals(Material.HOPPER)) {
            hopper = (Hopper) above.getState();
            inv = hopper.getInventory();
        }
        return inv;

    }

    private boolean containerSlotEmpty(Inventory inv, ItemStack item){
        for(int i = 0; i < inv.getSize(); i++){
            ItemStack is = inv.getItem(i);
            if(is == null)
                return true;
            else {
                if(is.equals(item) && is.getAmount() < is.getMaxStackSize())
                    return true;
            }
        }
        return false;
    }

    /*
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(e.getClickedBlock().getType().equals(Material.DROPPER)){
                File f = Utilities.getFile("setacciatori.yml");
                YamlConfiguration setacciatori = YamlConfiguration.loadConfiguration(f);
                Block b = e.getClickedBlock();
                for(String s : setacciatori.getStringList("StoneSieve")){
                    
                }
            }
        }
    }

     */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        File f = Utilities.getFile("setacciatori.yml");
        YamlConfiguration setacciatori = YamlConfiguration.loadConfiguration(f);
        if (b.getType().equals(Material.DROPPER)) {
            List<String> lista1 = setacciatori.getStringList("Setacciatori.StoneSieve");
            int x = b.getX();
            int y = b.getY();
            int z = b.getZ();
            for (int i = 0; i < lista1.size(); i++) {
                String[] split = (lista1.get(i)).split("/");
                if (Integer.parseInt(split[0]) == x && Integer.parseInt(split[1]) == y && Integer.parseInt(split[2]) == z) {
                    e.setDropItems(false);
                    ItemStack is = new ItemStack(Material.DROPPER);
                    ItemMeta isMeta = is.getItemMeta();
                    isMeta.setDisplayName("§7§lStone Sieve");
                    is.setItemMeta(isMeta);
                    Bukkit.getWorld(b.getWorld().getName()).dropItem(b.getLocation(), is);
                    lista1.remove(i);
                    setacciatori.set("Setacciatori.StoneSieve", lista1);
                    break;
                }
            }
            List<String> lista2 = setacciatori.getStringList("Setacciatori.NetherSieve");
            for (int i = 0; i < lista2.size(); i++) {
                String[] split = lista2.get(i).split("/");
                if (Integer.parseInt(split[0]) == x && Integer.parseInt(split[1]) == y && Integer.parseInt(split[2]) == z) {
                    e.setDropItems(false);
                    ItemStack is = new ItemStack(Material.DROPPER);
                    ItemMeta isMeta = is.getItemMeta();
                    isMeta.setDisplayName("§c§lNether Sieve");
                    is.setItemMeta(isMeta);
                    Bukkit.getWorld(b.getWorld().getName()).dropItem(b.getLocation(), is);

                    try {
                        lista2.remove(i);
                        setacciatori.set("Setacciatori.NetherSieve", lista2);
                        break;
                    } catch (Exception ex) {
                        e.getPlayer().sendMessage(String.valueOf(ex));

                        break;
                    }
                }
            }
            try {
                setacciatori.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if(b.getType().equals(Material.OAK_WALL_SIGN)) {
            List<String> sieves = setacciatori.getStringList("Setacciatori.NetherSieve");
            sieves.addAll(setacciatori.getStringList("Setacciatori.StoneSieve"));
            //plugin.getLogger().info(sieves.toString());
            for(String sieve : sieves){
                String[] split = sieve.split("/");
                int x = Integer.parseInt(split[0]);
                int y = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                Location signLocation = new Location(b.getWorld(), x, y, z);
                //plugin.getLogger().info("signLocation: " + signLocation.toString() + " - - - bLocation" + b.getLocation());
                if(b.getLocation().distance(signLocation) < 2)
                    e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        ItemStack itemBlock = e.getItemInHand();

        if (block.getType().equals(Material.DROPPER)) {
            Dropper d = (Dropper) block.getState();
            //plugin.getLogger().info("customName: " + d.getCustomName());
            if(d.getCustomName().contains("Sieve")) {
                Block placed = e.getBlockPlaced();
                BlockFace targetFace = ((org.bukkit.material.Dispenser) placed.getState().getData()).getFacing();

                if(targetFace.equals(BlockFace.UP) || targetFace.equals(BlockFace.DOWN)){
                    e.setCancelled(true);
                } else{

                    File f = Utilities.getFile("setacciatori.yml");
                    YamlConfiguration setacciatori = YamlConfiguration.loadConfiguration(f);
                    String top = "";
                    if (itemBlock.getItemMeta().getDisplayName().contains("Stone")) {
                        if(p.hasPermission("bettersieves.stone")){
                            List<String> lista = setacciatori.getStringList("Setacciatori.StoneSieve");
                            lista.add(placed.getX() + "/" + placed.getY() + "/" + placed.getZ());
                            setacciatori.set("Setacciatori.StoneSieve", lista);
                            top = "&0[&f&lStone&7&lSieve&0]";
                        } else {
                            p.sendMessage(prefix + " §cNon hai i permessi!");
                            e.setCancelled(true);
                            return;
                        }
                    } else {
                        if(p.hasPermission("bettersieves.nether")){
                            List<String> lista = setacciatori.getStringList("Setacciatori.NetherSieve");
                            lista.add(placed.getX() + "/" + placed.getY() + "/" + placed.getZ());
                            setacciatori.set("Setacciatori.NetherSieve", lista);
                            top = "&0[&c&lNether&7&lSieve&0]";
                        } else {
                            p.sendMessage(prefix + " §cNon hai i permessi!");
                            e.setCancelled(true);
                            return;
                        }
                    }

                    double x = placed.getX();
                    double y = placed.getY();
                    double z = placed.getZ();

                    if (targetFace.equals(BlockFace.NORTH))
                        z -= 1;
                    else if (targetFace.equals(BlockFace.EAST))
                        x += 1;
                    else if (targetFace.equals(BlockFace.SOUTH))
                        z += 1;
                    else if (targetFace.equals(BlockFace.WEST))
                        x -= 1;

                    Location signLocation = new Location(p.getWorld(), x, y, z);

                    p.getWorld().getBlockAt(signLocation).setType(Material.OAK_WALL_SIGN);
                    Block signBlock = p.getWorld().getBlockAt(signLocation);

                    org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
                    org.bukkit.block.data.type.WallSign wallSign = (org.bukkit.block.data.type.WallSign) sign.getBlockData();
                    wallSign.setFacing(targetFace);

                    signBlock.getState().setData(new MaterialData(Material.OAK_WALL_SIGN));

                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', top));
                    sign.setBlockData(wallSign);
                    sign.update();

                    try {
                        setacciatori.save(f);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e){
        for(Block b : e.getBlocks()) {
            if(b.getType().equals(Material.DROPPER)){
                Dropper d = (Dropper) b.getState();
                if(d.getCustomName().contains("Sieve")){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e){
        for(Block b : e.blockList()){
            if(b.getType().equals(Material.DROPPER)){
                Dropper d = (Dropper) b.getState();
                if(d.getCustomName().contains("Sieve")){
                    e.setCancelled(true);
                }
            }
        }
    }

    /*
    @EventHandler
    public void onRedstoneImpulse(BlockRedstoneEvent e ){
        plugin.getLogger().info("REDSTONE EVENT TRIGGERATO");
        Block block = e.getBlock();
        for (Block b : SurroundingBlocks(block)){
            if (CheckBlock(b)){
                plugin.getLogger().info("E' UN GENERATORE");
                Generator generator = null;

                for(Generator g : listofgenerators){
                    if(g.getLocation().equals(b.getLocation())) {
                        generator = g;
                        break;
                    }
                }
                if(generator != null){
                    if (b.isBlockIndirectlyPowered()){
                        if(!listofactivated.containsKey(block)){
                            plugin.getLogger().info("Avvio il generatore..");
                            generator.start();
                            listofactivated.put(block, generator);
                        }
                    }
                    else{
                        if(listofactivated.containsKey(block)){
                            plugin.getLogger().info("Spengo il generatore..");
                            generator.stop();
                            listofactivated.remove(block);
                        }
                    }
                }
            }
        }
    }

    public boolean CheckBlock(Block block){
        return Utilities.isGenerator(block);
    }

    public ArrayList<Block> SurroundingBlocks(Block block){
        ArrayList<Block> Blocks = new ArrayList<Block>();
        for (BlockFace face : BlockFace.values()){
            if (face == BlockFace.UP){
                Block above = block.getRelative(BlockFace.UP);
                Block above2 = above.getRelative(BlockFace.UP);
                Blocks.add(above);
                Blocks.add(above2);}
            Blocks.add(block.getRelative(face));
        }
        return Blocks;
    }


     */
}
