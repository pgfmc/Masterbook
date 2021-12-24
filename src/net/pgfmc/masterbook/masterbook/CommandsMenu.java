package net.pgfmc.masterbook.masterbook;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.pgfmc.core.DimManager;
import net.pgfmc.core.cmd.Blocked;
import net.pgfmc.core.inventoryAPI.InteractableInventory;
import net.pgfmc.core.inventoryAPI.PagedInventory;
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
	
	public static class Homepage extends InteractableInventory {
		
		@SuppressWarnings("unchecked")
		public Homepage(PlayerData pd) {
			super(SizeData.SMALL, "Commands");
			if (!TEAMINIT) {
				TEAMINIT = (Bukkit.getServer().getPluginManager().getPlugin("Teams").isEnabled());
			}
			
			pd.getPlayer().getEffectivePermissions().forEach(x-> {
				/* 
				 * [] [] XX [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 */
				if (x.getPermission().equals("pgf.cmd.link") && x.getValue()) {
					
					if (pd.getData("Discord") != null) {
						createButton(Material.AMETHYST_SHARD, 2, "§dUnlink Discord", (p, e) -> {
							p.openInventory(new DiscordConfirm(pd).getInventory());
						});
					} else {
						createButton(Material.QUARTZ, 2, "§dLink Discord", (p, e) -> {
							p.closeInventory();
							p.performCommand("link");
						});
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
						createButton(Material.BLUE_ICE, 3, "§r§7AFK: §aEnabled", "§r§7Click to disable!", (p, e) -> {
							// p.closeInventory(); // Better if not close
							p.performCommand("afk");
							p.openInventory(new Homepage(pd).getInventory());
						});
					} else {
						createButton(Material.ICE, 3, "§r§7AFK: §cDisabled", "§r§7Click to enable!", (p, e) -> {
							// p.closeInventory(); // Better if not close
							p.performCommand("afk");
							p.openInventory(new Homepage(pd).getInventory());
						});
					}
				}
				
				/* 
				 * [] [] [] [] [] XX [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * Back command
				 */
				if (x.getPermission().equals("pgf.cmd.back") && x.getValue()) {
					createButton(Material.ARROW, 5, "§r§4Back", "§r§7Go back to your last location", (p, e) -> {
						p.openInventory(new BackConfirm(pd).getInventory());
					});
				}
				
				/* 
				 * [] [] [] [] [] [] XX [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * shows the current world's seed on screen.
				 */
				
				createButton(Material.BOOK, 6, "§r§dInfo", "§r§7Bring up the guidebook", (p, e) -> {
					p.closeInventory();
					p.openBook(Guidebook.getCopmleteBook());
				});
				
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] XX [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * /dim command
				 */
				if (x.getPermission().equals("pgf.cmd.goto") && x.getValue()) {
					createButton(Material.SPYGLASS, 13, "§r§9Dimensions", "§r§7Go to other worlds!", (p, e) -> {
						p.openInventory(new DimSelect(pd).getInventory());
					});
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] XX [] [] [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.home.*") && x.getValue()) {
					createButton(Material.COMPASS, 20, "§r§eHomes", (p, e) -> {
						p.openInventory(new HomeMenu(pd).getInventory());
					});
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] XX [] [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.tp.tpa") && x.getValue()) {
					
					if (Bukkit.getOnlinePlayers().size() == 1) {
						createButton(Material.GRAY_CONCRETE, 21, "§r§5Tpa", "§r§cNo players online.", (p, e) -> {
							pd.playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
						});
					} else {
						createButton(Material.ENDER_PEARL, 21, "§r§5Tpa", "§r§7Teleport to another player!", (p, e) -> {
							p.openInventory(new TpaList(pd).getInventory());
						});
					}
					
					
					
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] XX [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("teams.friend.*") && x.getValue() && TEAMINIT) {
					createButton(Material.TOTEM_OF_UNDYING, 23, "§r§6Friends", (p, e) -> {
						p.openInventory(new FriendsList(pd).getInventory());
					});
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] XX [] []
				 * home menu
				 */
				if (x.getPermission().equals("bukkit.command.list") && x.getValue() && TEAMINIT) {
					createButton(Material.PLAYER_HEAD, 24, "§r§bPlayer List", (p, e) -> {
						 p.openInventory(new PlayerList(pd).getInventory());
					});
				}
				
				// Other buttons -
				
				/* 
				 * [] [] [] [] XX [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * home menu
				 */
				if (pd.getData("Roles") != null && ((List<Role>) pd.getData("Roles")).contains(Role.ADMIN)) {
					createButton(Material.EMERALD, 4, "§r§cAdmin", (p, e) -> {
						 // open up admin
					});
				}
				
				/* 
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] [] [] [] [] []
				 * [] [] [] [] XX [] [] [] []
				 * home menu
				 */
				if (x.getPermission().equals("pgf.cmd.donator.echest") && x.getValue()) {
					createButton(Material.ENDER_CHEST, 22, "§r§3Ender Chest", "§r§9VIP perk!", (p, e) -> {
						p.closeInventory();
						p.performCommand("echest");
					});
				}
				
				createButton(Material.LEVER, 9, "§r§4Requests", (p, e) -> {
					p.openInventory(new RequestList(pd).getInventory());
				});
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
	private static class DiscordConfirm extends InteractableInventory {
		
		public DiscordConfirm(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Unlink Account?");
			
			/*
			 * checks if discord is already linked, and creates buttons corresponding to this information.
			 * sets buttons to the already-coded commands, because there is no difference. 
			 */
			
			createButton(Material.LIME_CONCRETE, 11, "§r§cUnlink", (p, e) -> {
				// p.closeInventory(); // I think it is better if the unlink doesn't close the inventory
				p.performCommand("unlink");
				p.openInventory(new Homepage(pd).getInventory());
			});
			
			// cancel button.
			createButton(Material.RED_CONCRETE, 15, "§r§7Cancel", (p, e) -> {
				p.openInventory(new Homepage(pd).getInventory());
			});
		}
	}
	
	private static class BackConfirm extends InteractableInventory {
		public BackConfirm(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Tp to last location?");
			
			createButton(Material.LIME_CONCRETE, 11, "§r§dTeleport", (p, e) -> {
				p.closeInventory();
				p.performCommand("back");
			});
			
			createButton(Material.RED_CONCRETE, 15, "§r§7Cancel", (p, e) -> {
				p.closeInventory();
				p.openInventory(new Homepage(pd).getInventory());
			});
		}
	}
	
	private static class DimSelect extends PagedInventory {
		
		public DimSelect(PlayerData pd) {
			super(SizeData.SMALL, "§r§5Dimension Select", DimManager.getAllWorlds(false).stream()
					.map( x-> {
						return new Button(Material.ENDER_PEARL, "§r§9" + x.getName(), null, (p, e) -> {
							p.performCommand("goto " + x.getName());
						});
					})
					.collect(Collectors.toList())
			);
			setBackButton(new Homepage(pd));
		}
		
	}
	
	private static class HomeMenu extends InteractableInventory {
		
		public HomeMenu(PlayerData pd) {
			super(SizeData.SMALL, "§r§8Home");
			
			createButton(Material.FEATHER, 0, "§r§7Back", (p, e) -> {
				p.openInventory(new Homepage(pd).getInventory());
			});
			
			createButton(Material.ENDER_PEARL, 11, "§r§dGo to Home", (p, e) -> {
				p.openInventory(new HomeList(pd, "home ").getInventory());
			});
			
			
			if (!(Homes.getHomes(pd.getOfflinePlayer()).size() >= 3)) {
				createButton(Material.OAK_SAPLING, 13, "§r§aSet Home", (p, e) -> {
					p.openInventory(new SetConfirm(pd).getInventory());
				});
			}
			
			createButton(Material.FLINT_AND_STEEL, 15, "§r§cDelete Home", (p, e) -> {
				p.openInventory(new DelList(pd).getInventory());
			});
		}
		
		private static class HomeList extends PagedInventory {
			
			public HomeList(PlayerData pd, String dingus) {
				super(SizeData.SMALL, "§r§8Home Select", Homes.getHomes(pd.getPlayer()).keySet().stream()
						.map(x-> {
							return new Button(Material.PAPER, x, null, (p, e) -> {
								p.performCommand(dingus + x);
								p.closeInventory();
							});
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
		private static class SetConfirm extends InteractableInventory {
			public SetConfirm(PlayerData pd) {
				super(SizeData.SMALL, "§r§8Set home here?");
				
				createButton(Material.LIME_CONCRETE, 11, "§r§aSet Home", (p, e) -> {
					pd.setData("tempHomeLocation", pd.getPlayer().getLocation());
					p.closeInventory();
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
				});
				
				createButton(Material.RED_CONCRETE, 15, "§r§7Cancel", (p, e) -> {
					p.openInventory(new HomeMenu(pd).getInventory());
				});
			}
		}
		
		
		private static class DelList extends PagedInventory {
			
			public DelList(PlayerData pd) {
				super(SizeData.SMALL, "§r§8Delete Home", Homes.getHomes(pd.getPlayer()).keySet().stream()
						.map(x-> {
							return new Button(Material.PAPER, "§r§a" + x, null, (p, e) -> {
								p.performCommand("delhome " + x);
								p.openInventory(new DelList(pd).getInventory());
							});
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
						return new Button(Material.PLAYER_HEAD, "§r§a" + x.getName(), null, (p, e) -> {
							p.performCommand("tpa " + x.getName());
							p.openInventory(new Homepage(pd).getInventory());
						});
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
				return new Button(Material.PAPER, "§r" + x.getRankedName(), null, (p, e) -> {
					
					p.openInventory(new FriendOptions(player, x).getInventory());
					
				});
			}).collect(Collectors.toList()));
			
			setBackButton(new Homepage(player));
		}
		
		public static class FriendOptions extends InteractableInventory {

			public FriendOptions(PlayerData player, PlayerData friend) {
				super(SizeData.SMALL, "§r§8Options for " + friend.getRankedName());
				
				createButton(Material.ARROW, 12, "§r§cUnfriend", (p, e) -> {
					Friends.setRelation(player, Relation.NONE, friend, Relation.NONE);
					player.sendMessage("§cYou have Unfriended " + friend.getName() + ".");
					player.playSound(Sound.BLOCK_CALCITE_HIT);
					// player.getPlayer().closeInventory(); // Better if not close
					p.openInventory(new FriendOptions(player, friend).getInventory());
					
				});
				
				Relation r = Friends.getRelation(player, friend);
				
				if (r == Relation.FRIEND) {
					createButton(Material.NETHER_STAR, 14, "§r§eFavorite", (p, e) -> {
						
						Friends.setRelation(player, friend, Relation.FAVORITE);
						player.sendMessage("§r§6" + friend.getName() + " is now a favorite!");
						player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
						// player.getPlayer().closeInventory(); // Better if not close
						p.openInventory(new FriendOptions(player, friend).getInventory());
						
					});
				} else if (r == Relation.FAVORITE) {
					createButton(Material.NETHER_STAR, 14, "§r§6Unfavorite", (p, e) -> {
						
						Friends.setRelation(player, friend, Relation.FRIEND);
						player.sendMessage("§r§c" + friend.getName() + " has Been unfavorited!");
						player.playSound(Sound.BLOCK_CALCITE_HIT);
						// player.getPlayer().closeInventory(); // Better if not close
						p.openInventory(new FriendOptions(player, friend).getInventory());
						
					});
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
						return new Button(Material.PLAYER_HEAD, "§r§a" + x.getName(), (x.getOfflinePlayer().isOnline()) ? "§r§aOnline" : "§r§cOffline", 
						(p, e) -> {
							p.openInventory(new PlayerOptions(pd, x).getInventory());
						}
						);
					})
					.collect(Collectors.toList())
			);
			
			setBackButton(new Homepage(pd));
		}
		
		private static class PlayerOptions extends InteractableInventory {
			
			public PlayerOptions(PlayerData pd, PlayerData player) {
				super(SizeData.SMALL, player.getRankedName());
				
				createButton(Material.FEATHER, 0, "§r§7Back", (p, e) -> {
					p.openInventory(new PlayerList(pd).getInventory());
				});
				
				pd.getPlayer().getEffectivePermissions().forEach(x-> {
					if (x.getPermission().equals("teams.friend.*") && x.getValue() && TEAMINIT) {
						
						Relation r = Friends.getRelation(pd, player);
						if (r == Relation.FRIEND || r == Relation.FAVORITE) {
							createButton(Material.TOTEM_OF_UNDYING, 11, "§r§cUnfriend", (p, e) -> {
								p.openInventory(new UnfriendConfirm(pd, player).getInventory());
							});
							
							if (r == Relation.FAVORITE) {
								createButton(Material.TOTEM_OF_UNDYING, 12, "§r§cUnfavorite", (p, e) -> {
									p.performCommand("unfav " + player.getName());
									// p.closeInventory();
									p.openInventory(new PlayerOptions(pd, player).getInventory());
								});
							} else {
								createButton(Material.TOTEM_OF_UNDYING, 12, "§r§eFavorite", (p, e) -> {
									p.performCommand("fav " + player.getName());
									// p.closeInventory();
									p.openInventory(new PlayerOptions(pd, player).getInventory());
								});
							}
							
						} else {
							createButton(Material.TOTEM_OF_UNDYING, 11, "§r§6Friend", (p, e) -> {
								p.openInventory(new FriendConfirm(pd, player).getInventory());
							});
						}
					}
					
					if (x.getPermission().equals("pgf.cmd.block") && x.getValue()) {
						
						if (Blocked.GET_BLOCKED(pd.getOfflinePlayer()).contains(player.getUniqueId())) {
							createButton(Material.RED_STAINED_GLASS_PANE, 14, "§r§4Unblock", (p, e) -> {
								p.performCommand("unblock " + player.getName());
								// p.closeInventory();
								p.openInventory(new PlayerOptions(pd, player).getInventory());
							});
						} else {
							createButton(Material.WHITE_STAINED_GLASS_PANE, 14, "§r§4Block", (p, e) -> {
								p.performCommand("block " + player.getName());
								// p.closeInventory();
								p.openInventory(new PlayerOptions(pd, player).getInventory());
							});
						}
					}
					
					createButton(Material.RED_BANNER, 15, "§r§4Report", "§r§7If someone is bullying or\ngriefing you, use this!" + "\nWIP");
				});
			}
			
			private static class FriendConfirm extends InteractableInventory {
				
				public FriendConfirm(PlayerData pd, PlayerData player) {
					super(SizeData.SMALL, "§r§6Friend " + player.getName() + "?");
					
					createButton(Material.LIME_CONCRETE, 11, "§r§aSend Request", (p, e) -> {
						// p.closeInventory();
						p.performCommand("friendrequest " + player.getName());
						p.openInventory(new PlayerOptions(pd, player).getInventory());
					});
					
					createButton(Material.RED_CONCRETE, 15, "§r§7Cancel", (p, e) -> {
						p.openInventory(new PlayerOptions(pd, player).getInventory());
					});
				}
			}
			
			private static class UnfriendConfirm extends InteractableInventory {
				
				public UnfriendConfirm(PlayerData pd, PlayerData player) {
					super(SizeData.SMALL, "§r§cUnfriend " + player.getName() + "?");
					
					createButton(Material.LIME_CONCRETE, 11, "§r§cUnfriend", (p, e) -> {
						// p.closeInventory();
						p.performCommand("unfriend " + player.getName());
						p.openInventory(new PlayerOptions(pd, player).getInventory());
					});
					
					createButton(Material.RED_CONCRETE, 15, "§r§7Cancel", (p, e) -> {
						p.openInventory(new PlayerOptions(pd, player).getInventory());
					});
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
						return new Button(Material.ARROW, x.getParent().getName(), null, (p, e) -> {
							if (x.expireNow(Reason.Accept) != false) {
								x.act();
							} else {
								pd.playSound(Sound.BLOCK_NOTE_BLOCK_BASS);
								p.openInventory(new RequestList(pd).getInventory());
							}
						});
					})
					.collect(Collectors.toList())
			);
			setBackButton(new Homepage(pd));
		}
	}
	
	/*
	@Deprecated
	private final Comparator<PlayerData> playerSorter = (o1, o2) -> {
		if (o1.isOnline() && o2.isOnline()) { // both online
			
			//Relation r1 = Friends.getRelation(pd, o1);
			//Relation r2 = Friends.getRelation(pd, o2);
			
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
	};
	*/
	
}
