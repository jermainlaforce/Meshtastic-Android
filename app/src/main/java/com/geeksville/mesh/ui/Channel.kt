package com.geeksville.mesh.ui

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.graphics.*
import androidx.ui.graphics.colorspace.ColorSpace
import androidx.ui.graphics.colorspace.ColorSpaces
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.OutlinedButton
import androidx.ui.material.ripple.Ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Density
import androidx.ui.unit.PxSize
import androidx.ui.unit.dp
import androidx.ui.unit.toRect
import com.geeksville.analytics.DataPair
import com.geeksville.android.GeeksvilleApplication
import com.geeksville.android.Logging
import com.geeksville.mesh.R
import com.geeksville.mesh.model.UIState

/// The Compose IDE preview doesn't like the protobufs
data class Channel(val name: String, val num: Int)

object ChannelLog : Logging

/// Borrowed from Compose
class AndroidImage(val bitmap: Bitmap) : Image {

    /**
     * @see Image.width
     */
    override val width: Int
        get() = bitmap.width

    /**
     * @see Image.height
     */
    override val height: Int
        get() = bitmap.height

    override val config: ImageConfig get() = ImageConfig.Argb8888

    /**
     * @see Image.colorSpace
     */
    override val colorSpace: ColorSpace
        get() = ColorSpaces.Srgb

    /**
     * @see Image.hasAlpha
     */
    override val hasAlpha: Boolean
        get() = bitmap.hasAlpha()

    /**
     * @see Image.nativeImage
     */
    override val nativeImage: NativeImage
        get() = bitmap

    /**
     * @see
     */
    override fun prepareToDraw() {
        bitmap.prepareToDraw()
    }
}


/// Stolen from the Compose SimpleImage, replace with their real Image component someday
// TODO(mount, malkov) : remove when RepaintBoundary is a modifier: b/149982905
// This is class and not val because if b/149985596
private object ClipModifier : DrawModifier {
    override fun draw(density: Density, drawContent: () -> Unit, canvas: Canvas, size: PxSize) {
        canvas.save()
        canvas.clipRect(size.toRect())
        drawContent()
        canvas.restore()
    }
}

/// Stolen from the Compose SimpleImage, replace with their real Image component someday
@Composable
fun ScaledImage(
    image: Image,
    modifier: Modifier = Modifier.None,
    tint: Color? = null
) {
    with(DensityAmbient.current) {
        val imageModifier = ImagePainter(image).toModifier(
            scaleFit = ScaleFit.FillMaxDimension,
            colorFilter = tint?.let { ColorFilter(it, BlendMode.srcIn) }
        )
        Box(modifier + ClipModifier + imageModifier)
    }
}

@Composable
fun ChannelContent(channel: Channel = Channel("Default", 7)) {
    analyticsScreen(name = "channel")

    val typography = MaterialTheme.typography()
    val context = ContextAmbient.current

    Column(modifier = LayoutSize.Fill + LayoutPadding(16.dp)) {
        Text(
            text = "Channel: ${channel.name}",
            modifier = LayoutGravity.Center,
            style = typography.h4
        )

        Row(modifier = LayoutGravity.Center) {
            // simulated qr code
            // val image = imageResource(id = R.drawable.qrcode)
            val image = AndroidImage(UIState.getChannelQR(context))

            ScaledImage(
                image = image,
                modifier = LayoutGravity.Center + LayoutSize.Min(200.dp, 200.dp)
            )

            Ripple(bounded = false) {
                OutlinedButton(modifier = LayoutGravity.Center + LayoutPadding(start = 24.dp),
                    onClick = {
                        GeeksvilleApplication.analytics.track(
                            "share",
                            DataPair("content_type", "channel")
                        ) // track how many times users share channels

                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, UIState.getChannelUrl(context))
                            putExtra(Intent.EXTRA_TITLE, "A URL for joining a Meshtastic mesh")
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                    VectorImage(
                        id = R.drawable.ic_twotone_share_24,
                        tint = palette.onBackground
                    )
                }
            }
        }

        Text(
            text = "Number: ${channel.num}",
            modifier = LayoutGravity.Center
        )
        Text(
            text = "Mode: Long range (but slow)",
            modifier = LayoutGravity.Center
        )
    }
}


@Preview
@Composable
fun previewChannel() {
    // another bug? It seems modaldrawerlayout not yet supported in preview
    MaterialTheme(colors = palette) {
        ChannelContent()
    }
}
