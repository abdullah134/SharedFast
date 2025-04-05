package com.app.sharedfast.ui.folder

import FolderFileAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.sharedfast.databinding.FragmentFolderContentBinding
import java.io.File

class FolderContentFragment : Fragment() {
    private var _binding: FragmentFolderContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentFolder: File
    private lateinit var photoFile: File
    private lateinit var fileAdapter: FolderFileAdapter

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 2
        private const val REQUEST_PICK_VIDEO = 3
        private const val REQUEST_PICK_AUDIO = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderContentBinding.inflate(inflater, container, false)

        currentFolder = File(requireArguments().getString("folderPath")!!)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        // Initialize RecyclerView with layout manager
        binding.recyclerViewFiles.layoutManager = LinearLayoutManager(requireContext())
        fileAdapter = FolderFileAdapter()
        binding.recyclerViewFiles.adapter = fileAdapter

        binding.btnAddImage.setOnClickListener { pickImage() }
        binding.btnAddVideo.setOnClickListener { pickVideo() }
        binding.btnAddAudio.setOnClickListener { pickAudio() }
        binding.btnTakePhoto.setOnClickListener { takePhoto() }

        refreshContent()
    }

    private fun refreshContent() {
        val files = currentFolder.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
        fileAdapter.updateFiles(files)
    }

    private fun pickImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, REQUEST_PICK_IMAGE)
        }
    }

    private fun pickVideo() {
        Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, REQUEST_PICK_VIDEO)
        }
    }

    private fun pickAudio() {
        Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, REQUEST_PICK_AUDIO)
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            photoFile = File(currentFolder, "photo_${System.currentTimeMillis()}.jpg")
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.app.sharedfast.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> refreshContent()
                REQUEST_PICK_IMAGE, REQUEST_PICK_VIDEO, REQUEST_PICK_AUDIO -> {
                    data?.data?.let { uri -> copyFileToFolder(uri) }
                }
            }
        }
    }

    private fun copyFileToFolder(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        val fileName = getFileNameFromUri(uri) ?: "file_${System.currentTimeMillis()}"
        val destFile = File(currentFolder, fileName)

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                destFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(requireContext(), "File added: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to copy file: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

        refreshContent()
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
