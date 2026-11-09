package studio.cluvex.aether.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.R
import studio.cluvex.aether.BuildConfig

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
        Image(
            painter = painterResource(R.drawable.dopamine_logo),
            contentDescription = "Dopamine logo",
            modifier = Modifier.size(116.dp).clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(Color(0xFF28104B), Color(0xFF090511)))),
        )
        Text(stringResource(R.string.dopamine_brand), color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        Text("v1", color = AboutViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier
            .clip(RoundedCornerShape(50)).background(Color(0xFF2A1947)).padding(horizontal = 14.dp, vertical = 5.dp))

        Card(colors = CardDefaults.cardColors(containerColor = AboutCard), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VersionRow(stringResource(R.string.about_app_version_label), BuildConfig.VERSION_NAME)
                VersionRow(stringResource(R.string.about_core_version_label), BuildConfig.CORE_VERSION)
                VersionRow(stringResource(R.string.about_hev_version_label), "2.19.0")
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = AboutCard), shape = RoundedCornerShape(18.dp), modifier = Modifier
            .fillMaxWidth().clickable { runCatching { uriHandler.openUri("https://t.me/Darlingn00") } }) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Send, contentDescription = null, tint = AboutViolet, modifier = Modifier.size(23.dp))
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(stringResource(R.string.about_support_id_title), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("@Darlingn00", color = AboutViolet, fontSize = 13.sp)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AboutViolet)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = AboutCard), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(stringResource(R.string.about_license_title), color = AboutViolet, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.about_license_body),
                    color = Color(0xFFE5DFF0), fontSize = 13.sp, lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun VersionRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFFD8D0E8), fontSize = 13.sp, modifier = Modifier.weight(1f))
        Spacer(Modifier.size(8.dp))
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
