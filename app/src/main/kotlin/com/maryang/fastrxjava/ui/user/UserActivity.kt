package com.maryang.fastrxjava.ui.user

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.base.BaseViewModelActivity
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.DataObserver
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_github_repos.recyclerView
import kotlinx.android.synthetic.main.activity_user.*
import org.jetbrains.anko.intentFor

class UserActivity : BaseViewModelActivity() {
    override val viewModel: UserViewModel
        get() = UserViewModel()

    private val adapter: UserAdapter by lazy {
        UserAdapter()
    }

    companion object {
        private const val KEY_USER = "KEY_USER"

        fun start(context: Context, user: GithubRepo.GithubRepoUser) {
            context.startActivity(
                context.intentFor<UserActivity>(
                    KEY_USER to user
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.maryang.fastrxjava.R.layout.activity_user)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        intent.getParcelableExtra<GithubRepo.GithubRepoUser>(KEY_USER).let {
            supportActionBar?.run {
                title = it.userName
                setDisplayHomeAsUpEnabled(true)
            }
            getUserRepos(it.userName)
        }

        subscribeDataObserver()

    }

    private fun subscribeDataObserver() {
        compositeDisposable += DataObserver.observe()
                .filter { it is GithubRepo }
                .subscribe { repo ->
                    adapter.items.find {
                        it.id == repo.id
                    }?.apply {
                        star = star.not()
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun getUserRepos(userName: String) {
        compositeDisposable += viewModel.getUserRepos(userName)
                .doOnSubscribe {
                    showLoading()
                }
                .subscribe({
                    hideLoading()
                    adapter.items = it
                }, {
                    hideLoading()
                    it.printStackTrace()
                })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

}
