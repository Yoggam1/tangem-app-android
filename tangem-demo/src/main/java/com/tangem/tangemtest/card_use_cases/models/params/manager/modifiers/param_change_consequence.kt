package com.tangem.tangemtest.card_use_cases.models.params.manager.modifiers

import com.tangem.common.tlv.TlvTag
import com.tangem.tangemtest.card_use_cases.models.params.manager.IncomingParameter

/**
 * Created by Anton Zhilenkov on 12.03.2020.
 *
 * The ParamsChangeConsequence class family modifies parameters depending on the state
 * of the incoming parameter
 */
interface ParamsChangeConsequence {
    fun affectChanges(changedParameter: IncomingParameter, paramsList: List<IncomingParameter>): List<IncomingParameter>
}

class ExampleConsequenceForCardId : ParamsChangeConsequence {
    override fun affectChanges(changedParameter: IncomingParameter, paramsList: List<IncomingParameter>): List<IncomingParameter> {
        if (changedParameter.tlvTag != TlvTag.CardId) return listOf()

        val affectedList = paramsList.filter { it.tlvTag != changedParameter.tlvTag }
        affectedList.forEach { it.data = changedParameter.data.hashCode() }
        return affectedList
    }
}