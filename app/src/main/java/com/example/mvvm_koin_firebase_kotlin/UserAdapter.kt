package com.example.mvvm_koin_firebase_kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(dataList: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserAdapter.DataVHolder>() {
    var dataList=ArrayList<UserModel>()
    init {
        this.dataList=dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataVHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return DataVHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataVHolder, position: Int) {
        holder.nameText.text=dataList[position].userName
        holder.desigText.text=dataList[position].userDesignation
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    inner class DataVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameText = itemView.findViewById(R.id.userNameTextID) as TextView
            val desigText = itemView.findViewById(R.id.userDesigTextID) as TextView
            private val imageView=itemView.findViewById(R.id.clickID) as ImageView
            init {
                imageView.setOnClickListener {
                    (itemView.context as MainActivity).itemClick(it,adapterPosition)
                }
            }

    }
    interface OnItemClickListener{
        fun itemClick(view: View,position: Int)
    }
    fun addUserList(userList:List<UserModel>){
        this.dataList.clear()
        this.dataList.addAll(userList)
        this.notifyDataSetChanged()
    }
}