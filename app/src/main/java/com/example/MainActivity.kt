package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.StudentSessionStore
import com.example.data.StudyRepository
import com.example.ui.StudyViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudyPortalScreen
import com.example.ui.theme.StudyAspirantTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

const val ROUTE_SPLASH = "splash"
const val ROUTE_LOGIN = "login"
const val ROUTE_PORTAL = "portal"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      StudyAspirantTheme {
        AppNavigation()
      }
    }
  }
}

@Composable
fun AppNavigation() {
  val context = LocalContext.current
  val appContext = context.applicationContext
  val scope = rememberCoroutineScope()
  val navController = rememberNavController()

  val sessionStore = remember(appContext) { StudentSessionStore(appContext) }
  val repository = remember(appContext) {
    val databaseId = appContext.getString(R.string.firestore_database_id)
    StudyRepository(
      db = FirebaseFirestore.getInstance(databaseId),
    )
  }

  val studentSession by sessionStore.sessionFlow
    .collectAsStateWithLifecycle(initialValue = null)

  var isAdminLocal by remember { mutableStateOf(false) }
  var shouldOpenAdminDashboard by remember { mutableStateOf(false) }

  val isAdminActive = (studentSession?.isAdminSession == true) || isAdminLocal
  val isStudentLoggedIn = studentSession != null && studentSession?.isAdminSession == false && !isAdminLocal
  val isLoggedIn = isAdminActive || isStudentLoggedIn
  val latestIsLoggedIn by rememberUpdatedState(isLoggedIn)

  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route

  LaunchedEffect(isLoggedIn, currentRoute) {
    if (currentRoute == null || currentRoute == ROUTE_SPLASH) return@LaunchedEffect
    if (!isLoggedIn && currentRoute != ROUTE_LOGIN) {
      shouldOpenAdminDashboard = false
      navController.navigate(ROUTE_LOGIN) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
      }
    } else if (isLoggedIn && currentRoute != ROUTE_PORTAL) {
      navController.navigate(ROUTE_PORTAL) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = ROUTE_SPLASH,
    enterTransition = { fadeIn() },
    exitTransition = { fadeOut() },
  ) {
    composable(ROUTE_SPLASH) {
      SplashScreen(
        onSplashFinished = {
          val target = if (latestIsLoggedIn) ROUTE_PORTAL else ROUTE_LOGIN
          navController.navigate(target) {
            popUpTo(ROUTE_SPLASH) { inclusive = true }
            launchSingleTop = true
          }
        },
      )
    }

    composable(ROUTE_LOGIN) {
      AuthScreen(
        repository = repository,
        sessionStore = sessionStore,
        onAdminAuthSuccess = {
          isAdminLocal = true
          shouldOpenAdminDashboard = true
          scope.launch {
            sessionStore.setAdminSession(true)
          }
        },
        onStudentLoginSuccess = {
          isAdminLocal = false
          shouldOpenAdminDashboard = false
        },
      )
    }

    composable(ROUTE_PORTAL) {
      val session = studentSession
      if (isLoggedIn) {
        val sessionKey = if (isAdminActive) "admin_local" else "stu_${session?.studentId}"
        key(sessionKey) {
          val studyViewModel: StudyViewModel = viewModel(
            key = sessionKey,
            factory = viewModelFactory {
              initializer {
                StudyViewModel(
                  repository = repository,
                  sessionStore = sessionStore,
                  initialAdminUnlocked = isAdminActive,
                  openAdminDashboardOnStart = isAdminActive && shouldOpenAdminDashboard,
                )
              }
            },
          )

          LaunchedEffect(sessionKey, shouldOpenAdminDashboard) {
            if (isAdminActive && shouldOpenAdminDashboard) {
              shouldOpenAdminDashboard = false
              studyViewModel.onAdminUnlockedFromLogin()
            }
          }

          val contactLabel = if (isAdminActive) {
            "Admin"
          } else {
            val name = session?.fullName.orEmpty()
            val phone = session?.phoneNumber.orEmpty()
            if (name.isNotBlank() && phone.isNotBlank()) "$name • $phone" else name.ifBlank { phone }
          }

          StudyPortalScreen(
            viewModel = studyViewModel,
            userContactLabel = contactLabel,
            onSignOut = {
              isAdminLocal = false
              shouldOpenAdminDashboard = false
              scope.launch {
                sessionStore.clearSession()
                navController.navigate(ROUTE_LOGIN) {
                  popUpTo(0) { inclusive = true }
                  launchSingleTop = true
                }
              }
            },
          )
        }
      }
    }
  }
}
