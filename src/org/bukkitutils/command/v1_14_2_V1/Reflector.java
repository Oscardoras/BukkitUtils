package org.bukkitutils.command.v1_14_2_V1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_14_2_V1.CommandRegister.CommandExecutorType;
import org.bukkitutils.command.v1_14_2_V1.CommandRegister.CommandRunnable;
import org.bukkitutils.command.v1_14_2_V1.arguments.Argument;
import org.bukkitutils.command.v1_14_2_V1.arguments.CustomArgument;
import org.bukkitutils.command.v1_14_2_V1.arguments.GreedyStringArgument;
import org.bukkitutils.command.v1_14_2_V1.arguments.LiteralArgument;
import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.md_5.bungee.api.chat.TranslatableComponent;

/** Contains methods for arguments reflexion */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Reflector {
	private Reflector() {}
	
	
	@SuppressWarnings("unused")
	private static class ClassCache {
		
		private Class<?> clazz;
		private String name;
		
		public ClassCache(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		
		public Class<?> getClazz() {
			return clazz;
		}
		
		public String getName() {
			return name;
		}
		
	}

	//Cache maps
	private static Map<String, Class<?>> nmsClasses;
	private static Map<String, Class<?>> obcClasses;
	private static Map<ClassCache, Constructor> constructors;
	private static Map<ClassCache, Method> methods;
	private static Map<ClassCache, Field> fields;
	
	private static String obcPackageName = null;
	private static String nmsPackageName = null;
	
	private static CommandDispatcher dispatcher;
	
	static {
		try {
			if(Package.getPackage("com.mojang.brigadier") == null) {
				throw new ClassNotFoundException("Brigadier not found, plugin commands are not compatible with this version");
			}
			
			//Setup NMS
			Object server = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
			nmsPackageName = server.getClass().getPackage().getName();
			obcPackageName = Bukkit.getServer().getClass().getPackage().getName();
			
			//Everything from this line will use getNMSClass(), so we initialize our cache here
			nmsClasses = new HashMap<>();
			obcClasses = new HashMap<>();
			constructors = new HashMap<>();
			methods = new HashMap<>();
			fields = new HashMap<>();
			
			dispatcher = (CommandDispatcher) getNmsClass("CommandDispatcher").getDeclaredMethod("a").invoke(getField(getNmsClass("MinecraftServer"), "commandDispatcher").get(server)); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Registration                                                                            //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected static void unregister(String name) {
		try {
			Field children = getField(CommandNode.class, "children");
			Map<String, CommandNode<?>> c = (Map<String, CommandNode<?>>) children.get(dispatcher.getRoot());
			c.remove(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void register(String name, LinkedHashMap<String, Argument<?>> argumentsOriginal, Permission permission, CommandExecutorType executorType, CommandRunnable runnable, String[] aliases) {
		try {
			
			final LinkedHashMap<String, Argument<?>> arguments = (LinkedHashMap<String, Argument<?>>) argumentsOriginal.clone();
			long numGreedyArgs = arguments.values().stream().filter(arg -> arg instanceof GreedyStringArgument).count();
			if(numGreedyArgs >= 1) {
				if(!(arguments.values().toArray(new Argument[arguments.size()])[arguments.size() - 1] instanceof GreedyStringArgument)) {
					throw new IllegalArgumentException("GreedyStringArgument must be declared at the end of a LinkedHashMap");
				}
				if(numGreedyArgs > 1) {
					throw new IllegalArgumentException("GreedyStringArgument must be declared at the end of a LinkedHashMap");
				}
			}
			
			Command command = (context) -> {
				
				CommandParameters parameters = getCommandParameters(context);
				
				if (executorType == CommandExecutorType.PLAYER) {
					if (!(parameters.executor instanceof Player)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.player");
						message.setColor(net.md_5.bungee.api.ChatColor.RED);
						parameters.sender.spigot().sendMessage(message);
						return 0;
					}
				} else if (executorType == CommandExecutorType.ENTITY) {
					if (!(parameters.executor instanceof Entity)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.entity");
						message.setColor(net.md_5.bungee.api.ChatColor.RED);
						parameters.sender.spigot().sendMessage(message);
						return 0;
					}
				}
							
				//Array for arguments for executor
				List<Object> args = new ArrayList<>();
				for(Entry<String, Argument<?>> entry : arguments.entrySet()) {
					try {
						populateArgs(entry, context, parameters.sender, parameters.executor, parameters.location, args);
					} catch (CommandSyntaxException e) {
						throw e;
					} catch (InvocationTargetException e) {
						Throwable ex = e.getCause();
						if (ex instanceof CommandSyntaxException) throw (CommandSyntaxException) ex;
						else ex.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//Run resulting executor
				try {
					return runnable.run(parameters.sender, parameters.executor, parameters.location, args.toArray(new Object[args.size()]));
				} catch (CommandSyntaxException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			};
			
			LiteralCommandNode resultantNode;
			if(arguments.isEmpty()) {
				//Link command name to the executor
		        resultantNode = dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(name, permission).executes(command));
		        
		        //Register aliases
		        for(String str : aliases) {
		        	dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(str, permission).redirect(resultantNode));
		        }
			} else {
				
				//List of keys for reverse iteration
		        ArrayList<String> keys = new ArrayList<>(arguments.keySet());
	
		        //Link the last element to the executor
		        ArgumentBuilder inner;
		        //New scope used here to prevent innerArg accidentally being used below
		        {
			        Argument innerArg = arguments.get(keys.get(keys.size() - 1));
			        if(innerArg instanceof LiteralArgument) {
			        	String str = ((LiteralArgument) innerArg).getLiteral();
			        	inner = getLiteralArgumentBuilder(str, innerArg.getPermission()).executes(command);
			        } else {
	        			inner = getRequiredArgumentBuilder(keys.get(keys.size() - 1), innerArg, innerArg.getPermission(), executorType, arguments).executes(command);
					}
		        }
	
		        //Link everything else up, except the first
		        ArgumentBuilder outer = inner;
		        for(int i = keys.size() - 2; i >= 0; i--) {
		        	Argument outerArg = arguments.get(keys.get(i));
		        	if(outerArg instanceof LiteralArgument) {
		        		String str = ((LiteralArgument) outerArg).getLiteral();
		        		outer = getLiteralArgumentBuilder(str, outerArg.getPermission()).then(outer);
		        	} else {
	        			outer = getRequiredArgumentBuilder(keys.get(i), outerArg, outerArg.getPermission(), executorType, arguments).then(outer);
	        		}
		        }        
		        
		        //Link command name to first argument and register        
		       resultantNode = dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(name, permission).then(outer));
			}
			
			//Register aliases
			for(String str : aliases) dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(str, permission).redirect(resultantNode));
			
			Class<?> craftServer = getObcClass("CraftServer");
			getMethod(craftServer, "setVanillaCommands", boolean.class).invoke(Bukkit.getServer(), false);
			SimpleCommandMap map = (SimpleCommandMap) getMethod(craftServer, "getCommandMap").invoke(Bukkit.getServer());
			map.getCommand(name).setPermission(permission.getName());
			org.bukkit.command.Command cmd = map.getCommand("minecraft:" + name);
			if (cmd != null) cmd.setPermission(permission.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: CommandSender                                                                           //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static class CommandParameters {
		public CommandSender sender;
		public CommandSender executor;
		public Location location;
	}
	
	private static CommandParameters getCommandParameters(CommandContext context) {
		CommandParameters parameters = new CommandParameters();
		
		parameters.sender = getCommandSender(context.getSource());
		parameters.executor = parameters.sender;
		
		try {
			//getMethod(getNMSClass("CommandListenerWrapper"), "f").invoke(cmdCtx.getSource()); -> getMethod(getNMSClass("CommandListenerWrapper"), "getEntity").invoke(cmdCtx.getSource());
			//Both of these return field CommandListenerWrapper.k
			Object proxyEntity = getField(getNmsClass("CommandListenerWrapper"), "k").get(context.getSource());
				
			if(proxyEntity != null) {
				//Force proxyEntity to be a NMS Entity object
				Object bukkitProxyEntity = getMethod(getNmsClass("Entity"), "getBukkitEntity").invoke(getNmsClass("Entity").cast(proxyEntity));
				CommandSender proxy  = (CommandSender) bukkitProxyEntity;
				
				if(!proxy.equals(parameters.sender)) {
					Class proxyClass = getObcClass("command.ProxiedNativeCommandSender");
					//ProxiedNativeCommandSender(CommandListenerWrapper orig, CommandSender caller, CommandSender callee)
					Constructor proxyConstructor = proxyClass.getConstructor(getNmsClass("CommandListenerWrapper"), CommandSender.class, CommandSender.class);
					Object proxyInstance = proxyConstructor.newInstance(context.getSource(), parameters.sender, proxy);
					parameters.sender = ((ProxiedCommandSender) proxyInstance);
					parameters.executor = ((ProxiedCommandSender) proxyInstance).getCallee();
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InstantiationException e) {
			e.printStackTrace();
		}
		
		if(parameters.executor instanceof BlockCommandSender) {
			parameters.location = ((BlockCommandSender) parameters.executor).getBlock().getLocation();
		} else if(parameters.executor instanceof Entity) {
			parameters.location = ((Entity) parameters.executor).getLocation();
		} else {
			parameters.location = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		
		return parameters;
	}
	
	private static CommandSender getCommandSender(Object source) {
		CommandSender sender = null;
		try {
			sender = (CommandSender) getMethod(getNmsClass("CommandListenerWrapper"), "getBukkitSender").invoke(source);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sender;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Argument Builders                                                                       //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//Populates arguments list
	private static void populateArgs(Entry<String, Argument<?>> entry, CommandContext context, CommandSender sender, CommandSender executor, Location location, List<Object> args) throws Exception {
		Argument argument = entry.getValue();
		if (!(argument instanceof LiteralArgument)) {
			Object arg;
			String key = entry.getKey();
			if (argument instanceof CustomArgument) {
				
				CustomArgument customArgument = (CustomArgument) argument;
				String str = customArgument.getArg(key, context, executor, location);
				arg = customArgument.getArg(str, executor, location);
				if (arg == null) {
					TranslatableMessage error = customArgument.getMessage();
					if (error != null) {
						final CommandSender send = sender;
						throw new SimpleCommandExceptionType(new com.mojang.brigadier.Message() {
							public String getString() {
								return error.getMessage(send, str);
							}
						}).create();
					} else throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
				}
				
			} else arg = argument.getArg(key, context, executor, location);
			args.add(arg);
		}
	}
	
	//Gets a LiteralArgumentBuilder for a command name
	private static LiteralArgumentBuilder<?> getLiteralArgumentBuilder(String commandName, Permission permission) {
		return LiteralArgumentBuilder.literal(commandName).requires(source -> {
			return permission == null || getCommandSender(source).hasPermission(permission);
		});
	}
	
	//Gets a RequiredArgumentBuilder for a DynamicSuggestedStringArgument
	private static <T> RequiredArgumentBuilder<?, ?> getRequiredArgumentBuilder(String argumentName, Argument<?> type, Permission permission, CommandExecutorType executorType, final LinkedHashMap<String, Argument<?>> arguments) {
		SuggestionProvider provider = null;
		
		try {
			if (!type.getClass().getMethod("getSuggestions", CommandSender.class, Location.class, Object[].class).getDeclaringClass().equals(Argument.class))
			provider = (context, builder) -> {
				CommandParameters parameters = getCommandParameters(context);
				
				if (executorType == CommandExecutorType.PLAYER) {
					if (!(parameters.executor instanceof Player)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.player");
						message.setColor(net.md_5.bungee.api.ChatColor.RED);
						parameters.sender.spigot().sendMessage(message);
						return builder.buildFuture();
					}
				} else if (executorType == CommandExecutorType.ENTITY) {
					if (!(parameters.executor instanceof Entity)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.entity");
						message.setColor(net.md_5.bungee.api.ChatColor.RED);
						parameters.sender.spigot().sendMessage(message);
						return builder.buildFuture();
					}
				}
				
				//Array for arguments for executor
				List<Object> args = new ArrayList<>();
				for(Entry<String, Argument<?>> entry : arguments.entrySet()) {
					try {
						populateArgs(entry, context, parameters.sender, parameters.executor, parameters.location, args);
					} catch (Exception e) {}
				}
				
				Collection<String> list =  type.getSuggestions(parameters.executor, parameters.location, args.toArray(new Object[args.size()]));
				if (list != null) {
					String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
					for (String suggestion : list) {
						if (suggestion.toLowerCase(Locale.ROOT).startsWith(remaining)) builder.suggest(suggestion);
					}
				}
				return builder.buildFuture();
			};
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		RequiredArgumentBuilder<?, ?> a = ((RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument(argumentName, type.getRawType()).requires(source -> {
			return permission == null || getCommandSender(source).hasPermission(permission);
		}));
		if (provider != null) return a.suggests(provider);
		else return a;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Reflection                                                                              //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Gets a constructor.
	 * @param clazz the class where is the constructor
	 * @param parameterTypes the parameter array
	 * @return the Constructor object for the specified constructor in this class
	 * @throws NoSuchMethodException if a constructor with the specified name is not found
	 * @throws SecurityException
	 */
	public static Constructor getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		ClassCache key = new ClassCache(clazz, null);
		if(constructors.containsKey(key)) return constructors.get(key);
		else {
			Constructor result = null;
			result = clazz.getDeclaredConstructor(parameterTypes);
			result.setAccessible(true);
			constructors.put(key, result);
			return result;
		}
	}
	
	/** Gets a method.
	 * @param clazz the class where is the method
	 * @param name the name of the method
	 * @return the Method object for the specified method in this class
	 * @throws NoSuchMethodException if a method with the specified name is not found
	 * @throws SecurityException
	 */
	public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException, SecurityException {
		ClassCache key = new ClassCache(clazz, name);
		if(methods.containsKey(key)) return methods.get(key);
		else {
			Method result = null;
			result = clazz.getDeclaredMethod(name);
			result.setAccessible(true);
			methods.put(key, result);
			return result;
		}
	}
	
	/** Gets a method.
	 * @param clazz the class where is the method
	 * @param name the name of the method
	 * @param parameterTypes the parameter array
	 * @return the Method object for the specified method in this class
	 * @throws NoSuchMethodException if a method with the specified name is not found
	 * @throws SecurityException
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		ClassCache key = new ClassCache(clazz, name);
		if(methods.containsKey(key)) return methods.get(key);
		else {
			Method result = null;
			result = clazz.getDeclaredMethod(name, parameterTypes);
			result.setAccessible(true);
			methods.put(key, result);
			return result;
		}
	}
	
	/** Gets a field.
	 * @param clazz the class where is the field
	 * @param name the name of the field
	 * @return the Field object for the specified field in this class
	 * @throws NoSuchFieldException if a field with the specified name is not found
	 * @throws SecurityException
	 */
	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		ClassCache key = new ClassCache(clazz, name);
		if(fields.containsKey(key)) return fields.get(key);
		else {
			Field result = null;
			result = clazz.getDeclaredField(name);
			result.setAccessible(true);
			fields.put(key, result);
			return result;
		}
	}
	
	/** Gets a class in the package net.minecraft.server.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static Class<?> getNmsClass(final String className) throws ClassNotFoundException {
		if (nmsClasses.containsKey(className)) return nmsClasses.get(className);
		else {
			Class<?> clazz = Class.forName(nmsPackageName + "." + className);
			nmsClasses.put(className, clazz);
			return clazz;
		}
	}
	
	/** Gets a class in the package org.bukkit.craftbukkit.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static Class<?> getObcClass(final String className) throws ClassNotFoundException {
		if (obcClasses.containsKey(className)) return obcClasses.get(className);
		else {
			Class<?> clazz = Class.forName(obcPackageName + "." + className);
			obcClasses.put(className, clazz);
			return clazz;
		}
	}

	/**
	* Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @return the ArgumentType<?>
	 * @see getNMSArgumentInstance
	 */
	public static ArgumentType<?> getNmsArgumentInstance(String nmsClassName) {
		return getNmsArgumentInstance(nmsClassName, "a");		
	}
	
	/**
	 * Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @param methodName the name of the method
	 * @return the ArgumentType<?>
	 */
	public static ArgumentType<?> getNmsArgumentInstance(String nmsClassName, String methodName) {
		try {
			Class clazz = getNmsClass(nmsClassName);
			Method method;
			try {
				method = getMethod(clazz, methodName);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				try {
					method = getMethod(clazz, "a");
				} catch (NoSuchMethodException | SecurityException e1) {
					return null;
				}
			}
			
			return (ArgumentType<?>) method.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}