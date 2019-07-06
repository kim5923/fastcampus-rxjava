package com.maryang.fastrxjava.ui

import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.util.Operators
import com.maryang.fastrxjava.util.applySchedulersExtension
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GithubReposViewModel {
    private val repository = GithubRepository()

    var searchText = ""

    fun searchGithubRepos(search: String): Single<List<GithubRepo>> {
        searchText = search
        return Single.create<List<GithubRepo>> { emitter ->
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            checkStar(repo.owner.userName, repo.name)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete()
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // compose
        return Single.create<List<GithubRepo>> { emitter ->
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            checkStar(repo.owner.userName, repo.name)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete()
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .compose(Operators.applySchedulers())

        // extension 활용
        return Single.create<List<GithubRepo>> { emitter ->
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            checkStar(repo.owner.userName, repo.name)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete()
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .applySchedulersExtension()

        // applySchedulers 함수를 통해 반복적인 코드 제거
//        return Operators.applySchedulers(Single.create<List<GithubRepo>> { emitter ->
//            repository.searchGithubRepos(searchText)
//                .subscribe({
//                    Completable.merge(
//                        it.map { repo ->
//                            checkStar(repo.owner.userName, repo.name)
//                                .doOnComplete { repo.star = true }
//                                .onErrorComplete()
//                        }
//                    ).subscribe {
//                        emitter.onSuccess(it)
//                    }
//                }, {})
//        })
    }

    private fun checkStar(owner: String, repo: String): Completable =
        repository.checkStar(owner, repo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
