package com.example.googlemapdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googlemapdemo.R
import com.example.googlemapdemo.model.GeometryData
import com.example.googlemapdemo.model.PlaceData
import kotlin.math.min

class SearchAdapter(
    private val onItemClick: (GeometryData, String) -> Unit,
    private val onFillClick: (name: String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.PlaceVH>() {

    private var items = listOf<PlaceData>()

    class PlaceVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvLocationDesc: TextView = itemView.findViewById(R.id.tvLocationDesc)
        val ivFill: ImageView = itemView.findViewById(R.id.ivFill)
    }

    fun updateData(newList: List<PlaceData>, onEmpty: (isEmptyList: Boolean) -> Unit) {
        onEmpty(newList.isEmpty())
        this.items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceVH {
        return PlaceVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_place, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceVH, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener {
            item.geometry?.let { geo -> onItemClick(geo, item.name ?: "") }
        }
        holder.ivFill.setOnClickListener {
            item.name?.let { name -> onFillClick(name) }
        }
        holder.tvLocation.text = item.name
        holder.tvLocationDesc.text = item.formattedAddress?.substringAfter(",")
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
