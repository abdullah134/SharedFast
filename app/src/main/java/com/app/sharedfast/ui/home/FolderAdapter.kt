package com.app.sharedfast.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.sharedfast.databinding.ItemFolderBinding
import java.io.File

class FolderAdapter : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private var folders = listOf<File>()

    class FolderViewHolder(val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.binding.folderName.text = folder.name
    }

    override fun getItemCount() = folders.size

    fun updateFolders(newFolders: List<File>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
