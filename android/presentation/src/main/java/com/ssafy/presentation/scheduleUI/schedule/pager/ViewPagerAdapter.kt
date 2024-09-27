package com.ssafy.presentation.scheduleUI.schedule.pager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.presentation.databinding.PlanItemBinding

class ViewPagerAdapter(
    private var datas: List<ContentListDto>,
    private val getReport: (ContentListDto, (Report?) -> Unit) -> Unit
) : RecyclerView.Adapter<ViewPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlanItemBinding.inflate(inflater, parent, false)
        return ViewPagerHolder(binding)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        getReport(datas[position]) { report ->
            report?.let { holder.bind(it) }
        }
    }

    fun updateItems(newDatas: List<ContentListDto>) {
        datas = newDatas
        notifyDataSetChanged()
    }
}