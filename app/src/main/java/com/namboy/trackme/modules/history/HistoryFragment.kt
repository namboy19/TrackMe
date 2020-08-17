package com.namboy.trackme.modules.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namboy.trackme.R
import com.namboy.trackme.base.BaseActivity
import com.namboy.trackme.base.BaseLifecycleFragment
import com.namboy.trackme.modules.record.RecordFragment
import com.namboy.trackme.utils.EndlessScrollView
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseLifecycleFragment<HistoryViewModel>() {

    private var mAdapter: HistoryAdapter? = null
    private lateinit var endlessScrollView: EndlessScrollView

    override val viewModelClass: Class<HistoryViewModel>
        get() = HistoryViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fh_imv_record.setOnClickListener {
            (activity as? BaseActivity)?.backStack?.addFragment(RecordFragment())
        }

        initRecyclerView()

        fh_refresh.setOnRefreshListener {
            endlessScrollView.resetState()
            fh_refresh.isRefreshing = false
            viewModel.loadData()
        }
    }

    fun initRecyclerView() {
        fh_listHistory.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(requireContext())
        fh_listHistory.layoutManager = layoutManager
        endlessScrollView = object : EndlessScrollView(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.loadMore(viewModel.trackSessionList.size)
            }
        }
        fh_listHistory.addOnScrollListener(endlessScrollView)
    }

    override fun observeLiveData() {
        super.observeLiveData()

        viewModel.trackSessionLiveData.observe(this, Observer {
            if (mAdapter == null) {
                mAdapter = HistoryAdapter(it.second)
                fh_listHistory.adapter = mAdapter
            } else {
                if (it.first) {
                    mAdapter?.listTrackSession = it.second
                    mAdapter?.notifyDataSetChanged()
                } else {
                    mAdapter?.listTrackSession?.addAll(it.second)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

}