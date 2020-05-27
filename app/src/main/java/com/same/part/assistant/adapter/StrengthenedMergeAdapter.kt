package com.same.part.assistant.adapter

import com.commonsware.cwac.merge.MergeAdapter
import com.commonsware.cwac.sacklist.SackOfViewsAdapter

class StrengthenedMergeAdapter : MergeAdapter() {

    override fun getViewTypeCount(): Int {
        var total = 0

        for (piece in pieces.rawPieces) {
            if (piece.adapter is SackOfViewsAdapter) {
                total += piece.adapter.viewTypeCount
            }
        }
        return total.coerceAtLeast(1)
    }

    override fun getItemViewType(position: Int): Int {
        var typeOffset = 0
        var result = -1
        var positionSub = position

        for (piece in pieces.rawPieces) {
            if (piece.isActive) {
                val size = piece.adapter.count
                if (positionSub < size) {
                    var itemViewType = -1
                    if (piece.adapter is SackOfViewsAdapter) {
                        itemViewType = piece.adapter.getItemViewType(positionSub)
                    }
                    result = typeOffset + itemViewType
                    break
                }
                positionSub -= size
            }
            var viewTypeCount = 0
            if (piece.adapter is SackOfViewsAdapter) {
                viewTypeCount = piece.adapter.viewTypeCount
            }
            typeOffset += viewTypeCount
        }

        return result
    }
}