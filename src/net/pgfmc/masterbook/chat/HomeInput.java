package net.pgfmc.masterbook.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.pgfmc.core.playerdataAPI.PlayerData;

public class HomeInput implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
		
		// For homes
		if (pd.getData("tempHomeLocation") != null) {
			
			e.setCancelled(true);
			SetHome.setHome(pd.getPlayer(), e.getMessage(), pd.getData("tempHomeLocation"));
			pd.setData("tempHomeLocation", null);
			
			return;
		}
	}
}
