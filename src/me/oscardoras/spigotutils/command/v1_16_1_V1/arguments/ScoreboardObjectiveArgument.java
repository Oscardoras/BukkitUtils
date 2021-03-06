package me.oscardoras.spigotutils.command.v1_16_1_V1.arguments;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;

import com.mojang.brigadier.context.CommandContext;

import me.oscardoras.spigotutils.command.v1_16_1_V1.Argument;
import me.oscardoras.spigotutils.command.v1_16_1_V1.Reflector;

/** Represents a scoreboard objective argument for a Mojang Brigadier command. */
public class ScoreboardObjectiveArgument extends Argument<Objective> {
	
	/** Represents a scoreboard objective argument for a Mojang Brigadier command. */
	public ScoreboardObjectiveArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentScoreboardObjective"));
	}
	
	@Override
	protected Objective parse(String key, CommandContext<?> context) throws Exception {
		Constructor<?> constructor = Reflector.getConstructor(Reflector.getObcClass("scoreboard.CraftObjective"), Reflector.getObcClass("scoreboard.CraftScoreboard"), Reflector.getNmsClass("ScoreboardObjective"));
		Object scoreboardObjective = Reflector.getMethod(Reflector.getNmsClass("ArgumentScoreboardObjective"), "a", CommandContext.class, String.class).invoke(null, context, key);
		return (Objective) constructor.newInstance(Bukkit.getScoreboardManager().getMainScoreboard(), scoreboardObjective);
	}
	
}