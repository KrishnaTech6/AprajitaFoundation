package com.example.aprajitafoundation.ui.adapter


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.AnimationUtils
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.viewmodel.DataViewModel

class ImageAdapter(
    private val context: Context,
    private var memberItem: List<MemberModel>,
    private val isAdmin: Boolean = false,
    private val viewModel: DataViewModel,
    private val onItemClickListener: (MemberModel) -> Unit,
) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_person)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvDesignation: TextView = view.findViewById(R.id.tv_designation)
        val llEditDelMember :LinearLayout = view.findViewById(R.id.ll_admin_del_edit_member)
        val editMember: ImageView = view.findViewById(R.id.edit_member)
        val delMember: ImageView = view.findViewById(R.id.del_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_item, parent, false)

        return ImageViewHolder(adapterLayout)
    }

    override fun getItemCount() = memberItem.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = memberItem[position]

        if(isAdmin){
            holder.llEditDelMember.visibility = View.VISIBLE
            holder.delMember.setOnClickListener{
                // Create an AlertDialog to ask
                AlertDialog.Builder(context)
                    .setTitle("Delete Team Member")
                    .setMessage("Do you really want to delete the team member?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        //delete from the server
                        viewModel.deleteMember(context, item.id)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            }
            holder.editMember.setOnClickListener{
                val bundle = Bundle().apply {
                    putParcelable(context.getString(R.string.member_parcelable), item)
                }
                val navController = (context as? AppCompatActivity)?.findNavController(R.id.nav_host_fragment_content_admin)
                navController?.navigate(R.id.action_nav_team_member_to_editMemberFragment, bundle)
            }
        }else{
            holder.llEditDelMember.visibility = View.GONE
        }


        // Using Glide for image loading
        Glide.with(context)
            .load(item.image)
            .thumbnail(0.1f)
            .into(holder.ivImage)

        holder.tvName.text = item.name ?: ""
        holder.tvDesignation.text = item.position ?: ""
        AnimationUtils.fadeIn(holder.itemView, 500)

        holder.itemView.setOnClickListener {
            onItemClickListener(item)
        }
    }

    // Method to update the list of members
    fun updateMembers(newMembers: List<MemberModel>) {
        memberItem = newMembers
        notifyDataSetChanged()
    }
}