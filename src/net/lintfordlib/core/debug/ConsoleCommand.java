package net.lintfordlib.core.debug;

public abstract class ConsoleCommand {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final String Owner;
	public final String Command;
	public final String Description;
	public boolean isActive;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Registers a new command with the {@link DebugConsole}.
	 * 
	 * @param owner
	 *            is the owning class of this command (usually where the doCommand is implemented).
	 * @param commandString
	 *            is the command string as it should be typed into the console
	 * @param description
	 *            is a user-friendly description of the command */
	public ConsoleCommand(final String owner, final String commandString, final String description) {
		Owner = owner;
		Command = commandString;
		Description = description;

		isActive = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** The actions to perform if the Command is typed into the console by the user. Returns true if the doCommand was successful, false otherwise. */
	public abstract boolean doCommand();

}
