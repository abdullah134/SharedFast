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
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        
        // Add FAB click listener
        binding.fabAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        return root
    }

    private fun setupRecyclerView() {
        val adapter = FolderAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        
        val notes = InternalNotesManager.listFilesInNotesDirectory(requireContext())
        adapter.updateFolders(notes)
    }

    private fun setupViewToggle() {
        binding.viewToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.toggleGrid.id -> {
                        binding.recyclerView.layoutManager = 
                            androidx.recyclerview.widget.GridLayoutManager(context, 2)
                    }
                    binding.toggleList.id -> {
                        binding.recyclerView.layoutManager = 
                            androidx.recyclerview.widget.LinearLayoutManager(context)
                    }
                }
            }
        }
        // Set initial selection
//        binding.toggleGrid.isChecked = true
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
                // Refresh the RecyclerView
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