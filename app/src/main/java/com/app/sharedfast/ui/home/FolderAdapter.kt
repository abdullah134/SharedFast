package com.app.sharedfast.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.sharedfast.databinding.ItemFolderBinding
import java.io.File

class FolderAdapter(private val onFolderClick: (File) -> Unit) : 
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders: List<File> = emptyList()

    inner class FolderViewHolder(
        private val binding: ItemFolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(folder: File) {
            binding.folderName.text = folder.name
            binding.root.setOnClickListener { onFolderClick(folder) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(folders[position])
    }

    override fun getItemCount(): Int = folders.size

    fun updateFolders(newFolders: List<File>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
