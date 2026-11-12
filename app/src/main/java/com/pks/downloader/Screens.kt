package com.pks.downloader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ---------------------------------------------------------------- Home

@Composable
fun HomeScreen(vm: DownloadViewModel, onViewAll: () -> Unit, onSettings: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val history = Downloader.history

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HomeHeader(onSettings) }

        item {
            OutlinedTextField(
                value = vm.url,
                onValueChange = { vm.url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Paste your video link here...", color = cs.onSurfaceVariant) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                trailingIcon = {
                    IconButton(onClick = { clipboard.getText()?.text?.let { vm.url = it.trim() } }) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = cs.primary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cs.primary,
                    unfocusedBorderColor = cs.outline,
                    focusedContainerColor = cs.surface,
                    unfocusedContainerColor = cs.surface,
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface,
                    cursorColor = cs.primary
                ),
                shape = RoundedCornerShape(18.dp)
            )
        }

        item {
            Button(
                onClick = { vm.fetchInfo() },
                enabled = vm.url.isNotBlank() && !vm.fetching && !vm.downloading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)
            ) {
                if (vm.fetching) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = cs.onPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Search, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Search Video", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        }

        if (Downloader.downloading) {
            item { ActiveDownloadCard() }
        } else {
            vm.meta?.let { m -> item { ResultCard(vm, m) } }
        }

        Downloader.message?.let { msg ->
            item { Text(msg, color = cs.onSurfaceVariant, fontSize = 14.sp) }
        }

        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.History, contentDescription = null, tint = cs.primary)
                Spacer(Modifier.width(10.dp))
                Text("Recent Downloads", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onBackground)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onViewAll) {
                    Text("View All", color = cs.primary, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = cs.primary)
                }
            }
        }

        if (history.isEmpty()) {
            item { EmptyState() }
        } else {
            items(history.take(3), key = { it.id }) { h ->
                DownloadRow(h) { openItem(ctx, h) }
            }
        }
    }
}

@Composable
private fun HomeHeader(onSettings: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(54.dp).clip(RoundedCornerShape(16.dp))
                .border(2.dp, cs.primary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Download, contentDescription = null, tint = cs.primary, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("PK'S Downloader", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = cs.onBackground)
            Text("Download made simple.", color = cs.onSurfaceVariant, fontSize = 14.sp)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = cs.onBackground, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ResultCard(vm: DownloadViewModel, m: VideoMeta) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surface)
            .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
    ) {
        AsyncImage(
            model = m.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(190.dp).background(cs.surfaceVariant)
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(m.title, color = cs.onSurface, fontWeight = FontWeight.SemiBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            val sub = listOfNotNull(m.uploader, durationText(m.durationSec)).joinToString("  •  ")
            if (sub.isNotEmpty()) Text(sub, color = cs.onSurfaceVariant, fontSize = 13.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Format.values().forEach { f ->
                    PkChip(f.label, vm.format == f) { vm.format = f }
                }
            }
            if (vm.format == Format.VIDEO) {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoQuality.values().forEach { q ->
                        PkChip(q.label, vm.quality == q) { vm.quality = q }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { vm.startDownload() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Download", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun PkChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cs.primary,
            selectedLabelColor = cs.onPrimary,
            containerColor = cs.surface,
            labelColor = cs.onSurface
        )
    )
}

@Composable
fun ActiveDownloadCard() {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surface)
            .border(1.dp, cs.primary, RoundedCornerShape(20.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = Downloader.activeThumb,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 112.dp, height = 76.dp)
                .clip(RoundedCornerShape(12.dp)).background(cs.surfaceVariant)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(Downloader.activeTitle ?: "Downloading...", maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold, color = cs.onSurface)
            LinearProgressIndicator(
                progress = { Downloader.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = cs.primary, trackColor = cs.outline
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val stats = listOf(
                    "${(Downloader.progress * 100).toInt()}%",
                    Downloader.speed,
                    Downloader.eta.takeIf { it.isNotEmpty() }?.let { "$it left" }
                ).filter { !it.isNullOrEmpty() }.joinToString("  •  ")
                Text(stats, color = cs.onSurfaceVariant, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { Downloader.cancel() }) { Text("Cancel", color = cs.primary) }
            }
        }
    }
}

@Composable
fun DownloadRow(item: HistoryItem, onOpen: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surface)
            .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onOpen)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(width = 112.dp, height = 76.dp)
                .clip(RoundedCornerShape(12.dp)).background(cs.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold, color = cs.onSurface)
            Text("${item.quality}  •  ${item.ext}  •  ${formatBytes(item.sizeBytes)}",
                color = cs.onSurfaceVariant, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = cs.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Downloaded", color = cs.primary, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.width(6.dp))
        Box(Modifier.width(1.dp).height(48.dp).background(cs.outline))
        IconButton(onClick = onOpen) {
            Icon(Icons.Filled.OpenInNew, contentDescription = "Open", tint = cs.onSurface)
        }
    }
}

@Composable
fun EmptyState() {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.CloudDownload, contentDescription = null, tint = cs.primary, modifier = Modifier.size(84.dp))
        Spacer(Modifier.height(12.dp))
        Text("No downloads yet", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = cs.onBackground)
        Spacer(Modifier.height(4.dp))
        Text("Your downloaded videos will appear here.", color = cs.onSurfaceVariant, fontSize = 14.sp)
    }
}

// ---------------------------------------------------------------- Downloads tab

@Composable
fun DownloadsScreen() {
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    val history = Downloader.history

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Downloads", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = cs.onBackground)
                Text("${history.size} saved", color = cs.onSurfaceVariant, fontSize = 14.sp)
            }
        }
        if (Downloader.downloading) item { ActiveDownloadCard() }
        if (history.isEmpty() && !Downloader.downloading) {
            item { EmptyState() }
        } else {
            items(history, key = { it.id }) { h -> DownloadRow(h) { openItem(ctx, h) } }
        }
    }
}

private fun durationText(sec: Int): String? {
    if (sec <= 0) return null
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
