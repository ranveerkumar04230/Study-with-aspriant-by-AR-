package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.data.model.StudyBatch
import com.example.data.model.StudyLecture
import com.example.data.model.StudySubject
import com.example.ui.StudyViewModel
import com.example.ui.StudyWorkspaceData
import com.example.ui.SyncStatus
import com.example.ui.UiState
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueBright
import com.example.ui.theme.CobaltBlueDeep
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDeep
import com.example.ui.theme.DarkPink
import com.example.ui.theme.DisabledButtonBg
import com.example.ui.theme.DisabledButtonText
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkDark
import com.example.ui.theme.NeonPinkSoft
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.StudyBlueTintCard
import com.example.ui.theme.StudyCanvasBottom
import com.example.ui.theme.StudyCanvasMid
import com.example.ui.theme.StudyCanvasTop
import com.example.ui.theme.StudyInkPrimary
import com.example.ui.theme.StudyInkSecondary
import com.example.ui.theme.StudyLightBorder
import com.example.ui.theme.StudyPinkTintCard
import com.example.ui.theme.StudyWhiteCard
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.WhatsAppGreen
import kotlinx.coroutines.launch

const val ROUTE_BATCHES = "batches"
const val ROUTE_SUBJECTS = "subjects/{batchId}"
const val ROUTE_LECTURES = "lectures/{batchId}/{subjectId}"

@Composable
fun StudyPortalScreen(
  viewModel: StudyViewModel,
  userContactLabel: String?,
  onSignOut: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val navController = rememberNavController()

  val workspaceState by viewModel.workspaceState.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
  val isAdminSheetOpen by viewModel.isAdminSheetOpen.collectAsStateWithLifecycle()
  val isSavingAdmin by viewModel.isSavingAdmin.collectAsStateWithLifecycle()
  val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

  var mediaRequest by remember { mutableStateOf<MediaViewerRequest?>(null) }
  var showProfileDialog by remember { mutableStateOf(false) }

  LaunchedEffect(viewModel) {
    viewModel.toastMessages.collect { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val activeBatchId = navBackStackEntry?.arguments?.getString("batchId")
  val activeSubjectId = navBackStackEntry?.arguments?.getString("subjectId")

  val workspaceData = (workspaceState as? UiState.Success)?.data ?: StudyWorkspaceData()
  val activeBatch = workspaceData.batches.firstOrNull { it.id == activeBatchId }
  val activeSubject = workspaceData.subjects.firstOrNull { it.id == activeSubjectId }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(StudyCanvasTop, StudyCanvasMid, StudyCanvasBottom)
        )
      ),
  ) {
    StudyDecorativeCanvas()

    Scaffold(
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      snackbarHost = {
        SnackbarHost(
          hostState = snackbarHostState,
          modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        )
      },
      floatingActionButton = {
        if (isAdminUnlocked) {
          ExtendedFloatingActionButton(
            onClick = { viewModel.openAdminSheetIfUnlocked() },
            containerColor = DarkPink,
            contentColor = Color.White,
            icon = {
              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
              )
            },
            text = {
              Text(
                text = "Manage Content",
                style = MaterialTheme.typography.labelLarge,
              )
            },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("admin_manage_fab"),
          )
        }
      },
      bottomBar = {
        StudyBottomBar(
          whatsappUrl = workspaceData.preferences.whatsappUrl,
          telegramUrl = workspaceData.preferences.telegramUrl,
          footerText = if (activeBatch != null) {
            "⚡ ${activeBatch.name} • ${activeBatch.subtitle}"
          } else {
            stringResource(R.string.footer_default)
          },
          onOpenExternalError = { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
          },
        )
      },
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp),
        ) {
          PortalHeader(
            activeBatch = activeBatch,
            syncStatus = syncStatus,
            isAdminUnlocked = isAdminUnlocked,
            onProfileClick = { showProfileDialog = true },
          )

          SearchBarField(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onClear = viewModel::clearSearch,
          )

          when (val state = workspaceState) {
            is UiState.Loading -> {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f),
                contentAlignment = Alignment.Center,
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  CircularProgressIndicator(color = DarkPink)
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "Loading your study workspace...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudyInkSecondary,
                  )
                }
              }
            }

            is UiState.Error -> {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
                  .padding(24.dp),
                contentAlignment = Alignment.Center,
              ) {
                Surface(
                  color = StudyPinkTintCard,
                  border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                  shape = RoundedCornerShape(18.dp),
                  shadowElevation = 4.dp,
                ) {
                  Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(18.dp),
                  )
                }
              }
            }

            is UiState.Success -> {
              NavHost(
                navController = navController,
                startDestination = ROUTE_BATCHES,
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f),
              ) {
                composable(ROUTE_BATCHES) {
                  BatchesPage(
                    batches = state.data.batches,
                    subjects = state.data.subjects,
                    lectures = state.data.lectures,
                    searchQuery = searchQuery,
                    onBatchClick = { batch ->
                      viewModel.clearSearch()
                      navController.navigate("subjects/${batch.id}")
                    },
                  )
                }

                composable(
                  route = ROUTE_SUBJECTS,
                  arguments = listOf(navArgument("batchId") { type = NavType.StringType }),
                ) { entry ->
                  val batchId = entry.arguments?.getString("batchId").orEmpty()
                  val batch = state.data.batches.firstOrNull { it.id == batchId }
                  val batchSubjects = state.data.subjects.filter { it.batchId == batchId }
                  SubjectsPage(
                    batch = batch,
                    subjects = batchSubjects,
                    lectures = state.data.lectures,
                    searchQuery = searchQuery,
                    onBack = {
                      viewModel.clearSearch()
                      navController.popBackStack()
                    },
                    onSubjectClick = { subject ->
                      viewModel.clearSearch()
                      navController.navigate("lectures/$batchId/${subject.id}")
                    },
                  )
                }

                composable(
                  route = ROUTE_LECTURES,
                  arguments = listOf(
                    navArgument("batchId") { type = NavType.StringType },
                    navArgument("subjectId") { type = NavType.StringType },
                  ),
                ) { entry ->
                  val batchId = entry.arguments?.getString("batchId").orEmpty()
                  val subjectId = entry.arguments?.getString("subjectId").orEmpty()
                  val subject = state.data.subjects.firstOrNull { it.id == subjectId }
                  val subjectLectures = state.data.lectures.filter {
                    it.batchId == batchId && it.subjectId == subjectId
                  }
                  LecturesPage(
                    subjectName = subject?.name ?: "SUBJECT",
                    lectures = subjectLectures,
                    searchQuery = searchQuery,
                    isAdminUnlocked = isAdminUnlocked,
                    onBack = {
                      viewModel.clearSearch()
                      navController.popBackStack()
                    },
                    onOpenMedia = { req -> mediaRequest = req },
                    onToggleCompleted = viewModel::toggleLectureCompleted,
                    onDeleteLecture = { lecture -> viewModel.deleteLecture(lecture.id) },
                  )
                }
              }
            }
          }
        }
      }
    }

    mediaRequest?.let { req ->
      MediaViewerDialog(
        request = req,
        onDismiss = { mediaRequest = null },
        onExternalLaunchError = { msg ->
          scope.launch { snackbarHostState.showSnackbar(msg) }
        },
      )
    }

    if (showProfileDialog) {
      StudentProfileDialog(
        userContactLabel = userContactLabel,
        isAdminUnlocked = isAdminUnlocked,
        onDismiss = { showProfileDialog = false },
        onLogout = {
          showProfileDialog = false
          onSignOut()
        },
      )
    }

    if (isAdminSheetOpen && isAdminUnlocked) {
      AdminSheet(
        initialBatchName = activeBatch?.name ?: "",
        initialBatchSubtitle = activeBatch?.subtitle ?: "",
        initialSubjectName = activeSubject?.name ?: "",
        initialWhatsappUrl = workspaceData.preferences.whatsappUrl,
        initialTelegramUrl = workspaceData.preferences.telegramUrl,
        isSaving = isSavingAdmin,
        onDismiss = viewModel::closeAdminSheet,
        onSaveLecture = { batch, sub, subj, lec, vid, eng, hin, ppt, onCleared ->
          viewModel.saveLecture(batch, sub, subj, lec, vid, eng, hin, ppt, onCleared)
        },
        onSaveSocials = viewModel::saveSocialLinks,
      )
    }
  }
}

@Composable
private fun StudyDecorativeCanvas() {
  Box(modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      drawCircle(
        color = CobaltBlue.copy(alpha = 0.06f),
        radius = w * 0.42f,
        center = Offset(w * 0.92f, h * 0.34f),
      )
      drawCircle(
        color = NeonPink.copy(alpha = 0.06f),
        radius = w * 0.36f,
        center = Offset(w * 0.06f, h * 0.56f),
      )
      drawCircle(
        color = SkyAccent.copy(alpha = 0.05f),
        radius = w * 0.28f,
        center = Offset(w * 0.84f, h * 0.80f),
      )

      val wavePath = Path().apply {
        moveTo(0f, h * 0.78f)
        cubicTo(
          w * 0.28f,
          h * 0.72f,
          w * 0.72f,
          h * 0.86f,
          w,
          h * 0.77f,
        )
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = wavePath,
        brush = Brush.verticalGradient(
          colors = listOf(
            CobaltBlue.copy(alpha = 0.045f),
            NeonPink.copy(alpha = 0.05f),
          )
        ),
      )

      // Subtle ruled notebook dots
      val dotStep = 56f
      var y = h * 0.28f
      while (y < h * 0.85f) {
        var x = 28f
        while (x < w) {
          drawCircle(
            color = RoyalBlue.copy(alpha = 0.035f),
            radius = 2.2f,
            center = Offset(x, y),
          )
          x += dotStep
        }
        y += dotStep
      }
    }

    Icon(
      imageVector = Icons.AutoMirrored.Rounded.MenuBook,
      contentDescription = null,
      tint = RoyalBlue.copy(alpha = 0.055f),
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .offset(x = 18.dp, y = (-30).dp)
        .size(132.dp)
        .rotate(-14f),
    )
    Icon(
      imageVector = Icons.Rounded.Create,
      contentDescription = null,
      tint = DarkPink.copy(alpha = 0.05f),
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = (-14).dp, y = (-92).dp)
        .size(116.dp)
        .rotate(18f),
    )
  }
}

@Composable
private fun PortalHeader(
  activeBatch: StudyBatch?,
  syncStatus: SyncStatus,
  isAdminUnlocked: Boolean,
  onProfileClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
    color = Color.Transparent,
    shadowElevation = 10.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(RoyalBlue, CobaltBlueDeep, DarkPink, NeonPinkDark)
          )
        ),
    ) {
      Canvas(modifier = Modifier.matchParentSize()) {
        val w = size.width
        val h = size.height
        val wave = Path().apply {
          moveTo(0f, h * 0.62f)
          cubicTo(w * 0.35f, h * 0.38f, w * 0.65f, h * 0.88f, w, h * 0.56f)
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }
        drawPath(
          path = wave,
          color = Color.White.copy(alpha = 0.08f),
        )
        drawCircle(
          color = Color.White.copy(alpha = 0.08f),
          radius = w * 0.22f,
          center = Offset(w * 0.12f, h * 0.18f),
        )
        drawCircle(
          color = GoldLight.copy(alpha = 0.10f),
          radius = w * 0.16f,
          center = Offset(w * 0.92f, h * 0.24f),
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
          .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Non-interactive decorative graduation cap study badge (strictly no "+" button)
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .border(1.dp, Color.White.copy(alpha = 0.38f), CircleShape),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.School,
                contentDescription = null,
                tint = GoldLight,
                modifier = Modifier.size(20.dp),
              )
            }
          }

          Box(
            modifier = Modifier
              .size(48.dp)
              .shadow(12.dp, RoundedCornerShape(15.dp), ambientColor = GoldBase, spotColor = DarkPink)
              .clip(RoundedCornerShape(15.dp))
              .background(Brush.linearGradient(listOf(GoldLight, GoldBase, GoldDark)))
              .border(1.5.dp, Color(0xFFFEF08A), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = "AR",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
              color = Color.Black,
            )
          }

          IconButton(
            onClick = onProfileClick,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("account_menu_button"),
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Open Student Profile",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = activeBatch?.name ?: "${stringResource(R.string.brand_title_prefix)} ${stringResource(R.string.brand_title_accent)}",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
          color = Color.White,
          textAlign = TextAlign.Center,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val statusText = when (syncStatus) {
            SyncStatus.CONNECTING -> "🟡 Connecting..."
            SyncStatus.LIVE_SYNCED -> "🟢 Live Cloud Synced"
            SyncStatus.ERROR -> "🔴 Sync Error"
          }
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.16f))
              .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
              .padding(horizontal = 10.dp, vertical = 3.dp),
          ) {
            Text(
              text = statusText,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
            )
          }
          if (isAdminUnlocked) {
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(GoldLight)
                .padding(horizontal = 9.dp, vertical = 3.dp),
            ) {
              Text(
                text = "ADMIN ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                ),
                color = StudyInkPrimary,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        MotivationalBanner()
      }
    }
  }
}

@Composable
private fun MotivationalBanner() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White.copy(alpha = 0.16f))
      .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Icon(
      imageVector = Icons.Rounded.AutoStories,
      contentDescription = null,
      tint = GoldLight,
      modifier = Modifier.size(16.dp),
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = stringResource(R.string.banner_quote),
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      color = Color.White,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun SearchBarField(
  query: String,
  onQueryChange: (String) -> Unit,
  onClear: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(30.dp),
    color = StudyWhiteCard,
    shadowElevation = 6.dp,
    border = BorderStroke(
      1.2.dp,
      Brush.horizontalGradient(listOf(RoyalBlue.copy(alpha = 0.45f), DarkPink.copy(alpha = 0.45f))),
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = null,
        tint = DarkPink,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Box(
        modifier = Modifier
          .weight(1f)
          .padding(vertical = 10.dp),
      ) {
        if (query.isEmpty()) {
          Text(
            text = stringResource(R.string.search_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = StudyInkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = StudyInkPrimary),
          cursorBrush = SolidColor(RoyalBlue),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_input"),
        )
      }
      if (query.isNotEmpty()) {
        IconButton(
          onClick = onClear,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(36.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Clear search",
            tint = StudyInkSecondary,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun BatchesPage(
  batches: List<StudyBatch>,
  subjects: List<StudySubject>,
  lectures: List<StudyLecture>,
  searchQuery: String,
  onBatchClick: (StudyBatch) -> Unit,
) {
  val q = searchQuery.trim().lowercase()
  val filtered = remember(batches, q) {
    if (q.isEmpty()) batches
    else batches.filter {
      it.name.lowercase().contains(q) || it.subtitle.lowercase().contains(q)
    }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    if (q.isEmpty()) {
      item {
        HomeStudySummaryCard(
          batchCount = batches.size,
          subjectCount = subjects.size,
          lectureCount = lectures.size,
          completedCount = lectures.count { it.completed },
        )
      }
    }

    if (filtered.isEmpty()) {
      item {
        EmptyStateCard(
          message = if (q.isNotEmpty()) "No batches match \"$searchQuery\"" else "No batches found yet.",
        )
      }
    } else {
      itemsIndexed(filtered, key = { _, b -> b.id }) { index, batch ->
        val subCount = subjects.count { it.batchId == batch.id }
        val batchLectures = lectures.filter { it.batchId == batch.id }
        val completedCount = batchLectures.count { it.completed }
        val isEven = index % 2 == 0
        val cardSurface = if (isEven) StudyWhiteCard else StudyBlueTintCard
        val borderBrush = if (isEven) {
          Brush.linearGradient(listOf(DarkPink.copy(alpha = 0.45f), RoyalBlue.copy(alpha = 0.45f)))
        } else {
          Brush.linearGradient(listOf(RoyalBlue.copy(alpha = 0.45f), NeonPink.copy(alpha = 0.45f)))
        }
        val iconBadgeGradient = if (isEven) {
          listOf(DarkPink, NeonPinkDark)
        } else {
          listOf(RoyalBlue, CobaltBlueBright)
        }
        StudyRowCard(
          title = batch.name,
          subtitle = batch.subtitle,
          statChipPrimary = "$subCount Subjects",
          statChipSecondary = if (completedCount > 0) {
            "$completedCount/${batchLectures.size} Done"
          } else {
            "${batchLectures.size} Lectures"
          },
          primaryChipColor = if (isEven) StudyPinkTintCard else StudyBlueTintCard,
          primaryChipTextColor = if (isEven) DarkPink else RoyalBlue,
          secondaryChipColor = if (isEven) StudyBlueTintCard else StudyPinkTintCard,
          secondaryChipTextColor = if (isEven) RoyalBlue else DarkPink,
          icon = if (isEven) Icons.Rounded.School else Icons.AutoMirrored.Rounded.MenuBook,
          cardSurfaceColor = cardSurface,
          borderBrush = borderBrush,
          iconGradient = iconBadgeGradient,
          accentStripColor = if (isEven) DarkPink else RoyalBlue,
          onClick = { onBatchClick(batch) },
          tag = "batch_card_${batch.id}",
        )
      }
    }
  }
}

@Composable
private fun HomeStudySummaryCard(
  batchCount: Int,
  subjectCount: Int,
  lectureCount: Int,
  completedCount: Int,
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = Color.Transparent,
    shadowElevation = 8.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            listOf(RoyalBlue, CobaltBlueDeep, DarkPink)
          )
        ),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_study_hero_banner),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .matchParentSize()
          .alpha(0.18f),
      )

      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White.copy(alpha = 0.2f))
              .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Rounded.School,
              contentDescription = null,
              tint = GoldLight,
              modifier = Modifier.size(24.dp),
            )
          }
          Column {
            Text(
              text = "YOUR STUDY DASHBOARD",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = Color.White,
            )
            Text(
              text = "Select a batch to watch lectures & read notes",
              style = MaterialTheme.typography.bodySmall,
              color = NeonPinkSoft,
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          DashboardStatPill(
            icon = Icons.Rounded.School,
            value = "$batchCount Batches",
            tint = GoldLight,
            modifier = Modifier.weight(1f),
          )
          DashboardStatPill(
            icon = Icons.Rounded.AutoStories,
            value = "$subjectCount Subjects",
            tint = NeonPinkSoft,
            modifier = Modifier.weight(1f),
          )
          DashboardStatPill(
            icon = Icons.Rounded.PlayCircle,
            value = if (completedCount > 0) "$completedCount/$lectureCount Done" else "$lectureCount Classes",
            tint = Color.White,
            modifier = Modifier.weight(1f),
          )
        }
      }
    }
  }
}

@Composable
private fun DashboardStatPill(
  icon: ImageVector,
  value: String,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White.copy(alpha = 0.16f))
      .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = tint,
      modifier = Modifier.size(14.dp),
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
      ),
      color = Color.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun SubjectsPage(
  batch: StudyBatch?,
  subjects: List<StudySubject>,
  lectures: List<StudyLecture>,
  searchQuery: String,
  onBack: () -> Unit,
  onSubjectClick: (StudySubject) -> Unit,
) {
  val q = searchQuery.trim().lowercase()
  val filtered = remember(subjects, q) {
    if (q.isEmpty()) subjects
    else subjects.filter { it.name.lowercase().contains(q) }
  }

  val batchLectures = remember(batch, lectures) {
    if (batch == null) emptyList() else lectures.filter { it.batchId == batch.id }
  }
  val completedInBatch = batchLectures.count { it.completed }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    if (batch != null && q.isEmpty()) {
      item {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = StudyWhiteCard,
          shadowElevation = 6.dp,
          border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(RoyalBlue.copy(alpha = 0.4f), DarkPink.copy(alpha = 0.4f))),
          ),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(11.dp))
                  .background(Brush.linearGradient(listOf(RoyalBlue, DarkPink))),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp),
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = batch.subtitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = StudyInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "${subjects.size} Subjects • ${batchLectures.size} Lectures",
                  style = MaterialTheme.typography.bodySmall,
                  color = StudyInkSecondary,
                )
              }
            }
            if (batchLectures.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              LinearProgressIndicator(
                progress = { completedInBatch.toFloat() / batchLectures.size.toFloat() },
                color = DarkPink,
                trackColor = StudyLightBorder,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(7.dp)
                  .clip(CircleShape),
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "$completedInBatch of ${batchLectures.size} lectures completed",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = RoyalBlue,
              )
            }
          }
        }
      }
    }

    if (filtered.isEmpty()) {
      item {
        EmptyStateCard(message = "No subjects found")
      }
    } else {
      itemsIndexed(filtered, key = { _, s -> s.id }) { index, subject ->
        val subjectLectures = lectures.filter { it.subjectId == subject.id }
        val completedCount = subjectLectures.count { it.completed }
        val isEven = index % 2 == 0
        val cardSurface = if (isEven) StudyWhiteCard else StudyPinkTintCard
        val borderBrush = if (isEven) {
          Brush.linearGradient(listOf(RoyalBlue.copy(alpha = 0.45f), CobaltBlue.copy(alpha = 0.45f)))
        } else {
          Brush.linearGradient(listOf(DarkPink.copy(alpha = 0.45f), NeonPink.copy(alpha = 0.45f)))
        }
        val iconGradient = if (isEven) {
          listOf(RoyalBlue, CobaltBlueBright)
        } else {
          listOf(DarkPink, NeonPinkDark)
        }
        StudyRowCard(
          title = subject.name,
          subtitle = "${subjectLectures.size} Lectures Available",
          statChipPrimary = if (completedCount > 0) "$completedCount Done" else "Notes & Video",
          statChipSecondary = "",
          primaryChipColor = if (isEven) StudyBlueTintCard else StudyPinkTintCard,
          primaryChipTextColor = if (isEven) RoyalBlue else DarkPink,
          secondaryChipColor = StudyBlueTintCard,
          secondaryChipTextColor = RoyalBlue,
          icon = subjectIconFor(subject.name),
          cardSurfaceColor = cardSurface,
          borderBrush = borderBrush,
          iconGradient = iconGradient,
          accentStripColor = if (isEven) RoyalBlue else DarkPink,
          onClick = { onSubjectClick(subject) },
          tag = "subject_card_${subject.id}",
        )
      }
    }

    item {
      BackNavButton(
        text = stringResource(R.string.back_to_batches),
        onClick = onBack,
        tag = "back_to_batches_button",
      )
    }
  }
}

@Composable
private fun LecturesPage(
  subjectName: String,
  lectures: List<StudyLecture>,
  searchQuery: String,
  isAdminUnlocked: Boolean,
  onBack: () -> Unit,
  onOpenMedia: (MediaViewerRequest) -> Unit,
  onToggleCompleted: (StudyLecture) -> Unit,
  onDeleteLecture: (StudyLecture) -> Unit,
) {
  val q = searchQuery.trim().lowercase()
  val filtered = remember(lectures, q) {
    if (q.isEmpty()) lectures
    else lectures.filter { it.title.lowercase().contains(q) }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        shadowElevation = 6.dp,
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(listOf(RoyalBlue, CobaltBlueDeep, DarkPink)),
            RoundedCornerShape(18.dp),
          ),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = subjectIconFor(subjectName),
                contentDescription = null,
                tint = GoldLight,
                modifier = Modifier.size(20.dp),
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = subjectName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.18f))
              .padding(horizontal = 10.dp, vertical = 4.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.EditNote,
              contentDescription = null,
              tint = GoldLight,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${lectures.size} Classes",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
            )
          }
        }
      }
    }

    if (filtered.isEmpty()) {
      item {
        EmptyStateCard(
          message = "$subjectName में अभी कोई लेक्चर नहीं जुड़ा है।",
        )
      }
    } else {
      itemsIndexed(filtered, key = { _, l -> l.id }) { _, lecture ->
        LectureCard(
          lecture = lecture,
          isAdminUnlocked = isAdminUnlocked,
          onWatchVideo = {
            onOpenMedia(MediaViewerRequest(lecture.title, lecture.videoUrl, MediaType.VIDEO))
          },
          onOpenEngNotes = {
            onOpenMedia(MediaViewerRequest(lecture.title, lecture.engNotesUrl, MediaType.PDF))
          },
          onOpenHinNotes = {
            onOpenMedia(MediaViewerRequest(lecture.title, lecture.hinNotesUrl, MediaType.PDF))
          },
          onOpenPpt = {
            onOpenMedia(MediaViewerRequest(lecture.title, lecture.pptUrl, MediaType.PDF))
          },
          onToggleCompleted = { onToggleCompleted(lecture) },
          onDelete = { onDeleteLecture(lecture) },
        )
      }
    }

    item {
      BackNavButton(
        text = stringResource(R.string.back_to_subjects),
        onClick = onBack,
        tag = "back_to_subjects_button",
      )
    }
  }
}

@Composable
private fun StudyRowCard(
  title: String,
  subtitle: String,
  statChipPrimary: String,
  statChipSecondary: String,
  primaryChipColor: Color,
  primaryChipTextColor: Color,
  secondaryChipColor: Color,
  secondaryChipTextColor: Color,
  icon: ImageVector,
  cardSurfaceColor: Color,
  borderBrush: Brush,
  iconGradient: List<Color>,
  accentStripColor: Color,
  onClick: () -> Unit,
  tag: String,
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = cardSurfaceColor,
    border = BorderStroke(1.2.dp, borderBrush),
    shadowElevation = 6.dp,
    modifier = Modifier
      .fillMaxWidth()
      .minimumInteractiveComponentSize()
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag(tag),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(end = 14.dp, top = 14.dp, bottom = 14.dp, start = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
        modifier = Modifier
          .width(5.dp)
          .height(52.dp)
          .clip(CircleShape)
          .background(accentStripColor),
      )

      Spacer(modifier = Modifier.width(10.dp))

      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Brush.linearGradient(iconGradient)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(24.dp),
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = StudyInkPrimary,
        )
        if (subtitle.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = StudyInkSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
        if (statChipPrimary.isNotBlank() || statChipSecondary.isNotBlank()) {
          Spacer(modifier = Modifier.height(7.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (statChipPrimary.isNotBlank()) {
              MiniBadge(
                text = statChipPrimary,
                bgColor = primaryChipColor,
                textColor = primaryChipTextColor,
              )
            }
            if (statChipSecondary.isNotBlank()) {
              MiniBadge(
                text = statChipSecondary,
                bgColor = secondaryChipColor,
                textColor = secondaryChipTextColor,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(RoyalBlue, DarkPink))),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
          contentDescription = "Open $title",
          tint = Color.White,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@Composable
private fun MiniBadge(
  text: String,
  bgColor: Color,
  textColor: Color,
) {
  Box(
    modifier = Modifier
      .clip(CircleShape)
      .background(bgColor)
      .border(1.dp, textColor.copy(alpha = 0.25f), CircleShape)
      .padding(horizontal = 9.dp, vertical = 3.dp),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
      ),
      color = textColor,
    )
  }
}

@Composable
private fun LectureCard(
  lecture: StudyLecture,
  isAdminUnlocked: Boolean,
  onWatchVideo: () -> Unit,
  onOpenEngNotes: () -> Unit,
  onOpenHinNotes: () -> Unit,
  onOpenPpt: () -> Unit,
  onToggleCompleted: () -> Unit,
  onDelete: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = StudyWhiteCard,
    shadowElevation = 6.dp,
    border = BorderStroke(
      1.2.dp,
      Brush.linearGradient(listOf(RoyalBlue.copy(alpha = 0.38f), DarkPink.copy(alpha = 0.38f))),
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("lecture_card_${lecture.id}"),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Brush.linearGradient(listOf(RoyalBlue, DarkPink))),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = lecture.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = StudyInkPrimary,
          modifier = Modifier.weight(1f),
        )
        IconButton(
          onClick = onToggleCompleted,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(48.dp)
            .testTag("toggle_complete_${lecture.id}"),
        ) {
          Icon(
            imageVector = if (lecture.completed) {
              Icons.Rounded.CheckCircle
            } else {
              Icons.Rounded.RadioButtonUnchecked
            },
            contentDescription = if (lecture.completed) "Mark incomplete" else "Mark complete",
            tint = if (lecture.completed) WhatsAppGreen else StudyInkSecondary,
          )
        }
        if (isAdminUnlocked) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("delete_lecture_${lecture.id}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DeleteOutline,
              contentDescription = "Delete lecture",
              tint = DarkPink,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        LectureActionButton(
          label = if (lecture.videoUrl.isNotBlank()) stringResource(R.string.watch_video) else "NO VIDEO",
          icon = Icons.Rounded.PlayArrow,
          enabled = lecture.videoUrl.isNotBlank(),
          gradient = listOf(RoyalBlue, CobaltBlueBright),
          onClick = onWatchVideo,
          modifier = Modifier.weight(1f),
        )
        LectureActionButton(
          label = if (lecture.engNotesUrl.isNotBlank()) stringResource(R.string.eng_notes) else "NO ENG PDF",
          icon = Icons.Rounded.Description,
          enabled = lecture.engNotesUrl.isNotBlank(),
          gradient = listOf(DarkPink, NeonPinkDark),
          onClick = onOpenEngNotes,
          modifier = Modifier.weight(1f),
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        LectureActionButton(
          label = if (lecture.hinNotesUrl.isNotBlank()) stringResource(R.string.hin_notes) else "NO HIN PDF",
          icon = Icons.Rounded.AutoStories,
          enabled = lecture.hinNotesUrl.isNotBlank(),
          gradient = listOf(VioletAccent, VioletDeep),
          onClick = onOpenHinNotes,
          modifier = Modifier.weight(1f),
        )
        LectureActionButton(
          label = if (lecture.pptUrl.isNotBlank()) stringResource(R.string.class_ppt) else "NO PPT",
          icon = Icons.Rounded.Slideshow,
          enabled = lecture.pptUrl.isNotBlank(),
          gradient = listOf(CyanAccent, CyanDeep),
          onClick = onOpenPpt,
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}

@Composable
private fun LectureActionButton(
  label: String,
  icon: ImageVector,
  enabled: Boolean,
  gradient: List<Color>,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val bgBrush = if (enabled) {
    Brush.linearGradient(gradient)
  } else {
    SolidColor(DisabledButtonBg)
  }

  Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Transparent,
      disabledContainerColor = Color.Transparent,
    ),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
    modifier = modifier
      .minimumInteractiveComponentSize()
      .height(48.dp)
      .background(brush = bgBrush, shape = RoundedCornerShape(12.dp)),
  ) {
    if (enabled) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(5.dp))
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
      color = if (enabled) Color.White else DisabledButtonText,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun BackNavButton(
  text: String,
  onClick: () -> Unit,
  tag: String,
) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(30.dp),
    colors = ButtonDefaults.buttonColors(containerColor = StudyWhiteCard),
    border = BorderStroke(
      1.2.dp,
      Brush.horizontalGradient(listOf(RoyalBlue, DarkPink)),
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
    contentPadding = PaddingValues(0.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 6.dp, bottom = 14.dp)
      .minimumInteractiveComponentSize()
      .height(48.dp)
      .testTag(tag),
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
      contentDescription = null,
      tint = DarkPink,
      modifier = Modifier.size(16.dp),
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text.removePrefix("← "),
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
      color = RoyalBlue,
    )
  }
}

@Composable
private fun EmptyStateCard(message: String) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = StudyWhiteCard,
    shadowElevation = 4.dp,
    border = BorderStroke(1.dp, StudyLightBorder),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 20.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(RoyalBlue, DarkPink))),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.MenuBook,
          contentDescription = null,
          tint = GoldLight,
          modifier = Modifier.size(32.dp),
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = StudyInkSecondary,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun StudyBottomBar(
  whatsappUrl: String,
  telegramUrl: String,
  footerText: String,
  onOpenExternalError: (String) -> Unit,
) {
  val context = LocalContext.current

  Surface(
    color = StudyWhiteCard.copy(alpha = 0.96f),
    shadowElevation = 10.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      HorizontalDivider(color = StudyLightBorder, thickness = 1.dp)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = { launchExternalUrl(context, whatsappUrl, onOpenExternalError) },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("whatsapp_link_button"),
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.Forum,
                contentDescription = "Open WhatsApp",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
              )
            }
          }

          IconButton(
            onClick = { launchExternalUrl(context, telegramUrl, onOpenExternalError) },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("telegram_link_button"),
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SkyAccent),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Open Telegram",
                tint = Color.White,
                modifier = Modifier.size(19.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = footerText,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
          color = StudyInkSecondary,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun StudentProfileDialog(
  userContactLabel: String?,
  isAdminUnlocked: Boolean,
  onDismiss: () -> Unit,
  onLogout: () -> Unit,
) {
  val parts = remember(userContactLabel) {
    userContactLabel
      ?.split(" • ", limit = 2)
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
      .orEmpty()
  }
  val primaryLabel = parts.getOrNull(0) ?: if (isAdminUnlocked) "Admin" else "Student"
  val secondaryLabel = parts.getOrNull(1)

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = StudyWhiteCard,
      shadowElevation = 14.dp,
      border = BorderStroke(
        width = 1.5.dp,
        brush = Brush.linearGradient(listOf(RoyalBlue, DarkPink)),
      ),
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 420.dp)
        .testTag("student_profile_dialog"),
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(RoyalBlue, CobaltBlueDeep, DarkPink, NeonPinkDark)
              )
            )
            .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 18.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f),
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.2f))
                  .border(1.5.dp, Color.White.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = if (isAdminUnlocked) Icons.Rounded.School else Icons.Rounded.Person,
                  contentDescription = null,
                  tint = GoldLight,
                  modifier = Modifier.size(26.dp),
                )
              }
              Column {
                Text(
                  text = if (isAdminUnlocked) "Admin Profile" else "Student Profile",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                  color = Color.White,
                )
                Text(
                  text = stringResource(R.string.app_name),
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.85f),
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .size(48.dp)
                .testTag("close_profile_dialog_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close Profile Dialog",
                tint = Color.White,
              )
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = StudyBlueTintCard,
            border = BorderStroke(1.dp, StudyLightBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(RoyalBlue.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Rounded.Person,
                  contentDescription = null,
                  tint = RoyalBlue,
                  modifier = Modifier.size(20.dp),
                )
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isAdminUnlocked) "Signed In Account" else "Student Name",
                  style = MaterialTheme.typography.labelMedium,
                  color = StudyInkSecondary,
                )
                Text(
                  text = primaryLabel,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = StudyInkPrimary,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }

          if (secondaryLabel != null) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = StudyPinkTintCard,
              border = BorderStroke(1.dp, DarkPink.copy(alpha = 0.18f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkPink.copy(alpha = 0.14f)),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Rounded.PhoneAndroid,
                    contentDescription = null,
                    tint = DarkPink,
                    modifier = Modifier.size(20.dp),
                  )
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Mobile Number",
                    style = MaterialTheme.typography.labelMedium,
                    color = StudyInkSecondary,
                  )
                  Text(
                    text = secondaryLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = StudyInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Button(
            onClick = onLogout,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = ErrorRed,
              contentColor = Color.White,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .minimumInteractiveComponentSize()
              .height(50.dp)
              .testTag("logout_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.Logout,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Logout",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
          }
        }
      }
    }
  }
}

private fun subjectIconFor(subjectName: String): ImageVector {
  val upper = subjectName.uppercase()
  return when {
    upper.contains("BIO") || upper.contains("PHY") || upper.contains("CHEM") || upper.contains("SCIENCE") ->
      Icons.Rounded.Science
    upper.contains("HISTORY") -> Icons.Rounded.HistoryEdu
    upper.contains("POLITY") || upper.contains("ECON") -> Icons.Rounded.AccountBalance
    upper.contains("GEO") || upper.contains("ENV") || upper.contains("ECOLOGY") -> Icons.Rounded.Public
    upper.contains("CURRENT") || upper.contains("STATIC") || upper.contains("BIHAR") -> Icons.Rounded.AutoStories
    else -> Icons.AutoMirrored.Rounded.MenuBook
  }
}

private fun launchExternalUrl(
  context: android.content.Context,
  rawUrl: String,
  onError: (String) -> Unit,
) {
  val trimmed = rawUrl.trim()
  if (!trimmed.startsWith("http://", ignoreCase = true) &&
    !trimmed.startsWith("https://", ignoreCase = true)
  ) {
    onError("Invalid link URL")
    return
  }
  try {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trimmed)))
  } catch (_: ActivityNotFoundException) {
    onError("No app installed to open this link.")
  } catch (_: Exception) {
    onError("Unable to open link.")
  }
}
