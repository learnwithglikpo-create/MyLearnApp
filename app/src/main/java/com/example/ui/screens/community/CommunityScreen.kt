package com.example.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityPostEntity
import com.example.ui.components.CodeBlockView
import com.example.ui.components.CodeCraftButton
import com.example.ui.components.CodeCraftCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    posts: List<CommunityPostEntity>,
    onToggleLike: (String) -> Unit,
    onCreatePost: (content: String, code: String?, tags: String, type: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showComposerSheet by remember { mutableStateOf(false) }

    val filters = listOf("All", "Questions", "Snippet", "Achievement")

    val filteredPosts = posts.filter { post ->
        val matchesFilter = if (selectedFilter == "All") true else post.type.equals(selectedFilter, ignoreCase = true)
        val matchesQuery = if (searchQuery.isBlank()) true else {
            post.content.contains(searchQuery, ignoreCase = true) ||
                    post.authorName.contains(searchQuery, ignoreCase = true) ||
                    post.tags.contains(searchQuery, ignoreCase = true)
        }
        matchesFilter && matchesQuery
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Developer Community",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Ask questions, share snippets, and celebrate streak milestones.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search topics, tags or authors...", color = TextSecondaryDark) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = TextSecondaryDark
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = ElectricTeal,
                        unfocusedBorderColor = SurfaceBorderDark
                    ),
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DeepIndigo else SurfaceDark)
                                .border(1.dp, if (isSelected) ElectricTeal else SurfaceBorderDark, RoundedCornerShape(12.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else TextSecondaryDark,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Posts List
            items(filteredPosts) { post ->
                CommunityPostCard(
                    post = post,
                    onLike = { onToggleLike(post.id) }
                )
            }
        }

        // New Post Floating Action Button (FAB)
        FloatingActionButton(
            onClick = { showComposerSheet = true },
            containerColor = ElectricTeal,
            contentColor = CharcoalBg,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Post"
            )
        }
    }

    // Composer Sheet
    if (showComposerSheet) {
        PostComposerSheet(
            onDismiss = { showComposerSheet = false },
            onSubmit = { content, code, tags, type ->
                onCreatePost(content, code, tags, type)
                showComposerSheet = false
            }
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPostEntity,
    onLike: () -> Unit
) {
    CodeCraftCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceDark,
        borderColor = SurfaceBorderDark
    ) {
        // Author Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C344A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricTeal
                        )
                    )
                }

                Column {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${post.authorHandle} • Just now",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Post Type Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF22283A))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = post.type,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = when (post.type) {
                            "Achievement" -> AmberStreak
                            "Snippet" -> ElectricTeal
                            else -> Color(0xFF8172FF)
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Body Content
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                lineHeight = 20.sp
            )
        )

        // Code Snippet Card
        if (!post.codeSnippet.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            CodeBlockView(
                code = post.codeSnippet,
                language = post.language ?: "python",
                showLineNumbers = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Tags
        if (post.tags.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = post.tags,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricTeal,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Actions Row (Like, Comment, Share)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onLike),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) CoralRed else TextSecondaryDark,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${post.likeCount}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (post.isLiked) CoralRed else TextSecondaryDark
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comments",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${post.commentCount}",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share",
                tint = TextSecondaryDark,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostComposerSheet(
    onDismiss: () -> Unit,
    onSubmit: (content: String, code: String?, tags: String, type: String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("#codecraft, #coding") }
    var postType by remember { mutableStateOf("Question") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF191C28),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Create Community Post",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            // Post type selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Question", "Snippet", "Achievement").forEach { type ->
                    val isSelected = postType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DeepIndigo else Color(0xFF262A3B))
                            .clickable { postType = type }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) ElectricTeal else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("What are you working on or asking about?", color = TextSecondaryDark) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = ElectricTeal,
                    unfocusedBorderColor = SurfaceBorderDark
                )
            )

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                placeholder = { Text("Optional code snippet (JavaScript, Python, etc.)", color = TextSecondaryDark) },
                modifier = Modifier.fillMaxWidth().height(90.dp),
                textStyle = CodeTextStyle.copy(fontSize = 12.sp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CodeEditorBg,
                    unfocusedContainerColor = CodeEditorBg,
                    focusedBorderColor = SurfaceBorderDark,
                    unfocusedBorderColor = SurfaceBorderDark
                )
            )

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags", color = TextSecondaryDark) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = ElectricTeal,
                    unfocusedBorderColor = SurfaceBorderDark
                )
            )

            CodeCraftButton(
                text = "Publish Post",
                onClick = {
                    if (content.isNotBlank()) {
                        onSubmit(content, if (code.isBlank()) null else code, tags, postType)
                    }
                },
                enabled = content.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
