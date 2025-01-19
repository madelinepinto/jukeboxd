package widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun SideNav(tabs: List<Tab>) {
    val tabNavigator = LocalTabNavigator.current

    Column(
        modifier = Modifier.width(width = 200.dp).background(Color.LightGray).fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (tab in tabs) {
           TextButton(
               onClick = { tabNavigator.current = tab },
               modifier = Modifier.fillMaxWidth()
           ) {
               Text(tab.options.title)
           }
        }
    }
}
