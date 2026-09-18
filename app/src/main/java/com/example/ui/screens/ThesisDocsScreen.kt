package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ThesisDocsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BÁO CÁO ĐỒ ÁN: GAME 'TURBO PURR CAT'",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tài liệu hệ thống thiết kế và kế hoạch phát triển chi tiết thể loại Clicker/Idle trên nền tảng Android.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 1: Tổng quan đề tài",
                content = "• 1.1 Lý do chọn đề tài: Thể loại clicker/idle dễ tiếp cận, chi phí thấp, phù hợp làm sản phẩm portfolio.\n• 1.2 Mục tiêu: Game clicker cơ chế chạm tăng điểm, hài hước (mèo), tích hợp AdMob, phát hành Google Play.\n• 1.3 Phạm vi: Android (Kotlin & Compose / Unity), MVP offline-first.\n• 1.4 Đối tượng: Casual 12+, thích giải trí ngắn hạn và meme."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 2: Cơ sở công nghệ",
                content = "• 2.1 Lựa chọn: Unity + C# hoặc Android Native Kotlin + Compose.\n• 2.2 Kiến trúc tổng quát: Client chạy offline, lưu trữ cục bộ (Local Storage/SharedPreferences/Room), Google Mobile Ads SDK.\n• 2.3 Công cụ hỗ trợ: Android Studio / Jetpack Compose, Git, AI asset generator."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 3: Phân tích & Thiết kế hệ thống",
                content = "• 3.1 Yêu cầu chức năng: Tap tăng PurrMeter, decay tự động theo thời gian, 4 mốc trạng thái mèo, hiệu ứng đỉnh 100%, đếm số lần đạt đỉnh mở khóa skin, Ad banner & rewarded ads.\n• 3.2 Yêu cầu phi chức năng: Phản hồi <100ms, dung lượng <100MB, Android 7.0+.\n• 3.3 State Machine: 4 mốc (0-40% mở to, 41-80% lim dim đuôi ngoe nguẩy, 81-99% nhắm hờ rung nhẹ, 100% mãn nguyện thăng hoa).\n• 3.4 Local Schema: PurrCount, Skin unlocked flags, ActiveSkin."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 4: Triển khai (Implementation)",
                content = "• 4.1 Xây dựng UI & Animator Controller quản lý 4 trạng thái.\n• 4.2 Module logic cốt lõi: xử lý input, tính toán decay rate, state transition.\n• 4.3 Hiệu ứng & Âm thanh: Particle system, meo/grừ grừ audio simulation, screen shake.\n• 4.4 Lưu tiến độ & Mở khóa skin dạng grid UI.\n• 4.5 Quảng cáo: Tích hợp Banner & Rewarded Ad SDK."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 5: Kiểm thử (Testing)",
                content = "• 5.1 Unit Test: Kiểm tra hàm decay và mốc chuyển trạng thái %.\n• 5.2 Kiểm thử thiết bị thật: APK debug, đo độ trễ chạm, test load quảng cáo test ID.\n• 5.3 UAT: Đánh giá độ gây nghiện và phản hồi người dùng casual."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 6: Triển khai phát hành (Deployment)",
                content = "• 6.1 Build production: Ký app bằng Keystore riêng, chuyển sang Ad ID thật.\n• 6.2 Google Play Console: Phí 25$, chuẩn bị asset 512x512, screenshot, Privacy Policy.\n• 6.3 Quy trình: Internal Testing -> Production -> Review."
            )
        }

        item {
            ThesisChapterCard(
                chapter = "Chương 7: Kết luận & Hướng phát triển",
                content = "• 7.1 Kết quả: Sản phẩm MVP hoàn chỉnh chạy mượt trên Android.\n• 7.2 Hạn chế: Offline-first chưa có backend leaderboard online.\n• 7.3 Hướng phát triển: Thêm skin theo mùa, tích hợp Firebase cloud sync, nhiệm vụ hàng ngày (daily quest)."
            )
        }
    }
}

@Composable
fun ThesisChapterCard(chapter: String, content: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = chapter,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
