package com.app.sharedfast.ui.home

import InternalNotesManager
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
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var notes = InternalNotesManager.listFilesInNotesDirectory(requireContext())

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = notes.toString()
        }

        // Add FAB click listener
        binding.fabAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        return root
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
                    InternalNotesManager.createFolder(requireContext(),folderName)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}