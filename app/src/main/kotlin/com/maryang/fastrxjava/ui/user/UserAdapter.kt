package com.maryang.fastrxjava.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.ui.repo.GithubRepoActivity
import kotlinx.android.synthetic.main.item_github_repo.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk21.listeners.onClick

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var items: List<GithubRepo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_github_repo, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(repo: GithubRepo) {
            with(itemView) {
                repoName.text = repo.name
                repoDescription.text = repo.description
                repoStar.imageResource =
                        if (repo.star) R.drawable.baseline_star_24
                        else R.drawable.baseline_star_border_24
                onClick { GithubRepoActivity.start(context, repo)}
                repoStar.onClick { }
            }
        }
    }
}