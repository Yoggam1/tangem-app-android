package com.tangem.features.details.di

import com.tangem.core.featuretoggle.manager.FeatureTogglesManager
import com.tangem.features.details.DefaultDetailsFeatureToggles
import com.tangem.features.details.DetailsEntryPoint
import com.tangem.features.details.DetailsFeatureToggles
import com.tangem.features.details.DetailsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FeatureModule {

    @Provides
    @Singleton
    fun provideFeatureToggles(featureTogglesManager: FeatureTogglesManager): DetailsFeatureToggles {
        return DefaultDetailsFeatureToggles(featureTogglesManager)
    }

    @Provides
    @Singleton
    fun provideEntryPoint(): DetailsEntryPoint {
        return DetailsFragment.Companion
    }
}
