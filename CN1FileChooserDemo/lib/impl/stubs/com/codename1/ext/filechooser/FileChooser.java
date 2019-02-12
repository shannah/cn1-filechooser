package com.codename1.ext.filechooser;


/**
 * 
 *  @author shannah
 */
public class FileChooser {

	public FileChooser() {
	}

	public static String[] getMimeTypes() {
	}

	public static void addMimeType(String extension, String mime) {
	}

	/**
	 *  Sets a flag that causes the simulator to open files in place (i.e. without copying the file).
	 */
	public static void setOpenFilesInPlace(boolean inPlace) {
	}

	/**
	 *  If true, then the simulator will open files in place rather than copying files upon opening.
	 */
	public static boolean isOpenFilesInPlace() {
	}

	public static boolean isAvailable() {
	}

	public static void showOpenDialog(String accept, com.codename1.ui.events.ActionListener onComplete) {
	}

	public static void showOpenDialog(boolean multi, String accept, com.codename1.ui.events.ActionListener onComplete) {
	}
}
