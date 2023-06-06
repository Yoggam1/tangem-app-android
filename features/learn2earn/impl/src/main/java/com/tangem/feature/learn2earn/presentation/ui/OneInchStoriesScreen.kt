package com.tangem.feature.learn2earn.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tangem.core.ui.components.SecondaryButton
import com.tangem.core.ui.components.SpacerH16
import com.tangem.core.ui.components.SpacerH32
import com.tangem.core.ui.res.TangemTheme
import com.tangem.core.ui.res.TangemTypography
import com.tangem.feature.learn2earn.impl.R
import com.tangem.feature.learn2earn.presentation.ui.component.GradientCircle

/**
 * @author Anton Zhilenkov on 01.06.2023.
 */
// TODO: fixme: make function as internal after adding feature interface
@Composable
fun OneInchStoriesScreen(onLearnClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
        )
        StoryDescription(
            modifier = Modifier
                .padding(horizontal = TangemTheme.dimens.size40)
                .fillMaxSize(),
            headerText = stringResource(id = R.string.story_learn_title),
            bodyText = stringResource(id = R.string.story_learn_description),
        )
        Image(
            modifier = Modifier
                .padding(bottom = TangemTheme.dimens.size68)
                .align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.img_1inch_logo_401_378),
            contentDescription = null,
        )
        SecondaryButton(
            modifier = Modifier
                .padding(horizontal = TangemTheme.dimens.size16)
                .padding(bottom = TangemTheme.dimens.size84)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            text = stringResource(id = R.string.story_learn_learn),
            onClick = onLearnClick,
        )
    }
}

@Suppress("MagicNumber")
@Composable
private fun ContentBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        GradientCircle(
            size = 800.dp,
            offsetX = (-220).dp,
            offsetY = (-200).dp,
            startColor = Color(0xCE164594),
            endColor = Color(0x000B173D),
        )
        GradientCircle(
            size = 800.dp,
            offsetX = 200.dp,
            offsetY = 290.dp,
            startColor = Color(0xFF3D0230),
            endColor = Color(0x000B0E17),
        )
    }
}

@Composable
private fun StoryDescription(headerText: String, bodyText: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpacerH32()
        Text(
            text = headerText,
            textAlign = TextAlign.Center,
            style = TangemTheme.typography.h1.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = TangemTheme.colors.text.primary2,
        )
        SpacerH16()
        Text(
            text = bodyText,
            textAlign = TextAlign.Center,
            style = TangemTypography.subtitle1,
            color = TangemTheme.colors.text.tertiary,
        )
    }
}

@Preview
@Composable
private fun OneInchStoriesContentPreview_Light() {
    TangemTheme(
        isDark = false,
    ) {
        OneInchStoriesScreen(
            onLearnClick = {},
        )
    }
}

@Preview
@Composable
private fun OneInchStoriesContentPreview_Dark() {
    TangemTheme(
        isDark = true,
    ) {
        OneInchStoriesScreen(
            onLearnClick = {},
        )
    }
}
