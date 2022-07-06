@file:JvmName("Transactions")

package starknet.data.types

import starknet.crypto.StarknetCurve
import starknet.crypto.StarknetCurveSignature
import types.Felt

typealias Calldata = List<Felt>
typealias Signature = List<Felt>

enum class TransactionStatus {
    NOT_RECEIVED, RECEIVED, PENDING, ACCEPTED_ON_L1, ACCEPTED_ON_L2, REJECTED
}

enum class TransactionType(val txPrefix: Felt) {
    DECLARE(Felt.fromHex("0x6465636c617265")), // encodeShortString('declare'),
    DEPLOY(Felt.fromHex("0x6465706c6f79")), // encodeShortString('deploy'),
    INVOKE(Felt.fromHex("0x696e766f6b65")), // encodeShortString('invoke'),
}

enum class StarknetChainId(val value: Felt) {
    MAINNET(Felt.fromHex("0x534e5f4d41494e")), // encodeShortString('SN_MAIN'),
    TESTNET(Felt.fromHex("0x534e5f474f45524c49")), // encodeShortString('SN_GOERLI'),
}

data class Invocation(
    val contractAddress: Felt, val entrypoint: Felt, val calldata: Calldata?, val signature: Signature?
)

data class InvocationDetails(
    val nonce: Felt?, val maxFee: Felt?, val version: Felt?
)

sealed class Transaction {
    abstract val type: TransactionType

    abstract fun getHash(): Felt
}

data class DeclareTransaction(
    val nonce: Felt, val contractClass: CompiledContract, val signerAddress: Felt, val signature: StarknetCurveSignature
) : Transaction() {
    override val type = TransactionType.DECLARE
    override fun getHash(): Felt {
        TODO("Not yet implemented")
    }
}

data class DeployTransaction(
    val contractDefinition: CompiledContract,
    val contractAddressSalt: Felt,
    val constructorCalldata: Calldata,
    val nonce: Felt?
) : Transaction() {
    override val type = TransactionType.DEPLOY

    override fun getHash(): Felt {
        TODO("Not yet implemented")
    }
}

data class InvokeTransaction(
    val contractAddress: Felt,
    val entrypointSelector: Felt,
    val calldata: Calldata,
    val chainId: Felt,
    val nonce: Felt,
    val maxFee: Felt,
    val version: Felt = Felt.ZERO,
    val signature: Signature? = null,
) : Transaction() {
    override val type = TransactionType.INVOKE

    override fun getHash(): Felt = StarknetCurve.pedersenOnElements(
        type.txPrefix,
        version,
        contractAddress,
        entrypointSelector,
        StarknetCurve.pedersenOnElements(calldata),
        maxFee,
        chainId,
    )

}

