package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ElectricianViewModel
import com.example.ui.components.ThreeDButton
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

data class PdfManualItem(
    val title: String,
    val fileName: String,
    val isBundled: Boolean,
    val fileSizeFormatted: String,
    val assetPath: String? = null,
    val localFile: File? = null
)

@Composable
fun PdfManualsScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var manualsList by remember { mutableStateOf<List<PdfManualItem>>(emptyList()) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var activeViewingPdf by remember { mutableStateOf<Pair<String, File>?>(null) }

    // If a PDF is selected, display the built-in PDF Viewer
    if (activeViewingPdf != null) {
        PdfViewerScreen(
            title = activeViewingPdf!!.first,
            pdfFile = activeViewingPdf!!.second,
            onBack = { activeViewingPdf = null }
        )
        return
    }

    // Load available manuals from assets/manuals and internal storage
    LaunchedEffect(refreshKey) {
        val list = mutableListOf<PdfManualItem>()

        // 1. Scan assets/manuals/
        try {
            val assetFiles = context.assets.list("manuals") ?: emptyArray()
            for (name in assetFiles) {
                if (name.endsWith(".pdf", ignoreCase = true)) {
                    val cleanTitle = name.removeSuffix(".pdf")
                        .replace("_", " ")
                        .replace("-", " ")
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

                    list.add(
                        PdfManualItem(
                            title = cleanTitle,
                            fileName = name,
                            isBundled = true,
                            fileSizeFormatted = "Pre-installed Offline",
                            assetPath = "manuals/$name"
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        // 2. Scan internal files/manuals/
        try {
            val internalDir = File(context.filesDir, "manuals")
            if (internalDir.exists()) {
                internalDir.listFiles()?.forEach { file ->
                    if (file.name.endsWith(".pdf", ignoreCase = true)) {
                        val cleanTitle = file.name.removeSuffix(".pdf")
                            .replace("_", " ")
                            .replace("-", " ")
                        val sizeKb = (file.length() / 1024).coerceAtLeast(1)
                        list.add(
                            PdfManualItem(
                                title = cleanTitle,
                                fileName = file.name,
                                isBundled = false,
                                fileSizeFormatted = "$sizeKb KB • Local File",
                                localFile = file
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {}

        manualsList = list
    }

    // Document Picker launcher for runtime opening & importing
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            importPdfUri(context, uri) { file, name ->
                refreshKey++
                // Automatically open the imported PDF in the built-in PDF viewer!
                activeViewingPdf = Pair(name, file)
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        modifier = modifier.fillMaxSize().testTag("pdf_manuals_screen")
    ) {
        // Hero / Import Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF Manuals",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Built-in PDF Viewer & Manuals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Read equipment cut-sheets, wiring schematics & codes offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ThreeDButton(
                        onClick = {
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                        modifier = Modifier.fillMaxWidth().testTag("import_pdf_button"),
                        baseColor = Color(0xFFFFA000),
                        contentColor = Color(0xFF111827),
                        depthColor = Color(0xFFD97706)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open / Import PDF File", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE PDF MANUALS (${manualsList.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
                IconButton(onClick = { refreshKey++ }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Manuals List
        if (manualsList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.FindInPage,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No PDF manuals found.\nTap 'Open / Import PDF File' above to view any PDF.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(manualsList) { manual ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val file = resolveManualFile(context, manual)
                            if (file != null) {
                                activeViewingPdf = Pair(manual.title, file)
                            } else {
                                Toast.makeText(context, "Could not open PDF file", Toast.LENGTH_SHORT).show()
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE53935).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = manual.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (manual.isBundled) Color(0xFF1E5AA8).copy(alpha = 0.15f)
                                                else Color(0xFFFFA000).copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (manual.isBundled) "BUNDLED" else "LOCAL",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (manual.isBundled) Color(0xFF1E5AA8) else Color(0xFFD97706)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = manual.fileSizeFormatted,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = {
                                    val file = resolveManualFile(context, manual)
                                    if (file != null) {
                                        activeViewingPdf = Pair(manual.title, file)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFFFEF3C7),
                                    contentColor = Color(0xFFD97706)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Read",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Read", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (!manual.isBundled && manual.localFile != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = {
                                    manual.localFile.delete()
                                    refreshKey++
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Extracts or resolves the physical File for a PDF item
private fun resolveManualFile(context: Context, manual: PdfManualItem): File? {
    return try {
        if (manual.isBundled && manual.assetPath != null) {
            val cacheDir = File(context.cacheDir, "manuals")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val targetFile = File(cacheDir, manual.fileName)
            if (!targetFile.exists() || targetFile.length() == 0L) {
                context.assets.open(manual.assetPath).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            targetFile
        } else if (manual.localFile != null && manual.localFile.exists()) {
            manual.localFile
        } else null
    } catch (_: Exception) {
        null
    }
}

// Helper function to import PDF from Uri into internal filesDir
private fun importPdfUri(context: Context, uri: Uri, onComplete: (File, String) -> Unit) {
    try {
        val resolver = context.contentResolver
        val cursor = resolver.query(uri, null, null, null, null)
        var displayName = "manual_${System.currentTimeMillis()}.pdf"
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    displayName = it.getString(nameIndex) ?: displayName
                }
            }
        }

        val cleanTitle = displayName.removeSuffix(".pdf")
            .replace("_", " ")
            .replace("-", " ")

        val manualsDir = File(context.filesDir, "manuals")
        if (!manualsDir.exists()) {
            manualsDir.mkdirs()
        }

        val targetFile = File(manualsDir, displayName)
        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        Toast.makeText(context, "Opened: $cleanTitle", Toast.LENGTH_SHORT).show()
        onComplete(targetFile, cleanTitle)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
