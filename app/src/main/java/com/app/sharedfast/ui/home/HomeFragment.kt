package com.app.sharedfast.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.sharedfast.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File
// import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController  // Add this import
import com.app.sharedfast.R  // Add this import

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupViewToggle()

        binding.fabAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        return root
    }

    private fun setupRecyclerView() {
        val adapter = FolderAdapter { folder ->
            val bundle = Bundle().apply {
                putString("folderPath", folder.absolutePath)
            }
            findNavController().navigate(R.id.action_global_to_folder_content, bundle)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)

        val notes = InternalNotesManager.listFilesInNotesDirectory(requireContext())
        adapter.updateFolders(notes)
    }

    private fun setupViewToggle() {
        val toggleGrid = binding.toggleGrid
        val toggleList = binding.toggleList

        toggleGrid.setOnClickListener {
            toggleGrid.isChecked = true
            toggleList.isChecked = false
            binding.recyclerView.layoutManager =
                androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        }

        toggleList.setOnClickListener {
            toggleList.isChecked = true
            toggleGrid.isChecked = false
            binding.recyclerView.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        }

        toggleGrid.isChecked = true
    }

    private fun showCreateFolderDialog() {
        val editText = EditText(context)
        editText.hint = "Folder name"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Folder")
            .setView(editText)
            .setPositiveButton("Create") { dialog, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotEmpty()) {
                    createFolder(folderName)
                } else {
                    Toast.makeText(context, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createFolder(folderName: String) {
        val notesDir = InternalNotesManager.getOrCreateNotesDirectory(requireContext())
        if (notesDir != null) {
            val newFolder = File(notesDir, folderName)
            if (newFolder.mkdir()) {
                Toast.makeText(context, "Folder created successfully", Toast.LENGTH_SHORT).show()
                (binding.recyclerView.adapter as FolderAdapter)
                    .updateFolders(InternalNotesManager.listFilesInNotesDirectory(requireContext()))
            } else {
                Toast.makeText(context, "Failed to create folder", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
