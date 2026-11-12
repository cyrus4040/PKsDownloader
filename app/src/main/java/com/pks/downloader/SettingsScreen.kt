package com.pks.downloader

import android.content.Intent
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onPickFolder: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    var dialog by remember { mutableStateOf<String?>(null) }
    var showContact by remember { mutableStateOf(false) }
    val version = remember {
        runCatching { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName }.getOrNull() ?: "1.0.0"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Settings", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = cs.onBackground)
                    Text("Customize your experience", color = cs.onSurfaceVariant, fontSize = 14.sp)
                }
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(cs.primary),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.Settings, contentDescription = null, tint = cs.onPrimary) }
            }
        }

        item {
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(cs.surface).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.pk_logo),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("PK'S Downloader", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                    Text("Version $version", color = cs.onSurfaceVariant, fontSize = 13.sp)
                }
                Text(
                    "Up to date", color = cs.primary, fontSize = 12.sp,
                    modifier = Modifier.clip(RoundedCornerShape(50))
                        .background(cs.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        item { SectionLabel("DOWNLOAD SETTINGS") }
        item {
            SettingsCard {
                SettingRow(Icons.Filled.Folder, "Download Location", "Choose where files are saved") { dialog = "location" }
                RowDivider()
                SettingRow(Icons.Filled.Storage, "Storage Information", "View used and available storage") { dialog = "storage" }
                RowDivider()
                SettingRow(Icons.Filled.Bolt, "Default Download Quality", "Choose preferred video quality",
                    trailing = { Pill(Prefs.quality.label) }) { dialog = "quality" }
                RowDivider()
                SettingRow(Icons.Filled.MusicNote, "Default Download Format", "Video or Audio preference") { dialog = "format" }
            }
        }

        item { SectionLabel("APP PREFERENCES") }
        item {
            SettingsCard {
                SettingRow(Icons.Filled.DarkMode, "Appearance", "App theme and display",
                    trailing = { Pill(Prefs.theme.label) }) { dialog = "theme" }
                RowDivider()
                SettingRow(Icons.Filled.Notifications, "Notifications", "Download notifications",
                    trailing = { Pill(if (Prefs.notifications) "ON" else "OFF", highlight = Prefs.notifications) }) {
                    Prefs.setNotifications(!Prefs.notifications)
                }
                RowDivider()
                SettingRow(Icons.Filled.Sync, "Clipboard Detection", "Detect copied video links",
                    trailing = { Pill(if (Prefs.clipboard) "ON" else "OFF", highlight = Prefs.clipboard) }) {
                    Prefs.setClipboard(!Prefs.clipboard)
                }
            }
        }

        item { SectionLabel("STORAGE & DATA") }
        item {
            SettingsCard {
                SettingRow(Icons.Filled.History, "Clear Download History", null) { dialog = "clear_history" }
                RowDivider()
                SettingRow(Icons.Filled.Delete, "Clear Cache", "Remove temporary app data") { dialog = "clear_cache" }
            }
        }

        item { SectionLabel("ABOUT") }
        item {
            SettingsCard {
                SettingRow(Icons.Filled.Info, "About PK'S Downloader", null) { dialog = "about" }
                RowDivider()
                SettingRow(Icons.Filled.Email, "Contact & Support", "Connect with the developer") { showContact = true }
                RowDivider()
                SettingRow(Icons.Filled.PrivacyTip, "Privacy Policy", null,
                    trailing = { Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = cs.onSurfaceVariant) }) {
                    dialog = "privacy"
                }
                RowDivider()
                SettingRow(Icons.Filled.Star, "Rate This App", null) { rateApp(ctx) }
                RowDivider()
                SettingRow(Icons.Filled.Share, "Share App", null) { shareApp(ctx) }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }

    // ------------------------------------------------------------ dialogs
    val close = { dialog = null }
    when (dialog) {
        "location" -> AlertDialog(
            onDismissRequest = close,
            title = { Text("Download Location") },
            text = { Text("Files are saved to:\n${Storage.locationLabel(ctx)}") },
            confirmButton = { TextButton(onClick = { close(); onPickFolder() }) { Text("Choose folder") } },
            dismissButton = {
                TextButton(onClick = { Prefs.setTreeUri(null); close() }) { Text("Use default") }
            }
        )
        "storage" -> {
            val (total, free) = remember {
                runCatching {
                    val s = StatFs(Environment.getExternalStorageDirectory().path)
                    s.totalBytes to s.availableBytes
                }.getOrDefault(0L to 0L)
            }
            val used = Downloader.history.sumOf { it.sizeBytes }
            AlertDialog(
                onDismissRequest = close,
                title = { Text("Storage Information") },
                text = {
                    Text(
                        "Downloaded files: ${Downloader.history.size}\n" +
                            "Used by downloads: ${formatBytes(used)}\n" +
                            "Free on device: ${formatBytes(free)}\n" +
                            "Total device storage: ${formatBytes(total)}"
                    )
                },
                confirmButton = { TextButton(onClick = close) { Text("OK") } }
            )
        }
        "quality" -> ChoiceDialog("Default Download Quality", VideoQuality.values().toList(), Prefs.quality,
            { it.label }, { Prefs.setQuality(it) }, close)
        "format" -> ChoiceDialog("Default Download Format", Format.values().toList(), Prefs.format,
            { it.label }, { Prefs.setFormat(it) }, close)
        "theme" -> ChoiceDialog("Appearance", ThemeMode.values().toList(), Prefs.theme,
            { it.label }, { Prefs.setTheme(it) }, close)
        "clear_history" -> AlertDialog(
            onDismissRequest = close,
            title = { Text("Clear Download History") },
            text = { Text("This removes the list in the app. The downloaded files stay on your phone.") },
            confirmButton = {
                TextButton(onClick = { Downloader.clearHistory(); close() }) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = close) { Text("Cancel") } }
        )
        "clear_cache" -> AlertDialog(
            onDismissRequest = close,
            title = { Text("Clear Cache") },
            text = { Text("Remove temporary files and cached thumbnails?") },
            confirmButton = {
                TextButton(onClick = {
                    val freed = clearCache(ctx)
                    toast(ctx, "Freed ${formatBytes(freed)}")
                    close()
                }) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = close) { Text("Cancel") } }
        )
        "about" -> AlertDialog(
            onDismissRequest = close,
            title = { Text("About PK'S Downloader") },
            text = { Text("Version $version\n\nA simple video downloader. Paste a link, pick a quality, and download. Downloads keep running in the background.") },
            confirmButton = { TextButton(onClick = close) { Text("OK") } }
        )
        "privacy" -> AlertDialog(
            onDismissRequest = close,
            title = { Text("Privacy Policy") },
            text = { Text("PK'S Downloader has no accounts, ads or analytics. Your download history and settings stay on your device. A link you enter is sent only to the website it belongs to, so the video can be fetched.") },
            confirmButton = { TextButton(onClick = close) { Text("OK") } }
        )
    }

    if (showContact) {
        ModalBottomSheet(onDismissRequest = { showContact = false }, containerColor = cs.background) {
            ContactSheet(ctx)
        }
    }
}

@Composable
private fun ContactSheet(ctx: android.content.Context) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Contact & Support", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = cs.onBackground)
        Spacer(Modifier.height(6.dp))
        Text("Connect with the developer", color = cs.onSurfaceVariant, fontSize = 15.sp)
        Spacer(Modifier.height(16.dp))
        SettingsCard {
            ContactRow(
                tile = { Text("f", color = cs.primary, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                title = "Facebook", sub = "Connect with me on Facebook"
            ) { openLink(ctx, FACEBOOK_URL) }
            RowDivider()
            ContactRow(
                tile = { Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = cs.primary) },
                title = "Instagram", sub = "@p_o_r_o_s"
            ) { openLink(ctx, INSTAGRAM_URL) }
            RowDivider()
            ContactRow(
                tile = { Icon(Icons.Filled.Send, contentDescription = null, tint = cs.primary) },
                title = "Telegram", sub = "@cyrus4040"
            ) { openLink(ctx, TELEGRAM_URL) }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ContactRow(tile: @Composable () -> Unit, title: String, sub: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(cs.surfaceVariant),
            contentAlignment = Alignment.Center
        ) { tile() }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
            Text(sub, color = cs.onSurfaceVariant, fontSize = 14.sp)
        }
        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = cs.primary)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp, start = 2.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surface),
        content = content
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    trailing: @Composable () -> Unit = {
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    },
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = cs.primary, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = cs.onSurface)
            if (subtitle != null) Text(subtitle, color = cs.onSurfaceVariant, fontSize = 13.sp)
        }
        trailing()
    }
}

@Composable
private fun Pill(text: String, highlight: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    Text(
        text,
        color = if (highlight) cs.primary else cs.onSurface,
        fontSize = 13.sp,
        modifier = Modifier.clip(RoundedCornerShape(50))
            .background(if (highlight) cs.primary.copy(alpha = 0.15f) else cs.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
private fun <T> ChoiceDialog(
    title: String,
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { o ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onSelect(o); onDismiss() }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = o == selected, onClick = { onSelect(o); onDismiss() })
                        Spacer(Modifier.width(8.dp))
                        Text(label(o))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

private fun clearCache(ctx: android.content.Context): Long {
    var freed = 0L
    ctx.cacheDir.listFiles()?.forEach { f ->
        freed += f.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        f.deleteRecursively()
    }
    return freed
}

private fun rateApp(ctx: android.content.Context) {
    try {
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=${ctx.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (e: Exception) {
        openLink(ctx, "https://play.google.com/store/apps/details?id=${ctx.packageName}")
    }
}

private fun shareApp(ctx: android.content.Context) {
    val send = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, "Try PK'S Downloader, a simple video downloader for Android.")
    ctx.startActivity(Intent.createChooser(send, "Share App").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
