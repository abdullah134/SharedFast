package com.app.sharedfast.ui.folder

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import com.app.sharedfast.databinding.FragmentFolderContentBinding
import java.io.File
import android.widget.Toast

class FolderContentFragment : Fragment() {
    private var _binding: FragmentFolderContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentFolder: File
    private lateinit var photoFile: File

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
        binding.btnAddImage.setOnClickListener { pickImage() }
        binding.btnAddVideo.setOnClickListener { pickVideo() }
        binding.btnAddAudio.setOnClickListener { pickAudio() }
        binding.btnTakePhoto.setOnClickListener { takePhoto() }
        
        refreshContent()
    }

    private fun refreshContent() {
        // Implement RecyclerView adapter to show folder contents
        val files = currentFolder.listFiles()?.toList() ?: emptyList()
        // Update RecyclerView with files
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
        // Implement file copying logic here
        // Copy the selected file to currentFolder
        refreshContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
