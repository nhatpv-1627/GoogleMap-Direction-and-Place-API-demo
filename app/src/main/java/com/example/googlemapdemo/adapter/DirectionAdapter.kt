package com.example.googlemapdemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googlemapdemo.Utils.ManeuverEnum
import com.example.googlemapdemo.R
import com.example.googlemapdemo.model.StepData

class DirectionAdapter : RecyclerView.Adapter<DirectionAdapter.DirectionVH>() {

    private var items = listOf<StepData>()

    class DirectionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDirection: ImageView = itemView.findViewById(R.id.ivDirection)
        val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
    }

    fun updateData(newList: List<StepData>, onEmpty: (isEmptyList: Boolean) -> Unit) {
        onEmpty(newList.isEmpty())
        this.items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectionVH {
        return DirectionVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_direction, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DirectionVH, position: Int) {
        holder.ivDirection.setImageResource(
            when (items[position].maneuver) {
                ManeuverEnum.FORK_LEFT.value -> R.drawable.ic_fork_left
                ManeuverEnum.FORK_RIGHT.value -> R.drawable.ic_fork_right
                ManeuverEnum.STRAIGHT.value -> R.drawable.ic_go_straight
                ManeuverEnum.MERGE.value -> R.drawable.ic_merge
                ManeuverEnum.ROUNDABOUT_LEFT.value -> R.drawable.ic_roundabout_left
                ManeuverEnum.ROUNDABOUT_RIGHT.value -> R.drawable.ic_roundabout_right
                ManeuverEnum.TURN_SLIGHT_LEFT.value -> R.drawable.ic_slight_left
                ManeuverEnum.TURN_SLIGHT_RIGHT.value -> R.drawable.ic_slight_right
                ManeuverEnum.TURN_LEFT.value -> R.drawable.ic_turn_left
                ManeuverEnum.TURN_RIGHT.value -> R.drawable.ic_turn_right
                null -> R.drawable.ic_go_straight
                else -> R.drawable.ic_undifine
            }
        )
        holder.tvDistance.text =
            "${items[position].maneuver ?: "straight"} \n ${items[position].distance?.text} \n ${items[position].duration?.text}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
