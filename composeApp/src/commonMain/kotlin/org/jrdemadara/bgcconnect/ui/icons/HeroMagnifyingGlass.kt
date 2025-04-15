package org.jrdemadara.bgcconnect.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val HeroMagnifyingGlass: ImageVector
	get() {
		if (_MagnifyingGlass != null) {
			return _MagnifyingGlass!!
		}
		_MagnifyingGlass = ImageVector.Builder(
			name = "MagnifyingGlass",
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
				moveTo(21f, 21f)
				lineTo(15.8033f, 15.8033f)
				moveTo(15.8033f, 15.8033f)
				curveTo(17.1605f, 14.4461f, 18f, 12.5711f, 18f, 10.5f)
				curveTo(18f, 6.3579f, 14.6421f, 3f, 10.5f, 3f)
				curveTo(6.3579f, 3f, 3f, 6.3579f, 3f, 10.5f)
				curveTo(3f, 14.6421f, 6.3579f, 18f, 10.5f, 18f)
				curveTo(12.5711f, 18f, 14.4461f, 17.1605f, 15.8033f, 15.8033f)
				close()
			}
		}.build()
		return _MagnifyingGlass!!
	}

private var _MagnifyingGlass: ImageVector? = null

