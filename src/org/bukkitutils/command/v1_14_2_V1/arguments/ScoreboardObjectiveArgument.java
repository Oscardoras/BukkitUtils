package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a scoreboard objective argument for a Mojang Brigadier command */
public class ScoreboardObjectiveArgument extends Argument<Objective> {
	
	/** Represents a scoreboard objective argument for a Mojang Brigadier command */
	public ScoreboardObjectiveArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentScoreboardObjective"));
	}
	
	@Override
	public Objective getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Constructor<?> constructor = Reflector.getConstructor(Reflector.getObcClass("scoreboard.CraftObjective"), Reflector.getObcClass("scoreboard.CraftScoreboard"), Reflector.getNmsClass("ScoreboardObjective"));
		Object scoreboardObjective = Reflector.getMethod(Reflector.getNmsClass("ArgumentScoreboardObjective"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Objective) constructor.newInstance(Bukkit.getScoreboardManager().getMainScoreboard(), scoreboardObjective);
	}
	
}