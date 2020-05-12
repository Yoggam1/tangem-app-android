package com.tangem.devkit.ucase.domain.actions

import com.tangem.common.extensions.hexToBytes
import com.tangem.common.extensions.toByteArray
import com.tangem.crypto.sign
import com.tangem.devkit._arch.structure.Id
import com.tangem.devkit._arch.structure.PayloadHolder
import com.tangem.devkit._arch.structure.abstraction.findItem
import com.tangem.devkit.ucase.domain.paramsManager.ActionCallback
import com.tangem.devkit.ucase.variants.TlvId
import com.tangem.devkit.ucase.variants.personalize.dto.DefaultPersonalizationParams
import ru.dev.gbixahue.eu4d.lib.kotlin.stringOf

class WriteIssuerDataAction : BaseAction() {
    override fun executeMainAction(payload: PayloadHolder, attrs: AttrForAction, callback: ActionCallback) {
        val cardId = attrs.itemList.findItem(TlvId.CardId)?.viewModel?.data ?: return
        val counter = (attrs.itemList.findItem(TlvId.Counter)?.viewModel?.data as? Int) ?: 1

        val issuerDataItem = attrs.itemList.findItem(TlvId.IssuerData) ?: return
        val issuerData = (issuerDataItem.getData() as? String)?.toByteArray() ?: return

        val signedIssuerData = (
                stringOf(cardId).hexToBytes() + issuerData + counter.toByteArray(4))
                .sign(DefaultPersonalizationParams.issuer().dataKeyPair.privateKey)

        attrs.tangemSdk.writeIssuerData(stringOf(cardId), issuerData, signedIssuerData, counter) {
            handleResult(payload, it, null, attrs, callback)
        }
    }

    override fun getActionByTag(payload: PayloadHolder, id: Id, attrs: AttrForAction): ((ActionCallback) -> Unit)? {
        return when (id) {
            TlvId.CardId -> { callback -> ScanAction().executeMainAction(payload, attrs, callback) }
            else -> null
        }
    }
}