package com.maryang.fastrxjava.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.base.BaseApplication
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.User
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_github_repos.*


class GithubReposActivity : AppCompatActivity() {

    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { load() }

        load(true)
    }

    private fun load(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()
        // back 버튼을 누르면 종료가 됩니ㅏㄷ.
        // 액티비티에 있는 Observable이 구독하는 Observer는 액티비티의 Context를 참조(view)하기 때문에
        // GC가 수직을 못한다.
        // 액티비티는 종료가 되어야하는 데, Dispose가 되어있지 않은 Observer에 wkqgu dl
        viewModel.getGithubRepos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<GithubRepo>>() {
                override fun onSuccess(t: List<GithubRepo>) {
                    hideLoading()
                    adapter.items = t
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                }
            })
    }

    private fun load2(showLoading: Boolean = false) {
        if (showLoading) {
            showLoading()
        }

        viewModel.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:DisposableMaybeObserver<User>() {
                override fun onSuccess(t: User) {
                    //null이 아닌것만 온다.
                }

                override fun onComplete() {
                    //null이 발생했을
                }

                override fun onError(e: Throwable) {
                }
            })

    }

    private fun load3(showLoading: Boolean = false) {
        viewModel.updateUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:DisposableCompletableObserver(){
                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    private fun load4(showLoading: Boolean = false) {
        // Single.zip: 두 개의 동작을 동시에 처리하고, 동작이 모두 끝나면 subscribe
        // map: element를 변경
        // flatmap:  객체 자체를 변경
        viewModel.getGithubRepos()
            .subscribeOn(Schedulers.io())
            .toMaybe()
            .doOnSuccess {
                //getGithubRepos 종료되면 아래의 로그가 블립니다.
                Log.d(BaseApplication.TAG, "getGitHubRepos: $it")
            }
            .flatMap {
                viewModel.getUser()
            }
            .doOnSuccess {
                //getUser가 종료되면 로그가 불림
                Log.d(BaseApplication.TAG, "getUser: $it")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:DisposableMaybeObserver<User>() {
                override fun onSuccess(t: User) {
                    //null이 아닌것만 온다.
                }

                override fun onComplete() {
                    //null이 발생했을
                }

                override fun onError(e: Throwable) {
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
