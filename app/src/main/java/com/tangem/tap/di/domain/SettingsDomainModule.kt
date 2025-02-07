package com.tangem.tap.di.domain

import com.tangem.domain.balancehiding.DeviceFlipDetector
import com.tangem.domain.balancehiding.GetBalanceHidingSettingsUseCase
import com.tangem.domain.balancehiding.ListenToFlipsUseCase
import com.tangem.domain.balancehiding.UpdateBalanceHidingSettingsUseCase
import com.tangem.domain.balancehiding.repositories.BalanceHidingRepository
import com.tangem.domain.settings.*
import com.tangem.domain.settings.repositories.AppRatingRepository
import com.tangem.domain.settings.repositories.PromoSettingsRepository
import com.tangem.domain.settings.repositories.SettingsRepository
import com.tangem.tap.domain.sdk.TangemSdkManager
import com.tangem.tap.domain.settings.DefaultLegacySettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SettingsDomainModule {

    @Provides
    @Singleton
    fun providesIsReadyToShowRatingUseCase(appRatingRepository: AppRatingRepository): IsReadyToShowRateAppUseCase {
        return IsReadyToShowRateAppUseCase(appRatingRepository = appRatingRepository)
    }

    @Provides
    @Singleton
    fun providesRemindToRateAppLaterUseCase(appRatingRepository: AppRatingRepository): RemindToRateAppLaterUseCase {
        return RemindToRateAppLaterUseCase(appRatingRepository = appRatingRepository)
    }

    @Provides
    @Singleton
    fun providesNeverToSuggestRateAppUseCase(appRatingRepository: AppRatingRepository): NeverToSuggestRateAppUseCase {
        return NeverToSuggestRateAppUseCase(appRatingRepository = appRatingRepository)
    }

    @Provides
    @Singleton
    fun providesSetWalletWithFundsFoundUseCase(
        appRatingRepository: AppRatingRepository,
    ): SetWalletWithFundsFoundUseCase {
        return SetWalletWithFundsFoundUseCase(appRatingRepository = appRatingRepository)
    }

    @Provides
    @Singleton
    fun providesShouldShowSaveWalletScreenUseCase(
        settingsRepository: SettingsRepository,
    ): ShouldShowSaveWalletScreenUseCase {
        return ShouldShowSaveWalletScreenUseCase(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun providesCanUseBiometryUseCase(tangemSdkManager: TangemSdkManager): CanUseBiometryUseCase {
        return CanUseBiometryUseCase(
            legacySettingsRepository = DefaultLegacySettingsRepository(tangemSdkManager = tangemSdkManager),
        )
    }

    @Provides
    @Singleton
    fun providesGetBalanceHidingSettingsUseCase(
        balanceHidingRepository: BalanceHidingRepository,
    ): GetBalanceHidingSettingsUseCase {
        return GetBalanceHidingSettingsUseCase(
            balanceHidingRepository = balanceHidingRepository,
        )
    }

    @Provides
    @Singleton
    fun providesListenUseCase(
        flipDetector: DeviceFlipDetector,
        balanceHidingRepository: BalanceHidingRepository,
    ): ListenToFlipsUseCase {
        return ListenToFlipsUseCase(
            flipDetector = flipDetector,
            balanceHidingRepository = balanceHidingRepository,
        )
    }

    @Provides
    @Singleton
    fun provideUpdateHideBalancesSettingsUseCase(
        balanceHidingRepository: BalanceHidingRepository,
    ): UpdateBalanceHidingSettingsUseCase {
        return UpdateBalanceHidingSettingsUseCase(balanceHidingRepository)
    }

    @Provides
    @Singleton
    fun provideSetWalletsScrollPreviewIsShown(settingsRepository: SettingsRepository): NeverToShowWalletsScrollPreview {
        return NeverToShowWalletsScrollPreview(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideIsWalletsScrollPreviewEnabled(settingsRepository: SettingsRepository): IsWalletsScrollPreviewEnabled {
        return IsWalletsScrollPreviewEnabled(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideShouldShowSwapPromoWalletUseCase(
        promoSettingsRepository: PromoSettingsRepository,
    ): ShouldShowSwapPromoWalletUseCase {
        return ShouldShowSwapPromoWalletUseCase(promoSettingsRepository)
    }

    @Provides
    @Singleton
    fun provideShouldShowTravalaPromoWalletUseCase(
        promoSettingsRepository: PromoSettingsRepository,
    ): ShouldShowTravalaPromoWalletUseCase {
        return ShouldShowTravalaPromoWalletUseCase(promoSettingsRepository)
    }

    @Provides
    @Singleton
    fun provideShouldShowSwapPromoTokenUseCase(
        promoSettingsRepository: PromoSettingsRepository,
    ): ShouldShowSwapPromoTokenUseCase {
        return ShouldShowSwapPromoTokenUseCase(promoSettingsRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteDeprecatedLogsUseCase(settingsRepository: SettingsRepository): DeleteDeprecatedLogsUseCase {
        return DeleteDeprecatedLogsUseCase(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideIsSendTapHelpPreviewEnabledUseCase(
        settingsRepository: SettingsRepository,
    ): IsSendTapHelpEnabledUseCase {
        return IsSendTapHelpEnabledUseCase(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideNeverShowTapHelpUseCase(settingsRepository: SettingsRepository): NeverShowTapHelpUseCase {
        return NeverShowTapHelpUseCase(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideSetSaveWalletScreenShownUseCase(
        settingsRepository: SettingsRepository,
    ): SetSaveWalletScreenShownUseCase {
        return SetSaveWalletScreenShownUseCase(settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideIncrementAppLaunchCounterUseCase(
        settingsRepository: SettingsRepository,
    ): IncrementAppLaunchCounterUseCase {
        return IncrementAppLaunchCounterUseCase(settingsRepository = settingsRepository)
    }
}
