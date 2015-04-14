package com.deltadrivedevelopment.wigglyWorlds;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;

//TODO Implement playback speed selection
//TODO Add copy function
//TODO Add rotate function

public class WigglyWorldsCommandExecutor implements CommandExecutor {

	private WigglyWorlds p;
	@SuppressWarnings("unused")
	private WorldEditPlugin wep = WigglyWorlds.getWep();

	public WigglyWorldsCommandExecutor(WigglyWorlds p, WorldEditPlugin wep) {
		this.p = p;
	}

	private boolean create(String[] args, Player player) {
		
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

		if (name.equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "You cannnot name an animation \"help\"");
			return true;
		}

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
			return true;
		}
	}

	private boolean addFrame(String[] args, Player player){
		
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww addframe <animation name> [index]");
			return true;
		}

		String name = args[1];

		if (name.equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "/ww AddFrame <Animation name>:");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Adds a frame to the given animation.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Animation name is required.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Index is optional, defaults to the end of the animation. If index is specified, it will add the frame at the given index.");
			return true;
		}

		Animation anim = Animation.getAnimation(name);
		
		if (anim == null) {
			player.sendMessage(p.getPrefix() + "Animation " + name
					+ " not found, did you spell it correctly?");
			return true;
		}

		if (args.length == 2) {
			if (anim.addFrame()) {
				player.sendMessage(p.getPrefix()
						+ "Frame added to animation " + name + "!");
				return true;
			} else {
				player.sendMessage(p.getPrefix()
						+ "Frame failed to save, no changes made");
				return true;
			}
		} else if (args.length == 3) {
			try {
				int index = Integer.parseInt(args[2]);
				if (index > 0 && index <= anim.getFrameCount()) {
					if (anim.addFrameAt(index - 1)) {
						player.sendMessage(p.getPrefix()
								+ "Frame successfully added at index "
								+ index + "!");
						return true;
					} else {
						player.sendMessage(p.getPrefix()
								+ "Something went wrong while adding the frame, animation may now be corrupt.");
						return true;
					}

				} else {
					player.sendMessage(p.getPrefix()
							+ "Index must be between 1 and "
							+ (anim.getFrameCount() + 1)
							+ " (inclusive) for animation " + name);
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(p.getPrefix()
						+ "Please only use a number for the index to add");
				return true;
			}
		}
		return false;
	}

	private boolean delFrame(String[] args, Player player){
		
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww delframe <animation name> [index]");
			return true;
		}

		String name = args[1];
		if (name.equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "/ww DelFrame <Animation name> [index]:");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Attempts to a frame of the animation.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Animation name is required.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Index is optional, defaults to the last frame of the animation. If index is specified, it will delete the frame at the given index.");
			return true;
		}

		Animation anim = Animation.getAnimation(name);
		if (anim == null) {
			player.sendMessage(p.getPrefix() + "Animation " + name
					+ " not found, did you spell it correctly?");
			return true;
		}

		if (anim.getFrameCount() > 0) {
			if (args.length == 2) {
				if (anim.delFrame()) {
					player.sendMessage(p.getPrefix()
							+ "Last frame  deleted from the animation "
							+ name + "!");
					return true;
				} else {
					player.sendMessage(p.getPrefix()
							+ "Frame failed to delete, no changes made");
					player.sendMessage("Frame count: "
							+ anim.getFrameCount());
					return true;
				}
			} else if (args.length == 3) {
				int index;
				try {
					index = Integer.parseInt(args[2]);
					if (index > 0 && index < anim.getFrameCount()) {
						if (anim.delFrameAt(index - 1)) {
							player.sendMessage(p.getPrefix()
									+ "Frame at index " + index
									+ " deleted successfully!");
							return true;
						} else {
							player.sendMessage(p.getPrefix()
									+ "Frame failed to delete successfully. Animation may be broken if index was not the last frame.");
						}

					} else {
						player.sendMessage(p.getPrefix()
								+ "Index must be between 1 and "
								+ (anim.getFrameCount())
								+ " (inclusive) for animation " + name
								+ ".");
						return true;
					}
				} catch (NumberFormatException e) {
					player.sendMessage(p.getPrefix()
							+ "Please only use a number for the index to delete");
					return true;
				}
			}
		} else {
			player.sendMessage(p.getPrefix()
					+ "That animation has no frames, try adding one with /ww addframe "
					+ name);
			return true;
		}
		return false;
	}

	private boolean play(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to play!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww play <animation name> [T, F] or use /ww play help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Play: /ww play <animation name> [T, F]");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Plays the named animation. The animation name is required.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "[T, F] is optional, T will cause the animation to be played in reverse. Defaults to F.");
			return true;
		}
		String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			if (args.length == 2
					|| (args.length == 3 && args[2].equalsIgnoreCase("F"))) {
				anim.play(false);
			} else if (args.length == 3 && args[2].equalsIgnoreCase("T")) {
				anim.play(true);
				name = name + ", reversed";
			} else {
				player.sendMessage("Only \"T\" or \"R\" is accepted after the name.");
				return true;
			}
			player.sendMessage(p.getPrefix() + "Starting animation " + name
					+ "!");
			return true;
		} else {
			player.sendMessage(p.getPrefix() + "Animaiton " + name
					+ " not found. Did you spell it correctly?");
			return true;
		}
	}

	private boolean playAndReset(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to play!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww play <animation name> [T, F] or use /ww play help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Play and Reset: /ww playandreset <animation name> [T, F]");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Plays the named animation and then resets the stage to the first frame. The animation name is required.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "[T, F] is optional, T will cause the animation to be played in reverse. Defaults to F.");
			return true;
		}
		String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			if (args.length == 2
					|| (args.length == 3 && args[2].equalsIgnoreCase("F"))) {
				anim.playAndReset(false);
			} else if (args.length == 3 && args[2].equalsIgnoreCase("T")) {
				anim.playAndReset(true);
				name = name + ", reversed";
			} else {
				player.sendMessage("Only \"T\" or \"R\" is accepted after the name.");
				return true;
			}
			player.sendMessage(p.getPrefix() + "Starting animation " + name
					+ "!");
			return true;
		} else {
			player.sendMessage(p.getPrefix() + "Animaiton " + name
					+ " not found. Did you spell it correctly?");
			return true;
		}
	}

	private boolean playPrivate(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to play!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww playprivate <animation name> [T, F] or use /ww playprivate help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Play Private: /ww playprivate <animation name> [T, F]");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Plays the named animation such that only the person who used the command will see the animation. The animation stage will resync with the server five (5) seonds after the animation is completed. The animation name is required.");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "[T, F] is optional, T will cause the animation to be played in reverse. Defaults to F.");
			return true;
		}
		String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			if (args.length == 2
					|| (args.length == 3 && args[2].equalsIgnoreCase("F"))) {
				anim.playprivate(player, false);
			} else if (args.length == 3 && args[2].equalsIgnoreCase("T")) {
				anim.playprivate(player, true);
				name = name + ", reversed";
			} else {
				player.sendMessage("Only \"T\" or \"R\" is accepted after the name.");
				return true;
			}
			player.sendMessage(p.getPrefix()
					+ "Starting private animation " + name + "!");
			return true;
		} else {
			player.sendMessage(p.getPrefix() + "Animaiton " + name
					+ " not found. Did you spell it correctly?");
			return true;
		}
	}

	private boolean list(String[] args, Player player){
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("help")) {
				player.sendMessage(p.getPrefix() + "List: /ww list");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Displays a list of all created animations, if any have been created.");
				player.sendMessage(ChatColor.DARK_GREEN + "Usage: /ww list");
				return true;
			}
		}

		if (Animation.animations.isEmpty()) {
			player.sendMessage(p.getPrefix()
					+ "No animaitons, create one with /ww create <name>!");
			return true;
		}
		String result = "";
		for (Animation anim : Animation.animations) {
			result = result + anim.getName() + " (" + anim.getFrameCount()
					+ "), ";
		}
		result = result.substring(0, result.length() - 2);
		player.sendMessage(p.getPrefix() + result);
		return true;
	}

	private boolean set(String[] args, Player player){
		
		if (args.length < 3) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation and index to set!");
			return true;
		} else if (args.length > 3) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww set <name> <index> or use /ww set help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Set: /ww set <animation name> <index>");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Sets the animation stage to the given frame. The animation name and index are required.");
			return true;
		}

		String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			int index;
			try {
				index = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(p.getPrefix()
						+ "Please only use numbers to specify the frame");
				return true;
			}

			if (index > 0 && index <= anim.getFrameCount()) {
				player.sendMessage(p.getPrefix() + "Setting animation "
						+ name + " to frame " + index + "!");
				anim.loadFrame(index - 1);
				return true;
			} else {
				player.sendMessage(p.getPrefix()
						+ "Index must be between 1 and "
						+ anim.getFrameCount()
						+ " (inclusive) for animation " + name + "!");
				return true;
			}

		} else {
			player.sendMessage(p.getPrefix()
					+ "Animation not found. Did you spell it correctly?");
			return true;
		}
	}
	
	private boolean reset(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to reset!");
			return true;
		} else if (args.length > 2) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww reset <animation name> or use /ww reset help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Reset: /ww reset <animation name>");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Resets the stage to the first frame. The animation name is required.");
			return true;
		}

		String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			player.sendMessage(p.getPrefix() + "Resetting animation "
					+ name + "!");
			anim.reset();
			return true;
		} else {
			player.sendMessage(p.getPrefix() + "Animaiton " + name
					+ " not found. Did you spell it correctly?");
			return true;
		}
	}

	private boolean delete(String[] args, final Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to delete!");
			return true;
		} else if (args.length > 2) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix()
					+ "Usage: /ww delete <animation name> or use /ww delete help for more info");
			return true;
		}

		if (args[1].equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix()
					+ "Delete: /ww delete <animation name>");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Delete the animation, you must confirm the deletion. The animation name is required.");
			return true;
		}

		final String name = args[1];

		Animation anim = Animation.getAnimation(name);

		if (anim != null) {
			player.sendMessage(p.getPrefix()
					+ "If you are certian you want to delete animation "
					+ anim.getName() + " use /ww confirm " + anim.getName());
			player.sendMessage(p.getPrefix()
					+ "Ability to confirm will expire in 60 seconds.");
			player.setMetadata("delAnim" + name, new FixedMetadataValue(p,
					null));
			new BukkitRunnable() {

				@Override
				public void run() {
					if (player.hasMetadata("delAnim" + name)) {
						player.removeMetadata("delAnim" + name, p);
						player.sendMessage(p.getPrefix()
								+ "You can no longer confirm the deletion of "
								+ name);
					}
				}
			}.runTaskLater(WigglyWorlds.getP(), 1200);

			return true;
		} else {
			player.sendMessage(p.getPrefix() + "Animation " + name
					+ " not found. Did you spell it correctly?");
			return true;
		}

	}
	
	private boolean confirm(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to confirm!");
			player.sendMessage(p.getPrefix()
					+ "Use /ww confirm <name> to confirm the deletion");
			return true;
		} else if (args.length > 2) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix() + "Usage: /ww confirm <name>");
			return true;
		}

		String name = args[1];

		if (player.hasMetadata("delAnim" + name)) {

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
				player.sendMessage(p.getPrefix() + "Animation " + name
						+ " has been deleted!");

				return true;
			} else {
				player.sendMessage(p.getPrefix() + "Animaiton " + name
						+ " not found. Did you spell it correctly?");
				return true;
			}
		} else {
			player.sendMessage(p.getPrefix()
					+ "You cannot confirm the deletion of that animation. Your time may have expired.");
			return true;
		}
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	private boolean frame(String[] args, Player player){
		if (args.length < 2) {
			player.sendMessage(p.getPrefix()
					+ "You must provide the name of the animation to frame!");
			player.sendMessage(p.getPrefix() + "Usage /ww frame <name>");
			return true;
		} else if (args.length > 2) {
			player.sendMessage(p.getPrefix() + "Too many arguments!");
			player.sendMessage(p.getPrefix() + "Usage: /ww frame <name>");
			return true;
		}

		if (player.hasMetadata("frame")) {
			player.sendMessage(p.getPrefix()
					+ "You must clear your current frame before you can create another one");
			return true;
		}

		String name = args[1];

		if (name.equalsIgnoreCase("help")) {
			player.sendMessage(p.getPrefix() + "Frame: /ww frame <name>");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Creates a wool frame around the stage border that only you can see");
			player.sendMessage(ChatColor.DARK_GREEN
					+ "Everything WITHIN the frame will be saved. Clear the frame with /ww clearframe");
			return true;
		}

		Animation anim = Animation.getAnimation(name);

		if (anim == null) {
			player.sendMessage(p.getPrefix()
					+ "Animation not found. Did you spell it correctly?");
			return true;
		}

		Location topLeftFar = anim.getMaximumPoint().unpack();
		Location bottomRightNear = anim.getMinimumPoint().unpack();

		topLeftFar.add(1, 1, 1);
		if (bottomRightNear.getBlockY() > 0) {
			bottomRightNear.add(-1, -1, -1);
		} else {
			bottomRightNear.add(-1, 0, -1);
		}

		Location topRightFar = new Location(topLeftFar.getWorld(),
				bottomRightNear.getBlockX(), topLeftFar.getBlockY(),
				topLeftFar.getBlockZ());
		Location topLeftNear = new Location(topLeftFar.getWorld(),
				topLeftFar.getBlockX(), topLeftFar.getBlockY(),
				bottomRightNear.getBlockZ());
		Location topRightNear = new Location(topLeftFar.getWorld(),
				topRightFar.getBlockX(), topLeftFar.getBlockY(),
				topLeftNear.getBlockZ());

		Location bottomRightFar = new Location(topRightFar.getWorld(),
				topRightFar.getBlockX(), bottomRightNear.getBlockY(),
				topRightFar.getBlockZ());
		Location bottomLeftnear = new Location(topRightFar.getWorld(),
				topLeftNear.getBlockX(), bottomRightNear.getBlockY(),
				topLeftNear.getBlockZ());
		Location bottomLeftFar = new Location(topRightFar.getWorld(),
				topLeftFar.getBlockX(), bottomRightNear.getBlockY(),
				topLeftFar.getBlockZ());

		boolean alternate = false;
		for (int i = topRightFar.getBlockX(); i <= topLeftFar.getBlockX(); i++) {
			Location topFar = new Location(topLeftFar.getWorld(), i,
					topLeftFar.getBlockY(), topRightFar.getBlockZ());
			Location topNear = new Location(topLeftFar.getWorld(), i,
					topLeftNear.getBlockY(), topLeftNear.getBlockZ());
			Location bottomFar = new Location(topFar.getWorld(), i,
					bottomLeftFar.getBlockY(), bottomLeftFar.getBlockZ());
			Location bottomNear = new Location(topFar.getWorld(), i,
					bottomRightNear.getBlockY(),
					bottomRightNear.getBlockZ());

			if (alternate) {
				player.sendBlockChange(topFar, Material.WOOL, (byte) 15);
				player.sendBlockChange(topNear, Material.WOOL, (byte) 15);
				player.sendBlockChange(bottomFar, Material.WOOL, (byte) 15);
				player.sendBlockChange(bottomNear, Material.WOOL, (byte) 15);
				alternate = !alternate;
			} else {
				player.sendBlockChange(topFar, Material.WOOL, (byte) 4);
				player.sendBlockChange(topNear, Material.WOOL, (byte) 4);
				player.sendBlockChange(bottomFar, Material.WOOL, (byte) 4);
				player.sendBlockChange(bottomNear, Material.WOOL, (byte) 4);
				alternate = !alternate;
			}
		}

		for (int i = bottomLeftFar.getBlockY(); i <= topLeftFar.getBlockY(); i++) {
			Location leftFar = new Location(topLeftFar.getWorld(),
					topLeftFar.getBlockX(), i, topLeftFar.getBlockZ());
			Location leftNear = new Location(topLeftFar.getWorld(),
					topLeftNear.getBlockX(), i, topLeftNear.getBlockZ());
			Location rightFar = new Location(leftFar.getWorld(),
					bottomRightFar.getBlockX(), i,
					bottomRightFar.getBlockZ());
			Location rightNear = new Location(leftFar.getWorld(),
					bottomRightNear.getBlockX(), i,
					bottomRightNear.getBlockZ());

			if (alternate) {
				player.sendBlockChange(leftFar, Material.WOOL, (byte) 15);
				player.sendBlockChange(leftNear, Material.WOOL, (byte) 15);
				player.sendBlockChange(rightFar, Material.WOOL, (byte) 15);
				player.sendBlockChange(rightNear, Material.WOOL, (byte) 15);
				alternate = !alternate;
			} else {
				player.sendBlockChange(leftFar, Material.WOOL, (byte) 4);
				player.sendBlockChange(leftNear, Material.WOOL, (byte) 4);
				player.sendBlockChange(rightFar, Material.WOOL, (byte) 4);
				player.sendBlockChange(rightNear, Material.WOOL, (byte) 4);
				alternate = !alternate;
			}
		}

		for (int i = topRightNear.getBlockZ(); i <= topRightFar.getBlockZ(); i++) {
			Location topRight = new Location(topLeftFar.getWorld(),
					topRightFar.getBlockX(), topLeftFar.getBlockY(), i);
			Location topLeft = new Location(topLeftFar.getWorld(),
					topLeftFar.getBlockX(), topLeftNear.getBlockY(), i);
			Location bottomRight = new Location(topRight.getWorld(),
					bottomRightFar.getBlockX(), bottomLeftFar.getBlockY(),
					i);
			Location bottomLeft = new Location(topRight.getWorld(),
					bottomLeftFar.getBlockX(), bottomRightNear.getBlockY(),
					i);

			if (alternate) {
				player.sendBlockChange(topRight, Material.WOOL, (byte) 15);
				player.sendBlockChange(topLeft, Material.WOOL, (byte) 15);
				player.sendBlockChange(bottomRight, Material.WOOL,
						(byte) 15);
				player.sendBlockChange(bottomLeft, Material.WOOL, (byte) 15);
				alternate = !alternate;
			} else {
				player.sendBlockChange(topRight, Material.WOOL, (byte) 4);
				player.sendBlockChange(topLeft, Material.WOOL, (byte) 4);
				player.sendBlockChange(bottomRight, Material.WOOL, (byte) 4);
				player.sendBlockChange(bottomLeft, Material.WOOL, (byte) 4);
				alternate = !alternate;
			}
		}

		player.setMetadata("frame", new FixedMetadataValue(p, name));
		player.sendMessage(p.getPrefix()
				+ "Animation framed, everything within the frame is saved when a new frame is added. Use /ww clearframe to remove the frame");
		return true;
	}

	@SuppressWarnings("unused")
	private boolean clearFrame(String[] args, final Player player){
		
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("help")) {
				player.sendMessage(p.getPrefix()
						+ "Clear Frame: /ww clearframe");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "removes any frames you have created.");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Usage: /ww clearframe");
				return true;
			}
		}

		if (!player.hasMetadata("frame")) {
			player.sendMessage(p.getPrefix()
					+ "You haven't set any frames to be cleared");
			return true;
		}

		String name;
		Animation anim = null;

		for (MetadataValue mdv : player.getMetadata("frame")) {
			if (mdv.getOwningPlugin().equals(p)) {
				anim = Animation.getAnimation(mdv.asString());
			}
		}

		player.removeMetadata("frame", p);

		if (anim == null) {
			player.sendMessage(p.getPrefix()
					+ "The animation you previously created a frame for was not found. You may create other frames now.");
			return true;
		}

		final String framesDirPath = anim.getAnimDirPath();
		final World world = anim.getMaximumPoint().unpack().getWorld();

		player.sendMessage(p.getPrefix() + "Clearing Frame...");

		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				File frame = new File(framesDirPath + File.separator + "-1");
				TerrainManager tm = new TerrainManager(
						WigglyWorlds.getWep(), world);

				LocalSession ls;
				try {
					ls = tm.getLocalSessionWithClipboard(frame);
					CuboidClipboard clipboard = ls.getClipboard();
					int x = clipboard.getWidth();
					int y = clipboard.getHeight();
					int z = clipboard.getLength();
					Vector origin = clipboard.getOrigin();
					for (int x1 = -1; x1 <= x; x1++) {
						for (int y1 = -1; y1 <= y; y1++) {
							for (int z1 = -1; z1 <= z; z1++) {
								Location loc = new Location(world,
										origin.getBlockX() + x1,
										origin.getBlockY() + y1,
										origin.getBlockZ() + z1);
								player.sendBlockChange(loc, loc.getBlock()
										.getType(), loc.getBlock()
										.getData());
							}
						}
					}

				} catch (FilenameException | MaxChangedBlocksException
						| EmptyClipboardException | DataException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.runTaskLater(WigglyWorlds.getP(), 1);
		return true;
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
		
		if(!player.hasPermission("WigglyWorlds.use")){
			player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
			return true;
		}

		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			sendHelpMessage(player);
			return true;
		}

		/*************************************
		 * 
		 * 
		 * CREATE
		 * 
		 * 
		 *************************************/
		if (args[0].equalsIgnoreCase("create")) {
			return create(args, player);
		}

		/**************************************
		 * 
		 * 
		 * ADD FRAME
		 * 
		 * 
		 **************************************/

		if (args[0].equalsIgnoreCase("addFrame")) {
			return addFrame(args, player);			
		}

		/**************************************
		 * 
		 * 
		 * DELETE FRAME
		 * 
		 * 
		 *************************************/

		if (args[0].equalsIgnoreCase("delFrame")) {
			return delFrame(args, player);
		}

		/*************************************
		 * 
		 * 
		 * PLAY
		 * 
		 * 
		 *************************************/

		if (args[0].equalsIgnoreCase("play")) {
			return play(args, player);			
		}

		/*****************************************
		 * 
		 * 
		 * PLAY AND RESET
		 * 
		 * 
		 *****************************************/
		if (args[0].equalsIgnoreCase("playandreset")) {
			return playAndReset(args, player);
		}

		/*************************************
		 * 
		 * 
		 * SET
		 * 
		 * 
		 *************************************/

		if (args[0].equalsIgnoreCase("set")) {
			return set(args, player);
		}

		/*************************************
		 * 
		 * 
		 * RESET
		 * 
		 * 
		 **************************************/
		if (args[0].equalsIgnoreCase("reset")) {
			return reset(args, player);
		}

		/************************************
		 * 
		 * 
		 * DELETE
		 * 
		 * 
		 ************************************/
		if (args[0].equalsIgnoreCase("delete")) {
			return delete(args, player);
		}

		/****************************
		 * 
		 * 
		 * CONFIRM
		 * 
		 * 
		 ****************************/

		if (args[0].equalsIgnoreCase("confirm")) {
			return confirm(args, player);
		}

		/*********************************
		 * 
		 * 
		 * LIST
		 * 
		 * 
		 *********************************/

		if (args[0].equalsIgnoreCase("list")) {
			return list(args, player);
		}

		/***************************
		 * 
		 * 
		 * PLAY PRIVATE
		 * 
		 * 
		 ***************************/

		if (args[0].equalsIgnoreCase("playprivate")) {
			return playPrivate(args, player);
		}

		/*************************************************
		 * 
		 * 
		 * FRAME
		 * 
		 * 
		 ************************************************/

		if (args[0].equalsIgnoreCase("frame")) {
			return frame(args, player);
		}

		/***********************************************
		 * 
		 * 
		 * CLEAR FRAME
		 * 
		 * 
		 ***********************************************/

		if (args[0].equalsIgnoreCase("clearframe")) {
			return clearFrame(args, player);
		}
		
		player.sendMessage(p.getPrefix() + "Use /ww to bring up the help page");
		return true;
	}

	private void sendHelpMessage(Player player) {
		player.sendMessage(p.getPrefix() + "Commands:");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww: Displays this help message");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww help: Displays this help message");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww create <name>: Creates a new animaiton with the name <name>");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww addFrame <Animation name> [index]: Adds a new frame to the given animation");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww delFrame <Animation name> [index]: Deletes the last frame added to the given animation if index is not specified");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww lsit: Lists all created animations and their frame lengths");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww play <name> [T, F]: Plays the given animation");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww playandreset <name> [T, F]: Plays the given animation and resets it to the first frame when complete");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww playPrivate <name> [T, F]: Plays the animation, but it is only visible to you");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww set <name> <index>: Sets the animation stage to the specified index");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww reset <name>: Resets the animation stage to the state of the first frame");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww delete <Animation name>: Deletes the given animation (requires confirmation)");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww frame <Animation name>: Creates a frame around the animation stage. Only you can see it");
		player.sendMessage(ChatColor.DARK_GREEN
				+ "/ww clearFrame: Clears the frame from around an animation stage");
	}

	public static Collection<? extends String> getCommands() {
		List<String> commands = Arrays.asList("create", "addframe", "delframe",
				"list", "play", "playandreset", "playprivate", "reset",
				"delete", "confirm", "frame", "clearFrame");
		return commands;
	}
}
