package com.example.gralos.playerResult

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gralos.R


class playerResultAdapter(
    private val results: MutableList<playerResult>
) : RecyclerView.Adapter<playerResultAdapter.resultViewHolder>() {
    class resultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val login = itemView.findViewById<TextView>(R.id.textLogin)
        val score = itemView.findViewById<TextView>(R.id.textScore)


        fun bind(curResult: playerResult){
            login.text = curResult.login
            score.text = curResult.score

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): resultViewHolder {
        return resultViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        )
    }

    fun addResult(result: playerResult) {
        results.add(result)
        notifyItemInserted(results.size - 1)
    }

    override fun onBindViewHolder(holder: resultViewHolder, position: Int) {
        val curResult = results[position]
        holder.bind(curResult)
    }

    override fun getItemCount(): Int {
        return results.size
    }


}
