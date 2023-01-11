/* Copyright 2021 Tusky Contributors
 *
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */

package com.keylesspalace.tusky.components.trending

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keylesspalace.tusky.R
import com.keylesspalace.tusky.adapter.StatusBaseViewHolder
import com.keylesspalace.tusky.adapter.TagViewHolder
import com.keylesspalace.tusky.interfaces.LinkListener
import com.keylesspalace.tusky.util.StatusDisplayOptions
import com.keylesspalace.tusky.viewdata.TrendingViewData

class TrendingPagingAdapter(
    private var statusDisplayOptions: StatusDisplayOptions,
    private val trendingListener: LinkListener,
) : ListAdapter<TrendingViewData, RecyclerView.ViewHolder>(TrendingDifferCallback) {

    var mediaPreviewEnabled: Boolean
        get() = statusDisplayOptions.mediaPreviewEnabled
        set(mediaPreviewEnabled) {
            statusDisplayOptions = statusDisplayOptions.copy(
                mediaPreviewEnabled = mediaPreviewEnabled
            )
        }

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TAG -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_trending, viewGroup, false)
                TagViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_trending, viewGroup, false)
                TagViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        bindViewHolder(viewHolder, position, null)
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<*>
    ) {
        bindViewHolder(viewHolder, position, payloads)
    }

    private fun bindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<*>?
    ) {
        val trending = getItem(position)
        if (trending is TrendingViewData.Tag) {
            this.currentList

            val maxTrendingValue = currentList
                .flatMap { trendingViewData ->
                    trendingViewData.asTagOrNull()?.tag?.history ?: emptyList()
                }
                .mapNotNull { it.uses.toIntOrNull() }
                .maxOrNull() ?: 1

            val holder = viewHolder as TagViewHolder
            holder.setup(trending, maxTrendingValue, trendingListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is TrendingViewData.Tag) {
            VIEW_TYPE_TAG
        } else {
            VIEW_TYPE_TAG
        }
    }

    companion object {
        private const val VIEW_TYPE_TAG = 0

        val TrendingDifferCallback = object : DiffUtil.ItemCallback<TrendingViewData>() {
            override fun areItemsTheSame(
                oldItem: TrendingViewData,
                newItem: TrendingViewData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TrendingViewData,
                newItem: TrendingViewData
            ): Boolean {
                return false // Items are different always. It allows to refresh timestamp on every view holder update
            }

            override fun getChangePayload(
                oldItem: TrendingViewData,
                newItem: TrendingViewData
            ): Any? {
                return if (oldItem == newItem) {
                    // If items are equal - update timestamp only
                    listOf(StatusBaseViewHolder.Key.KEY_CREATED)
                } else // If items are different - update the whole view holder
                    null
            }
        }
    }
}
