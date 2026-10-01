package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.theme.ElectricAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    title: String,
    pdfFile: File,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var pageCount by remember { mutableIntStateOf(0) }
    var isReady by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showJumpDialog by remember { mutableStateOf(false) }

    // Page aspect ratios (width / height)
    val pageDimensions = remember { mutableStateMapOf<Int, Pair<Int, Int>>() }
    // Rendered Bitmaps cache (pageIndex -> Bitmap)
    val bitmapCache = remember { mutableStateMapOf<Int, Bitmap>() }
    // Rendering lock since Android PdfRenderer requires single-threaded access
    val renderMutex = remember { Mutex() }
    var pdfRendererHolder by remember { mutableStateOf<PdfRenderer?>(null) }
    var pfdHolder by remember { mutableStateOf<ParcelFileDescriptor?>(null) }

    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BackHandler {
        onBack()
    }

    // Initialize PdfRenderer on background thread
    LaunchedEffect(pdfFile) {
        withContext(Dispatchers.IO) {
            try {
                if (!pdfFile.exists() || pdfFile.length() == 0L) {
                    errorMessage = "PDF file is empty or missing."
                    return@withContext
                }

                val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                pfdHolder = pfd
                val renderer = PdfRenderer(pfd)
                pdfRendererHolder = renderer
                val count = renderer.pageCount
                pageCount = count

                // Pre-read dimensions of pages
                renderMutex.withLock {
                    for (i in 0 until minOf(count, 5)) {
                        val page = renderer.openPage(i)
                        pageDimensions[i] = Pair(page.width, page.height)
                        page.close()
                    }
                }

                isReady = true
            } catch (e: Exception) {
                errorMessage = "Could not open PDF: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    // Cleanup native resources on exit
    DisposableEffect(Unit) {
        onDispose {
            try {
                pdfRendererHolder?.close()
                pfdHolder?.close()
                bitmapCache.values.forEach { it.recycle() }
                bitmapCache.clear()
            } catch (_: Exception) {}
        }
    }

    // Determine current page based on list scroll position
    val currentPage by remember {
        derivedStateOf {
            if (pageCount == 0) 1
            else (listState.firstVisibleItemIndex + 1).coerceIn(1, pageCount)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("pdf_viewer_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (pageCount > 0) {
                            Text(
                                text = "Page $currentPage of $pageCount • Built-in Offline Reader",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Zoom Out
                    IconButton(
                        onClick = {
                            scale = (scale - 0.25f).coerceAtLeast(0.75f)
                            if (scale <= 1.0f) offset = Offset.Zero
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = Color.White
                        )
                    }

                    // Reset Zoom
                    if (scale != 1.0f) {
                        TextButton(
                            onClick = {
                                scale = 1.0f
                                offset = Offset.Zero
                            }
                        ) {
                            Text(
                                text = "${(scale * 100).toInt()}%",
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Zoom In
                    IconButton(
                        onClick = {
                            scale = (scale + 0.25f).coerceAtMost(3.0f)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = Color.White
                        )
                    }

                    // Jump to Page
                    IconButton(onClick = { showJumpDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FindInPage,
                            contentDescription = "Jump to Page",
                            tint = Color.White
                        )
                    }

                    // Share PDF
                    IconButton(
                        onClick = {
                            sharePdfFile(context, pdfFile, title)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share PDF",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF131B2E)
                )
            )
        },
        bottomBar = {
            if (isReady && pageCount > 0) {
                Surface(
                    color = Color(0xFF131B2E),
                    border = BorderStroke(1.dp, Color(0xFF26334D)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prev Page button
                        FilledTonalIconButton(
                            onClick = {
                                if (currentPage > 1) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(currentPage - 2)
                                    }
                                }
                            },
                            enabled = currentPage > 1,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF26334D),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF1A2234),
                                disabledContentColor = Color(0xFF475569)
                            )
                        ) {
                            Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Page")
                        }

                        // Center Page pill (clickable to jump)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showJumpDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF26334D)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Page $currentPage of $pageCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFF59E0B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.UnfoldMore,
                                    contentDescription = "Change page",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Next Page button
                        FilledTonalIconButton(
                            onClick = {
                                if (currentPage < pageCount) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(currentPage)
                                    }
                                }
                            },
                            enabled = currentPage < pageCount,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF26334D),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF1A2234),
                                disabledContentColor = Color(0xFF475569)
                            )
                        ) {
                            Icon(Icons.Default.NavigateNext, contentDescription = "Next Page")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            when {
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }

                !isReady -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFD97706))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading PDF document...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                }

                pageCount == 0 -> {
                    Text(
                        text = "Document contains no readable pages.",
                        color = Color(0xFF64748B)
                    )
                }

                else -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val screenWidth = maxWidth
                        val density = LocalDensity.current

                        // Pinch-to-zoom & pan gesture handler
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(0.75f, 3.5f)
                                        if (scale > 1.0f) {
                                            val maxOffsetX = (size.width * (scale - 1f)) / 2f
                                            val maxOffsetY = (size.height * (scale - 1f)) / 2f
                                            offset = Offset(
                                                x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                                y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                            )
                                        } else {
                                            offset = Offset.Zero
                                        }
                                    }
                                }
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offset.x
                                    translationY = offset.y
                                }
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(pageCount, key = { it }) { pageIndex ->
                                    PdfPageView(
                                        pageIndex = pageIndex,
                                        pdfRenderer = pdfRendererHolder,
                                        renderMutex = renderMutex,
                                        bitmapCache = bitmapCache,
                                        screenWidthPx = with(density) { (screenWidth - 24.dp).roundToPx() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Jump to Page Dialog
    if (showJumpDialog && pageCount > 0) {
        var targetPage by remember { mutableFloatStateOf(currentPage.toFloat()) }

        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            containerColor = Color(0xFF131B2E),
            title = {
                Text(
                    text = "Jump to Page",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Page ${targetPage.toInt()} of $pageCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = targetPage,
                        onValueChange = { targetPage = it },
                        valueRange = 1f..pageCount.toFloat(),
                        steps = (pageCount - 2).coerceAtLeast(0),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFF59E0B),
                            activeTrackColor = Color(0xFFF59E0B),
                            inactiveTrackColor = Color(0xFF26334D)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = { targetPage = 1f },
                            border = BorderStroke(1.dp, Color(0xFF26334D))
                        ) {
                            Text("First (1)", color = Color.White, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { targetPage = pageCount.toFloat() },
                            border = BorderStroke(1.dp, Color(0xFF26334D))
                        ) {
                            Text("Last ($pageCount)", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pageIdx = (targetPage.toInt() - 1).coerceIn(0, pageCount - 1)
                        coroutineScope.launch {
                            listState.scrollToItem(pageIdx)
                        }
                        showJumpDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("Go", color = Color(0xFF111827), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
fun PdfPageView(
    pageIndex: Int,
    pdfRenderer: PdfRenderer?,
    renderMutex: Mutex,
    bitmapCache: MutableMap<Int, Bitmap>,
    screenWidthPx: Int
) {
    var pageBitmap by remember { mutableStateOf(bitmapCache[pageIndex]) }
    var isLoading by remember { mutableStateOf(pageBitmap == null) }

    // Asynchronously render page bitmap
    LaunchedEffect(pageIndex, pdfRenderer) {
        if (pageBitmap == null && pdfRenderer != null) {
            withContext(Dispatchers.IO) {
                renderMutex.withLock {
                    try {
                        if (bitmapCache.containsKey(pageIndex)) {
                            pageBitmap = bitmapCache[pageIndex]
                            isLoading = false
                            return@withLock
                        }

                        val page = pdfRenderer.openPage(pageIndex)
                        val pageWidth = page.width
                        val pageHeight = page.height

                        // Calculate high-resolution scale (2x for crisp text)
                        val targetWidth = screenWidthPx.coerceAtLeast(600)
                        val scaleFactor = targetWidth.toFloat() / pageWidth.toFloat()
                        val targetHeight = (pageHeight * scaleFactor).toInt().coerceAtLeast(400)

                        val bmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bmp)
                        canvas.drawColor(AndroidColor.WHITE)

                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()

                        bitmapCache[pageIndex] = bmp
                        pageBitmap = bmp
                        isLoading = false
                    } catch (e: Exception) {
                        isLoading = false
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Page Header info bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAGE ${pageIndex + 1}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "PowerCalc Reader",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Page Render or Placeholder
            if (pageBitmap != null) {
                Image(
                    bitmap = pageBitmap!!.asImageBitmap(),
                    contentDescription = "PDF Page ${pageIndex + 1}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFFD97706),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rendering Page ${pageIndex + 1}...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

// Helper to share PDF file
private fun sharePdfFile(context: Context, file: File, title: String) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share $title"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
