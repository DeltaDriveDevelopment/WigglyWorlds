package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public final class WigglyWorlds extends JavaPlugin {

	private static WigglyWorlds p;
	private static String prefix = ChatColor.GREEN + "[" + ChatColor.WHITE + "WigglyWorlds" + ChatColor.GREEN + "]" + ChatColor.DARK_GREEN + " ";
	private static WorldEditPlugin wep;

	/*
	 * Initialization
	 */

	public void onEnable() {
		registerListeners();
		registerCommands();
		loadFiles();
		getDataFolder().mkdir();
		p = this;
		wep = (WorldEditPlugin) Bukkit.getPluginManager()
				.getPlugin("WorldEdit");
		if (wep == null) {
			this.getServer().getLogger()
					.severe("No world edit plugin found, disabling plugin");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		getLogger().info("World edit found, wep is not null");
	}

	private void loadFiles() {
		// TODO Auto-generated method stub
		File file = new File(getDataFolder().getAbsolutePath()
				+ File.separator + "animations.bin");
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		try {
			Animation.animations = SLAPI.load(getDataFolder().getAbsolutePath()
					+ File.separator + "animations.bin");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getServer()
					.getLogger()
					.info(prefix
							+ "Failed to load animations, try a reload/restart.");
		}

	}

	private void registerCommands() {
		// TODO Auto-generated method stub
		getCommand("WigglyWorlds").setExecutor(
				new WigglyWorldsCommandExecutor(this, getWep()));
		getCommand("WigglyWorlds").setTabCompleter(new WigglyWorldsTabCompleter(this));
	}

	private void registerListeners() {
		// TODO Auto-generated method stub

	}

	/*
	 * Shutdown
	 */

	public void onDisable() {
		saveFiles();
	}

	private void saveFiles() {
		// TODO Auto-generated method stub
		if (!(Animation.animations.isEmpty())) {
			
			try {
				SLAPI.save(Animation.animations, getDataFolder()
						.getAbsolutePath() + File.separator + "animations.bin");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getServer()
						.getLogger()
						.severe("Failed to save animations list. Frames are NOT lost. You will need to use /ww force <name> with the same selection to recover animations");
			}
		}

	}

	public static WigglyWorlds getP() {
		return p;
	}

	public String getPrefix() {
		return prefix;
	}

	public static WorldEditPlugin getWep() {
		return wep;
	}

}
