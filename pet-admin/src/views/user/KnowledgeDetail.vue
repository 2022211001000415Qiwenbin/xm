
<template>
  <div class="knowledge-detail-container">
    <el-card class="detail-card" v-loading="loading">
      <div class="detail-header">
        <h1 class="detail-title">{{ article.title }}</h1>
        <div class="detail-meta">
          <el-tag type="primary" size="small">{{ article.category }}</el-tag>
          <span>作者：{{ article.authorName }}</span>
          <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
          <span>{{ article.createTime }}</span>
        </div>
      </div>
      <el-divider />
      <div class="detail-content">{{ article.content }}</div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-card">
      <template #header>
        <div class="comment-header">
          <span>评论区（{{ comments.length }}条）</span>
        </div>
      </template>

      <!-- 发表评论 -->
      <div class="comment-form">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
        />
        <el-button type="primary" @click="handleComment" style="margin-top: 10px" :disabled="!commentContent.trim()">发表评论</el-button>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条吧" />
        <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
          <div class="comment-avatar">
            <el-avatar :size="36" :src="comment.userAvatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="comment-body">
            <div class="comment-top">
              <span class="comment-user">{{ comment.userName }}</span>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <div class="back-btn" @click="$router.back()">
      <el-icon><ArrowLeft /></el-icon>返回列表
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { View, User } from '@element-plus/icons-vue'
import { getKnowledgeDetail, getCommentList, addComment } from '@/api/petKnowledge'

const route = useRoute()
const store = useStore()
const loading = ref(false)
const article = ref({})
const comments = ref([])
const commentContent = ref('')

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeDetail(route.params.id)
    article.value = res.data
  } catch (error) {
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await getCommentList(route.params.id)
    comments.value = res.data
  } catch (error) {
    ElMessage.error('获取评论失败')
  }
}

const handleComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    await addComment({
      knowledgeId: route.params.id,
      userId: store.state.user.userId,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

onMounted(() => {
  loadDetail()
  loadComments()
})
</script>

<style scoped lang="scss">
.knowledge-detail-container {

  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 10px;
    background: #fff;
    border: 1px solid #E5E7EB;
    color: #6B7280;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    margin-bottom: 20px;

    &:hover {
      color: #FF6B35;
      border-color: rgba(255,107,53,0.3);
      background: rgba(255,107,53,0.04);
    }
  }

  .detail-card {
    margin-bottom: 24px;
    border-radius: 20px;
    border: 1px solid #F3F4F6;

    .detail-header {
      .detail-title {
        margin: 0 0 16px;
        font-size: 26px;
        color: #1F2937;
        font-weight: 800;
        line-height: 1.4;
      }

      .detail-meta {
        display: flex;
        align-items: center;
        gap: 18px;
        color: #9CA3AF;
        font-size: 13px;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .el-tag { border-radius: 8px; }
      }
    }

    .detail-content {
      line-height: 2;
      font-size: 15px;
      color: #374151;
      white-space: pre-wrap;
      padding: 8px 0;
    }
  }

  .comment-card {
    border-radius: 20px;
    border: 1px solid #F3F4F6;

    .comment-header {
      font-size: 18px;
      font-weight: 700;
      color: #1F2937;
    }

    .comment-form {
      margin-bottom: 28px;

      :deep(.el-textarea__inner) {
        border-radius: 12px;
      }

      :deep(.el-button--primary) {
        background: linear-gradient(135deg, #FF8A65, #FF6B35);
        border: none;
        border-radius: 10px;
        font-weight: 600;
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 14px;
        padding: 18px 0;
        border-bottom: 1px solid #F3F4F6;

        &:last-child { border-bottom: none; }

        .comment-avatar {
          flex-shrink: 0;

          .el-avatar {
            border: 2px solid #FFD5C0;
          }
        }

        .comment-body {
          flex: 1;

          .comment-top {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;

            .comment-user {
              font-weight: 700;
              color: #374151;
              font-size: 14px;
            }

            .comment-time {
              color: #D1D5DB;
              font-size: 12px;
            }
          }

          .comment-text {
            margin: 0;
            color: #4B5563;
            font-size: 14px;
            line-height: 1.7;
          }
        }
      }
    }
  }
}
</style>
