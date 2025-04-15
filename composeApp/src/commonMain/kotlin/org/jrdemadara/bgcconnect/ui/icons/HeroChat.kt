package org.jrdemadara.bgcconnect.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val HeroChat: ImageVector
	get() {
		if (_ChatBubbleBottomCenterText != null) {
			return _ChatBubbleBottomCenterText!!
		}
		_ChatBubbleBottomCenterText = ImageVector.Builder(
			name = "ChatBubbleBottomCenterText",
			defaultWidth = 24.dp,
			defaultHeight = 24.dp,
			viewportWidth = 24f,
			viewportHeight = 24f
		).apply {
			path(
				fill = null,
				fillAlpha = 1.0f,
				stroke = SolidColor(Color(0xFF0F172A)),
				strokeAlpha = 1.0f,
				strokeLineWidth = 1.5f,
				strokeLineCap = StrokeCap.Round,
				strokeLineJoin = StrokeJoin.Round,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(7.5f, 8.25f)
				horizontalLineTo(16.5f)
				moveTo(7.5f, 11.25f)
				horizontalLineTo(12f)
				moveTo(2.25f, 12.7593f)
				curveTo(2.25f, 14.3604f, 3.3734f, 15.754f, 4.9575f, 15.987f)
				curveTo(6.086f, 16.1529f, 7.2272f, 16.2796f, 8.3798f, 16.3655f)
				curveTo(8.73f, 16.3916f, 9.0502f, 16.5753f, 9.245f, 16.8674f)
				lineTo(12f, 21f)
				lineTo(14.755f, 16.8675f)
				curveTo(14.9498f, 16.5753f, 15.2699f, 16.3917f, 15.6201f, 16.3656f)
				curveTo(16.7727f, 16.2796f, 17.914f, 16.153f, 19.0425f, 15.9871f)
				curveTo(20.6266f, 15.7542f, 21.75f, 14.3606f, 21.75f, 12.7595f)
				verticalLineTo(6.74056f)
				curveTo(21.75f, 5.1395f, 20.6266f, 3.7458f, 19.0425f, 3.5129f)
				curveTo(16.744f, 3.175f, 14.3926f, 3f, 12.0003f, 3f)
				curveTo(9.6078f, 3f, 7.2561f, 3.175f, 4.9575f, 3.513f)
				curveTo(3.3734f, 3.7459f, 2.25f, 5.1396f, 2.25f, 6.7406f)
				verticalLineTo(12.7593f)
				close()
			}
		}.build()
		return _ChatBubbleBottomCenterText!!
	}

private var _ChatBubbleBottomCenterText: ImageVector? = null
