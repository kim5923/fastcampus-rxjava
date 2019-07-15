package com.maryang.fastrxjava.ui.repos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.base.BaseApplication
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.util.BackpressureSample
import com.maryang.fastrxjava.util.EventBus
import com.maryang.fastrxjava.util.HotObservable
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_github_repos.*
import org.jetbrains.anko.alert


class GithubReposActivity : AppCompatActivity() {

    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.maryang.fastrxjava.R.layout.activity_github_repos)

        HotObservable.logReplaySubject()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { viewModel.searchGithubRepos() }

        BackpressureSample.overBackpressure()

        searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                viewModel.searchGithubRepos(text.toString())
                EventBus.post(1)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        subscribeSearch()

        EventBus.subscribe {
            Log.d(BaseApplication.TAG, "event bus $it")
        }
        
    }

    private fun subscribeSearch() {
        viewModel.searchGithubReposSubject()
            .doOnNext {
                if (it) showLoading()
            }
            .flatMap { viewModel.searchGithubReposObservable() }
            .subscribe(object : DisposableObserver<List<GithubRepo>>() {
                override fun onNext(t: List<GithubRepo>) {
                    hideLoading()
                    adapter.items = t
                }

                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                }
            })
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }
}
