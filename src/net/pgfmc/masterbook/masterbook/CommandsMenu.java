package net.pgfmc.masterbook.masterbook;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.pgfmc.core.DimManager;
import net.pgfmc.core.cmd.Blocked;
import net.pgfmc.core.inventoryAPI.BaseInventory;
import net.pgfmc.core.inventoryAPI.Button;
import net.pgfmc.core.inventoryAPI.PagedInventory;
import net.pgfmc.core.inventoryAPI.SizeData;
import net.pgfmc.core.permissions.Role;
import net.pgfmc.core.playerdataAPI.PlayerData;
import net.pgfmc.core.requestAPI.Requester;
import net.pgfmc.core.requestAPI.Requester.Reason;
import net.pgfmc.masterbook.Main;
import net.pgfmc.survival.cmd.Afk;
import net.pgfmc.teams.friends.Friends;
import net.pgfmc.teams.friends.Friends.Relation;
import net.pgfmc.teleport.home.Homes;

public class CommandsMenu {
	
	private static boolean TEAMINIT = false;
	
	public static class Homepage extends BaseInventory {
		
		@SuppressWarnings("unchecked")
		public Homepage(PlayerData pd) {
			super(SizeData.SMALL, "Commands");
			if (!TEAMINIT) {
				TEAMINIT = (Bukkit.getServer().getPluginManager().getPlugin("Teams").isEnabled());
			}
			
			pd.getPlayer().getEffectivePermissions().forEach( x-> {
				/* 
				 * [] [] XX [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 */
				if (x.getPermission().equals("pgf.cmd.link") && x.getValue()) {
					
					if (pd.getData("Discord") != null) {
						setButton(2, new Button(Material.AMETHYST_SHARD, (e, i) -> {
							e.getWhoClicked().openInventory(new DiscordConfirm(pd).getInventory());
						}, "§dUnlink Discord"));
					} else {
						setButton(2, new Button(Material.QUARTZ, (e, i) -> {
							e.getWhoClicked().closeInventory();
							((Player) e.getWhoClicked()).performCommand("link");
						}, "§dLink Discord"));
					}
					
				} else 
				
				/* 
				 * [] [] [] XX [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * toggles AFK in-menu
				 */
				if (x.getPermission().equals("pgf.cmd.afk") && x.getValue()) {
					
					if (Afk.isAfk(pd.getPlayer())) {
						setButton(3, new Button(Material.BLUE_ICE, (e, i) -> {
							
							((Player) e.getWhoClicked()).performCommand("afk");
							e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
						}, "§r§7AFK: §aEnabled", "§r§7Click to disable!"));
					} else {
						setButton(3, new Button(Material.ICE, (e, i) -> {

							
							((Player) e.getWhoClicked()).performCommand("afk");
							e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
						}, "§r§7AFK: §cDisabled", "§r§7Click to enable!"));
					}
				}
				
				/* 
				 * [] [] [] [] [] XX [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * Back command
				 */
				if (x.getPermission().equals("pgf.cmd.back") && x.getValue()) {
					setButton(5, new Button(Material.ARROW, (e, i) -> {
						e.getWhoClicked().openInventory(new BackConfirm(pd).getInventory());
					}, "§r§4Back", "§r§7Go back to your last location"));
				}
				
				/* 
				 * [] [] [] [] [] [] XX [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * shows the current world's seed on screen.
				 */
				
				setButton(6, new Button(Material.BOOK, (e, i) -> {
					e.getWhoClicked().closeInventory();
					((Player) e.getWhoClicked()).openBook(Guidebook.getCopmleteBook());
				}, "§r§dInfo", "§r§7Bring up the guidebook"));
				
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] XX [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * /dim command
				 */
				if (x.getPermission().equals("pgf.cmd.goto") && x.getValue()) {
					setButton(13, new Button(Material.SPYGLASS, (e, i) -> {
						e.getWhoClicked().openInventory(new DimSelect(pd).getInventory());
					}, "§r§9Dimensions", "§r§7Go to other worlds!"));
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] XX [] [] [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.home.*") && x.getValue()) {
					setButton(20, new Button(Material.COMPASS, (e, i) -> {
						e.getWhoClicked().openInventory(new HomeMenu(pd).getInventory());
					}, "§r§eHomes"));
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] XX [] [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.tp.tpa") && x.getValue()) {
					
					if (Bukkit.getOnlinePlayers().size() == 1) {
						setButton(21, new Button(Material.GRAY_CONCRETE, (e, i) -> {
							pd.playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
						}, "§r§5Tpa", "§r§cNo players online."));
					} else {
						setButton(21, new Button(Material.ENDER_PEARL, (e, i) -> {
							e.getWhoClicked().openInventory(new TpaList(pd).getInventory());
						}, "§r§5Tpa", "§r§7Teleport to another player!"));
					}
					
					
					
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] XX [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("teams.friend.*") && x.getValue() && TEAMINIT) {
					setButton(23, new Button(Material.TOTEM_OF_UNDYING, (e, i) -> {
						e.getWhoClicked().openInventory(new FriendsList(pd).getInventory());
					}, "§r§6Friends"));
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] XX [] []
				 * home menu
				 */
				if (x.getPermission().equals("bukkit.command.list") && x.getValue() && TEAMINIT) {
					setButton(24, new Button(Material.PLAYER_HEAD, (e, i) -> {
						e.getWhoClicked().openInventory(new PlayerList(pd).getInventory());
					}, "§r§bPlayer List"));
				}
				
				// Other buttons -
				
				/* 
				 * [] [] [] [] XX [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * home menu
				 */
				if (pd.getData("Roles") != null && ((List<Role>) pd.getData("Roles")).contains(Role.ADMIN)) {
					setButton(4, new Button(Material.EMERALD, "§r§cAdmin"));
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] XX [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.donator.echest") && x.getValue()) {
					setButton(22, new Button(Material.ENDER_CHEST, (e, i) -> {
						e.getWhoClicked().closeInventory();
						((Player) e.getWhoClicked()).performCommand("echest");
					}, "§r§3Ender Chest", "§r§9VIP perk!"));
				}
				
				setButton(9, new Button(Material.LEVER, (e, i) -> {
					e.getWhoClicked().openInventory(new RequestList(pd).getInventory());
				}, "§r§4Requests"));
			});
		}
	}
	
	// all nested inventories are below.
	// so ye, classes within classes
	// how about that, huh?
	
	/**
	 * Discord Link/unlink command inventory.
	 * @author CrimsonDart
	 *
	 */
	private static class DiscordConfirm extends BaseInventory {
		
		public DiscordConfirm(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Unlink Account?");
			
			/*
			 * checks if discord is already linked, and creates buttons corresponding to this information.
			 * sets buttons to the already-coded commands, because there is no difference. 
			 */
			
			setButton(11, new Button(Material.LIME_CONCRETE, (e, i) -> {
				// p.closeInventory(); // I think it is better if the unlink doesn't close the inventory
				((Player) e.getWhoClicked()).performCommand("unlink");
				e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
			}, "§r§cUnlink"));
			
			// cancel button.
			setButton(15, new Button(Material.RED_CONCRETE, (e, i) -> {
				e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
			}, "§r§7Cancel"));
		}
	}
	
	private static class BackConfirm extends BaseInventory {
		public BackConfirm(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Tp to last location?");
			
			setButton(11, new Button(Material.LIME_CONCRETE, (e, i) -> {
				e.getWhoClicked().closeInventory();
				((Player) e.getWhoClicked()).performCommand("back");
			}, "§r§dTeleport"));
			
			setButton(15, new Button(Material.RED_CONCRETE, (e, i) -> {
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
			}, "§r§7Cancel"));
		}
	}
	
	private static class DimSelect extends PagedInventory {
		
		public DimSelect(PlayerData pd) {
			super(SizeData.SMALL, "§r§5Dimension Select", DimManager.getAllWorlds(false).stream()
					.map( x-> {
						return new Button(Material.ENDER_PEARL, (e, i) -> {
							((Player) e.getWhoClicked()).performCommand("goto " + x.getName());
						}, "§r§9" + x.getName());
					})
					.collect(Collectors.toList())
			);
			setBackButton(new Homepage(pd));
		}
		
	}
	
	private static class HomeMenu extends BaseInventory {
		
		public HomeMenu(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Home");
			
			setButton(0, new Button(Material.FEATHER, (e, i) -> {
				e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
			}, "§r§7Back"));
			
			setButton(11, new Button(Material.ENDER_PEARL, (e, i) -> {
				e.getWhoClicked().openInventory(new HomeList(pd, "home ").getInventory());
			}, "§r§dGo to Home"));
			
			if (!(Homes.getHomes(pd.getOfflinePlayer()).size() >= 3)) {
				
				setButton(11, new Button(Material.OAK_SAPLING, (e, i) -> {
					e.getWhoClicked().openInventory(new SetConfirm(pd).getInventory());
				}, "§r§aSet Home"));
			}
			
			setButton(15, new Button(Material.FLINT_AND_STEEL, (e, i) -> {
				e.getWhoClicked().openInventory(new DelList(pd).getInventory());
			}, "§r§cDelete Home"));
		}
		
		private static class HomeList extends PagedInventory {
			
			public HomeList(PlayerData pd, String dingus) {
				super(SizeData.SMALL, "§r§8Home Select", Homes.getHomes(pd.getPlayer()).keySet().stream()
						.map(x-> {
							return new Button(Material.PAPER,  (e, i) -> {
								((Player) e.getWhoClicked()).performCommand(dingus + x);
								e.getWhoClicked().closeInventory();
							}, x);
						})
						.collect(Collectors.toList())
				);
				setBackButton(new HomeMenu(pd));
			}
		}
		
		/**
		 * /sethome confirm inventory.
		 * 
		 * @author CrimsonDart
		 * {@link net.pgfmc.core.ChatEvents}
		 *
		 */
		private static class SetConfirm extends BaseInventory {
			public SetConfirm(PlayerData pd) {
				super(SizeData.SMALL, "§r§8Set home here?");
				
				setButton(11, new Button(Material.LIME_CONCRETE, (e, i) -> {
					pd.setData("tempHomeLocation", pd.getPlayer().getLocation());
					e.getWhoClicked().closeInventory();
					pd.sendMessage("§r§dType into chat to set the name of your Home!");
					pd.sendMessage("§r§dYou can only name the home for 4 minutes.");
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						
						@Override
						public void run()
						{
							if (pd.getData("tempHomeLocation") != null) {
								pd.setData("tempHomeLocation", null);
								pd.sendMessage("§r§cYour home couldnt be set.");
							}
						}
						
					}, 20 * 60 * 4);
				}, "§r§aSet Home"));
				
				setButton(15, new Button(Material.RED_CONCRETE, (e, i) -> {
					e.getWhoClicked().openInventory(new HomeMenu(pd).getInventory());
				}, "§r§7Cancel"));
			}
		}
		
		
		private static class DelList extends PagedInventory {
			
			public DelList(PlayerData pd) {
				super(SizeData.SMALL, "§r§8Delete Home", Homes.getHomes(pd.getPlayer()).keySet().stream()
						.map(x-> {
							return new Button(Material.PAPER, (e, i) -> {
								((Player) e.getWhoClicked()).performCommand("delhome " + x);
								e.getWhoClicked().openInventory(new DelList(pd).getInventory());
							}, "§r§a" + x);
						})
						.collect(Collectors.toList()));
				
				setBackButton(new HomeMenu(pd));
			}
		}
		
	}
	
	private static class TpaList extends PagedInventory {
		public TpaList(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Select who to teleport to!", Bukkit.getOnlinePlayers().stream()
					.filter(x-> {
						return (!x.getUniqueId().toString().equals(pd.getUniqueId().toString()));
					})
					.map( (x) -> {
						return new Button(Material.PLAYER_HEAD, (e, i) -> {
							((Player) e.getWhoClicked()).performCommand("tpa " + x.getName());
							e.getWhoClicked().openInventory(new Homepage(pd).getInventory());
						}, "§r§a" + x.getName());
					})
					.collect(Collectors.toList())
			);
			
			setBackButton(new Homepage(pd));
		}
	}
	
	public static class FriendsList extends PagedInventory {
		
		public FriendsList(PlayerData player) {
			super(SizeData.SMALL, "§r§8Friends List", 
					Friends.getFriendsMap(player).keySet().stream().map((x) -> {
				return new Button(Material.PAPER, (e, i) -> {
					
					e.getWhoClicked().openInventory(new FriendOptions(player, x).getInventory());
					
				}, "§r" + x.getRankedName() );
			}).collect(Collectors.toList()));
			
			setBackButton(new Homepage(player));
		}
		
		public static class FriendOptions extends BaseInventory {

			public FriendOptions(PlayerData player, PlayerData friend) {
				super(SizeData.SMALL, "§r§8Options for " + friend.getRankedName());
				
				setButton(12, new Button(Material.ARROW, (e, i) -> {
					Friends.setRelation(player, Relation.NONE, friend, Relation.NONE);
					player.sendMessage("§cYou have Unfriended " + friend.getName() + ".");
					player.playSound(Sound.BLOCK_CALCITE_HIT);
					// player.getPlayer().closeInventory(); // Better if not close
					e.getWhoClicked().openInventory(new FriendOptions(player, friend).getInventory());
					
				}, "§r§cUnfriend"));
				
				Relation r = Friends.getRelation(player, friend);
				
				if (r == Relation.FRIEND) {
					setButton(14, new Button(Material.NETHER_STAR, (e, i) -> {
						
						Friends.setRelation(player, friend, Relation.FAVORITE);
						player.sendMessage("§r§6" + friend.getName() + " is now a favorite!");
						player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
						// player.getPlayer().closeInventory(); // Better if not close
						e.getWhoClicked().openInventory(new FriendOptions(player, friend).getInventory());
						
					}, "§r§eFavorite"));
					
				} else if (r == Relation.FAVORITE) {
					setButton(14, new Button(Material.NETHER_STAR, (e, i) -> {
						
						Friends.setRelation(player, friend, Relation.FRIEND);
						player.sendMessage("§r§c" + friend.getName() + " has Been unfavorited!");
						player.playSound(Sound.BLOCK_CALCITE_HIT);
						// player.getPlayer().closeInventory(); // Better if not close
						e.getWhoClicked().openInventory(new FriendOptions(player, friend).getInventory());
						
					}, "§r§6Unfavorite"));
				}
			}
		}
	}
	
	public static class PlayerList extends PagedInventory {
		
		public PlayerList(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Player List", PlayerData.stream()
					.filter(x-> {
						return (x != pd);
					})
					.sorted((o1, o2) -> { // player sorter.
						
						if (o1.isOnline() && o2.isOnline()) { // both online
							
							Relation r1 = Friends.getRelation(pd, o1);
							Relation r2 = Friends.getRelation(pd, o2);
							
							if (r1 == r2) { // both are equal.
								return 0;
							} else if (r1 == Relation.NONE && r2 != Relation.NONE) { // r2 friended &^ but not r1.
								return -1;
							} else if (r1 != Relation.NONE && r2 == Relation.NONE) { // r1 friended &^ but not r2.
								return 1;
							} else if (r1 == Relation.FRIEND && r2 == Relation.FAVORITE) {
								return -1;
							} else if (r1 == Relation.FAVORITE && r2 == Relation.FRIEND) {
								return 1;
							} else {
								return 0;
							}
						} else if (o1.isOnline()) { // 1 is online
							return 1;
						} else if (o2.isOnline()) { // 2 is online
							return -1;
						} else { // ------------------ none are online
							Relation r1 = Friends.getRelation(pd, o1);
							Relation r2 = Friends.getRelation(pd, o2);
							
							if (r1 == r2) { // both are equal.
								return 0;
							} else if (r1 == Relation.NONE && r2 != Relation.NONE) { // r2 friended &^ but not r1.
								return -1;
							} else if (r1 != Relation.NONE && r2 == Relation.NONE) { // r1 friended &^ but not r2.
								return 1;
							} else if (r1 == Relation.FRIEND && r2 == Relation.FAVORITE) {
								return -1;
							} else if (r1 == Relation.FAVORITE && r2 == Relation.FRIEND) {
								return 1;
							} else {
								return 0;
							}
						}
					})
					.map(x -> {
						return new Button(Material.PLAYER_HEAD, (e, i) -> {
							e.getWhoClicked().openInventory(new PlayerOptions(pd, x).getInventory());
						}, "§r§a" + x.getName(), (x.getOfflinePlayer().isOnline()) ? "§r§aOnline" : "§r§cOffline"
						
						);
					})
					.collect(Collectors.toList())
			);
			
			setBackButton(new Homepage(pd));
		}
		
		private static class PlayerOptions extends BaseInventory {
			
			public PlayerOptions(PlayerData pd, PlayerData player) {
				super(SizeData.SMALL, player.getRankedName());
				
				setButton(0, new Button(Material.FEATHER, (e, i) -> {
					e.getWhoClicked().openInventory(new PlayerList(pd).getInventory());
				}, "§r§7Back"));
				
				pd.getPlayer().getEffectivePermissions().forEach(x-> {
					if (x.getPermission().equals("teams.friend.*") && x.getValue() && TEAMINIT) {
						
						Relation r = Friends.getRelation(pd, player);
						if (r == Relation.FRIEND || r == Relation.FAVORITE) {
							setButton(11, new Button(Material.TOTEM_OF_UNDYING, (e, i) -> {
								e.getWhoClicked().openInventory(new UnfriendConfirm(pd, player).getInventory());
							}, "§r§cUnfriend"));
							
							if (r == Relation.FAVORITE) {
								setButton(12, new Button(Material.TOTEM_OF_UNDYING, (e, i) -> {
									((Player) e.getWhoClicked()).performCommand("unfav " + player.getName());
									e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
									
								}, "§r§cUnfavorite"));
							} else {
								setButton(12, new Button(Material.TOTEM_OF_UNDYING, (e, i) -> {
									((Player) e.getWhoClicked()).performCommand("fav " + player.getName());
									// p.closeInventory();
									e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
								}, "§r§eFavorite"));
							}
							
						} else {
							setButton(11, new Button(Material.TOTEM_OF_UNDYING, (e, i) -> {
								e.getWhoClicked().openInventory(new FriendConfirm(pd, player).getInventory());
							}, "§r§6Friend"));
						}
					}
					
					if (x.getPermission().equals("pgf.cmd.block") && x.getValue()) {
						
						if (Blocked.GET_BLOCKED(pd.getOfflinePlayer()).contains(player.getUniqueId())) {
							setButton(14, new Button(Material.RED_STAINED_GLASS_PANE, (e, i) -> {
								((Player) e.getWhoClicked()).performCommand("unblock " + player.getName());
								// p.closeInventory();
								e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
							}, "§r§4Unblock"));
						} else {
							setButton(14, new Button(Material.WHITE_STAINED_GLASS_PANE, (e, i) -> {
								((Player) e.getWhoClicked()).performCommand("block " + player.getName());
								// p.closeInventory();
								e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
							}, "§r§4Block"));
						}
					}
					
					setButton(15, new Button(Material.RED_BANNER, "§r§4Report", "§r§7If someone is bullying or\ngriefing you, use this!" + "\nWIP"));
				});
			}
			
			private static class FriendConfirm extends BaseInventory {
				
				public FriendConfirm(PlayerData pd, PlayerData player) {
					super(SizeData.SMALL, "§r§6Friend " + player.getName() + "?");
					
					setButton(11, new Button(Material.LIME_CONCRETE, (e, i) -> {
						// p.closeInventory();
						((Player) e.getWhoClicked()).performCommand("friendrequest " + player.getName());
						e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
					}, "§r§aSend Request"));
					
					setButton(15, new Button(Material.RED_CONCRETE, (e, i) -> {
						e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
					}, "§r§7Cancel"));
				}
			}
			
			private static class UnfriendConfirm extends BaseInventory {
				
				public UnfriendConfirm(PlayerData pd, PlayerData player) {
					super(SizeData.SMALL, "§r§cUnfriend " + player.getName() + "?");
					
					setButton(11, new Button(Material.LIME_CONCRETE, (e, i) -> {
						// p.closeInventory();
						((Player) e.getWhoClicked()).performCommand("unfriend " + player.getName());
						e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
					}, "§r§cUnfriend"));
					
					setButton(15, new Button(Material.RED_CONCRETE, (e, i) -> {
						e.getWhoClicked().openInventory(new PlayerOptions(pd, player).getInventory());
					}, "§r§7Cancel"));
				}
			}
		}
	}
	
	public static class RequestList extends PagedInventory {
		public RequestList(PlayerData pd) {
			super(SizeData.SMALL, "Pending Requests", Requester.ALLREQUESTS.stream()
					.filter(x-> {
						return (x.getTarget() == pd);
					})
					.map( x -> {
						return new Button(Material.ARROW, (e, i) -> {
							if (x.expireNow(Reason.Accept) != false) {
								x.act();
							} else {
								pd.playSound(Sound.BLOCK_NOTE_BLOCK_BASS);
								e.getWhoClicked().openInventory(new RequestList(pd).getInventory());
							}
						}, x.getParent().getName());
					})
					.collect(Collectors.toList())
			);
			setBackButton(new Homepage(pd));
		}
	}
}