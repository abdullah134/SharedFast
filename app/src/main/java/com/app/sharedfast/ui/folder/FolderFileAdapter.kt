import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.sharedfast.databinding.ItemFileBinding
import java.io.File

class FolderFileAdapter(private val files: MutableList<File> = mutableListOf()) :
	RecyclerView.Adapter<FolderFileAdapter.FileViewHolder>() {

	inner class FileViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
		val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return FileViewHolder(binding)
	}

	override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
		val file = files[position]
		holder.binding.fileName.text = file.name

		// Set icon based on file extension
		val iconRes = when {
			file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true) -> android.R.drawable.ic_menu_gallery
			file.name.endsWith(".mp4", true) || file.name.endsWith(".mkv", true) -> android.R.drawable.ic_media_play
			file.name.endsWith(".mp3", true) || file.name.endsWith(".wav", true) -> android.R.drawable.ic_media_ff
			else -> android.R.drawable.ic_menu_help
		}
		holder.binding.iconFileType.setImageResource(iconRes)

		holder.itemView.setOnClickListener {
			try {
				// Open the file with external app
				val uri = FileProvider.getUriForFile(
					holder.itemView.context,
					"com.app.sharedfast.fileprovider",
					file
				)

				val intent = Intent(Intent.ACTION_VIEW).apply {
					setDataAndType(uri, getMimeType(file))
					flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
				}

				// Check if there's an app that can handle this intent
				if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
					holder.itemView.context.startActivity(intent)
				} else {
					Toast.makeText(
						holder.itemView.context,
						"No app found to open this file type",
						Toast.LENGTH_SHORT
					).show()
				}
			} catch (e: Exception) {
				Toast.makeText(
					holder.itemView.context,
					"Error opening file: ${e.message}",
					Toast.LENGTH_SHORT
				).show()
				e.printStackTrace()
			}
		}
	}

	override fun getItemCount(): Int = files.size

	private fun getMimeType(file: File): String {
		return when {
			file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true) -> "image/jpeg"
			file.name.endsWith(".png", true) -> "image/png"
			file.name.endsWith(".mp4", true) -> "video/mp4"
			file.name.endsWith(".mkv", true) -> "video/x-matroska"
			file.name.endsWith(".mp3", true) -> "audio/mpeg"
			file.name.endsWith(".wav", true) -> "audio/wav"
			else -> "*/*"
		}
	}

	fun updateFiles(newFiles: List<File>) {
		files.clear()
		files.addAll(newFiles)
		notifyDataSetChanged()
	}
}