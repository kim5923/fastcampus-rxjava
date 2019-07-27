package com.maryang.fastrxjava.ui.issue

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.base.BaseActivity
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.Issue
import com.maryang.fastrxjava.event.DataObserver
import com.maryang.fastrxjava.observer.DefaultSingleObserver
import com.maryang.fastrxjava.util.RxBindingSample
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_issue_create.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk21.listeners.onClick
import org.jetbrains.anko.sdk21.listeners.textChangedListener
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class IssueCreateActivity : BaseActivity() {

    companion object {
        private const val KEY_REPO = "KEY_REPO"

        fun start(context: Context, repo: GithubRepo) {
            context.startActivity(
                context.intentFor<IssueCreateActivity>(
                    KEY_REPO to repo
                )
            )
        }
    }

    private val viewModel: IssueCreateViewModel by lazy {
        IssueCreateViewModel()
    }
    private lateinit var repo: GithubRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_create)
        supportActionBar?.run {
            title = "이슈 생성"
            setDisplayHomeAsUpEnabled(true)
        }
        this.repo = intent.getParcelableExtra(KEY_REPO)
        setEditText()
        setOnClickListener()
    }

    private fun setEditText() {
        Observable.merge(
            titleText.textChanges(), bodyText.textChanges()
        )
            .subscribe {
                enableComplete(it.isNotEmpty() && titleText.length() > 0)
            }
//        titleText.textChanges()
//                .subscribe { text ->
//                    text?.let {
//                        enableComplete(it.isNotEmpty() && bodyText.length() > 0)
//                    }
//
//                }
//        bodyText.textChanges()
//                .subscribe { text ->
//                    text?.let {
//                        enableComplete(it.isNotEmpty() && titleText.length() > 0)
//                    }
//                }

        bodyText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                text?.let {
                    enableComplete(it.isNotEmpty() && titleText.length() > 0)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun enableComplete(enable: Boolean) {
        complete.isEnabled = enable
        complete.backgroundColorResource =
            if (enable) R.color.colorPrimary else R.color.grey_500
    }

    private fun setOnClickListener() {
//        complete.clicks()
        RxBindingSample.click(complete)
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewModel.create(repo, titleText.text.toString(), bodyText.text.toString())
                    .subscribe(object : DefaultSingleObserver<Issue>() {
                        override fun onSuccess(t: Issue) {
                            DataObserver.post(t)
                            toast("이슈를 생성하였습니다")
                            finish()
                        }
                    })
            }
//        complete.onClick {
//            if (isCompleteClicking) return@onClick
//            isCompleteClicking = true
//            Observable.timer(1, TimeUnit.SECONDS)
//                .subscribe { isCompleteClicking = false }
//
//            viewModel.create(repo, titleText.text.toString(), bodyText.text.toString())
//                .subscribe(object : DefaultSingleObserver<Issue>() {
//                    override fun onSuccess(t: Issue) {
//                        DataObserver.post(t)
//                        toast("이슈를 생성하였습니다")
//                        finish()
//                    }
//                })
//        }
    }
}
