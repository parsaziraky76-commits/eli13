package studio.cluvex.aether.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.R
import studio.cluvex.aether.ai.AiTopic
import studio.cluvex.aether.ui.ai.AiTopicIcon

private val AboutBackground = Color(0xFF0B0716)
private val AboutCard = Color(0xFF130E24)
private val AboutViolet = Color(0xFFB49AFF)

@Composable
fun AboutPanel(modifier: Modifier = Modifier, startExpanded: Boolean = true) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier.fillMaxWidth().background(AboutBackground).padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Keep the About topic entry point; only simplify its informational content.
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
            AiTopicIcon(AiTopic.ABOUT)
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = AboutCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth().clickable {
                runCatching { uriHandler.openUri("https://t.me/Darlingn00") }
            },
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null, tint = AboutViolet, modifier = Modifier.size(23.dp))
                Column(Modifier.weight(1f).padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(R.string.about_contact_support),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                    Text("@Darlingn00", color = AboutViolet, fontSize = 13.sp)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AboutViolet)
            }
        }
    }
}
