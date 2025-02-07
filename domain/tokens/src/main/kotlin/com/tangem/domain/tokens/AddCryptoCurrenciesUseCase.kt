package com.tangem.domain.tokens

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.toNonEmptyListOrNull
import com.tangem.domain.tokens.error.AddCurrencyError
import com.tangem.domain.tokens.model.CryptoCurrency
import com.tangem.domain.tokens.model.Network
import com.tangem.domain.tokens.repository.CurrenciesRepository
import com.tangem.domain.tokens.repository.NetworksRepository
import com.tangem.domain.wallets.models.UserWalletId

/**
 * A use case for adding multiple cryptocurrencies to a user's wallet.
 *
 * This use case interacts with the underlying repositories to both add currencies and refresh
 * network statuses, particularly after the addition of new tokens.
 */
// TODO: Add tests
class AddCryptoCurrenciesUseCase(
    private val currenciesRepository: CurrenciesRepository,
    private val networksRepository: NetworksRepository,
) {

    /**
     * Adds a [currency] to the wallet identified by [userWalletId].
     *
     * After successfully adding a currency, it also refreshes the networks for tokens
     * that are being added and have corresponding coins in the existing currencies list.
     *
     * @param userWalletId The ID of the user's wallet.
     * @param currency Cryptocurrency to add.
     * @return Either an [AddCurrencyError] or [Unit] indicating the success of the operation.
     */
    suspend operator fun invoke(userWalletId: UserWalletId, currency: CryptoCurrency): Either<AddCurrencyError, Unit> {
        return invoke(userWalletId, listOf(currency))
    }

    /**
     * Adds a [cryptoCurrency] token with specific [network] and derivation to the wallet identified by [userWalletId].
     *
     * After successfully adding a currency, it also refreshes the networks for tokens
     * that are being added and have corresponding coins in the existing currencies list.
     *
     * @param userWalletId The ID of the user's wallet.
     * @param cryptoCurrency Token to add.
     * @param network Network where we add
     * @return Either an [AddCurrencyError] or [Unit] indicating the success of the operation.
     */
    suspend operator fun invoke(
        userWalletId: UserWalletId,
        cryptoCurrency: CryptoCurrency.Token,
        network: Network,
    ): Either<AddCurrencyError, Unit> = either {
        val tokenToAdd = currenciesRepository.createTokenCurrency(cryptoCurrency = cryptoCurrency, network = network)
        invoke(userWalletId = userWalletId, currencies = listOf(tokenToAdd))
    }

    /**
     * Adds a list of [currencies] to the wallet identified by [userWalletId].
     *
     * After successfully adding currencies, it also refreshes the networks for tokens
     * that are being added and have corresponding coins in the existing currencies list.
     *
     * @param userWalletId The ID of the user's wallet.
     * @param currencies The list of cryptocurrencies to add.
     * @return Either an [AddCurrencyError] or [Unit] indicating the success of the operation.
     */
    suspend operator fun invoke(
        userWalletId: UserWalletId,
        currencies: List<CryptoCurrency>,
    ): Either<AddCurrencyError, Unit> = either {
        val existingCurrencies = catch({ currenciesRepository.getMultiCurrencyWalletCurrenciesSync(userWalletId) }) {
            raise(AddCurrencyError.DataError(it))
        }
        val currenciesToAdd = currencies
            .filterNot(existingCurrencies::contains)
            .toNonEmptyListOrNull()
            ?: return@either

        catch({ currenciesRepository.addCurrencies(userWalletId, currenciesToAdd) }) {
            raise(AddCurrencyError.DataError(it))
        }

        refreshUpdatedNetworks(userWalletId, currenciesToAdd, existingCurrencies)
    }

    /**
     * Refreshes the network statuses for tokens that have corresponding coins in the
     * [existingCurrencies] list.
     */
    private suspend fun Raise<AddCurrencyError>.refreshUpdatedNetworks(
        userWalletId: UserWalletId,
        currenciesToAdd: List<CryptoCurrency>,
        existingCurrencies: List<CryptoCurrency>,
    ) {
        val networksToUpdate = currenciesToAdd
            .asSequence()
            .filterIsInstance<CryptoCurrency.Token>()
            .filter { hasCoinForToken(existingCurrencies, it) }
            .mapTo(hashSetOf(), CryptoCurrency.Token::network)

        val networkToUpdate = currenciesToAdd.map { it.network }
            .subtract(existingCurrencies.map { it.network }.toSet())

        catch(
            {
                networksRepository.getNetworkStatusesSync(
                    userWalletId = userWalletId,
                    networks = networksToUpdate + networkToUpdate,
                    refresh = true,
                )
            },
        ) {
            raise(AddCurrencyError.DataError(it))
        }
    }

    /**
     * Determines if the [existingCurrencies] list contains a coin that corresponds
     * to the given [token].
     */
    private fun hasCoinForToken(existingCurrencies: List<CryptoCurrency>, token: CryptoCurrency.Token): Boolean {
        return existingCurrencies.any { currency ->
            currency is CryptoCurrency.Coin && currency.network == token.network
        }
    }
}
