package org.bukkitutils.command.v1_14_3_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;
import org.bukkitutils.command.v1_14_3_V1.Argument;
import org.bukkitutils.command.v1_14_3_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents a scoreboard team argument for a Mojang Brigadier command. */
public class ScoreboardTeamArgument extends Argument<Team> {
	
	/** Represents a scoreboard team argument for a Mojang Brigadier command. */
	public ScoreboardTeamArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentScoreboardTeam"));
	}
	
	@Override
	protected Team parse(String key, CommandContext<?> context) throws Exception {
		Constructor<?> constructor = Reflector.getConstructor(Reflector.getObcClass("scoreboard.CraftTeam"), Reflector.getObcClass("scoreboard.CraftScoreboard"), Reflector.getNmsClass("ScoreboardTeam"));
		Object scoreboardTeam = Reflector.getMethod(Reflector.getNmsClass("ArgumentScoreboardTeam"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Team) constructor.newInstance(Bukkit.getScoreboardManager().getMainScoreboard(), scoreboardTeam);
	}
	
}