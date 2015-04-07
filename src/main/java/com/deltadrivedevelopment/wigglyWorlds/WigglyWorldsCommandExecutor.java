package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WigglyWorldsCommandExecutor implements CommandExecutor {

	private WigglyWorlds p;
	@SuppressWarnings("unused")
	private WorldEditPlugin wep = WigglyWorlds.getWep();

	public WigglyWorldsCommandExecutor(WigglyWorlds p, WorldEditPlugin wep) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		final Player player;

		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("Wiggly Worlds commands are only avaible in game");
			return true;
		}
		
		if(args.length == 0){
			sendHelpMessage(player);
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {

			// check arguments
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide a name for the animation!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix() + "Usage: /ww create <name>");
				return true;
			}

			String name = args[1];

			// get location points
			Selection sel = WigglyWorlds.getWep().getSelection(player);

			if (sel == null) {
				player.sendMessage(p.getPrefix()
						+ "You must select a region first!");
				return true;
			}

			Location loc1 = sel.getMinimumPoint();
			Location loc2 = sel.getMaximumPoint();

			Animation temp = Animation.createAnimation(loc1, loc2, name);

			// if the animation created successfully
			if (temp != null) {
				loc1.getBlock().setType(Material.LAPIS_BLOCK);
				loc2.getBlock().setType(Material.LAPIS_BLOCK);
				player.sendMessage(p.getPrefix() + "Animation " + name
						+ ", successfully created");
				player.sendMessage(p.getPrefix() + "Use /ww addframe " + name
						+ " to add a frame to your animation!");
				return true;
			} else { // if the animation creation failed, either by IO or
						// pre-existance of name
				player.sendMessage(p.getPrefix()
						+ "Anim creation failed, is there already an animation named "
						+ name + "?");
				return false;
			}
		}

		if (args[0].equalsIgnoreCase("addFrame")) {

			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww addframe <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);
			if (anim == null) {
				player.sendMessage(p.getPrefix() + "Animation " + name
						+ " not found, did you spell it correctly?");
				return true;
			}

			if (anim.addFrame()) {
				player.sendMessage(p.getPrefix() + "Frame added to animation "
						+ name + "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix()
						+ "Frame failed to save, no changes made");
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("play")) {

			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to play!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww play <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);

			if (anim != null) {
				anim.play(true);
				player.sendMessage(p.getPrefix() + "Starting animation " + name
						+ "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("playandreset")) {
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to play!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww playandreset <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);

			if (anim != null) {
				anim.playAndReset(false);
				player.sendMessage(p.getPrefix() + "Starting animation " + name
						+ "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("reset")) {
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to reset!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww reset <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);

			if (anim != null) {
				anim.reset();
				player.sendMessage(p.getPrefix() + "Resetting animation "
						+ name + "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		}
		
		

		if (args[0].equalsIgnoreCase("delete")) {
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to delete!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww delete <animation name>");
				return true;
			}
			
			final String name = args[1];
			
			Animation anim = Animation.getAnimation(name);
			
			if(anim != null){
				player.sendMessage(p.getPrefix() + "If you are certian you want to delete animation " + anim.getName() + " use /ww confirm " + anim.getName());
				player.sendMessage(p.getPrefix() + "Ability to confirm will expire in 60 seconds.");
				player.setMetadata("delAnim" + name, new FixedMetadataValue(p, null));
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(player.hasMetadata("delAnim" + name)){
							player.removeMetadata("delAnim" + name, p);
							player.sendMessage(p.getPrefix() + "You can no longer confirm the deletion of " + name);
						}
					}
				}.runTaskLater(WigglyWorlds.getP(), 1200);
				
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animation " + name + " not found. Did you spell it correctly?");
				return true;
			}

		}
		
		if(args[0].equalsIgnoreCase("confirm")){
			
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to confirm!");
				player.sendMessage(p.getPrefix() + "Use /ww confirm <name> to confirm the deletion");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww confirm <name>");
				return true;
			}
			
			String name = args[1];
			
			if(player.hasMetadata("delAnim" + name)){				

				Animation anim = Animation.getAnimation(name);
				player.sendMessage(p.getPrefix() + "Deleting animation " + name
						+ "...");

				if (anim != null) {
					
					File[] files = anim.getAnimDir().listFiles();
					if (files != null) {
						for (File file : files) {
							if (file != null) {
								file.delete();
							}
						}
					}
					if (anim.getAnimDir() != null) {
						anim.getAnimDir().delete();
					}
					Animation.animations.remove(anim);
					player.removeMetadata("delAnim" + name, p);
					player.sendMessage(p.getPrefix() + "Animation " + name + " has been deleted!");

					return true;
				} else {
					player.sendMessage(p.getPrefix() + "Animaiton " + name
							+ " not found. Did you spell it correctly?");
					return true;
				}
			} else {
				player.sendMessage(p.getPrefix() + "You cannot confirm the deletion of that animation. Your time may have expired.");
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("list")) {
			if (Animation.animations.isEmpty()) {
				player.sendMessage(p.getPrefix()
						+ "No animaitons, create one with /ww create <name>!");
				return true;
			}
			String result = "";
			for (Animation anim : Animation.animations) {
				result = result + anim.getName() + ", ";
			}
			result = result.substring(0, result.length() - 2);
			player.sendMessage(p.getPrefix() + result);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("playprivate")){
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to play!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww playprivate <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);

			if (anim != null) {
				anim.playprivate(player, false);
				player.sendMessage(p.getPrefix() + "Starting animation " + name
						+ "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		}
		
		if(args[0].equalsIgnoreCase("playprivatereversed")){
			if (args.length < 2) {
				player.sendMessage(p.getPrefix()
						+ "You must provide the name of the animation to play!");
				return true;
			} else if (args.length > 2) {
				player.sendMessage(p.getPrefix() + "Too many arguments!");
				player.sendMessage(p.getPrefix()
						+ "Usage: /ww playprivate <animation name>");
				return true;
			}

			String name = args[1];

			Animation anim = Animation.getAnimation(name);

			if (anim != null) {
				anim.playprivate(player, true);
				player.sendMessage(p.getPrefix() + "Starting animation " + name
						+ "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		}
		player.sendMessage(p.getPrefix() + "Use /ww to bring up the help page");
		return true;
	}

	private void sendHelpMessage(Player player) {
		player.sendMessage(p.getPrefix() + "Commands:");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww: Displays this help message");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww create <name>: Creates a new animaiton with the name <name>");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww addFrame <Animation name>: Adds a new frame to the given animation");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww delFrame <Animation name>: Deletes the last frame added to the given animation");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww lsit: Lists all created animations and their frame lengths");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww play <name>: Plays the given animation");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww playandreset <name>: Plays the given animation and resets it to the first frame when complete");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww playPrivate <name>: Plays the animation, but it is only visible to you");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww reset <name>: Resets the animation stage to the state of the first frame");
		player.sendMessage(ChatColor.DARK_GREEN + "/ww delete <Animation name>: Deletes the given animation (requires confirmation)");
		
	}

	public static Collection<? extends String> getCommands() {
		// TODO Auto-generated method stub
		List<String> commands = Arrays.asList("create", "addframe", "delframe", "list", "play", "playandreset", "playprivate", "reset", "delete", "confirm");
		return commands;
	}
}
