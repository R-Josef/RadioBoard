package io.github.bananapuncher714.radioboard;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import io.github.bananapuncher714.radioboard.api.MapDisplay;

public class PlayerListener implements Listener {
	private RadioBoard plugin;

	protected PlayerListener( RadioBoard plugin ) {
		this.plugin = plugin;
	}

	@EventHandler
	private void onPlayerJoinEvent( PlayerJoinEvent event ) {
		Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
			@Override
			public void run() {
				plugin.getPacketHandler().registerPlayer( event.getPlayer() );
				updateMapsFor( event.getPlayer() );
			}
		}, 5 );
	}

	@EventHandler
	private void onPlayerQuitEvent( PlayerQuitEvent event ) {
		plugin.getPacketHandler().unregisterPlayer( event.getPlayer().getUniqueId() );
	}

	@EventHandler
	private void onPlayerChangeWorldEvent( PlayerChangedWorldEvent event ) {
		Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
			@Override
			public void run() {
				updateMapsFor( event.getPlayer() );
			}
		}, 5 );
	}

	@EventHandler
	private void onPlayerRespawnEvent( PlayerRespawnEvent event ) {
		Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
			@Override
			public void run() {
				updateMapsFor( event.getPlayer() );
			}
		}, 5 );
	}
	
	private void updateMapsFor( Player player ) {
		RadioObserver observer = new RadioObserver( player.getUniqueId() );
		Set< MapDisplay > displays = new HashSet< MapDisplay >();
		for ( BoardFrame frame : FrameManager.INSTANCE.getBoardFrames() ) {
			if ( frame.getTopLeftCorner().getWorld() == player.getWorld() ) {
				for ( MapDisplay display : FrameManager.INSTANCE.getDisplays() ) {
					if ( display.getMapId() == frame.getId() && display.isObserving( observer ) ) {
						displays.add( display );
					}
				}
			}
		}
		for ( MapDisplay display : displays ) {
			display.update( observer );
		}
	}
}
