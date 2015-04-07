package com.deltadrivedevelopment.wigglyWorlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WigglyWorldsTabCompleter implements TabCompleter {
	
	WigglyWorlds p;
	
	public WigglyWorldsTabCompleter(WigglyWorlds plugin) {
		// TODO Auto-generated constructor stub
		this.p = plugin;		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String label, String[] args) {
		// TODO Auto-generated method stub

		ArrayList<String> result = new ArrayList<>();

		if (args.length == 0) {
			result.addAll(WigglyWorldsCommandExecutor.getCommands());
		} else if (args.length == 1) {
			result.addAll(WigglyWorldsCommandExecutor.getCommands());

			// Remove all string that don't start the same as what is currently
			// typed, if what is currently typed is not empty
			if (!args[0].equalsIgnoreCase("")) {
				for (String string : result) {
					if (!string.startsWith(args[0])) {
						result.remove(string);
					}
				}
			}

			
		} else if (args.length == 2) {
			
			result.addAll(Animation.getAnimationNames());
			
			if (!args[1].equalsIgnoreCase("")) {
				for (String string : result) {
					if (!string.startsWith(args[1])) {
						result.remove(string);
					}
				}
			}
		} else if (args.length == 3) {
			result.addAll(Arrays.asList("T", "F"));
		}

		return result;
	}

}
