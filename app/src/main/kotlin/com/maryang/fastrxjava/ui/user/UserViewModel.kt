package com.maryang.fastrxjava.ui.user

import android.util.Log
import com.maryang.fastrxjava.base.BaseViewModel
import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.util.applySchedulersExtension
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class UserViewModel : BaseViewModel() {
    private val repository = GithubRepository()

    fun getUserRepos(userName: String) =
            Single.create<List<GithubRepo>> { emitter ->
                repository.userRepos(userName)
                        .subscribe({
                            Completable.merge(
                                    it.map { repo ->
                                        repository.checkStar(repo.owner.userName, repo.name)
                                                .doOnComplete { repo.star = true }
                                                .onErrorComplete()
                                    }

                            ).subscribe {
                                emitter.onSuccess(it)
                            }
                        }, {})
            }
                    .applySchedulersExtension()
}
