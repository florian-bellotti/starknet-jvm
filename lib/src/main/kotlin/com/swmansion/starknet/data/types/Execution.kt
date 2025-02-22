@file:JvmName("Execution")

package com.swmansion.starknet.data.types

import com.swmansion.starknet.data.selectorFromName
import com.swmansion.starknet.data.types.transactions.InvokeTransaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias Calldata = List<Felt>
typealias Signature = List<Felt>

@Serializable
data class Call(
    @SerialName("contract_address")
    val contractAddress: Felt,

    @SerialName("entry_point_selector")
    val entrypoint: Felt,

    @SerialName("calldata")
    val calldata: Calldata,
) {
    constructor(contractAddress: Felt, entrypoint: String, calldata: Calldata) : this(
        contractAddress,
        selectorFromName(entrypoint),
        calldata,
    )

    constructor(contractAddress: Felt, entrypoint: Felt) : this(
        contractAddress,
        entrypoint,
        emptyList(),
    )

    constructor(contractAddress: Felt, entrypoint: String) : this(
        contractAddress,
        entrypoint,
        emptyList(),
    )
}

data class ExecutionParams(
    val nonce: Felt,
    val maxFee: Felt,
)

@Serializable
data class CallContractPayload(
    @SerialName("request")
    val request: Call,

    @SerialName("block_id")
    val blockId: BlockId,
)

@Serializable
data class GetStorageAtPayload(
    @SerialName("contract_address")
    val contractAddress: Felt,

    @SerialName("key")
    val key: Felt,

    @SerialName("block_id")
    val blockId: BlockId,
)

@Serializable
data class GetTransactionByHashPayload(
    @SerialName("transaction_hash")
    val transactionHash: Felt,
)

@Serializable
data class GetTransactionReceiptPayload(
    @SerialName("transaction_hash")
    val transactionHash: Felt,
)

@Serializable
data class EstimateFeePayload(
    @SerialName("request")
    val request: InvokeTransaction,

    @SerialName("block_id")
    val blockId: BlockId,
)

@Serializable
data class GetBlockTransactionCountPayload(
    @SerialName("block_id")
    val blockId: BlockId,
)

@JvmSynthetic
internal fun callsToExecuteCalldata(calls: List<Call>): List<Felt> {
    val wholeCalldata = mutableListOf<Felt>()
    val callArray = mutableListOf<Felt>()
    for (call in calls) {
        callArray.add(call.contractAddress) // to
        callArray.add(call.entrypoint) // selector
        callArray.add(Felt(wholeCalldata.size)) // offset
        callArray.add(Felt(call.calldata.size)) // len

        wholeCalldata.addAll(call.calldata)
    }

    return buildList {
        add(Felt(calls.size))
        addAll(callArray)
        add(Felt(wholeCalldata.size))
        addAll(wholeCalldata)
    }
}
