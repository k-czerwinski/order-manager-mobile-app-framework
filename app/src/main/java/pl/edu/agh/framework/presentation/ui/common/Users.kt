package pl.edu.agh.framework.presentation.ui.common

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.agh.R
import pl.edu.agh.framework.model.User
import pl.edu.agh.framework.model.UserListViewItem

@Composable
fun UserListItem(user: UserListViewItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(
                        R.string.user_identity_title,
                        user.firstName,
                        user.lastName
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.user_role_subtitle,
                        stringResource(user.role.displayNameCode)
                    ),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UserList(users: List<UserListViewItem>, onUserClick: (user: UserListViewItem) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            UserListItem(
                user = user,
                onClick = { onUserClick(user) })
        }
    }
}

@Composable
fun UserListScreen(
    users: List<UserListViewItem>,
    onUserClick: (user: UserListViewItem) -> Unit,
    bottomButton: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.user_list_title),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            UserList(users, onUserClick = onUserClick)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            bottomButton()
        }
    }
}

@Composable
fun UserDetails(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoBlock(title = stringResource(R.string.user_info_first_name), content = user.firstName)
        InfoBlock(title = stringResource(R.string.user_info_last_name), content = user.lastName)
        InfoBlock(
            title = stringResource(R.string.user_info_role),
            content = stringResource(user.role.displayNameCode)
        )
        InfoBlock(title = stringResource(R.string.user_info_username), content = user.username)
    }
}

@Composable
fun InfoBlock(title: String, content: String) {
    InfoBlock(title) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InfoBlock(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
