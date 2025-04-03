import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException

object InternalNotesManager {

    private const val TAG = "InternalNotesManager"
    private const val NOTES_FOLDER_NAME = "notes" // Name of the subfolder

    /**
     * Gets a File object representing the 'notes' subdirectory within internal storage.
     * Creates the directory if it doesn't exist.
     *
     * @param context The application context.
     * @return The File object for the 'notes' directory, or null if creation failed.
     */
    fun getOrCreateNotesDirectory(context: Context): File? {
        // Get the app's private files directory: /data/data/your.package.name/files
        val internalFilesDir: File = context.filesDir

        // Create a File object representing the desired subdirectory path
        val notesDir = File(internalFilesDir, NOTES_FOLDER_NAME)

        // Check if it exists, if not, try to create it (and parent dirs if needed)
        if (!notesDir.exists()) {
            Log.i(TAG, "Notes directory does not exist. Attempting to create: ${notesDir.absolutePath}")
            val created = notesDir.mkdirs() // Use mkdirs() to create parent dirs if necessary
            if (!created) {
                Log.e(TAG, "Failed to create notes directory: ${notesDir.absolutePath}")
                return null // Return null to indicate failure
            } else {
                Log.i(TAG, "Successfully created notes directory: ${notesDir.absolutePath}")
            }
        } else {
            if (!notesDir.isDirectory) {
                Log.e(TAG, "Path exists but is not a directory: ${notesDir.absolutePath}")
                return null // Path exists but it's a file, not a directory
            }
            Log.d(TAG, "Notes directory already exists: ${notesDir.absolutePath}")
        }

        return notesDir
    }

    /**
     * Lists the files and directories directly inside the 'notes' internal subdirectory.
     *
     * @param context The application context.
     * @return A List of File Name representing the contents, or an empty list if
     *         the directory doesn't exist, isn't a directory, is empty, or an error occurred.
     */
    fun listFilesInNotesDirectory(context: Context): List<String> {
        val notesDir = getOrCreateNotesDirectory(context)

        if (notesDir == null) {
            Log.e(TAG, "Cannot list files because notes directory is not accessible.")
            return emptyList() // Return empty list if directory couldn't be obtained/created
        }

        // Check again (though getOrCreateNotesDirectory should handle most cases)
        if (!notesDir.exists() || !notesDir.isDirectory) {
            Log.w(TAG, "Notes directory doesn't exist or isn't a directory when trying to list files: ${notesDir.absolutePath}")
            return emptyList()
        }

        // listFiles() can return null if an I/O error occurs or if it's not a directory
        val filesArray: Array<File>? = notesDir.listFiles()

        return if (filesArray != null) {
            Log.i(TAG, "Found ${filesArray.size} items in ${notesDir.name}")
            //return only names of files not the full path
            filesArray.toList().map { file ->
                Log.d(TAG, "File: ${file.name}")
                file.name // Return the File object itself
            }
        } else {
            Log.e(TAG, "Failed to list files in ${notesDir.absolutePath}. listFiles() returned null.")
            emptyList() // Return empty list on error
        }
    }

   /**
     * Deletes a file or directory in the 'notes' internal subdirectory.
     *
     * @param context The application context.
     * @param fileName The name of the file or directory to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    fun deleteFileOrDirectory(context: Context, fileName: String): Boolean {
        val notesDir = getOrCreateNotesDirectory(context) ?: return false
        val fileToDelete = File(notesDir, fileName)

        return try {
            if (fileToDelete.exists()) {
                if (fileToDelete.isDirectory) {
                    // Recursively delete the directory and its contents
                    fileToDelete.deleteRecursively()
                } else {
                    // Delete the file
                    fileToDelete.delete()
                }
            } else {
                Log.w(TAG, "File or directory does not exist: ${fileToDelete.absolutePath}")
                false
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error deleting file or directory: ${e.message}")
            false
        }
    }
    /* Create a new folder in the 'notes' internal subdirectory.
     *
     * @param context The application context.
     * @param folderName The name of the folder to create.
     * @return True if the folder was created successfully, false otherwise.
     */
    fun createFolder(context: Context, folderName: String): Boolean {
        val notesDir = getOrCreateNotesDirectory(context) ?: return false
        val newFolder = File(notesDir, folderName)

        return if (newFolder.mkdir()) {
            Log.i(TAG, "Successfully created folder: ${newFolder.absolutePath}")
            true
        } else {
            Log.e(TAG, "Failed to create folder: ${newFolder.absolutePath}")
            false
        }


}}