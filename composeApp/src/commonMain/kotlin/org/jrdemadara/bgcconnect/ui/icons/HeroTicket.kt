package org.jrdemadara.bgcconnect.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val HeroTicket: ImageVector
	get() {
		if (_Ticket != null) {
			return _Ticket!!
		}
		_Ticket = ImageVector.Builder(
			name = "Ticket",
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
				moveTo(16.5f, 6f)
				verticalLineTo(6.75f)
				moveTo(16.5f, 9.75f)
				verticalLineTo(10.5f)
				moveTo(16.5f, 13.5f)
				verticalLineTo(14.25f)
				moveTo(16.5f, 17.25f)
				verticalLineTo(18f)
				moveTo(7.5f, 12.75f)
				horizontalLineTo(12.75f)
				moveTo(7.5f, 15f)
				horizontalLineTo(10.5f)
				moveTo(3.375f, 5.25f)
				curveTo(2.7537f, 5.25f, 2.25f, 5.7537f, 2.25f, 6.375f)
				verticalLineTo(9.40135f)
				curveTo(3.1467f, 9.9201f, 3.75f, 10.8896f, 3.75f, 12f)
				curveTo(3.75f, 13.1104f, 3.1467f, 14.0799f, 2.25f, 14.5987f)
				verticalLineTo(17.625f)
				curveTo(2.25f, 18.2463f, 2.7537f, 18.75f, 3.375f, 18.75f)
				horizontalLineTo(20.625f)
				curveTo(21.2463f, 18.75f, 21.75f, 18.2463f, 21.75f, 17.625f)
				verticalLineTo(14.5987f)
				curveTo(20.8533f, 14.0799f, 20.25f, 13.1104f, 20.25f, 12f)
				curveTo(20.25f, 10.8896f, 20.8533f, 9.9201f, 21.75f, 9.4014f)
				verticalLineTo(6.375f)
				curveTo(21.75f, 5.7537f, 21.2463f, 5.25f, 20.625f, 5.25f)
				horizontalLineTo(3.375f)
				close()
			}
		}.build()
		return _Ticket!!
	}

private var _Ticket: ImageVector? = null

