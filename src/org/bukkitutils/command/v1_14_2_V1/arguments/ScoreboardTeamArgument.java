package org.bukkitutils.command.v1_14_2_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a scoreboard team argument for a Mojang Brigadier command */
public class ScoreboardTeamArgument extends Argument<Team> {
	
	/** Represents a scoreboard team argument for a Mojang Brigadier command */
	public ScoreboardTeamArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentScoreboardTeam"));
	}
	
	@Override
	public Team getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Constructor<?> constructor = Reflector.getConstructor(Reflector.getObcClass("scoreboard.CraftTeam"), Reflector.getObcClass("scoreboard.CraftScoreboard"), Reflector.getNmsClass("ScoreboardTeam"));
		Object scoreboardTeam = Reflector.getMethod(Reflector.getNmsClass("ArgumentScoreboardTeam"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Team) constructor.newInstance(Bukkit.getScoreboardManager().getMainScoreboard(), scoreboardTeam);
	}
	
}