package com.maryang.fastrxjava.ui

import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class GithubReposViewModel {
    private val repository = GithubRepository()

    fun getGithubRepos() : Single<List<GithubRepo>> = repository.getGithubRepos()

    fun getUser(): Maybe<User> = repository.getUser()

    fun updateUser():Completable = repository.updateUser()
}
