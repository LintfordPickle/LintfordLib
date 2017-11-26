package net.lintford.library.core.debug;

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
	 * @param pOwner
	 *            is the owning class of this command (usually where the doCommand is implemented).
	 * @param pCommandString
	 *            is the command string as it should be typed into the console
	 * @param pDescription
	 *            is a user-friendly description of the command */
	public ConsoleCommand(final String pOwner, final String pCommandString, final String pDescription) {
		Owner = pOwner;
		Command = pCommandString;
		Description = pDescription;

		isActive = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** The actions to perform if the Command is typed into the console by the user. Returns true if the doCommand was successful, false otherwise. */
	public abstract boolean doCommand();

}
