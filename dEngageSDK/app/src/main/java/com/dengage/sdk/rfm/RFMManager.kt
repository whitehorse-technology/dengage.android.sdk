package com.dengage.sdk.rfm

import com.dengage.sdk.cache.Prefs
import com.dengage.sdk.rfm.model.RFMGender
import com.dengage.sdk.rfm.model.RFMItem
import com.dengage.sdk.rfm.model.RFMScore
import kotlin.math.sqrt

class RFMManager(
    private val prefs: Prefs
) {

    fun saveRFMScores(scores: MutableList<RFMScore>?) {
        prefs.rfmScores = scores
    }

    fun categoryView(categoryId: String) {
        var rfmScores = prefs.rfmScores
        val foundCategoryScore = rfmScores?.firstOrNull { it.categoryId == categoryId }
        if (foundCategoryScore == null) {
            if (rfmScores == null) {
                rfmScores = mutableListOf()
            }
            rfmScores.add(
                RFMScore(
                    categoryId = categoryId,
                    score = 0.5
                )
            )
        } else {
            increaseRfmScore(foundCategoryScore)
        }
        prefs.rfmScores = rfmScores
    }

    fun <T> sortRFMItems(
        rfmGender: RFMGender,
        rfmItems: MutableList<RFMItem>
    ): MutableList<T> {
        val scoreArray = prefs.rfmScores ?: mutableListOf()

        val notPersonalized = rfmItems.filter { !it.personalized }.toMutableList()
        val sortedNotPersonalized = sortByRfmScores(scoreArray, notPersonalized)

        val personalized = rfmItems.filter { it.personalized }.toMutableList()
        val genderSelecteds = mutableListOf<RFMItem>()
        val removePersonalizedItemIds = mutableListOf<String>()
        personalized.forEach { rfmItem ->
            if (rfmItem.gender == rfmGender || rfmItem.gender == RFMGender.NEUTRAL) {
                genderSelecteds.add(rfmItem)
                removePersonalizedItemIds.add(rfmItem.id)
            }
        }
        removePersonalizedItemIds.forEach { itemId ->
            personalized.removeAll { it.id == itemId }
        }
        val sortedGenderSelecteds = sortByRfmScores(scoreArray, genderSelecteds)

        val sortedPersonalized = sortByRfmScores(scoreArray, personalized)

        val result = mutableListOf<RFMItem>()
        result.addAll(sortedNotPersonalized)
        result.addAll(sortedGenderSelecteds)
        result.addAll(sortedPersonalized)

        return result as MutableList<T>
    }

    private fun sortByRfmScores(scores: MutableList<RFMScore>, rfmItems: MutableList<RFMItem>): MutableList<RFMItem> {
        val items = rfmItems.toMutableList()
        val result = mutableListOf<RFMItem>()

        val removeItemIds = mutableListOf<String>()
        scores.forEach { rfmScore ->
            items.forEach { rfmItem ->
                if (rfmScore.categoryId == rfmItem.categoryId) {
                    result.add(rfmItem)
                    removeItemIds.add(rfmItem.id)
                }
            }
        }

        removeItemIds.forEach { itemId ->
            items.removeAll { it.id == itemId }
        }

        val sortedItems = items.sortedBy { it.sequence }
        result.addAll(sortedItems)
        return result
    }

    private fun increaseRfmScore(rfmScore: RFMScore) {
        rfmScore.score += (sqrt(rfmScore.score) * rfmScore.score) / 4
    }

}